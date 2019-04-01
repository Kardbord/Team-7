package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class OrderConfirmationMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.ORDER_CONFIRMATION;
        UUID expectedUUID = UUID.randomUUID();
        short expectedPlayerId = 2;
        short expectedOrderId = 3;
        short expectedExecutedQty = 4;
        short expectedRestingQty = 5;
        int expectedPrice = 6;
        String expectedSymbol = "NVDA";

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedPlayerId)
                .encodeShort(expectedOrderId)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeInt(expectedPrice)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualMessageBytes =
                new OrderConfirmationMessage(expectedUUID, expectedPlayerId, expectedOrderId,
                        expectedExecutedQty, expectedRestingQty, expectedPrice, expectedSymbol).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.ORDER_CONFIRMATION;
        UUID expectedUUID = UUID.randomUUID();
        short expectedPlayerId = 2;
        short expectedOrderId = 3;
        short expectedExecutedQty = 4;
        short expectedRestingQty = 5;
        int expectedPrice = 6;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedPlayerId)
                .encodeShort(expectedOrderId)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeInt(expectedPrice)
                .encodeString(expectedSymbol)
                .toByteArray();

        OrderConfirmationMessage victim = OrderConfirmationMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedPlayerId, victim.getPlayerId());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedExecutedQty, victim.getExecutedQty());
        assertEquals(expectedRestingQty, victim.getRestingQty());
        assertEquals(expectedPrice, victim.getPrice());
        assertEquals(expectedSymbol, victim.getSymbol());
    }
}