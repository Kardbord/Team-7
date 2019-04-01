package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class SubmitOrderMessage extends Message {

    private short playerId;
    private OrderType orderType;
    private short quantity;
    private int price;
    private String symbol;

    public SubmitOrderMessage(short playerId, OrderType orderType, short quantity, int price, String symbol) {
        super(MessageType.SUBMIT_ORDER);
        this.playerId = playerId;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.symbol = symbol;
    }

    SubmitOrderMessage(UUID uuid, short playerId, OrderType orderType, short quantity, int price, String symbol) {
        super(MessageType.SUBMIT_ORDER, uuid);
        this.playerId = playerId;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "SubmitOrderMessage{" +
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
        SubmitOrderMessage that = (SubmitOrderMessage) o;
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

    public static SubmitOrderMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.SUBMIT_ORDER) {
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

        return new SubmitOrderMessage(uuid, playerId, OrderType.getOrderTypeFromByte(orderTypeByte), quantity, price, symbol);
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

    public enum OrderType {
        SELL,
        BUY,
        ;


        public byte toByte() {
            return (byte) this.ordinal();
        }

        public static OrderType getOrderTypeFromByte(byte orderTypeByte) {
            return OrderType.values()[orderTypeByte];
        }

        public static boolean contains(byte orderTypeByte) {
            return (orderTypeByte >= 0 && orderTypeByte < OrderType.values().length);
        }
    }
}
