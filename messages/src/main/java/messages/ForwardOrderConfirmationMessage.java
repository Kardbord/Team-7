package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class ForwardOrderConfirmationMessage extends Message {

    private short orderId;
    private String symbol;
    private short executedQty;
    private short restingQty;
    private int price;

    public ForwardOrderConfirmationMessage(UUID uuid, short orderId, String symbol, short executedQty, short restingQty, int price) {
        super(MessageType.FWD_ORDER_CONF, uuid);
        this.orderId = orderId;
        this.symbol = symbol;
        this.executedQty = executedQty;
        this.restingQty = restingQty;
        this.price = price;
    }

    public ForwardOrderConfirmationMessage(OrderConfirmationMessage orderConfirmationMessage) {
        super(MessageType.FWD_ORDER_CONF, orderConfirmationMessage.conversationId);
        this.orderId = orderConfirmationMessage.getOrderId();
        this.symbol = orderConfirmationMessage.getSymbol();
        this.executedQty = orderConfirmationMessage.getExecutedQty();
        this.restingQty = orderConfirmationMessage.getRestingQty();
        this.price = orderConfirmationMessage.getPrice();
    }

    @Override
    public String toString() {
        return "ForwardOrderConfirmationMessage{" +
                "orderId=" + orderId +
                ", symbol='" + symbol + '\'' +
                ", executedQty=" + executedQty +
                ", restingQty=" + restingQty +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ForwardOrderConfirmationMessage that = (ForwardOrderConfirmationMessage) o;
        return orderId == that.orderId &&
                executedQty == that.executedQty &&
                restingQty == that.restingQty &&
                price == that.price &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId, symbol, executedQty, restingQty, price);
    }

    public static ForwardOrderConfirmationMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.FWD_ORDER_CONF) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        short orderId = decoder.decodeShort();

        String symbol = decoder.decodeString();
        short executedQty = decoder.decodeShort();
        short restingQty = decoder.decodeShort();
        int price = decoder.decodeInt();

        return new ForwardOrderConfirmationMessage(uuid, orderId, symbol, executedQty, restingQty, price);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
                .encodeShort(orderId)
                .encodeString(symbol)
                .encodeShort(executedQty)
                .encodeShort(restingQty)
                .encodeInt(price)
                .toByteArray();
    }

    public short getOrderId() {
        return orderId;
    }

    public String getSymbol() {
        return symbol;
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

}
