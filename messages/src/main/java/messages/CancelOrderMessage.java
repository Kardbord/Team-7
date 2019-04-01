package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class CancelOrderMessage extends Message {

    private short playerId;
    private short orderId;
    private String symbol;

    public CancelOrderMessage(short playerId, short orderId, String symbol) {
        super(MessageType.CANCEL_ORDER);
        this.playerId = playerId;
        this.orderId = orderId;
        this.symbol = symbol;
    }

    CancelOrderMessage(UUID uuid, short playerId, short orderId, String symbol) {
        super(MessageType.CANCEL_ORDER, uuid);
        this.playerId = playerId;
        this.orderId = orderId;
        this.symbol = symbol;
    }

    public static CancelOrderMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.CANCEL_ORDER) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        short playerId = decoder.decodeShort();

        short orderId = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new CancelOrderMessage(uuid, playerId, orderId, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
                .encodeShort(playerId)
                .encodeShort(orderId)
                .encodeString(symbol)
                .toByteArray();
    }

    public short getPlayerId() {
        return playerId;
    }

    public short getOrderId() { return orderId; }

    public String getSymbol() { return symbol; }

    @Override
    public String toString() {
        return "CancelOrderMessage{" +
                "playerId=" + playerId +
                ", orderId=" + orderId +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CancelOrderMessage that = (CancelOrderMessage) o;
        return playerId == that.playerId &&
                orderId == that.orderId &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, orderId, symbol);
    }
}
