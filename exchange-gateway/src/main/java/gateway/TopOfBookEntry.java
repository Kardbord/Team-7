package gateway;

import messages.TopOfBookResponseMessage;

import java.util.List;

public class TopOfBookEntry {
    private String symbol;

    private List<TopOfBookResponseMessage.PriceQuantityPair> asks;
    private List<TopOfBookResponseMessage.PriceQuantityPair> bids;

    public TopOfBookEntry(String symbol, List<TopOfBookResponseMessage.PriceQuantityPair> asks, List<TopOfBookResponseMessage.PriceQuantityPair> bids) {
        this.symbol = symbol;
        this.asks = asks;
        this.bids = bids;
    }

    String getSymbol() {
        return symbol;
    }

    void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<TopOfBookResponseMessage.PriceQuantityPair> getAsks() {
        return asks;
    }

    public void setAsks(List<TopOfBookResponseMessage.PriceQuantityPair> asks) {
        this.asks = asks;
    }

    public List<TopOfBookResponseMessage.PriceQuantityPair> getBids() {
        return bids;
    }

    public void setBids(List<TopOfBookResponseMessage.PriceQuantityPair> bids) {
        this.bids = bids;
    }

    @Override
    public String toString() {
        return "TopOfBookEntry{" +
                "symbol='" + symbol + '\'' +
                ", asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}