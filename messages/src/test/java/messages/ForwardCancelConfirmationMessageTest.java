package messages;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ForwardCancelConfirmationMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_CANCEL_CONF;
        short expectedOrderId = 42;
        short expectedCancelledQty = 100;
        String expectedSymbol = "NVDA";

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedOrderId)
                .encodeShort(expectedCancelledQty)
                .encodeString(expectedSymbol)
                .toByteArray();

        byte[] actualMessageBytes =
                new ForwardCancelConfirmationMessage(
                        expectedOrderId,
                        expectedCancelledQty,
                        expectedSymbol).encode();
        assertArrayEquals(expectedMessageBytes, actualMessageBytes);

        actualMessageBytes = new ForwardCancelConfirmationMessage(
                new CancelConfirmationMessage(
                        (short) 0,
                        expectedOrderId,
                        expectedCancelledQty,
                        expectedSymbol
                )
        ).encode();
        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.FWD_CANCEL_CONF;
        short expectedOrderId = 42;
        short expectedCancelledQty = 100;
        String expectedSymbol = "NVDA";

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeShort(expectedOrderId)
                .encodeShort(expectedCancelledQty)
                .encodeString(expectedSymbol)
                .toByteArray();

        ForwardCancelConfirmationMessage victim = ForwardCancelConfirmationMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedOrderId, victim.getOrderId());
        assertEquals(expectedCancelledQty, victim.getCancelledQty());
        assertEquals(expectedSymbol, victim.getSymbol());
    }
}
