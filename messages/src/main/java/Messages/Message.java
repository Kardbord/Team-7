package Messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public abstract class Message {

    protected MessageType messageType;

    protected Message(MessageType msgType) {
        messageType = msgType;
    }

    public static Message decode(byte[] messageBytes) {
        if(messageBytes.length < 2) {
            throw new IllegalArgumentException();
        }

        Decoder decoder = new Decoder(messageBytes);

        MessageType messageType = decoder.decodeMessageType();

        switch(messageType){
//            case REGISTER_PLAYER:
//                return RegisterPlayerMessage.decode(messageBytes);
            case PLAYER_REGISTERED:
                return PlayerRegisteredMessage.decode(messageBytes);
//            case SUBMIT_ORDER:
//                return SubmitOrderMessage.decode(messageBytes);
//            case FORWARD_ORDER:
//                return ForwardOrderMessage.decode(messageBytes);
//            case ORDER_CONFIRMATION:
//                return OrderConfirmationMessage.decode(messageBytes);
//            case FWD_ORDER_CONF:
//                return ForwardOrderConfirmationMessage.decode(messageBytes);
//            case CANCEL_ORDER:
//                return CancelOrderMessage.decode(messageBytes);
//            case FWD_CANCEL:
//                return ForwardCancelMessage.decode(messageBytes);
//            case CANCEL_CONF:
//                return CancelConfirmationMessage.decode(messageBytes);
//            case FWD_CANCEL_CONF:
//                return ForwardCancelConfirmationMessage.decode(messageBytes);
//            case REGISTER_MATCHING_ENGINE:
//                return RegisterMatchingEngineMessage.decode(messageBytes);
//            case ACK:
//                return AckMessage.decode(messageBytes);
//            case TOP_OF_BOOK_NOTIFICATION:
//                return TopOfBookNotificationMessage.decode(messageBytes);
//            case TOP_OF_BOOK_REQUEST:
//                return TopOfBookRequestMessage.decode(messageBytes);
//            case TOP_OF_BOOK_RESPONSE:
//                return TopOfBookResponseMessage.decode(messageBytes);
            default:
                throw new IllegalArgumentException();
        }
    }

    public abstract byte[] encode() throws IOException;

    public MessageType getMessageType() {
        return messageType;
    }

    protected static class Encoder {

        private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        public Encoder encodeShort(short value) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putShort(value);
            byteArrayOutputStream.write(buffer.array());
            return this;
        }

        public Encoder encodeInt(int value) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putInt(value);
            byteArrayOutputStream.write(buffer.array());
            return this;
        }

        public Encoder encodeByte(byte value) throws IOException {
            byteArrayOutputStream.write(value);
            return this;
        }

        public Encoder encodeLong(long value) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putLong(value);
            byteArrayOutputStream.write(buffer.array());
            return this;
        }

        public Encoder encodeString(String value) throws IOException {
            if (value==null)
                value="";

            byte[] textBytes = value.getBytes(Charset.forName("UTF-16BE"));
            encodeShort((short) (textBytes.length));
            byteArrayOutputStream.write(textBytes);
            return this;
        }

        public Encoder encodeMessageType(MessageType messageType) throws IOException {
            return encodeShort(messageType.toShort());
        }

        public byte[] toByteArray() {
            return byteArrayOutputStream.toByteArray();
        }
    }

    protected static class Decoder {

        private ByteBuffer byteBuffer;

        public Decoder(byte[] messageBytes) {
            byteBuffer = ByteBuffer.wrap(messageBytes);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
        }

        public short decodeShort() {
            return byteBuffer.getShort();
        }

        public int decodeInt() {
            return byteBuffer.getInt();
        }

        public long decodeLong() {
            return byteBuffer.getLong();
        }

        public byte decodeByte() {
            return byteBuffer.get();
        }

        public String decodeString() {
            short textLength = decodeShort();

            byte[] textBytes = new byte[textLength];
            byteBuffer.get(textBytes, 0, textLength);
            return new String(textBytes, Charset.forName("UTF-16BE"));
        }

        public MessageType decodeMessageType() {
            short messageTypeShort = decodeShort();
            return MessageType.getTypeFromShort(messageTypeShort);
        }
    }

    public enum MessageType {
        UNKNOWN,
        REGISTER_PLAYER,
        PLAYER_REGISTERED,
        SUBMIT_ORDER,
        FORWARD_ORDER,
        ORDER_CONFIRMATION,
        FWD_ORDER_CONF,
        CANCEL_ORDER,
        FWD_CANCEL,
        CANCEL_CONF,
        FWD_CANCEL_CONF,
        REGISTER_MATCHING_ENGINE,
        ACK,
        TOP_OF_BOOK_NOTIFICATION,
        TOP_OF_BOOK_REQUEST,
        TOP_OF_BOOK_RESPONSE,
        ;

        public short toShort() {
            return (short) this.ordinal();
        }

        public static MessageType getTypeFromShort(short ordinal) {
            return MessageType.values()[ordinal];
        }
    }
}
