package player;

import communicators.UdpCommunicator;
import messages.ForwardOrderConfirmationMessage;
import messages.PlayerRegisteredMessage;
import messages.RegisterPlayerMessage;
import messages.TopOfBookNotificationMessage;
import org.junit.Test;
import utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PlayerTest {

    @Test
    public void testPlayerConstructor() throws IOException {
        String expectedName = "John";
        String expectedServerAddress = "127.0.0.1";
        short expectedPlayerId = 42;
        int expectedCash = 1000;
        UdpCommunicator udpCommunicator = mock(UdpCommunicator.class);
        InetAddress expectedInetAddress = new InetSocketAddress(expectedServerAddress, Utils.PORT).getAddress();

        Player player = new Player(expectedName, udpCommunicator, expectedServerAddress);
        player.setCash(expectedCash);
        player.setPlayerId(expectedPlayerId);

        // Verify message listeners are registered and udpCommunicator's run method is called
        verify(udpCommunicator).registerForDispatch(
                eq(PlayerRegisteredMessage.class),
                any()
        );
        verify(udpCommunicator).registerForDispatch(
                eq(TopOfBookNotificationMessage.class),
                any()
        );
        verify(udpCommunicator).registerForDispatch(
                eq(ForwardOrderConfirmationMessage.class),
                any()
        );
        /* TODO: Add tests for Cancel Order protocol once it is implemented,
                as well as any other unimplemented protocols.
         */
        verify(udpCommunicator).run();

        // Verify RegisterPlayerMessage is sent
        verify(udpCommunicator).sendReliably(
                eq(new RegisterPlayerMessage(expectedName)),
                eq(expectedInetAddress),
                eq(Utils.PORT),
                eq(PlayerRegisteredMessage.class)
        );

    }

    @Test
    public void testGetters() throws IOException {
        String expectedName = "John";
        String expectedServer = "127.0.0.1";
        short expectedPlayerId = 42;
        int expectedCash = 1000;
        UdpCommunicator udpCommunicator = mock(UdpCommunicator.class);

        Player player = new Player(expectedName, udpCommunicator, expectedServer);
        player.setCash(expectedCash);
        player.setPlayerId(expectedPlayerId);

        assertEquals(expectedName, player.getName());
        assertEquals(expectedCash, player.getCash());
        assertEquals(expectedPlayerId, player.getPlayerId());
        assertEquals(expectedServer, player.getServerSocketAddress().getHostString());
    }

}
