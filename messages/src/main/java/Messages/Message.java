package Messages;//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public abstract class Message {

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

        public short toShort() { return (short) this.ordinal(); }

        public MessageType getTypeFromShort(short ordinal) {
            return MessageType.values()[ordinal];
        }

    }

//    private static Logger log = LogManager.getFormatterLogger(Messages.Message.class.getName());

    protected MessageType messageType;

    protected ByteArrayOutputStream outputStream;

    public Message(MessageType msgType) {
        messageType = msgType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    protected abstract void setStateFrom(ByteBuffer buff);

    public abstract ByteArrayOutputStream encode() throws IOException;

    public static Message decode(byte[] bytes) {
//        log.debug("Getting MessageType.");
        Message msg = null;
        ByteBuffer buff = ByteBuffer.wrap(bytes);
        buff.order(ByteOrder.BIG_ENDIAN);

        if (bytes.length >= 2) {
            short msgType = buff.getShort();
//            log.debug("Messages.Message ID: " + msgType);
            if (msgType == MessageType.UNKNOWN.toShort()) {
                return null;
            }
            else if (msgType == MessageType.REGISTER_PLAYER.toShort()) {
                msg = new RegisterPlayerMessage();
            }
            else if (msgType == MessageType.PLAYER_REGISTERED.toShort()) {
//                msg = new PlayerRegisteredMessage();
            }
            else if (msgType == MessageType.SUBMIT_ORDER.toShort()) {
//                msg = new SubmitOrderMessage();
            }
            else if (msgType == MessageType.FORWARD_ORDER.toShort()) {
//                msg = new ForwardOrderMessage();
            }
            else if (msgType == MessageType.ORDER_CONFIRMATION.toShort()) {
//                msg = new OrderConfirmation();
            }
            else if (msgType == MessageType.FWD_ORDER_CONF.toShort()) {
//                msg = new FwdOrderConfirmation();
            }
            else if (msgType == MessageType.CANCEL_ORDER.toShort()) {
//                msg = new CancelOrderMessage();
            }
            else if (msgType == MessageType.FWD_CANCEL.toShort()) {
//                msg = new FwdCancelMessage();
            }
            else if (msgType == MessageType.CANCEL_CONF.toShort()) {
//                msg = new CancelConfirmationMessage();
            }
            else if (msgType == MessageType.FWD_CANCEL_CONF.toShort()) {
//                msg = new FwdCancelConfirmationMessage();
            }
            else if (msgType == MessageType.REGISTER_MATCHING_ENGINE.toShort()) {
//                msg = new RegisterMEMessage();
            }
            else if (msgType == MessageType.ACK.toShort()) {
//                msg = new AckMessage();
            }
            else if (msgType == MessageType.TOP_OF_BOOK_NOTIFICATION.toShort()) {
//                msg = new TopOfBookNotificationMessage();
            }
            else if (msgType == MessageType.TOP_OF_BOOK_REQUEST.toShort()) {
//                msg = new TopOfBookRequestMessage();
            }
            else if (msgType == MessageType.TOP_OF_BOOK_RESPONSE.toShort()) {
//                msg = new TopOfBookResponseMessage();
            }
            else {
                return null;
            }
            try {
//                log.debug("Decoding a " + msg.messageType.name());
                msg.setStateFrom(buff);

            } catch (Exception e) {
//                log.error("[Messages.Message Error]: Could not setStateFrom " + msg.messageType.name());
//                log.error(e.getMessage());

            }
        }
        return msg;
    }


    protected void encodeShort(short value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort(value);
        outputStream.write(buffer.array());
    }

    protected void encodeInt(int value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(value);
        outputStream.write(buffer.array());
    }
    protected void encodeByte(byte value) throws IOException {
        outputStream.write(value);
    }

    protected void encodeLong(long value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(value);
        outputStream.write(buffer.array());
    }

    protected void encodeString(String value) throws IOException {
        if (value==null)
            value="";

        byte[] textBytes = value.getBytes(Charset.forName("UTF-16BE"));
        encodeShort((short) (textBytes.length));
        outputStream.write(textBytes);
    }

    protected static short decodeShort(ByteBuffer bytes) {
        return bytes.getShort();
    }

    protected static int decodeInt(ByteBuffer bytes) {
        return bytes.getInt();
    }

    protected static long decodeLong(ByteBuffer bytes) {
        return bytes.getLong();
    }

    protected static byte decodeByte(ByteBuffer bytes) {
        return bytes.get();
    }


    protected static String decodeString(ByteBuffer bytes) {
        short textLength = decodeShort(bytes);
        if (bytes.remaining() < textLength) {
            //log.warn("Byte array is too short for specific text");
            return null;
        }

        //log.debug("text length=%d", textLength);
        byte[] textBytes = new byte[textLength];
        bytes.get(textBytes, 0, textLength);
        return new String(textBytes, Charset.forName("UTF-16BE"));
    }

}
