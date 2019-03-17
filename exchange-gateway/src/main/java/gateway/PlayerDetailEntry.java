package gateway;

import java.net.InetAddress;
import java.net.InetSocketAddress;

class PlayerDetailEntry {
    public static final int STARTING_CASH = 50000;

    private static short nextPlayerId = 0;

    private int cash;

    private String name;

    private short id;

    private InetSocketAddress socketAddress;

    PlayerDetailEntry(String playerName, InetSocketAddress socketAddress) {
        this.id = getNextPlayerIdAndIncrement();
        this.cash = STARTING_CASH;
        this.socketAddress = socketAddress;
        this.name = playerName;
    }

    private static short getNextPlayerIdAndIncrement() {
        return PlayerDetailEntry.nextPlayerId++;
    }

    static short getNextPlayerId() {
        return PlayerDetailEntry.nextPlayerId;
    }

    InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    void setSocketAddress(InetAddress address, int port) {
        this.socketAddress = new InetSocketAddress(address, port);
    }

    void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    int getCash() {
        return this.cash;
    }

    void setCash(int cash) {
        this.cash = cash;
    }

    short getId() {
        return id;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }
}
