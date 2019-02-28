package messages;

import java.io.IOException;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "TopOfBookNotificationMessage{" +
                "symbol='" + symbol + '\'' +
                ", bidPrice=" + bidPrice +
                ", bidQuantity=" + bidQuantity +
                ", askPrice=" + askPrice +
                ", askQuantity=" + askQuantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TopOfBookNotificationMessage that = (TopOfBookNotificationMessage) o;
        return bidPrice == that.bidPrice &&
                bidQuantity == that.bidQuantity &&
                askPrice == that.askPrice &&
                askQuantity == that.askQuantity &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol, bidPrice, bidQuantity, askPrice, askQuantity);
    }
}
