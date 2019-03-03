package player;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import communicators.UdpCommunicator;
import communicators.Envelope;
import dispatcher.EnvelopeDispatcher;
import messages.*;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class Player {

    private static Logger log = LogManager.getFormatterLogger(Player.class.getName());

    private UdpCommunicator udpCommunicator;
    private InetSocketAddress serverSocketAddress;

    private String name;
    private int playerId;
    private int cash;

    public Player(String name) throws IOException {
        this.name = name;
        this.serverSocketAddress = new InetSocketAddress(Utils.getLocalIp(), Utils.PORT);
        this.udpCommunicator = new UdpCommunicator(DatagramChannel.open());
        registerPlayer();
        initPlayerRegisteredListener();

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
}
