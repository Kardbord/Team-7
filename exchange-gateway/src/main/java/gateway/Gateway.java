package gateway;

import communicators.Envelope;
import communicators.TcpCommunicator;
import communicators.UdpCommunicator;
import dispatcher.EnvelopeDispatcher;
import messages.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ConcurrentHashMap;

public class Gateway {

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
        this.udpCommunicator = new UdpCommunicator(DatagramChannel.open());
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
                    symbolToMatchingEngineMap.forEach((__, tcpCommunicator) -> {
                        try {
                            tcpCommunicator.send(new TopOfBookRequestMessage().encode());
                        } catch (IOException ignored) { /* continue */ }
                    });
                } catch (InterruptedException ignored) { /* continue */ }
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
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                } catch (InterruptedException ignored) { /* continue */ }
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
                } catch (IOException ignored) {
                    continue;
                }
                EnvelopeDispatcher<byte[]> envelopeDispatcher = new EnvelopeDispatcher<>(tcpCommunicator, Message::decode);
                envelopeDispatcher.registerForDispatch(
                        RegisterMatchingEngineMessage.class,
                        (Envelope<RegisterMatchingEngineMessage> env) -> {
                            try {
                                registerMatchingEngine(env, tcpCommunicator);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                envelopeDispatcher.registerForDispatch(TopOfBookResponseMessage.class, this::updateTopOfBook);
                // TODO: registerForDispatch any other messages we need to listen for

                new Thread(envelopeDispatcher).start();
            }
        }).start();
    }

    private void registerMatchingEngine(Envelope<RegisterMatchingEngineMessage> envelope, TcpCommunicator tcpCommunicator) throws IOException {
        this.symbolToMatchingEngineMap.put(envelope.getMessage().getSymbol(), tcpCommunicator);
        tcpCommunicator.send(new AckMessage().encode());
        tcpCommunicator.send(new TopOfBookRequestMessage().encode());
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
    }

    private void initRegisterPlayerListener() {
        EnvelopeDispatcher<byte[]> envelopeDispatcher = new EnvelopeDispatcher<>(udpCommunicator, Message::decode);
        envelopeDispatcher.registerForDispatch(RegisterPlayerMessage.class, this::registerPlayer);
        // TODO: registerForDispatch any other messages we need to listen for
        new Thread(envelopeDispatcher).start();
    }

    private void registerPlayer(Envelope<RegisterPlayerMessage> envelope) {
        PlayerDetailEntry newPlayerDetailEntry = new PlayerDetailEntry(
                envelope.getMessage().getPlayerName(),
                envelope.getSourceInetSocketAddress()
        );

        this.idToPlayerDetailMap.put(newPlayerDetailEntry.getId(), newPlayerDetailEntry);

        try {
            this.udpCommunicator.send(
                    new PlayerRegisteredMessage(
                            newPlayerDetailEntry.getId(),
                            newPlayerDetailEntry.getCash()
                    ).encode(),
                    newPlayerDetailEntry.getSocketAddress().getAddress(),
                    newPlayerDetailEntry.getSocketAddress().getPort()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}