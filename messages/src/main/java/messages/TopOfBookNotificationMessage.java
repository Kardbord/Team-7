package messages;

import java.io.IOException;

public class TopOfBookNotificationMessage extends Message {

    private String symbol;
    private short bidPrice;
    private short bidQuantity;
    private short askPrice;
    private short askQuantity;

    public TopOfBookNotificationMessage(String symbol, short bidPrice, short bidQuantity, short askPrice, short askQuantity) {
        super(MessageType.TOP_OF_BOOK_NOTIFICATION);
        this.symbol = symbol;
        this.bidPrice = bidPrice;
        this.bidQuantity = bidQuantity;
        this.askPrice = askPrice;
        this.askQuantity = askQuantity;
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeString(symbol)
                .encodeShort(bidPrice)
                .encodeShort(bidQuantity)
                .encodeShort(askPrice)
                .encodeShort(askQuantity)
                .toByteArray();
    }

    public static TopOfBookNotificationMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.TOP_OF_BOOK_NOTIFICATION) {
            throw new IllegalArgumentException();
        }

        String symbol = decoder.decodeString();
        short bidPrice = decoder.decodeShort();
        short bidQuantity = decoder.decodeShort();
        short askPrice = decoder.decodeShort();
        short askQuantity = decoder.decodeShort();

        return new TopOfBookNotificationMessage(symbol, bidPrice, bidQuantity, askPrice, askQuantity);
    }

    public String getSymbol() {
        return symbol;
    }

    public short getBidPrice() {
        return bidPrice;
    }

    public short getBidQuantity() {
        return bidQuantity;
    }

    public short getAskPrice() {
        return askPrice;
    }

    public short getAskQuantity() {
        return askQuantity;
    }
}
