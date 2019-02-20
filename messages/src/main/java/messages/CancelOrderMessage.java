package messages;

import java.io.IOException;

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

    public static CancelOrderMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.CANCEL_ORDER) {
            throw new IllegalArgumentException();
        }

        short playerId = decoder.decodeShort();

        short orderId = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new CancelOrderMessage(playerId, orderId, symbol);
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
}
