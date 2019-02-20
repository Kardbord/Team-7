package messages;

import org.junit.Test;

import java.io.IOException;

import messages.SubmitOrderMessage.OrderType;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ForwardOrderMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FORWARD_ORDER;
        short expectedPlayerId = 12;
        OrderType expectedOrderType = OrderType.BUY;
        short expectedQuantity = 42;
        short expectedPrice = 1200;
        String expectedSymbol = "NVDA";

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedPlayerId)
                .encodeByte(expectedOrderType.toByte())
                .encodeShort(expectedQuantity)
                .encodeShort(expectedPrice)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualMessageBytes =
                new ForwardOrderMessage(
                        expectedPlayerId,
                        expectedOrderType,
                        expectedQuantity,
                        expectedPrice,
                        expectedSymbol).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FORWARD_ORDER;
        short expectedPlayerId = 12;
        OrderType expectedOrderType = OrderType.BUY;
        short expectedQuantity = 42;
        short expectedPrice = 1200;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedPlayerId)
                .encodeByte(expectedOrderType.toByte())
                .encodeShort(expectedQuantity)
                .encodeShort(expectedPrice)
                .encodeString(expectedSymbol)
                .toByteArray();

        ForwardOrderMessage victim = ForwardOrderMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedPlayerId, victim.getPlayerId());
        assertEquals(expectedOrderType, victim.getOrderType());
        assertEquals(expectedQuantity, victim.getQuantity());
        assertEquals(expectedPrice, victim.getPrice());
        assertEquals(expectedSymbol, victim.getSymbol());
    }
}