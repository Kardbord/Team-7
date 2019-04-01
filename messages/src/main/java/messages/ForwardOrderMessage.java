package messages;

import messages.SubmitOrderMessage.OrderType;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class ForwardOrderMessage extends Message {

    private short playerId;
    private OrderType orderType;
    private short quantity;
    private int price;
    private String symbol;

    @Override
    public String toString() {
        return "ForwardOrderMessage{" +
                "playerId=" + playerId +
                ", orderType=" + orderType +
                ", quantity=" + quantity +
                ", price=" + price +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ForwardOrderMessage that = (ForwardOrderMessage) o;
        return playerId == that.playerId &&
                quantity == that.quantity &&
                price == that.price &&
                orderType == that.orderType &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, orderType, quantity, price, symbol);
    }

    public ForwardOrderMessage(UUID uuid, short playerId, OrderType orderType, short quantity, int price, String symbol) {
        super(MessageType.FORWARD_ORDER, uuid);
        this.playerId = playerId;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.symbol = symbol;
    }

    public ForwardOrderMessage(SubmitOrderMessage orderMessage) {
        super(MessageType.FORWARD_ORDER);
        this.playerId = orderMessage.getPlayerId();
        this.orderType = orderMessage.getOrderType();
        this.quantity = orderMessage.getQuantity();
        this.price = orderMessage.getPrice();
        this.symbol = orderMessage.getSymbol();
    }

    public static ForwardOrderMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.FORWARD_ORDER) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        short playerId = decoder.decodeShort();

        byte orderTypeByte = decoder.decodeByte();
        if (!OrderType.contains(orderTypeByte)) {
            throw new IllegalArgumentException();
        }

        short quantity = decoder.decodeShort();
        int price = decoder.decodeInt();
        String symbol = decoder.decodeString();

        return new ForwardOrderMessage(uuid, playerId, OrderType.getOrderTypeFromByte(orderTypeByte), quantity, price, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
                .encodeShort(playerId)
                .encodeByte(orderType.toByte())
                .encodeShort(quantity)
                .encodeInt(price)
                .encodeString(symbol)
                .toByteArray();
    }

    public short getPlayerId() {
        return playerId;
    }

    public short getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderType getOrderType() {
        return orderType;
    }
}
