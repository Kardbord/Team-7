package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CancelConfirmationMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.CANCEL_CONF;
        UUID expectedUUID = UUID.randomUUID();
        short expectedPlayerId = 12;
        short expectedOrderId = 42;
        short expectedQty = 100;
        String expectedSymbol = "NVDA";

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedPlayerId)
                .encodeShort(expectedOrderId)
                .encodeShort(expectedQty)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualMessageBytes =
                new CancelConfirmationMessage(
                        expectedUUID,
                        expectedPlayerId,
                        expectedOrderId,
                        expectedQty,
                        expectedSymbol).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.CANCEL_CONF;
        UUID expectedUUID = UUID.randomUUID();
        short expectedPlayerId = 12;
        short expectedOrderId = 42;
        short expectedQty = 100;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedPlayerId)
                .encodeShort(expectedOrderId)
                .encodeShort(expectedQty)
                .encodeString(expectedSymbol)
                .toByteArray();

        CancelConfirmationMessage victim = CancelConfirmationMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedPlayerId, victim.getPlayerId());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedQty, victim.getCancelledQty());
        assertEquals(expectedSymbol, victim.getSymbol());
    }
}
