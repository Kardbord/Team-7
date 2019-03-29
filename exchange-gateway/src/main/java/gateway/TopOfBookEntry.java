package gateway;

public class TopOfBookEntry {
    private String symbol;

    private int bidPrice;
    private short bidQuantity;

    private int askPrice;
    private short askQuantity;

    public TopOfBookEntry(String symbol, int bidPrice, short bidQuantity, int askPrice, short askQuantity) {
        this.symbol = symbol;
        this.bidPrice = bidPrice;
        this.bidQuantity = bidQuantity;
        this.askPrice = askPrice;
        this.askQuantity = askQuantity;
    }

    String getSymbol() {
        return symbol;
    }

    void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getBidPrice() {
        return bidPrice;
    }

    void setBidPrice(short bidPrice) {
        this.bidPrice = bidPrice;
    }

    public short getBidQuantity() {
        return bidQuantity;
    }

    void setBidQuantity(short bidQuantity) {
        this.bidQuantity = bidQuantity;
    }

    public int getAskPrice() {
        return askPrice;
    }

    void setAskPrice(short askPrice) {
        this.askPrice = askPrice;
    }

    public short getAskQuantity() {
        return askQuantity;
    }

    void setAskQuantity(short askQuantity) {
        this.askQuantity = askQuantity;
    }

    @Override
    public String toString() {
        return String.format( "Bid: $%d x %d\t Ask: $%d x %d", this.bidPrice, this.bidQuantity, this.askPrice, this.askQuantity);
    }
}