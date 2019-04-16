package gateway;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

class PlayerDetailEntry {
    public static final int STARTING_CASH = 50000;

    private static short nextPlayerId = 0;

    private int cash;

    private int totalInvestments;

    private int cashFromSales;

    private String name;

    private short id;

    private InetSocketAddress socketAddress;

    private ConcurrentHashMap<String, Integer> symbolToSharesMap;

    PlayerDetailEntry(String playerName, InetSocketAddress socketAddress) {
        this.id = getNextPlayerIdAndIncrement();
        this.cash = STARTING_CASH;
        this.totalInvestments = 0;
        this.cashFromSales = 0;
        this.socketAddress = socketAddress;
        this.name = playerName;
        this.symbolToSharesMap = new ConcurrentHashMap<>();
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

    int getTotalInvestments() {
        return totalInvestments;
    }

    void incrementTotalInvestments(int increment) {
        if (increment < 0) {
            throw new IllegalArgumentException();
        }
        this.totalInvestments += increment;
    }

    int getCashFromSales() {
        return cashFromSales;
    }

    void incrementCashFromSales(int increment) {
        if (increment < 0) {
            throw new IllegalArgumentException();
        }
        this.cashFromSales += increment;
    }

    ConcurrentHashMap<String, Integer> getSymbolToSharesMap() {
        return symbolToSharesMap;
    }

    void addShares(String symbol, int increment) {
        if (increment < 0) {
            throw new IllegalArgumentException();
        }
        int previousShares = this.symbolToSharesMap.getOrDefault(symbol, 0);
        this.symbolToSharesMap.put(symbol, previousShares + increment);
    }

    void subtractShares(String symbol, int decrement) {
        if (decrement < 0) {
            throw new IllegalArgumentException();
        }
        int previousShares = this.symbolToSharesMap.getOrDefault(symbol, 0);
        this.symbolToSharesMap.put(symbol, previousShares - decrement);
    }
}
