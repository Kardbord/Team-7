package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

public class TopOfBookRequestMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.TOP_OF_BOOK_REQUEST;
        UUID expectedUUID = UUID.randomUUID();
        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .toByteArray();

        byte[] actualMessageBytes = new TopOfBookRequestMessage(expectedUUID).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.TOP_OF_BOOK_REQUEST;
        UUID expectedUUID = UUID.randomUUID();
        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .toByteArray();

        TopOfBookRequestMessage victim = TopOfBookRequestMessage.decode(messageBytes);

        assertEquals(victim.getMessageType(), expectedMessageType);
    }

}