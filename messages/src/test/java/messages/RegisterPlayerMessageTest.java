package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RegisterPlayerMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.REGISTER_PLAYER;
        UUID expectedUUID = UUID.randomUUID();
        String expectedPlayerName = "Jimmy Page";
        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeString(expectedPlayerName)
                .toByteArray();

        byte[] actualMessageBytes = new RegisterPlayerMessage(expectedUUID, expectedPlayerName).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.REGISTER_PLAYER;
        UUID expectedUUID = UUID.randomUUID();
        String expectedPlayerName = "Jimmy Page";
        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeString(expectedPlayerName)
                .toByteArray();

        RegisterPlayerMessage victim = RegisterPlayerMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedPlayerName, victim.getPlayerName());
    }
}
