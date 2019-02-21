package messages;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ForwardOrderConfirmationMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_ORDER_CONF;
        short expectedOrderId = 1;
        SubmitOrderMessage.OrderType expectedOrderType = SubmitOrderMessage.OrderType.BUY;
        String expectedSymbol = "NVDA";
        short expectedExecutedQty = 2;
        short expectedRestingQty = 3;
        short expectedPrice = 4;
        ForwardOrderConfirmationMessage.Status expectedStatus = ForwardOrderConfirmationMessage.Status.SUCCESS;

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedOrderId)
                .encodeByte(expectedOrderType.toByte())
                .encodeString(expectedSymbol)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeShort(expectedPrice)
                .encodeByte(expectedStatus.toByte())
                .toByteArray();

        byte[] actualMessageBytes =
                new ForwardOrderConfirmationMessage(expectedOrderId, expectedOrderType, expectedSymbol, expectedExecutedQty,
                        expectedRestingQty, expectedPrice, expectedStatus).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_ORDER_CONF;
        short expectedOrderId = 1;
        SubmitOrderMessage.OrderType expectedOrderType = SubmitOrderMessage.OrderType.BUY;
        String expectedSymbol = "NVDA";
        short expectedExecutedQty = 2;
        short expectedRestingQty = 3;
        short expectedPrice = 4;
        ForwardOrderConfirmationMessage.Status expectedStatus = ForwardOrderConfirmationMessage.Status.SUCCESS;

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedOrderId)
                .encodeByte(expectedOrderType.toByte())
                .encodeString(expectedSymbol)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeShort(expectedPrice)
                .encodeByte(expectedStatus.toByte())
                .toByteArray();

        ForwardOrderConfirmationMessage victim = ForwardOrderConfirmationMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedOrderType, victim.getOrderType());
        assertEquals(expectedSymbol, victim.getSymbol());
        assertEquals(expectedExecutedQty, victim.getExecutedQty());
        assertEquals(expectedRestingQty, victim.getRestingQty());
        assertEquals(expectedPrice, victim.getPrice());
        assertEquals(expectedStatus, victim.getStatus());
    }
}