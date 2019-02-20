package messages;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class OrderConfirmationMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.ORDER_CONFIRMATION;
        short expectedBuyerPlayerId = 1;
        short expectedSellerPlayerId = 2;
        short expectedOrderId = 3;
        short expectedExecutedQty = 4;
        short expectedRestingQty = 5;
        short expectedPrice = 6;
        String expectedSymbol = "NVDA";

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedBuyerPlayerId)
                .encodeShort(expectedSellerPlayerId)
                .encodeShort(expectedOrderId)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeShort(expectedPrice)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualMessageBytes =
                new OrderConfirmationMessage(expectedBuyerPlayerId, expectedSellerPlayerId, expectedOrderId,
                        expectedExecutedQty, expectedRestingQty, expectedPrice, expectedSymbol).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.ORDER_CONFIRMATION;
        short expectedBuyerPlayerId = 1;
        short expectedSellerPlayerId = 2;
        short expectedOrderId = 3;
        short expectedExecutedQty = 4;
        short expectedRestingQty = 5;
        short expectedPrice = 6;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedBuyerPlayerId)
                .encodeShort(expectedSellerPlayerId)
                .encodeShort(expectedOrderId)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeShort(expectedPrice)
                .encodeString(expectedSymbol)
                .toByteArray();

        OrderConfirmationMessage victim = OrderConfirmationMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedBuyerPlayerId, victim.getBuyerPlayerId());
        assertEquals(expectedSellerPlayerId, victim.getSellerPlayerId());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedExecutedQty, victim.getExecutedQty());
        assertEquals(expectedRestingQty, victim.getRestingQty());
        assertEquals(expectedPrice, victim.getPrice());
        assertEquals(expectedSymbol, victim.getSymbol());
    }
}