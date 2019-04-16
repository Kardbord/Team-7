package messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TopOfBookNotificationMessage extends Message {

    private String symbol;
    private List<TopOfBookResponseMessage.PriceQuantityPair> asks;
    private List<TopOfBookResponseMessage.PriceQuantityPair> bids;

    public TopOfBookNotificationMessage(String symbol, List<TopOfBookResponseMessage.PriceQuantityPair> asks, List<TopOfBookResponseMessage.PriceQuantityPair> bids) {
        super(MessageType.TOP_OF_BOOK_NOTIFICATION);
        this.symbol = symbol;
        this.asks = asks;
        this.bids = bids;
    }

    TopOfBookNotificationMessage(UUID uuid, String symbol, List<TopOfBookResponseMessage.PriceQuantityPair> asks, List<TopOfBookResponseMessage.PriceQuantityPair> bids) {
        super(MessageType.TOP_OF_BOOK_NOTIFICATION, uuid);
        this.symbol = symbol;
        this.asks = asks;
        this.bids = bids;
    }

    @Override
    public byte[] encode() throws IOException {
        Encoder encoder = new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
                .encodeString(symbol)
                .encodeInt(asks.size());

        for(TopOfBookResponseMessage.PriceQuantityPair pair : asks) {
            encoder.encodeInt(pair.getPrice());
            encoder.encodeShort(pair.getQty());
        }

        encoder.encodeInt(bids.size());

        for(TopOfBookResponseMessage.PriceQuantityPair pair : bids) {
            encoder.encodeInt(pair.getPrice());
            encoder.encodeShort(pair.getQty());
        }

        return encoder.toByteArray();
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<TopOfBookResponseMessage.PriceQuantityPair> getAsks() {
        return asks;
    }

    public void setAsks(List<TopOfBookResponseMessage.PriceQuantityPair> asks) {
        this.asks = asks;
    }

    public List<TopOfBookResponseMessage.PriceQuantityPair> getBids() {
        return bids;
    }

    public void setBids(List<TopOfBookResponseMessage.PriceQuantityPair> bids) {
        this.bids = bids;
    }

    public static TopOfBookNotificationMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.TOP_OF_BOOK_NOTIFICATION) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        String symbol = decoder.decodeString();

        List<TopOfBookResponseMessage.PriceQuantityPair> asks = new ArrayList<>();
        int asksSize = decoder.decodeInt();

        for(int i = 0; i<asksSize; i++){
            asks.add(new TopOfBookResponseMessage.PriceQuantityPair(decoder.decodeInt(), decoder.decodeShort()));
        }

        List<TopOfBookResponseMessage.PriceQuantityPair> bids = new ArrayList<>();
        int bidsSize = decoder.decodeInt();

        for(int i = 0; i<bidsSize; i++){
            bids.add(new TopOfBookResponseMessage.PriceQuantityPair(decoder.decodeInt(), decoder.decodeShort()));
        }


        return new TopOfBookNotificationMessage(uuid, symbol, asks, bids);
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TopOfBookNotificationMessage that = (TopOfBookNotificationMessage) o;
        return Objects.equals(symbol, that.symbol) &&
                Objects.equals(asks, that.asks) &&
                Objects.equals(bids, that.bids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol, asks, bids);
    }

    @Override
    public String toString() {
        return "TopOfBookNotificationMessage{" +
                "symbol='" + symbol + '\'' +
                ", asks=" + asks +
                ", bids=" + bids +
                '}';
    }
}
