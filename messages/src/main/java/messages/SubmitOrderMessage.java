package messages;

import java.io.IOException;

public class SubmitOrderMessage extends Message {

    private short playerId;
    private OrderType orderType;
    private short quantity;
    private short price;
    private String symbol;

    public SubmitOrderMessage(short playerId, OrderType orderType, short quantity, short price, String symbol) {
        super(MessageType.SUBMIT_ORDER);
        this.playerId = playerId;
        this.orderType = orderType;
        this.quantity = quantity;
        this.price = price;
        this.symbol = symbol;
    }

    public static SubmitOrderMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.SUBMIT_ORDER) {
            throw new IllegalArgumentException();
        }

        short playerId = decoder.decodeShort();

        byte orderTypeByte = decoder.decodeByte();
        if (!OrderType.contains(orderTypeByte)) {
            throw new IllegalArgumentException();
        }

        short quantity = decoder.decodeShort();
        short price = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new SubmitOrderMessage(playerId, OrderType.getOrderTypeFromByte(orderTypeByte), quantity, price, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeShort(playerId)
                .encodeByte(orderType.toByte())
                .encodeShort(quantity)
                .encodeShort(price)
                .encodeString(symbol)
                .toByteArray();
    }

    public short getPlayerId() {
        return playerId;
    }

    public short getQuantity() {
        return quantity;
    }

    public short getPrice() {
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
