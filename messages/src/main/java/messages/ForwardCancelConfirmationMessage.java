package messages;

import java.io.IOException;

public class ForwardCancelConfirmationMessage extends Message {

    private short orderId;
    private short cancelledQty;
    private String symbol;

    public ForwardCancelConfirmationMessage(short orderId, short cancelledQty, String symbol) {
        super(MessageType.FWD_CANCEL_CONF);
        this.orderId = orderId;
        this.cancelledQty = cancelledQty;
        this.symbol = symbol;
    }

    public static ForwardCancelConfirmationMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.FWD_CANCEL_CONF) {
            throw new IllegalArgumentException();
        }

        short orderId = decoder.decodeShort();
        short cancelledQty = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new ForwardCancelConfirmationMessage(orderId, cancelledQty, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
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
}
