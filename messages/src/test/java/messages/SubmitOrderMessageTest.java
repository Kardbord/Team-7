package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SubmitOrderMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.SUBMIT_ORDER;
        UUID expectedUUID = UUID.randomUUID();
        short expectedPlayerId = 12;
        SubmitOrderMessage.OrderType expectedOrderType = SubmitOrderMessage.OrderType.BUY;
        short expectedQuantity = 42;
        int expectedPrice = 1200;
        String expectedSymbol = "NVDA";

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedPlayerId)
                .encodeByte(expectedOrderType.toByte())
                .encodeShort(expectedQuantity)
                .encodeInt(expectedPrice)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualMessageBytes =
                new SubmitOrderMessage(
                        expectedUUID,
                        expectedPlayerId,
                        expectedOrderType,
                        expectedQuantity,
                        expectedPrice,
                        expectedSymbol).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.SUBMIT_ORDER;
        UUID expectedUUID = UUID.randomUUID();
        short expectedPlayerId = 12;
        SubmitOrderMessage.OrderType expectedOrderType = SubmitOrderMessage.OrderType.BUY;
        short expectedQuantity = 42;
        int expectedPrice = 1200;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort(expectedPlayerId)
                .encodeByte(expectedOrderType.toByte())
                .encodeShort(expectedQuantity)
                .encodeInt(expectedPrice)
                .encodeString(expectedSymbol)
                .toByteArray();

        SubmitOrderMessage victim = SubmitOrderMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedPlayerId, victim.getPlayerId());
        assertEquals(expectedOrderType, victim.getOrderType());
        assertEquals(expectedQuantity, victim.getQuantity());
        assertEquals(expectedPrice, victim.getPrice());
        assertEquals(expectedSymbol, victim.getSymbol());
    }
}