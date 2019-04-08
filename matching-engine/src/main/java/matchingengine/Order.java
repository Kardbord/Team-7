package matchingengine;

import messages.SubmitOrderMessage;

import java.util.Objects;

public class Order implements Comparable<Order> {
    private short orderID;
    private short playerId;
    private SubmitOrderMessage.OrderType orderType;
    private short quantity;
    private int price;
    private String symbol;
    public Order(short playerId, SubmitOrderMessage.OrderType orderType, short quantity, int price,
                      String symbol,short orderID){
        this.playerId=playerId;
        this.orderType=orderType;
        this.quantity=quantity;
        this.price=price;
        this.symbol=symbol;
        this.orderID=orderID;
    }
    @Override
    public int compareTo(Order other){
        // primary sort by price
        // secondary sort by orderId

        if(price == other.price){
            return orderID - other.orderID;
        }

        return price - other.price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderID == order.orderID &&
                playerId == order.playerId &&
                quantity == order.quantity &&
                price == order.price &&
                orderType == order.orderType &&
                Objects.equals(symbol, order.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderID, playerId, orderType, quantity, price, symbol);
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

    public int getPrice() {
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

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", playerId=" + playerId +
                ", orderType=" + orderType +
                ", quantity=" + quantity +
                ", price=" + price +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    public short executeQty(short qtyToExecuteAgainst) {
        short executedQty = (short) Math.min(quantity, qtyToExecuteAgainst);
        quantity -= executedQty;
        return executedQty;
    }
}
