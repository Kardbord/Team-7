package player;

import communicators.TcpCommunicator;
import gateway.TopOfBookEntry;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import communicators.UdpCommunicator;
import communicators.Envelope;
import dispatcher.EnvelopeDispatcher;
import messages.*;
import utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class Player {

    private static Logger log = LogManager.getFormatterLogger(Player.class.getName());

    private UdpCommunicator udpCommunicator;
    private TcpCommunicator tcpCommunicator;
    private InetSocketAddress serverSocketAddress;
    private ConcurrentHashMap<String, TopOfBookEntry> topOfBookMap;

    private String name;
    private int playerId;
    private int cash;

    public Player(String name) throws IOException {
        this.name = name;
        this.serverSocketAddress = new InetSocketAddress(Utils.getLocalIp(),Utils.PORT);
        this.udpCommunicator = new UdpCommunicator(DatagramChannel.open(), new InetSocketAddress(0));
        registerPlayer();
        initPlayerRegisteredListener();

    }

    /**
     * Overloaded constructor for specifying a server IP
     * @param name
     * @param serverAddress
     * @throws IOException
     */
    public Player(String name, String serverAddress) throws IOException {
        this.name = name;
        this.serverSocketAddress = new InetSocketAddress(serverAddress,Utils.PORT);
        this.udpCommunicator = new UdpCommunicator(DatagramChannel.open(), new InetSocketAddress(0));
        this.topOfBookMap = new ConcurrentHashMap<>();
        registerPlayer();
        initPlayerRegisteredListener();
        initTopOfBookListener();
    }

    /**
     * Send the player register Message to the gateway
     */
    private void registerPlayer() {
        try {
            log.info("Sending Player Register Message");
            this.udpCommunicator.send(
                    new RegisterPlayerMessage(this.name).encode(),
                    this.serverSocketAddress.getAddress(),
                    this.serverSocketAddress.getPort()

            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Set up the listener to receive a Player Registered message
     */
    private void initPlayerRegisteredListener() {
        log.info("Starting PlayerRegisteredListener");
        EnvelopeDispatcher<byte[]> envelopeDispatcher = new EnvelopeDispatcher<>(udpCommunicator, Message::decode);
        envelopeDispatcher.registerForDispatch(PlayerRegisteredMessage.class, this::updateRegisteredPlayer);
        new Thread(envelopeDispatcher).start();

    }

    /**
     * Method that is called when a PlayerRegistered Message is received.
     * @param env received envelope
     */
    private void updateRegisteredPlayer(Envelope<PlayerRegisteredMessage> env) {
        log.info("Received Registered Player Message");
        this.playerId = env.getMessage().getPlayerId();
        this.cash = env.getMessage().getInitialCash();
        log.info("PlayerID: " + this.playerId);
        log.info("Cash: " + this.cash);
    }

    private void initTopOfBookListener() {
        log.info("Starting TopOfBookListener");
        EnvelopeDispatcher<byte[]> envelopeDispatcher = new EnvelopeDispatcher<>(udpCommunicator, Message::decode);
        envelopeDispatcher.registerForDispatch(TopOfBookNotificationMessage.class, this::updateTopOfBook);
        new Thread(envelopeDispatcher).start();
    }

    private void updateTopOfBook(Envelope<TopOfBookNotificationMessage> env) {
        log.info("Received TopOfBookMessage");
        TopOfBookNotificationMessage msg = env.getMessage();
        topOfBookMap.put(
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

    public void submitOrder(short playerId, short orderType, short quantity, int price, String symbol) {
        log.info("Player %d submitted a %d order for %d shares of %s at %d per share", playerId, orderType, quantity, symbol, price);

        cash -= price;
    }

    public String getName() {
        return name;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getCash() {
        return cash;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public InetSocketAddress getServerSocketAddress() {
        return serverSocketAddress;
    }

    public Enumeration<String> getTopOfBookSymbols() {
        return topOfBookMap.keys();
    }

    public TopOfBookEntry getTopOfBookEntry(String symbol) {
        if (topOfBookMap.containsKey(symbol)) {
            return topOfBookMap.get(symbol);
        } else {
            throw new NullPointerException();
        }
    }
}
