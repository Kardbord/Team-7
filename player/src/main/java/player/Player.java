package player;

import communicators.Envelope;
import communicators.UdpCommunicator;
import gateway.TopOfBookEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import messages.*;
import messages.SubmitOrderMessage.OrderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import portfolio.PortfolioEntry;
import utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;

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

    public Player(String name, UdpCommunicator udpCommunicator) throws IOException {
        this.name = name;
        this.serverSocketAddress = new InetSocketAddress(Utils.getLocalIp(), Utils.PORT);
        this.udpCommunicator = udpCommunicator;
        registerPlayer();
        initMessageListeners();

    }

    /**
     * Overloaded constructor for specifying a server IP
     *
     * @param name
     * @param serverAddress
     * @throws IOException
     */
    public Player(String name, UdpCommunicator udpCommunicator, String serverAddress) throws IOException {
        this.name = name;
        this.serverSocketAddress = new InetSocketAddress(serverAddress, Utils.PORT);
        this.udpCommunicator = udpCommunicator;
        this.topOfBookMap = FXCollections.observableHashMap();
        this.portfolio = FXCollections.observableHashMap();
        this.symbolList = FXCollections.observableArrayList();
        initMessageListeners();
        registerPlayer();
    }

    /**
     * Set up the listener to receive a Player Registered message
     */
    private void initMessageListeners() {
        udpCommunicator.registerForDispatch(PlayerRegisteredMessage.class, this::updateRegisteredPlayer);
        udpCommunicator.registerForDispatch(TopOfBookNotificationMessage.class, this::updateTopOfBook);
        udpCommunicator.registerForDispatch(ForwardOrderConfirmationMessage.class, this::updatePortfolio);
        new Thread(udpCommunicator).start();
        log.info("Initialized PlayerRegisteredListener");
        log.info("Initialized TopOfBookNotificationListener");

    }

    /**
     * Send the player register Message to the gateway
     */
    private void registerPlayer() {
        try {
            log.info("Sending Player Register Message");
            this.udpCommunicator.sendReliably(
                    new RegisterPlayerMessage(this.name),
                    this.serverSocketAddress.getAddress(),
                    this.serverSocketAddress.getPort(),
                    PlayerRegisteredMessage.class
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void submitOrder(short playerId, OrderType orderType, short quantity, int price, String symbol) {
        log.info("Player %d submitted a %s order for %d shares of %s at %d per share", playerId, orderType.name(), quantity, symbol, price);
        try {
            this.udpCommunicator.sendReliably(
                    new SubmitOrderMessage(playerId, orderType, quantity, price, symbol),
                    this.serverSocketAddress.getAddress(),
                    this.serverSocketAddress.getPort(),
                    ForwardOrderConfirmationMessage.class
            );
        } catch (IOException e) {
            log.error("Failed to send order: %s", e.getMessage());
        }
    }

    /**
     * Method that is called when a PlayerRegistered Message is received.
     *
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
        if (msg.getExecutedQty() > 0) {
            if (!portfolio.containsKey(msg.getSymbol())) {
                portfolio.put(msg.getSymbol(), new PortfolioEntry(msg.getSymbol(), msg.getExecutedQty(), msg.getPrice()));
            } else {
                PortfolioEntry entry = portfolio.get(msg.getSymbol());
                entry.updatePositions(msg.getExecutedQty());
                entry.updateEquity(msg.getPrice());
                portfolio.put(msg.getSymbol(), entry);
            }
        } // else add to the resting orders list
    }

    private void updateTopOfBook(Envelope<TopOfBookNotificationMessage> env) {
        log.info("Received TopOfBookMessage: " + env.getMessage());
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