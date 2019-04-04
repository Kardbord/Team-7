package portfolio;

import org.junit.Test;

import static org.junit.Assert.*;

public class PortfolioEntryTest {

    @Test
    public void testGetters() {
        String expectedSymbol = "NVDA";
        int expectedPositions = 8;
        int expectedEquity = 800;
        PortfolioEntry victim = new PortfolioEntry(expectedSymbol, expectedPositions, expectedEquity);

        assertEquals(expectedSymbol, victim.getSymbol());
        assertEquals(expectedPositions, victim.getPositions());
        assertEquals(expectedEquity, victim.getEquity());
    }

    @Test
    public void testSetters() {
        String initalSymbol = "NVDA";
        int initalPositions = 8;
        int initialEquity = 800;
        PortfolioEntry victim = new PortfolioEntry(initalSymbol, initalPositions, initialEquity);

        String newSymbol = "GOOG";
        int newPositions = 3;
        int newEquity = 450;
        victim.setSymbol(newSymbol);
        victim.setPositions(newPositions);
        victim.setEquity(newEquity);

        assertEquals(newSymbol, victim.getSymbol());
        assertEquals(newPositions, victim.getPositions());
        assertEquals(newEquity, victim.getEquity());

        int expectedIncrement = 3;
        victim.updatePositions(expectedIncrement);
        victim.updateEquity(expectedIncrement);

        assertEquals(newPositions + expectedIncrement, victim.getPositions());
        assertEquals(newEquity + expectedIncrement, victim.getEquity());
    }

}