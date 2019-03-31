package player;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import gateway.TopOfBookEntry;
import communicators.UdpCommunicator;
import communicators.Envelope;
import dispatcher.EnvelopeDispatcher;
import messages.*;
import messages.SubmitOrderMessage.OrderType;
import portfolio.PortfolioEntry;
import utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class Player {

    private static Logger log = LogManager.getFormatterLogger(Player.class.getName());

    private UdpCommunicator udpCommunicator;
    private InetSocketAddress serverSocketAddress;
    private ObservableList<String> symbolList;
    private ObservableMap<String, TopOfBookEntry> topOfBookMap;
    private ObservableMap<String, PortfolioEntry> portfolio;

    private String name;
    private int playerId;
    private int cash;

    public Player(String name) throws IOException {
        this.name = name;
        this.serverSocketAddress = new InetSocketAddress(Utils.getLocalIp(),Utils.PORT);
        this.udpCommunicator = new UdpCommunicator(DatagramChannel.open(), new InetSocketAddress(0));
        registerPlayer();
        initMessageListeners();

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
        this.topOfBookMap = FXCollections.observableHashMap();
        this.portfolio = FXCollections.observableHashMap();
        this.symbolList = FXCollections.observableArrayList();
        registerPlayer();
        initMessageListeners();
    }

    /**
     * Set up the listener to receive a Player Registered message
     */
    private void initMessageListeners() {
        EnvelopeDispatcher<byte[]> envelopeDispatcher = new EnvelopeDispatcher<>(udpCommunicator, Message::decode);
        envelopeDispatcher.registerForDispatch(PlayerRegisteredMessage.class, this::updateRegisteredPlayer);
        envelopeDispatcher.registerForDispatch(TopOfBookNotificationMessage.class, this::updateTopOfBook);
        envelopeDispatcher.registerForDispatch(ForwardOrderConfirmationMessage.class, this::updatePortfolio);
        new Thread(envelopeDispatcher).start();
        log.info("Initialized PlayerRegisteredListener");
        log.info("Initialized TopOfBookNotificationListener");

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

    public void submitOrder(short playerId, OrderType orderType, short quantity, int price, String symbol) {
        log.info("Player %d submitted a %s order for %d shares of %s at %d per share", playerId, orderType.name(), quantity, symbol, price);
        try {
            this.udpCommunicator.send(new SubmitOrderMessage(playerId, orderType, quantity, price, symbol).encode(),
                    this.serverSocketAddress.getAddress(),
                    this.serverSocketAddress.getPort());
        } catch (IOException e) {
            log.error("Failed to send order: %s", e.getMessage());
        }
    }

    /**
     * Method that is called when a PlayerRegistered Message is received.
     * @param env received envelope
     */
    private void updateRegisteredPlayer(Envelope<PlayerRegisteredMessage> env) {
        log.info("Received Registered Player Message");
        this.playerId = env.getMessage().getPlayerId();
        this.cash = env.getMessage().getInitialCash();
        log.info("PlayerID: %d", this.playerId);
        log.info("Cash: %d", this.cash);
    }

    private void updatePortfolio(Envelope<ForwardOrderConfirmationMessage> env) {
        log.info("Order Confirmation received");
        ForwardOrderConfirmationMessage msg = env.getMessage();
        if (!portfolio.containsKey(msg.getSymbol())) {
            portfolio.put(msg.getSymbol(), new PortfolioEntry(msg.getSymbol(), msg.getExecutedQty(), msg.getPrice()));
        } else {
            PortfolioEntry entry = portfolio.get(msg.getSymbol());
            entry.updatePositions(msg.getExecutedQty());
            entry.updateEquity(msg.getPrice());
        }
    }

    private void updateTopOfBook(Envelope<TopOfBookNotificationMessage> env) {
//        log.info("Received TopOfBookMessage");
        TopOfBookNotificationMessage msg = env.getMessage();
        String symbol = msg.getSymbol();
        topOfBookMap.put(msg.getSymbol(), new TopOfBookEntry(
                symbol,
                msg.getBidPrice(),
                msg.getBidQuantity(),
                msg.getAskPrice(),
                msg.getAskQuantity()
        ));
        if (!symbolList.contains(msg.getSymbol())) {
            symbolList.add(msg.getSymbol());
        }
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


    public ObservableMap<String, TopOfBookEntry> getTopOfBookMap() {
        return topOfBookMap;
    }

    public ObservableMap<String, PortfolioEntry> getPortfolioMap() {
        return portfolio;
    }

    public ObservableList<String> getSymbolList() {
        return symbolList;
    }
}