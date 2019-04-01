package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class ForwardCancelConfirmationMessage extends Message {

    private short orderId;
    private short cancelledQty;
    private String symbol;

    public ForwardCancelConfirmationMessage(UUID uuid, short orderId, short cancelledQty, String symbol) {
        super(MessageType.FWD_CANCEL_CONF, uuid);
        this.orderId = orderId;
        this.cancelledQty = cancelledQty;
        this.symbol = symbol;
    }

    public ForwardCancelConfirmationMessage(CancelConfirmationMessage cancelConfirmationMessage) {
        super(MessageType.FWD_CANCEL_CONF, cancelConfirmationMessage.conversationId);
        this.orderId = cancelConfirmationMessage.getOrderId();
        this.cancelledQty = cancelConfirmationMessage.getCancelledQty();
        this.symbol = cancelConfirmationMessage.getSymbol();
    }

    public static ForwardCancelConfirmationMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.FWD_CANCEL_CONF) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        short orderId = decoder.decodeShort();
        short cancelledQty = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new ForwardCancelConfirmationMessage(uuid, orderId, cancelledQty, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
                .encodeShort(orderId)
                .encodeShort(cancelledQty)
                .encodeString(symbol)
                .toByteArray();
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
        return "ForwardCancelConfirmationMessage{" +
                "orderId=" + orderId +
                ", cancelledQty=" + cancelledQty +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ForwardCancelConfirmationMessage that = (ForwardCancelConfirmationMessage) o;
        return orderId == that.orderId &&
                cancelledQty == that.cancelledQty &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId, cancelledQty, symbol);
    }
}
