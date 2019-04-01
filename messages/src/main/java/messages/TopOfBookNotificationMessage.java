package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class TopOfBookNotificationMessage extends Message {

    private String symbol;
    private int bidPrice;
    private short bidQuantity;
    private int askPrice;
    private short askQuantity;

    public TopOfBookNotificationMessage(String symbol, int bidPrice, short bidQuantity, int askPrice, short askQuantity) {
        super(MessageType.TOP_OF_BOOK_NOTIFICATION);
        this.symbol = symbol;
        this.bidPrice = bidPrice;
        this.bidQuantity = bidQuantity;
        this.askPrice = askPrice;
        this.askQuantity = askQuantity;
    }

    TopOfBookNotificationMessage(UUID uuid, String symbol, int bidPrice, short bidQuantity, int askPrice, short askQuantity) {
        super(MessageType.TOP_OF_BOOK_NOTIFICATION, uuid);
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
                .encodeUUID(conversationId)
                .encodeString(symbol)
                .encodeInt(bidPrice)
                .encodeShort(bidQuantity)
                .encodeInt(askPrice)
                .encodeShort(askQuantity)
                .toByteArray();
    }

    public static TopOfBookNotificationMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.TOP_OF_BOOK_NOTIFICATION) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        String symbol = decoder.decodeString();
        int bidPrice = decoder.decodeInt();
        short bidQuantity = decoder.decodeShort();
        int askPrice = decoder.decodeInt();
        short askQuantity = decoder.decodeShort();

        return new TopOfBookNotificationMessage(uuid, symbol, bidPrice, bidQuantity, askPrice, askQuantity);
    }

    public String getSymbol() {
        return symbol;
    }

    public int getBidPrice() {
        return bidPrice;
    }

    public short getBidQuantity() {
        return bidQuantity;
    }

    public int getAskPrice() {
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
