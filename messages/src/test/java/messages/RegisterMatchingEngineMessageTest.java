package messages;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class RegisterMatchingEngineMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.REGISTER_MATCHING_ENGINE;
        String expectedSymbol = "NVDA";

        byte[] expectedBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualBytes = new RegisterMatchingEngineMessage(expectedSymbol).encode();

        assertArrayEquals(expectedBytes, actualBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.REGISTER_MATCHING_ENGINE;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeString(expectedSymbol)
                .toByteArray();

        RegisterMatchingEngineMessage victim = RegisterMatchingEngineMessage.decode(messageBytes);

        assertEquals(victim.getMessageType(), expectedMessageType);
        assertEquals(victim.getSymbol(), expectedSymbol);
    }
}