package messages;

import java.io.IOException;
import java.util.Objects;

public class OrderConfirmationMessage extends Message {

    private short playerId;
    private short orderId;
    private short executedQty;
    private short restingQty;
    private int price;
    private String symbol;

    public OrderConfirmationMessage(short playerId, short orderId,
                                    short executedQty, short restingQty, int price, String symbol) {
        super(MessageType.ORDER_CONFIRMATION);
        this.playerId=playerId;
        this.orderId = orderId;
        this.executedQty = executedQty;
        this.restingQty = restingQty;
        this.price = price;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "OrderConfirmationMessage{" +
                "PlayerId=" + playerId +
                ", orderId=" + orderId +
                ", executedQty=" + executedQty +
                ", restingQty=" + restingQty +
                ", price=" + price +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderConfirmationMessage that = (OrderConfirmationMessage) o;
        return playerId == that.playerId &&
                orderId == that.orderId &&
                executedQty == that.executedQty &&
                restingQty == that.restingQty &&
                price == that.price &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, orderId, executedQty, restingQty, price, symbol);
    }

    public static OrderConfirmationMessage decode(byte[] messageBytes) {
        Message.Decoder decoder = new Message.Decoder(messageBytes);

        if (decoder.decodeMessageType() != Message.MessageType.ORDER_CONFIRMATION) {
            throw new IllegalArgumentException();
        }

        short playerId = decoder.decodeShort();
        short orderId = decoder.decodeShort();
        short executedQty = decoder.decodeShort();
        short restingQty = decoder.decodeShort();
        int price = decoder.decodeInt();
        String symbol = decoder.decodeString();

        return new OrderConfirmationMessage(playerId, orderId, executedQty,restingQty, price, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Message.Encoder()
                .encodeMessageType(messageType)
                .encodeShort(playerId)
                .encodeShort(orderId)
                .encodeShort(executedQty)
                .encodeShort(restingQty)
                .encodeInt(price)
                .encodeString(symbol)
                .toByteArray();
    }

    public short getPlayerId() {
        return playerId;
    }

    public short getOrderId() {
        return orderId;
    }

    public short getExecutedQty() {
        return executedQty;
    }

    public short getRestingQty() {
        return restingQty;
    }

    public int getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }
}
