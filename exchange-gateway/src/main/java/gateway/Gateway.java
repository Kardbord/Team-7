package gateway;

import communicators.Envelope;
import communicators.TcpCommunicator;
import communicators.UdpCommunicator;
import dispatcher.EnvelopeDispatcher;
import messages.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ConcurrentHashMap;

public class Gateway {

    private Logger log = LogManager.getFormatterLogger(Gateway.class.getName());

    /**
     * Rate is in milliseconds
     */
    private final static int TOP_OF_BOOK_REFRESH_RATE = 1000;

    /**
     * Rate is in milliseconds
     */
    private final static int TOP_OF_BOOK_BROADCAST_RATE = 1500;

    private final static int PORT = 2000;

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

    public Gateway() throws IOException {
        this.symbolToMatchingEngineMap = new ConcurrentHashMap<>();
        this.idToPlayerDetailMap = new ConcurrentHashMap<>();
        this.symbolToTopOfBookMap = new ConcurrentHashMap<>();
        this.udpCommunicator = new UdpCommunicator(DatagramChannel.open(), new InetSocketAddress("127.0.0.1", PORT));
        initRegisterMatchingEngineListener();
        initRegisterPlayerListener();
        initTopOfBookRefresh();
        initTopOfBookBroadcast();
    }

    private void initTopOfBookRefresh() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(TOP_OF_BOOK_REFRESH_RATE);
                    symbolToMatchingEngineMap.forEach((symbol, tcpCommunicator) -> {
                        try {
                            tcpCommunicator.send(new TopOfBookRequestMessage().encode());
                            log.info("Sent TopOfBookRefresh request to {}", symbol);
                        } catch (IOException e) {
                            log.error("Failure in Gateway while requesting Top Of Book from {} -> {}", symbol, e.getMessage());
                        }
                    });
                } catch (InterruptedException e) {
                    log.error("Top of Book refresh interrupted -> {}", e.getMessage());
                }
            }
        }).start();
    }

    private void initTopOfBookBroadcast() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(TOP_OF_BOOK_BROADCAST_RATE);
                    idToPlayerDetailMap.forEach((__, playerDetailEntry) -> {
                        symbolToTopOfBookMap.forEach((symbol, topofBookEntry) -> {
                            TopOfBookNotificationMessage notfication = new TopOfBookNotificationMessage(
                                    symbol,
                                    topofBookEntry.getBidPrice(),
                                    topofBookEntry.getBidQuantity(),
                                    topofBookEntry.getAskPrice(),
                                    topofBookEntry.getAskQuantity()
                            );
                            try {
                                udpCommunicator.send(
                                        notfication.encode(),
                                        playerDetailEntry.getSocketAddress().getAddress(),
                                        playerDetailEntry.getSocketAddress().getPort()
                                );
                                log.info(
                                        "Sent TopOfBookNotificationMessage to player {}: {}",
                                        playerDetailEntry.getId(),
                                        playerDetailEntry.getName()
                                );
                            } catch (IOException e) {
                                log.error("Failure in Gateway while broadcasting Top Of Book to {} -> {}", symbol, e.getMessage());
                            }
                        });
                    });
                } catch (InterruptedException e) {
                    log.error("Top of Book broadcast interrupted -> {}", e.getMessage());
                }
            }
        }).start();
    }

    private void initRegisterMatchingEngineListener() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        new Thread(() -> {
            while (true) {
                TcpCommunicator tcpCommunicator;
                try {
                    tcpCommunicator = new TcpCommunicator(serverSocket.accept());
                } catch (IOException e) {
                    log.error("Failure in Gateway RegisterMatchingEngine listener while awaiting connection -> {}", e.getMessage());
                    continue;
                }
                EnvelopeDispatcher<byte[]> envelopeDispatcher = new EnvelopeDispatcher<>(tcpCommunicator, Message::decode);
                envelopeDispatcher.registerForDispatch(
                        RegisterMatchingEngineMessage.class,
                        (Envelope<RegisterMatchingEngineMessage> env) -> {
                            try {
                                registerMatchingEngine(env, tcpCommunicator);
                            } catch (IOException e) {
                                log.error("Failure while attempting to register Matching Engine -> {}", e.getMessage());
                            }
                        });
                log.info("Registered handler for RegisterMatchingEngineMessage");
                envelopeDispatcher.registerForDispatch(TopOfBookResponseMessage.class, this::updateTopOfBook);
                log.info("Registered handler for TopOfBookResponseMessage");
                // TODO: registerForDispatch any other messages we need to listen for

                new Thread(envelopeDispatcher).start();
            }
        }).start();
    }

    private void registerMatchingEngine(Envelope<RegisterMatchingEngineMessage> envelope, TcpCommunicator tcpCommunicator) throws IOException {
        this.symbolToMatchingEngineMap.put(envelope.getMessage().getSymbol(), tcpCommunicator);
        log.info("Received RegisterMatchingEngineMessage from {}", envelope.getMessage().getSymbol());
        tcpCommunicator.send(new AckMessage().encode());
        log.info("Sent AckMessage to {}", envelope.getMessage().getSymbol());
        tcpCommunicator.send(new TopOfBookRequestMessage().encode());
        log.info("Sent TopOfBookRequestMessage to {}", envelope.getMessage().getSymbol());
    }

    private void updateTopOfBook(Envelope<TopOfBookResponseMessage> envelope) {
        TopOfBookResponseMessage msg = envelope.getMessage();
        symbolToTopOfBookMap.put(
                msg.getSymbol(),
                new TopOfBookEntry(
                        msg.getSymbol(),
                        msg.getBidPrice(),
                        msg.getBidQuantity(),
                        msg.getAskPrice(),
                        msg.getAskQuantity()
                )
        );
        log.info("Received TopOfBookResponseMessage from {}", msg.getSymbol());
    }

    private void initRegisterPlayerListener() {
        EnvelopeDispatcher<byte[]> envelopeDispatcher = new EnvelopeDispatcher<>(udpCommunicator, Message::decode);
        envelopeDispatcher.registerForDispatch(RegisterPlayerMessage.class, this::registerPlayer);
        // TODO: registerForDispatch any other messages we need to listen for
        new Thread(envelopeDispatcher).start();
        log.info("Registered handler for RegisterPlayerMessage");
    }

    private void registerPlayer(Envelope<RegisterPlayerMessage> envelope) {
        PlayerDetailEntry newPlayerDetailEntry = new PlayerDetailEntry(
                envelope.getMessage().getPlayerName(),
                envelope.getSourceInetSocketAddress()
        );

        this.idToPlayerDetailMap.put(newPlayerDetailEntry.getId(), newPlayerDetailEntry);
        log.info("Received RegisterPlayerMessage from {}", newPlayerDetailEntry.getName());

        try {
            this.udpCommunicator.send(
                    new PlayerRegisteredMessage(
                            newPlayerDetailEntry.getId(),
                            newPlayerDetailEntry.getCash()
                    ).encode(),
                    newPlayerDetailEntry.getSocketAddress().getAddress(),
                    newPlayerDetailEntry.getSocketAddress().getPort()
            );
            log.info("Sent PlayerRegisteredMessage to {} -- assigned playerId {}", newPlayerDetailEntry.getName(), newPlayerDetailEntry.getId());
        } catch (IOException e) {
            log.error("Failure in Gateway::registerPlayer -> {}", e.getMessage());
        }
    }

}
