package messages;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TopOfBookRequestMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.TOP_OF_BOOK_REQUEST;

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .toByteArray();

        byte[] actualMessageBytes = new TopOfBookRequestMessage().encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.TOP_OF_BOOK_REQUEST;

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .toByteArray();

        TopOfBookRequestMessage victim = TopOfBookRequestMessage.decode(messageBytes);

        assertEquals(victim.getMessageType(), expectedMessageType);
    }

}