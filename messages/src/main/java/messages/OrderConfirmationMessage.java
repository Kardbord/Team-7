package messages;

import java.io.IOException;
import java.util.Objects;

public class OrderConfirmationMessage extends Message {

    private short buyerPlayerId;
    private short sellerPlayerId;
    private short orderId;
    private short executedQty;
    private short restingQty;
    private short price;
    private String symbol;

    public OrderConfirmationMessage(short buyerPlayerId, short sellerPlayerId, short orderId,
                                    short executedQty, short restingQty, short price, String symbol) {
        super(MessageType.ORDER_CONFIRMATION);
        this.buyerPlayerId = buyerPlayerId;
        this.sellerPlayerId = sellerPlayerId;
        this.orderId = orderId;
        this.executedQty = executedQty;
        this.restingQty = restingQty;
        this.price = price;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "OrderConfirmationMessage{" +
                "buyerPlayerId=" + buyerPlayerId +
                ", sellerPlayerId=" + sellerPlayerId +
                ", orderId=" + orderId +
                ", executedQty=" + executedQty +
                ", restingQty=" + restingQty +
                ", price=" + price +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderConfirmationMessage that = (OrderConfirmationMessage) o;
        return buyerPlayerId == that.buyerPlayerId &&
                sellerPlayerId == that.sellerPlayerId &&
                orderId == that.orderId &&
                executedQty == that.executedQty &&
                restingQty == that.restingQty &&
                price == that.price &&
                Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), buyerPlayerId, sellerPlayerId, orderId, executedQty, restingQty, price, symbol);
    }

    public static OrderConfirmationMessage decode(byte[] messageBytes) {
        Message.Decoder decoder = new Message.Decoder(messageBytes);

        if (decoder.decodeMessageType() != Message.MessageType.ORDER_CONFIRMATION) {
            throw new IllegalArgumentException();
        }

        short buyerPlayerId = decoder.decodeShort();
        short sellerPlayerId = decoder.decodeShort();
        short orderId = decoder.decodeShort();
        short executedQty = decoder.decodeShort();
        short restingQty = decoder.decodeShort();
        short price = decoder.decodeShort();
        String symbol = decoder.decodeString();

        return new OrderConfirmationMessage(buyerPlayerId, sellerPlayerId, orderId, executedQty,restingQty, price, symbol);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Message.Encoder()
                .encodeMessageType(messageType)
                .encodeShort(buyerPlayerId)
                .encodeShort(sellerPlayerId)
                .encodeShort(orderId)
                .encodeShort(executedQty)
                .encodeShort(restingQty)
                .encodeShort(price)
                .encodeString(symbol)
                .toByteArray();
    }

    public short getBuyerPlayerId() {
        return buyerPlayerId;
    }

    public short getSellerPlayerId() {
        return sellerPlayerId;
    }

    public short getOrderId() {
        return orderId;
    }

    public short getExecutedQty() {
        return executedQty;
    }

    public short getRestingQty() {
        return restingQty;
    }

    public short getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }
}
