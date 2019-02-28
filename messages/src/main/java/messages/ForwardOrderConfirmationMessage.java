package messages;

import java.io.IOException;
import java.util.Objects;

public class ForwardOrderConfirmationMessage extends Message {

    private short orderId;
    private SubmitOrderMessage.OrderType orderType;
    private String symbol;
    private short executedQty;
    private short restingQty;
    private short price;
    private Status status;

    public ForwardOrderConfirmationMessage(short orderId, SubmitOrderMessage.OrderType orderType, String symbol,
                                           short executedQty, short restingQty, short price, Status status) {
        super(MessageType.FWD_ORDER_CONF);
        this.orderId = orderId;
        this.orderType = orderType;
        this.symbol = symbol;
        this.executedQty = executedQty;
        this.restingQty = restingQty;
        this.price = price;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ForwardOrderConfirmationMessage{" +
                "orderId=" + orderId +
                ", orderType=" + orderType +
                ", symbol='" + symbol + '\'' +
                ", executedQty=" + executedQty +
                ", restingQty=" + restingQty +
                ", price=" + price +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ForwardOrderConfirmationMessage that = (ForwardOrderConfirmationMessage) o;
        return orderId == that.orderId &&
                executedQty == that.executedQty &&
                restingQty == that.restingQty &&
                price == that.price &&
                orderType == that.orderType &&
                Objects.equals(symbol, that.symbol) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId, orderType, symbol, executedQty, restingQty, price, status);
    }

    public static ForwardOrderConfirmationMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.FWD_ORDER_CONF) {
            throw new IllegalArgumentException();
        }

        short orderId = decoder.decodeShort();
        byte orderTypeByte = decoder.decodeByte();

        if (!SubmitOrderMessage.OrderType.contains(orderTypeByte)) {
            throw new IllegalArgumentException();
        }

        String symbol = decoder.decodeString();
        short executedQty = decoder.decodeShort();
        short restingQty = decoder.decodeShort();
        short price = decoder.decodeShort();
        byte statusByte = decoder.decodeByte();

        if (!Status.contains(statusByte)) {
            throw new IllegalArgumentException();
        }

        return new ForwardOrderConfirmationMessage(orderId, SubmitOrderMessage.OrderType.getOrderTypeFromByte(orderTypeByte),
                symbol, executedQty, restingQty, price, Status.getStatusFromByte(statusByte));
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeShort(orderId)
                .encodeByte(orderType.toByte())
                .encodeString(symbol)
                .encodeShort(executedQty)
                .encodeShort(restingQty)
                .encodeShort(price)
                .encodeByte(status.toByte())
                .toByteArray();
    }

    public short getOrderId() {
        return orderId;
    }

    public SubmitOrderMessage.OrderType getOrderType() {
        return orderType;
    }

    public String getSymbol() {
        return symbol;
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

    public Status getStatus() {
        return status;
    }

    public enum Status {
        SUCCESS,
        ERROR,
        ;

        public byte toByte() {
            return (byte) this.ordinal();
        }

        public static Status getStatusFromByte(byte statusByte) {
            return Status.values()[statusByte];
        }

        public static boolean contains(byte statusByte) {
            return (statusByte >= 0 && statusByte < Status.values().length);
        }
    }
}
