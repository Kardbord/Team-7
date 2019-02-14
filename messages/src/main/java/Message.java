//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public abstract class Message {

    public enum MessageTypes{
        UNKNOWN,
        RegisterPlayer,
        PlayerRegistered,
        SubmitOrder,
        ForwardOrder,
        OrderConfirmation,
        FwdOrderConfirmation,
        CancelOrder,
        FwdCancel,
        CancelConfirmation,
        FwdCancelConfirmation,
        RegisterMEMessage,
        ACK,
        TopOfBookNotification,
        TopOfBookRequest,
        TopOfBookResponse,

    }

//    private static Logger log = LogManager.getFormatterLogger(Message.class.getName());

    protected MessageTypes messageType;

    public Message(MessageTypes msgType) {
        messageType = msgType;
    }

    public MessageTypes getMessageType() {
        return messageType;
    }

    public abstract void decode(ByteBuffer buff);

    public abstract ByteArrayOutputStream encode() throws IOException;

    public static Message getMessage(byte[] bytes) {
//        log.debug("Getting MessageType.");
        Message msg = null;
        ByteBuffer buff = ByteBuffer.wrap(bytes);
        buff.order(ByteOrder.BIG_ENDIAN);

        if (bytes.length >= 2) {
            short msgType = buff.getShort();
//            log.debug("Message ID: " + msgType);
            switch (msgType) {
                case 0:
                    return null;
                case 1:
//                    msg = new RegisterPlayerMessage();
                    break;
                case 2:
//                    msg = new PlayerRegisteredMessage();
                    break;
                case 3:
//                    msg = new SubmitOrderMessage();
                    break;
                case 4:
//                    msg = new ForwardOrderMessage();
                    break;
                case 5:
//                    msg = new OrderConfirmation();
                    break;
                case 6:
//                    msg = new FwdOrderConfirmation();
                    break;
                case 7:
//                    msg = new CancelOrderMessage();
                    break;
                case 8:
//                    msg = new FwdCancelMessage();
                    break;
                case 9:
//                    msg = new CancelConfirmationMessage();
                    break;
                case 10:
//                    msg = new FwdCancelConfirmationMessage();
                    break;
                case 11:
//                    msg = new RegisterMEMessage();
                    break;
                case 12:
//                    msg = new AckMessage();
                    break;
                case 13:
//                    msg = new TopOfBookNotificationMessage();
                    break;
                case 14:
//                    msg = new TopOfBookRequestMessage();
                    break;
                case 15:
//                    msg = new TopOfBookResponseMessage();
                    break;

            }
            try {
//                log.debug("Decoding a " + msg.messageType.name());
                msg.decode(buff);

            } catch (Exception e) {
//                log.error("[Message Error]: Could not decode " + msg.messageType.name());
//                log.error(e.getMessage());

            }
        }
        return msg;
    }

}
