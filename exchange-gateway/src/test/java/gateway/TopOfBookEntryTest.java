package gateway;

import org.junit.Test;

import static org.junit.Assert.*;

public class TopOfBookEntryTest {

    private String expectedSymbol = "NVDA";
    private short expectedBidPrice = 300;
    private short expectedBidQuantity = 6;
    private short expectedAskPrice = 245;
    private short expectedAskQuantity = 10;

    @Test
    public void testGetters() {
        TopOfBookEntry victim = new TopOfBookEntry(
                expectedSymbol,
                expectedBidPrice,
                expectedBidQuantity,
                expectedAskPrice,
                expectedAskQuantity
        );

        assertEquals(victim.getSymbol(), expectedSymbol);
        assertEquals(victim.getBidPrice(), expectedBidPrice);
        assertEquals(victim.getBidQuantity(), expectedBidQuantity);
        assertEquals(victim.getAskPrice(), expectedAskPrice);
        assertEquals(victim.getAskQuantity(), expectedAskQuantity);
    }

    @Test
    public void testSetters() {
        TopOfBookEntry victim = new TopOfBookEntry(
                expectedSymbol,
                expectedBidPrice,
                expectedBidQuantity,
                expectedAskPrice,
                expectedAskQuantity
        );

        String updatedSymbol = "AMZN";
        victim.setSymbol(updatedSymbol);
        assertEquals(victim.getSymbol(), updatedSymbol);

        short updatedBidPrice = 300;
        victim.setBidPrice(updatedBidPrice);
        assertEquals(victim.getBidPrice(), updatedBidPrice);

        short updatedBidQuantity = 1;
        victim.setBidQuantity(updatedBidQuantity);
        assertEquals(victim.getBidQuantity(), updatedBidQuantity);

        short updatedAskPrice = 800;
        victim.setAskPrice(updatedAskPrice);
        assertEquals(victim.getAskPrice(), updatedAskPrice);

        short updatedAskQuantity = 9;
        victim.setAskQuantity(updatedAskQuantity);
        assertEquals(victim.getAskQuantity(), updatedAskQuantity);
    }

}