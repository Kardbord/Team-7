package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ForwardCancelMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_CANCEL;
        UUID expectedUUID = UUID.randomUUID();
        short expectedPlayerId = 12;
        short expectedOrderId = 42;
        String expectedSymbol = "NVDA";

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedPlayerId)
                .encodeShort(expectedOrderId)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualMessageBytes =
                new ForwardCancelMessage(
                        expectedUUID,
                        expectedPlayerId,
                        expectedOrderId,
                        expectedSymbol).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_CANCEL;
        UUID expectedUUID = UUID.randomUUID();
        short expectedPlayerId = 12;
        short expectedOrderId = 42;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedPlayerId)
                .encodeShort(expectedOrderId)
                .encodeString(expectedSymbol)
                .toByteArray();

        ForwardCancelMessage victim = ForwardCancelMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedPlayerId, victim.getPlayerId());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedSymbol, victim.getSymbol());
    }
}

