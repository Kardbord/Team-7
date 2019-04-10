package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

public class ForwardOrderConfirmationMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_ORDER_CONF;
        UUID expectedUUID = UUID.randomUUID();
        short expectedOrderId = 1;
        String expectedSymbol = "NVDA";
        short expectedExecutedQty = 2;
        short expectedRestingQty = 3;
        int expectedPrice = 4;
        SubmitOrderMessage.OrderType expectedOrderType = SubmitOrderMessage.OrderType.BUY;

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedOrderId)
                .encodeString(expectedSymbol)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeInt(expectedPrice)
                .encodeByte(expectedOrderType.toByte())
                .toByteArray();

        byte[] actualMessageBytes =
                new ForwardOrderConfirmationMessage(expectedUUID, expectedOrderId, expectedSymbol, expectedExecutedQty,
                        expectedRestingQty, expectedPrice, expectedOrderType).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);

        actualMessageBytes = new ForwardOrderConfirmationMessage(
                new OrderConfirmationMessage(
                        expectedUUID,
                        (short) 0,
                        expectedOrderId,
                        expectedExecutedQty,
                        expectedRestingQty,
                        expectedPrice,
                        expectedSymbol,
                        expectedOrderType
                )
        ).encode();
        assertArrayEquals(expectedMessageBytes, actualMessageBytes);

    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_ORDER_CONF;
        UUID expectedUUID = UUID.randomUUID();
        short expectedOrderId = 1;
        String expectedSymbol = "NVDA";
        short expectedExecutedQty = 2;
        short expectedRestingQty = 3;
        int expectedPrice = 4;
        SubmitOrderMessage.OrderType expectedOrderType = SubmitOrderMessage.OrderType.BUY;

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedOrderId)
                .encodeString(expectedSymbol)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeInt(expectedPrice)
                .encodeByte(expectedOrderType.toByte())
                .toByteArray();

        ForwardOrderConfirmationMessage victim = ForwardOrderConfirmationMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedSymbol, victim.getSymbol());
        assertEquals(expectedExecutedQty, victim.getExecutedQty());
        assertEquals(expectedRestingQty, victim.getRestingQty());
        assertEquals(expectedPrice, victim.getPrice());
        assertEquals(expectedOrderType, victim.getOrderType());
    }
}