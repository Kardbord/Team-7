package gateway;

import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class PlayerDetailEntryTest {

    private int expectedCash = PlayerDetailEntry.STARTING_CASH;
    private String expectedName = "Phillip J. Fry";
    private short expectedId = PlayerDetailEntry.getNextPlayerId();
    private InetSocketAddress expectedInetSocketAddress = new InetSocketAddress(0);

    @Test
    public void testGetters() {
        PlayerDetailEntry victim = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(expectedCash, victim.getCash());
        assertEquals(expectedName, victim.getName());
        assertEquals(expectedId, victim.getId());
        assertEquals(expectedInetSocketAddress, victim.getSocketAddress());
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
    public void testNextPlayerIdIncrementsOnNewPlayerDetailEntryTestInstance() {
        PlayerDetailEntry victim1 = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(PlayerDetailEntry.getNextPlayerId(), expectedId + 1);

        PlayerDetailEntry victim2 = new PlayerDetailEntry(expectedName, expectedInetSocketAddress);
        assertEquals(victim2.getId(), expectedId + 1);
    }
}