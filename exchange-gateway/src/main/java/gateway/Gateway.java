package gateway;

import communicators.Envelope;
import communicators.TcpCommunicator;
import communicators.UdpCommunicator;
import messages.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import security.Decrypter;
import security.Encrypter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.*;

public class Gateway {

    private static final Logger LOG = LogManager.getFormatterLogger(Gateway.class.getName());

    /**
     * Rate is in milliseconds
     */
    private final static int TOP_OF_BOOK_REFRESH_RATE = 1000;

    /**
     * Rate is in milliseconds
     */
    private final static int TOP_OF_BOOK_BROADCAST_RATE = 1500;

    /**
     * Rate is in milliseconds
     */
    private final static int SCOREBOARD_BROADCAST_RATE = 1500;

    public final static int PORT = 2000;

    private UdpCommunicator udpCommunicator;

    /**
     * Key : matching engine symbol
     * Val : TcpCommunicator object
     */
    private ConcurrentHashMap<String, TcpCommunicator> symbolToMatchingEngineMap;

    /**
     * Key : playerId
     * Val : PlayerDetailEntry object
     */
    private ConcurrentHashMap<Short, PlayerDetailEntry> idToPlayerDetailMap;

    /**
     * Key : symbol
     * Val : TopOfBookEntry object
     */
    private ConcurrentHashMap<String, TopOfBookEntry> symbolToTopOfBookMap;

    public Gateway(UdpCommunicator udpCommunicator) throws IOException {
        this.symbolToMatchingEngineMap = new ConcurrentHashMap<>();
        this.idToPlayerDetailMap = new ConcurrentHashMap<>();
        this.symbolToTopOfBookMap = new ConcurrentHashMap<>();
        this.udpCommunicator = udpCommunicator;
        initMatchingEngineMessageListeners();
        initUdpDispatcherListeners();
        initTopOfBookRefresh();
        initTopOfBookBroadcast();
        initScoreboardBroadcast();
    }

    private void initTopOfBookRefresh() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(TOP_OF_BOOK_REFRESH_RATE);
                    symbolToMatchingEngineMap.forEach((symbol, tcpCommunicator) -> {
                        try {
                            tcpCommunicator.sendReliably(new TopOfBookRequestMessage(), TopOfBookResponseMessage.class);
                            LOG.info("Sent TopOfBookRefresh request to %s", symbol);
                        } catch (IOException e) {
                            if (e instanceof SocketException) {
                                LOG.error("Failure in Gateway while requesting Top Of Book from %s -> %s -- removing tcpCommunicator", symbol, e.getMessage());
                                symbolToMatchingEngineMap.remove(symbol);
                            } else {
                                LOG.error("Failure in Gateway while requesting Top Of Book from %s -> %s", symbol, e.getMessage());
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    LOG.error("Top of Book refresh interrupted -> %s", e.getMessage());
                }
            }
        }).start();
        LOG.info("Initialized TopOfBookRefresh worker");
    }

    private void initTopOfBookBroadcast() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(TOP_OF_BOOK_BROADCAST_RATE);
                    idToPlayerDetailMap.forEach((__, playerDetailEntry) ->
                            symbolToTopOfBookMap.forEach((symbol, topofBookEntry) -> {
                                TopOfBookNotificationMessage notfication = new TopOfBookNotificationMessage(
                                        symbol,
                                        topofBookEntry.getAsks(),
                                        topofBookEntry.getBids()
                                );
                                try {
                                    udpCommunicator.send(
                                            notfication,
                                            playerDetailEntry.getSocketAddress().getAddress(),
                                            playerDetailEntry.getSocketAddress().getPort()
                                    );
                                    LOG.info(
                                            "Sent TopOfBookNotificationMessage to player %d (%s)",
                                            playerDetailEntry.getId(),
                                            playerDetailEntry.getName()
                                    );
                                } catch (IOException e) {
                                    LOG.error("Failure in Gateway while broadcasting Top Of Book to %s -> %s",
                                            symbol,
                                            e.getMessage()
                                    );
                                }
                            }));
                } catch (InterruptedException e) {
                    LOG.error("Top of Book broadcast interrupted -> %s", e.getMessage());
                }
            }
        }).start();
        LOG.info("Initialized TopOfBookBroadcast worker");
    }

    private void initScoreboardBroadcast() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(SCOREBOARD_BROADCAST_RATE);
                } catch (InterruptedException e) {
                    LOG.error("Scoreboard broadcast interrupted -> %s", e.getMessage());
                }

                ArrayList<ScoreboardMessage.ScoreboardEntry> scoreboard = new ArrayList<>(symbolToTopOfBookMap.size());
                idToPlayerDetailMap.forEach((playerId, playerDetailEntry) -> {
                    scoreboard.add(
                            new ScoreboardMessage.ScoreboardEntry(
                                    playerDetailEntry.getName(),
                                    getPlayerNetWorth(playerId),
                                    getPlayerReturnOnInvestments(playerId)
                            )
                    );
                });

                ScoreboardMessage scoreboardMessage = new ScoreboardMessage(UUID.randomUUID(), scoreboard);
                idToPlayerDetailMap.forEach((__, playerDetailEntry) -> {
                    try {
                        udpCommunicator.send(
                                scoreboardMessage,
                                playerDetailEntry.getSocketAddress().getAddress(),
                                playerDetailEntry.getSocketAddress().getPort()
                        );
                    } catch (IOException e) {
                        LOG.error("Failure in Gateway while broadcasting scoreboard to %s -> %s",
                                playerDetailEntry.getName(),
                                e.getMessage()
                        );
                    }
                });
            }
        }).start();
        LOG.info("Initialized ScoreboardBroadcast worker");
    }

    // TODO: Test me
    public int getPlayerNetWorth(short playerId) {
        PlayerDetailEntry playerDetailEntry = idToPlayerDetailMap.get(playerId);
        final int[] debts = {0};
        playerDetailEntry.getSymbolToSharesMap().forEach((symbol, shares) -> {
            if (shares < 0) {
                debts[0] += symbolToTopOfBookMap.get(symbol).getBids().get(0).price * abs(shares);
            }
        });
        return playerDetailEntry.getCash() + getPlayerEquity(playerId) - debts[0];
    }

    // TODO: Test me
    public float getPlayerReturnOnInvestments(short playerId) {
        PlayerDetailEntry playerDetailEntry = idToPlayerDetailMap.get(playerId);

        // Handle short selling
        final int[] trueTotalInvestments = {playerDetailEntry.getTotalInvestments()};
        playerDetailEntry.getSymbolToSharesMap().forEach((symbol, shares) -> {
            if (shares >= 0) return;
            trueTotalInvestments[0] += symbolToTopOfBookMap.get(symbol).getBids().get(0).getPrice() * shares;
        });

        if (playerDetailEntry.getCashFromSales() == 0) {
            return 0;
        }
        return (float) playerDetailEntry.getCashFromSales() / trueTotalInvestments[0];
    }

    // TODO: Test me
    public int getPlayerEquity(short playerId) {
        PlayerDetailEntry playerDetailEntry = idToPlayerDetailMap.get(playerId);
        final int[] equity = {0};
        playerDetailEntry.getSymbolToSharesMap().forEach((symbol, shares) -> {
            if (shares > 0) {
                equity[0] += symbolToTopOfBookMap.get(symbol).getAsks().get(0).getPrice() * shares;
            }
        });
        return equity[0];
    }

    private void initMatchingEngineMessageListeners() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        new Thread(() -> {
            while (true) {
                TcpCommunicator tcpCommunicator;
                try {
                    tcpCommunicator = new TcpCommunicator(serverSocket.accept(), new Decrypter(), new Encrypter());
                } catch (IOException e) {
                    LOG.error("Failure in Gateway RegisterMatchingEngineMessage listener while awaiting connection -> %s", e.getMessage());
                    continue;
                }
                LOG.info("Connection from %s", serverSocket.getInetAddress().getHostAddress());

                tcpCommunicator.registerForDispatch(
                        RegisterMatchingEngineMessage.class,
                        (Envelope<RegisterMatchingEngineMessage> env) -> {
                            try {
                                registerMatchingEngine(env, tcpCommunicator);
                            } catch (IOException e) {
                                LOG.error("Failure while attempting to register Matching Engine %s -> %s",
                                        serverSocket.getInetAddress().getHostAddress(),
                                        e.getMessage()
                                );
                            }
                        });
                LOG.info("Registered RegisterMatchingEngineMessage handler for %s",
                        serverSocket.getInetAddress().getHostAddress()
                );

                tcpCommunicator.registerForDispatch(TopOfBookResponseMessage.class, this::updateTopOfBook);
                LOG.info("Registered TopOfBookResponseMessage handler for %s",
                        serverSocket.getInetAddress().getHostAddress()
                );

                tcpCommunicator.registerForDispatch(OrderConfirmationMessage.class, this::forwardOrderConfirmation);
                LOG.info("Registered OrderConfirmationMessage handler for %s",
                        serverSocket.getInetAddress().getHostAddress()
                );

                tcpCommunicator.registerForDispatch(CancelConfirmationMessage.class, this::forwardCancelConfirmation);
                LOG.info("Registered CancelConfirmationMessage handler for %s",
                        serverSocket.getInetAddress().getHostAddress()
                );

                // TODO: registerForDispatch any other messages we need to listen for
                new Thread(tcpCommunicator).start();
            }
        }).start();
        LOG.info("Initialized RegisterMatchingEngineMessage listener");
        LOG.info("Initialized TopOfBookResponseMessage listener");
        LOG.info("Initialized ForwardOrderConfirmationMessage listener");
        LOG.info("Initialized ForwardCancelConfirmationMessage listener");
    }

    private void registerMatchingEngine(Envelope<RegisterMatchingEngineMessage> envelope, TcpCommunicator tcpCommunicator) throws IOException {
        this.symbolToMatchingEngineMap.put(envelope.getMessage().getSymbol(), tcpCommunicator);
        LOG.info("Received RegisterMatchingEngineMessage from %s", envelope.getMessage().getSymbol());
        tcpCommunicator.send(new AckMessage(envelope.getMessage().getConversationId()));
        LOG.info("Sent AckMessage to %s", envelope.getMessage().getSymbol());
    }

    private void updateTopOfBook(Envelope<TopOfBookResponseMessage> envelope) {
        TopOfBookResponseMessage msg = envelope.getMessage();
        symbolToTopOfBookMap.put(
                msg.getSymbol(),
                new TopOfBookEntry(
                        msg.getSymbol(),
                        msg.getAsks(),
                        msg.getBids()
                )
        );
        LOG.info("Received TopOfBookResponseMessage from %s", msg.getSymbol());
    }

    private void initUdpDispatcherListeners() {
        udpCommunicator.registerForDispatch(RegisterPlayerMessage.class, this::registerPlayer);
        LOG.info("Initialized RegisterPlayerMessage listener");

        udpCommunicator.registerForDispatch(SubmitOrderMessage.class, this::forwardOrder);
        LOG.info("Initialized SubmitOrderMessage listener");

        udpCommunicator.registerForDispatch(CancelOrderMessage.class, this::forwardCancel);
        LOG.info("Initialized CancelOrderMessage listener");

        // TODO: registerForDispatch any other messages we need to listen for
        new Thread(udpCommunicator).start();
    }

    private void registerPlayer(Envelope<RegisterPlayerMessage> envelope) {
        PlayerDetailEntry newPlayerDetailEntry = new PlayerDetailEntry(
                envelope.getMessage().getPlayerName(),
                envelope.getSourceInetSocketAddress()
        );

        this.idToPlayerDetailMap.put(newPlayerDetailEntry.getId(), newPlayerDetailEntry);
        LOG.info("Received RegisterPlayerMessage from %s - player name %s",
                envelope.getSourceInetSocketAddress().getAddress().getHostAddress(),
                newPlayerDetailEntry.getName()
        );

        try {
            this.udpCommunicator.send(
                    new PlayerRegisteredMessage(
                            envelope.getMessage().getConversationId(),
                            newPlayerDetailEntry.getId(),
                            newPlayerDetailEntry.getCash()
                    ),
                    newPlayerDetailEntry.getSocketAddress().getAddress(),
                    newPlayerDetailEntry.getSocketAddress().getPort()
            );
            LOG.info("Sent PlayerRegisteredMessage to %s and assigned playerId %d",
                    envelope.getSourceInetSocketAddress().getAddress().getHostAddress(),
                    newPlayerDetailEntry.getId()
            );
        } catch (IOException e) {
            LOG.error("Failure in Gateway::registerPlayer -> %s", e.getMessage());
        }
    }

    private void forwardOrder(Envelope<SubmitOrderMessage> envelope) {
        ForwardOrderMessage forwardOrderMessage = new ForwardOrderMessage(envelope.getMessage());
        try {
            TcpCommunicator matchingEngineComm = symbolToMatchingEngineMap.get(forwardOrderMessage.getSymbol());
            LOG.info("Player %d submitted a %s order for %d shares of %s at %d per share", forwardOrderMessage.getPlayerId(),
                    forwardOrderMessage.getOrderType().name(),
                    forwardOrderMessage.getQuantity(),
                    forwardOrderMessage.getSymbol(),
                    forwardOrderMessage.getPrice());

            matchingEngineComm.sendReliably(forwardOrderMessage, OrderConfirmationMessage.class);
        } catch (IOException | NullPointerException e) {
            String player = idToPlayerDetailMap.get(forwardOrderMessage.getPlayerId()).getName();
            String symbol = forwardOrderMessage.getSymbol();
            String buy_sell = forwardOrderMessage.getOrderType().name();
            short qty = forwardOrderMessage.getQuantity();
            LOG.error("Failed to send ForwardOrderMessage. Order was placed by %s to %s %d shares of %s -> %s",
                    player,
                    buy_sell,
                    qty,
                    symbol,
                    e.getMessage()
            );
        }
    }

    private void forwardOrderConfirmation(Envelope<OrderConfirmationMessage> envelope) {
        ForwardOrderConfirmationMessage forwardOrderConfirmationMessage = new ForwardOrderConfirmationMessage(envelope.getMessage());
        String symbol = envelope.getMessage().getSymbol();
        PlayerDetailEntry player = idToPlayerDetailMap.get(envelope.getMessage().getPlayerId());

        // Forward message
        InetAddress playerAddress = player.getSocketAddress().getAddress();
        int playerPort = player.getSocketAddress().getPort();
        LOG.info("Forwarding Order Confirmation to Player %d", player.getId());
        try {
            udpCommunicator.send(forwardOrderConfirmationMessage, playerAddress, playerPort);
        } catch (IOException e) {
            LOG.error("Failed to send ForwardOrderConfirmationMessage. OrderConfirmationMessage came from %s bound for %s",
                    symbol,
                    player.getName()
            );
        }

        // Update scoreboard fields
        if (envelope.getMessage().getOrderType() == SubmitOrderMessage.OrderType.BUY) {
            player.setCash(player.getCash() - (envelope.getMessage().getExecutedQty() * envelope.getMessage().getPrice()));
            player.incrementTotalInvestments(envelope.getMessage().getExecutedQty() * envelope.getMessage().getPrice());
            player.addShares(symbol, envelope.getMessage().getExecutedQty());
        } else {
            player.incrementCashFromSales(envelope.getMessage().getExecutedQty() * envelope.getMessage().getPrice());
            player.setCash(player.getCash() + (envelope.getMessage().getExecutedQty() * envelope.getMessage().getPrice()));
            player.subtractShares(symbol, envelope.getMessage().getExecutedQty());
        }

    }

    private void forwardCancel(Envelope<CancelOrderMessage> envelope) {
        ForwardCancelMessage forwardCancelMessage = new ForwardCancelMessage(envelope.getMessage());
        TcpCommunicator matchingEngineComm = symbolToMatchingEngineMap.get(forwardCancelMessage.getSymbol());

        try {
            matchingEngineComm.sendReliably(forwardCancelMessage, CancelConfirmationMessage.class);
        } catch (IOException e) {
            String player = idToPlayerDetailMap.get(forwardCancelMessage.getPlayerId()).getName();
            String symbol = forwardCancelMessage.getSymbol();
            short orderID = forwardCancelMessage.getOrderId();
            LOG.error("Failed to send ForwardCancelMessage. Cancel requested by %s for order %d at %s -> %s",
                    player,
                    orderID,
                    symbol,
                    e.getMessage()
            );

        }
    }

    private void forwardCancelConfirmation(Envelope<CancelConfirmationMessage> envelope) {
        ForwardCancelConfirmationMessage forwardCancelConfirmationMessage = new ForwardCancelConfirmationMessage(envelope.getMessage());
        PlayerDetailEntry player = idToPlayerDetailMap.get(envelope.getMessage().getPlayerId());
        InetAddress playerAddress = player.getSocketAddress().getAddress();
        int playerPort = player.getSocketAddress().getPort();

        try {
            udpCommunicator.send(forwardCancelConfirmationMessage, playerAddress, playerPort);
        } catch (IOException e) {
            String symbol = forwardCancelConfirmationMessage.getSymbol();
            LOG.error("Failed to send ForwardCancelConfirmationMessage. CancelConfirmationMessage came from %s bound for %s",
                    symbol,
                    player.getName()
            );
        }
    }
}