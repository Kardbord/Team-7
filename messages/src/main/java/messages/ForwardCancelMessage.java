package messages;

import java.io.IOException;
import java.util.Objects;

public class ForwardCancelMessage extends Message {

    private short playerId;
    private short orderId;
    private String symbol;

    public ForwardCancelMessage(short playerId, short orderId, String symbol) {
        super(MessageType.FWD_CANCEL);
        this.playerId = playerId;
        this.orderId = orderId;
        this.symbol = symbol;
    }

    public static ForwardCancelMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.FWD_CANCEL) {
            throw new IllegalArgumentException();
        }

        short playerId = decoder.decodeShort();

        short orderId = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new ForwardCancelMessage(playerId, orderId, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
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
        return "ForwardCancelMessage{" +
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
        ForwardCancelMessage that = (ForwardCancelMessage) o;
        return playerId == that.playerId &&
                orderId == that.orderId &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, orderId, symbol);
    }
}
