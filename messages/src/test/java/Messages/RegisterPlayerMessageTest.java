package Messages;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RegisterPlayerMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.REGISTER_PLAYER;
        String expectedPlayerName = "Jimmy Page";
        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeString(expectedPlayerName)
                .toByteArray();

        byte[] actualMessageBytes = new RegisterPlayerMessage(expectedPlayerName).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.REGISTER_PLAYER;
        String expectedPlayerName = "Jimmy Page";
        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeString(expectedPlayerName)
                .toByteArray();

        RegisterPlayerMessage victim = RegisterPlayerMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedPlayerName, victim.getPlayerName());
    }
}
