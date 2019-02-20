package messages;

import messages.CancelOrderMessage;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CancelOrderMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.CANCEL_ORDER;
        short expectedPlayerId = 12;
        short expectedOrderId = 42;
        String expectedSymbol = "NVDA";

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedPlayerId)
                .encodeShort(expectedOrderId)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualMessageBytes =
                new CancelOrderMessage(
                        expectedPlayerId,
                        expectedOrderId,
                        expectedSymbol).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.CANCEL_ORDER;
        short expectedPlayerId = 12;
        short expectedOrderId = 42;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedPlayerId)
                .encodeShort(expectedOrderId)
                .encodeString(expectedSymbol)
                .toByteArray();

        CancelOrderMessage victim = CancelOrderMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedPlayerId, victim.getPlayerId());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedSymbol, victim.getSymbol());
    }
}