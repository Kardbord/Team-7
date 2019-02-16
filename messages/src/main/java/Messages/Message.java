package Messages;//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public abstract class Message {

    public enum MessageType {
        UNKNOWN                  ((short)  0),
        REGISTER_PLAYER          ((short)  1),
        PLAYER_REGISTERED        ((short)  2),
        SUBMIT_ORDER             ((short)  3),
        FORWARD_ORDER            ((short)  4),
        ORDER_CONFIRMATION       ((short)  5),
        FWD_ORDER_CONF           ((short)  6),
        CANCEL_ORDER             ((short)  7),
        FWD_CANCEL               ((short)  8),
        CANCEL_CONF              ((short)  9),
        FWD_CANCEL_CONF          ((short) 10),
        REGISTER_MATCHING_ENGINE ((short) 11),
        ACK                      ((short) 12),
        TOP_OF_BOOK_NOTIFICATION ((short) 13),
        TOP_OF_BOOK_REQUEST      ((short) 14),
        TOP_OF_BOOK_RESPONSE     ((short) 15),
        ;

        public final short VALUE;

        MessageType(short val) {
            this.VALUE = val;
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
            if (msgType == MessageType.UNKNOWN.VALUE) {
                return null;
            }
            else if (msgType == MessageType.REGISTER_PLAYER.VALUE) {
                msg = new RegisterPlayerMessage();
            }
            else if (msgType == MessageType.PLAYER_REGISTERED.VALUE) {
//                msg = new PlayerRegisteredMessage();
            }
            else if (msgType == MessageType.SUBMIT_ORDER.VALUE) {
//                msg = new SubmitOrderMessage();
            }
            else if (msgType == MessageType.FORWARD_ORDER.VALUE) {
//                msg = new ForwardOrderMessage();
            }
            else if (msgType == MessageType.ORDER_CONFIRMATION.VALUE) {
//                msg = new OrderConfirmation();
            }
            else if (msgType == MessageType.FWD_ORDER_CONF.VALUE) {
//                msg = new FwdOrderConfirmation();
            }
            else if (msgType == MessageType.CANCEL_ORDER.VALUE) {
//                msg = new CancelOrderMessage();
            }
            else if (msgType == MessageType.FWD_CANCEL.VALUE) {
//                msg = new FwdCancelMessage();
            }
            else if (msgType == MessageType.CANCEL_CONF.VALUE) {
//                msg = new CancelConfirmationMessage();
            }
            else if (msgType == MessageType.FWD_CANCEL_CONF.VALUE) {
//                msg = new FwdCancelConfirmationMessage();
            }
            else if (msgType == MessageType.REGISTER_MATCHING_ENGINE.VALUE) {
//                msg = new RegisterMEMessage();
            }
            else if (msgType == MessageType.ACK.VALUE) {
//                msg = new AckMessage();
            }
            else if (msgType == MessageType.TOP_OF_BOOK_NOTIFICATION.VALUE) {
//                msg = new TopOfBookNotificationMessage();
            }
            else if (msgType == MessageType.TOP_OF_BOOK_REQUEST.VALUE) {
//                msg = new TopOfBookRequestMessage();
            }
            else if (msgType == MessageType.TOP_OF_BOOK_RESPONSE.VALUE) {
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
