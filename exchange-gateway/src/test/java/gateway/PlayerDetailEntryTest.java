package gateway;

import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class PlayerDetailEntryTest {

    private int expectedCash = PlayerDetailEntry.STARTING_CASH;
    private String expectedName = "Phillip J. Fry";
    private short expectedId = PlayerDetailEntry.getNextPlayerId();
    private InetSocketAddress expectedInetSocketAddress = new InetSocketAddress(0);
    private int expectedTotalInvestments = 0;
    private int expectedCashFromSales = 0;
    private ConcurrentHashMap<String, Integer> expectedSymbolToSharesMap = new ConcurrentHashMap<>();

    @Test
    public void testGetters() {
        PlayerDetailEntry victim = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(expectedCash, victim.getCash());
        assertEquals(expectedName, victim.getName());
        assertEquals(expectedId, victim.getId());
        assertEquals(expectedInetSocketAddress, victim.getSocketAddress());
        assertEquals(expectedTotalInvestments, victim.getTotalInvestments());
        assertEquals(expectedCashFromSales, victim.getCashFromSales());
        assertEquals(expectedSymbolToSharesMap, victim.getSymbolToSharesMap());
    }

    @Test
    public void testSetters() throws UnknownHostException {
        PlayerDetailEntry victim = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);

        int updatedCash = expectedCash + 500;
        victim.setCash(updatedCash);
        assertEquals(victim.getCash(), updatedCash);

        String updatedName = "Bender B. Rodriguez";
        victim.setName(updatedName);
        assertEquals(victim.getName(), updatedName);

        InetSocketAddress updatedInetSocketAddress = new InetSocketAddress("localhost", 2000);
        victim.setSocketAddress(updatedInetSocketAddress);
        assertEquals(victim.getSocketAddress(), updatedInetSocketAddress);

        updatedInetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), 2000);
        victim.setSocketAddress(InetAddress.getLocalHost(), 2000);
        assertEquals(victim.getSocketAddress(), updatedInetSocketAddress);

    }

    @Test
    public void testIncrementTotalInvestments() {
        PlayerDetailEntry victim = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(expectedTotalInvestments, victim.getTotalInvestments());

        int increment = 200;
        victim.incrementTotalInvestments(increment);
        assertEquals(expectedTotalInvestments + increment, victim.getTotalInvestments());

        try {
            victim.incrementTotalInvestments(-increment);
            fail();
        } catch (IllegalArgumentException ignored) {
            // continue
        }
    }

    @Test
    public void testIncrementCashFromSales() {
        PlayerDetailEntry victim = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(expectedCashFromSales, victim.getCashFromSales());

        int increment = 200;
        victim.incrementCashFromSales(increment);
        assertEquals(expectedCashFromSales + increment, victim.getCashFromSales());

        try {
            victim.incrementCashFromSales(-increment);
            fail();
        } catch (IllegalArgumentException ignored) {
            // continue
        }
    }

    @Test
    public void testNextPlayerIdIncrementsOnNewPlayerDetailEntryTestInstance() {
        new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(PlayerDetailEntry.getNextPlayerId(), expectedId + 1);

        PlayerDetailEntry victim2 = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(victim2.getId(), expectedId + 1);
    }

    @Test
    public void testIncrementShares() {
        PlayerDetailEntry victim = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(expectedSymbolToSharesMap, victim.getSymbolToSharesMap());

        String symbol = "GOOG";
        int shares = 10;
        victim.addShares(symbol, shares);
        expectedSymbolToSharesMap.put(symbol, expectedSymbolToSharesMap.getOrDefault(symbol, 0) + shares);
        assertEquals(expectedSymbolToSharesMap, victim.getSymbolToSharesMap());

        try {
            victim.addShares(symbol, -19);
            fail();
        } catch (IllegalArgumentException ignored) {
            // continue
        }

        victim.subtractShares(symbol, shares);
        expectedSymbolToSharesMap.put(symbol, expectedSymbolToSharesMap.getOrDefault(symbol, 0) - shares);
        assertEquals(expectedSymbolToSharesMap, victim.getSymbolToSharesMap());

        try {
            victim.subtractShares(symbol, -19);
            fail();
        } catch (IllegalArgumentException ignored) {
            // continue
        }
    }
}