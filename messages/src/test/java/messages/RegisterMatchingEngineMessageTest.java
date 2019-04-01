package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

public class RegisterMatchingEngineMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.REGISTER_MATCHING_ENGINE;
        UUID expectedUUID = UUID.randomUUID();
        String expectedSymbol = "NVDA";

        byte[] expectedBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualBytes = new RegisterMatchingEngineMessage(expectedUUID, expectedSymbol).encode();

        assertArrayEquals(expectedBytes, actualBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.REGISTER_MATCHING_ENGINE;
        UUID expectedUUID = UUID.randomUUID();
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeString(expectedSymbol)
                .toByteArray();

        RegisterMatchingEngineMessage victim = RegisterMatchingEngineMessage.decode(messageBytes);

        assertEquals(victim.getMessageType(), expectedMessageType);
        assertEquals(victim.getSymbol(), expectedSymbol);
    }
}