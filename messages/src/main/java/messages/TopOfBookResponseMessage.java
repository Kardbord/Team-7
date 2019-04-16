package messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TopOfBookResponseMessage extends Message {

    private String symbol;
    private List<PriceQuantityPair> asks;
    private List<PriceQuantityPair> bids;

    public TopOfBookResponseMessage(UUID uuid, String symbol, List<PriceQuantityPair> asks, List<PriceQuantityPair> bids) {
        super(MessageType.TOP_OF_BOOK_RESPONSE, uuid);
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

        for(PriceQuantityPair pair : asks) {
            encoder.encodeInt(pair.getPrice());
            encoder.encodeShort(pair.getQty());
        }

        encoder.encodeInt(bids.size());

        for(PriceQuantityPair pair : bids) {
            encoder.encodeInt(pair.getPrice());
            encoder.encodeShort(pair.getQty());
        }

        return encoder.toByteArray();
    }

    public static TopOfBookResponseMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.TOP_OF_BOOK_RESPONSE) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        String symbol = decoder.decodeString();

        List<PriceQuantityPair> asks = new ArrayList<>();
        int asksSize = decoder.decodeInt();

        for(int i = 0; i<asksSize; i++){
            asks.add(new PriceQuantityPair(decoder.decodeInt(), decoder.decodeShort()));
        }

        List<PriceQuantityPair> bids = new ArrayList<>();
        int bidsSize = decoder.decodeInt();

        for(int i = 0; i<bidsSize; i++){
            bids.add(new PriceQuantityPair(decoder.decodeInt(), decoder.decodeShort()));
        }


        return new TopOfBookResponseMessage(uuid, symbol, asks, bids);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TopOfBookResponseMessage that = (TopOfBookResponseMessage) o;
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
        return "TopOfBookResponseMessage{" +
                "symbol='" + symbol + '\'' +
                ", asks=" + asks +
                ", bids=" + bids +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public List<PriceQuantityPair> getAsks() {
        return asks;
    }

    public List<PriceQuantityPair> getBids() {
        return bids;
    }

    public static class PriceQuantityPair {
        public int price;
        public short qty;

        public PriceQuantityPair(int price, short qty) {
            this.price = price;
            this.qty = qty;
        }

        public int getPrice() {
            return price;
        }

        public short getQty() {
            return qty;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PriceQuantityPair that = (PriceQuantityPair) o;
            return price == that.price &&
                    qty == that.qty;
        }

        @Override
        public int hashCode() {
            return Objects.hash(price, qty);
        }

        @Override
        public String toString() {
            return "PriceQuantityPair{" +
                    "price=" + price +
                    ", qty=" + qty +
                    '}';
        }
    }
}
