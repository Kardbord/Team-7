package messages;

import java.io.IOException;

public class CancelConfirmationMessage extends Message{

    private short playerId;
    private short orderId;
    private short cancelledQty;
    private String symbol;

    public CancelConfirmationMessage(short playerId, short orderId, short cancelledQty, String symbol) {
        super(MessageType.CANCEL_CONF);
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

        short playerId = decoder.decodeShort();
        short orderId = decoder.decodeShort();
        short cancelledQty = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new CancelConfirmationMessage(playerId, orderId, cancelledQty, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
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
}
