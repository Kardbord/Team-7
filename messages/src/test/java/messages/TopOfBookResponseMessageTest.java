package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.*;

public class TopOfBookResponseMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.TOP_OF_BOOK_RESPONSE;
        UUID expectedUUID = UUID.randomUUID();
        String expectedSymbol = "NVDA";
        int expectedBidPrice = 500;
        short expectedBidQty = 5;
        int expectedAskPrice = 550;
        short expectedAskQty = 3;

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeString(expectedSymbol)
                .encodeInt(expectedBidPrice)
                .encodeShort(expectedBidQty)
                .encodeInt(expectedAskPrice)
                .encodeShort(expectedAskQty)
                .toByteArray();

        byte[] actualMessageBytes =
                new TopOfBookResponseMessage(
                        expectedUUID,
                        expectedSymbol,
                        expectedBidPrice,
                        expectedBidQty, expectedAskPrice,
                        expectedAskQty).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.TOP_OF_BOOK_RESPONSE;
        UUID expectedUUID = UUID.randomUUID();
        String expectedSymbol = "NVDA";
        int expectedBidPrice = 500;
        short expectedBidQty = 5;
        int expectedAskPrice = 550;
        short expectedAskQty = 3;

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeString(expectedSymbol)
                .encodeInt(expectedBidPrice)
                .encodeShort(expectedBidQty)
                .encodeInt(expectedAskPrice)
                .encodeShort(expectedAskQty)
                .toByteArray();

        TopOfBookResponseMessage victim = TopOfBookResponseMessage.decode(messageBytes);

        assertEquals(expectedSymbol, victim.getSymbol());
        assertEquals(expectedBidPrice, victim.getBidPrice());
        assertEquals(expectedBidQty, victim.getBidQuantity());
        assertEquals(expectedAskPrice, victim.getAskPrice());
        assertEquals(expectedAskQty, victim.getAskQuantity());
    }

}