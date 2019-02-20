package messages;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AckMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.ACK;

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .toByteArray();

        byte[] actualMessageBytes = new AckMessage().encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.ACK;

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .toByteArray();

        AckMessage victim = AckMessage.decode(messageBytes);

        assertEquals(victim.getMessageType(), expectedMessageType);

    }
}