package messages;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class TopOfBookNotificationMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.TOP_OF_BOOK_NOTIFICATION;
        UUID expectedUUID = UUID.randomUUID();
        String expectedSymbol = "NVDA";

        int expectedAskPrice = 550;
        short expectedAskQty = 3;
        List<TopOfBookResponseMessage.PriceQuantityPair> asks =
                List.of(new TopOfBookResponseMessage.PriceQuantityPair(expectedAskPrice, expectedAskQty));

        int expectedBidPrice = 500;
        short expectedBidQty = 5;
        List<TopOfBookResponseMessage.PriceQuantityPair> bids =
                List.of(new TopOfBookResponseMessage.PriceQuantityPair(expectedBidPrice, expectedBidQty));

        byte[] expectedMessageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeString(expectedSymbol)
                .encodeInt(asks.size())
                .encodeInt(expectedAskPrice)
                .encodeShort(expectedAskQty)
                .encodeInt(bids.size())
                .encodeInt(expectedBidPrice)
                .encodeShort(expectedBidQty)
                .toByteArray();

        byte[] actualMessageBytes =
                new TopOfBookNotificationMessage(
                        expectedUUID,
                        expectedSymbol,
                        asks, bids).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);
    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.TOP_OF_BOOK_NOTIFICATION;
        UUID expectedUUID = UUID.randomUUID();
        String expectedSymbol = "NVDA";
        int expectedBidPrice = 500;
        short expectedBidQty = 5;
        TopOfBookResponseMessage.PriceQuantityPair bid = new TopOfBookResponseMessage.PriceQuantityPair(expectedBidPrice, expectedBidQty);
        int expectedAskPrice = 550;
        short expectedAskQty = 3;
        TopOfBookResponseMessage.PriceQuantityPair ask = new TopOfBookResponseMessage.PriceQuantityPair(expectedAskPrice, expectedAskQty);

        byte[] messageBytes = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeString(expectedSymbol)
                .encodeInt(1)
                .encodeInt(expectedAskPrice)
                .encodeShort(expectedAskQty)
                .encodeInt(1)
                .encodeInt(expectedBidPrice)
                .encodeShort(expectedBidQty)
                .toByteArray();

        TopOfBookNotificationMessage victim = TopOfBookNotificationMessage.decode(messageBytes);

        assertEquals(expectedSymbol, victim.getSymbol());
        assertEquals(bid, victim.getBids().get(0));
        assertEquals(ask, victim.getAsks().get(0));
    }

}