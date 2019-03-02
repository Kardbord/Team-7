package gateway;

class TopOfBookEntry {
    private String symbol;

    private short bidPrice;
    private short bidQuantity;

    private short askPrice;
    private short askQuantity;

    TopOfBookEntry(String symbol, short bidPrice, short bidQuantity, short askPrice, short askQuantity) {
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

    short getBidPrice() {
        return bidPrice;
    }

    void setBidPrice(short bidPrice) {
        this.bidPrice = bidPrice;
    }

    short getBidQuantity() {
        return bidQuantity;
    }

    void setBidQuantity(short bidQuantity) {
        this.bidQuantity = bidQuantity;
    }

    short getAskPrice() {
        return askPrice;
    }

    void setAskPrice(short askPrice) {
        this.askPrice = askPrice;
    }

    short getAskQuantity() {
        return askQuantity;
    }

    void setAskQuantity(short askQuantity) {
        this.askQuantity = askQuantity;
    }
}
