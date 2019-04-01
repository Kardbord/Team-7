package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

public class AckMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.ACK;
        UUID expectedUUID = UUID.randomUUID();

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .toByteArray();

        byte[] actualMessageBytes = new AckMessage(expectedUUID).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.ACK;
        UUID expectedUUID = UUID.randomUUID();

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .toByteArray();

        AckMessage victim = AckMessage.decode(messageBytes);

        assertEquals(victim.getMessageType(), expectedMessageType);
        assertEquals(victim.getConversationId(), expectedUUID);

    }
}