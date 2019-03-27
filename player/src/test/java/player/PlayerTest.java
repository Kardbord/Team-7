package player;

import org.junit.Test;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    @Test
    public void createPlayerTest() throws IOException {
        String name = "John";
        String server = "127.0.0.1";
        int playerId = 42;
        int cash = 1000;
        Player player = new Player(name, server);
        player.setPlayerId(playerId);
        player.setCash(cash);

        assertEquals(name, player.getName());
        assertEquals(cash, player.getCash());
        assertEquals(playerId, player.getPlayerId());
        assertEquals(server, player.getServerSocketAddress().getHostString());
    }

}
