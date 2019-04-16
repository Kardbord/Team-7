package portfolio;

import org.junit.Test;

import static org.junit.Assert.*;

public class PortfolioEntryTest {

    @Test
    public void testGetters() {
        String expectedSymbol = "NVDA";
        short expectedPositions = 8;
        int buyPrice = 100;
        int expectedEquity = buyPrice * expectedPositions;
        PortfolioEntry victim = new PortfolioEntry(expectedSymbol, expectedPositions, buyPrice);

        assertEquals(expectedSymbol, victim.getSymbol());
        assertEquals(expectedPositions, victim.getPositions());
        assertEquals(expectedEquity, victim.getEquity());
    }

    @Test
    public void testSetters() {
        String initalSymbol = "NVDA";
        short initalPositions = 8;
        int initialPrice = 100;
        PortfolioEntry victim = new PortfolioEntry(initalSymbol, initalPositions, initialPrice);

        short newPositions = 2;
        int newPrice = 200; // This will also represent the current price of the stock
        int totalPositions = initalPositions + newPositions;
        int totalEquity = totalPositions * newPrice;
        victim.updatePositions(newPositions);
        victim.updateEquity(newPrice);

        assertEquals(initalSymbol, victim.getSymbol());
        assertEquals(totalPositions, victim.getPositions());
        assertEquals(totalEquity, victim.getEquity());

    }

}