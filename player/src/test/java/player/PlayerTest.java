package player;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void createPlayerTest() {
        String name = "John";
        int playerId = 42;
        int cash = 1000;
        Player player = new Player(name);
        player.setPlayerId(playerId);
        player.setCash(cash);

        assertEquals(name, player.getName());
        assertEquals(cash, player.getCash());
        assertEquals(playerId, player.getPlayerId());
    }

}
