package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class CancelConfirmationMessage extends Message{

    private short playerId;
    private short orderId;
    private short cancelledQty;
    private String symbol;

    public CancelConfirmationMessage(UUID uuid, short playerId, short orderId, short cancelledQty, String symbol) {
        super(MessageType.CANCEL_CONF, uuid);
        this.playerId = playerId;
        this.orderId = orderId;
        this.cancelledQty = cancelledQty;
        this.symbol = symbol;
    }

    public static CancelConfirmationMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.CANCEL_CONF) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        short playerId = decoder.decodeShort();
        short orderId = decoder.decodeShort();
        short cancelledQty = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new CancelConfirmationMessage(uuid, playerId, orderId, cancelledQty, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
                .encodeShort(playerId)
                .encodeShort(orderId)
                .encodeShort(cancelledQty)
                .encodeString(symbol)
                .toByteArray();
    }

    public short getPlayerId() {
        return playerId;
    }

    public short getOrderId() {
        return orderId;
    }

    public short getCancelledQty() {
        return cancelledQty;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "CancelConfirmationMessage{" +
                "playerId=" + playerId +
                ", orderId=" + orderId +
                ", cancelledQty=" + cancelledQty +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CancelConfirmationMessage that = (CancelConfirmationMessage) o;
        return playerId == that.playerId &&
                orderId == that.orderId &&
                cancelledQty == that.cancelledQty &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, orderId, cancelledQty, symbol);
    }
}
