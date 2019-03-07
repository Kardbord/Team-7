package matchingengine;

import messages.SubmitOrderMessage;

public class Order {
    private short orderID;
    private short playerId;
    private SubmitOrderMessage.OrderType orderType;
    private short quantity;
    private short price;
    private String symbol;
    public Order(short playerId, SubmitOrderMessage.OrderType orderType, short quantity, short price,
                      String symbol,short orderID){
        this.playerId=playerId;
        this.orderType=orderType;
        this.quantity=quantity;
        this.price=price;
        this.symbol=symbol;
        this.orderID=orderID;
    }
    public short getOrderID() {
        return orderID;
    }

    public void setOrderID(short orderID) {
        this.orderID = orderID;
    }

    public short getPlayerId() {
        return playerId;
    }

    public void setPlayerId(short playerId) {
        this.playerId = playerId;
    }

    public SubmitOrderMessage.OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(SubmitOrderMessage.OrderType orderType) {
        this.orderType = orderType;
    }

    public short getQuantity() {
        return quantity;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public short getPrice() {
        return price;
    }

    public void setPrice(short price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

}
