package messages;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ForwardOrderConfirmationMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_ORDER_CONF;
        short expectedOrderId = 1;
        String expectedSymbol = "NVDA";
        short expectedExecutedQty = 2;
        short expectedRestingQty = 3;
        short expectedPrice = 4;

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedOrderId)
                .encodeString(expectedSymbol)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeShort(expectedPrice)
                .toByteArray();

        byte[] actualMessageBytes =
                new ForwardOrderConfirmationMessage(expectedOrderId, expectedSymbol, expectedExecutedQty,
                        expectedRestingQty, expectedPrice).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);

        actualMessageBytes = new ForwardOrderConfirmationMessage(
                new OrderConfirmationMessage(
                        (short) 0,
                        expectedOrderId,
                        expectedExecutedQty,
                        expectedRestingQty,
                        expectedPrice,
                        expectedSymbol
                )
        ).encode();
        assertArrayEquals(expectedMessageBytes, actualMessageBytes);

    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_ORDER_CONF;
        short expectedOrderId = 1;
        String expectedSymbol = "NVDA";
        short expectedExecutedQty = 2;
        short expectedRestingQty = 3;
        short expectedPrice = 4;

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedOrderId)
                .encodeString(expectedSymbol)
                .encodeShort(expectedExecutedQty)
                .encodeShort(expectedRestingQty)
                .encodeShort(expectedPrice)
                .toByteArray();

        ForwardOrderConfirmationMessage victim = ForwardOrderConfirmationMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedSymbol, victim.getSymbol());
        assertEquals(expectedExecutedQty, victim.getExecutedQty());
        assertEquals(expectedRestingQty, victim.getRestingQty());
        assertEquals(expectedPrice, victim.getPrice());
    }
}