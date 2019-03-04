package communicators;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UdpCommunicatorTest {

    private UdpCommunicator victim;
    private DatagramChannel datagramChannel = mock(DatagramChannel.class);

    @Before
    public void setup() {
        victim = new UdpCommunicator(datagramChannel);
    }

    @Test
    public void sendShouldDelegateToDatagramChannel() throws IOException {
        byte[] expectedMessageBytes = new byte[]{'t','e','s','t'};
        InetAddress inetAddress = mock(InetAddress.class);
        int port = 1;
        InetSocketAddress expectedSocketAddress = new InetSocketAddress(inetAddress, port);

        victim.send(expectedMessageBytes, inetAddress, port);

        verify(datagramChannel).send(ByteBuffer.wrap(expectedMessageBytes), expectedSocketAddress);
    }

    @Test
    public void receiveDelegatesToDatagramChannel() throws IOException {
        victim.receive();

        verify(datagramChannel).receive(any());
    }

    @Test
    public void receiveReturnsAnEnvelopeWithSenderSocketAddress() throws IOException {
        SocketAddress expectedSenderAdddress = InetSocketAddress.createUnresolved("test", 0);
        when(datagramChannel.receive(any()))
                .thenReturn(expectedSenderAdddress);

        Envelope<byte[]> actualEnvelope = victim.receive();

        assertEquals(expectedSenderAdddress, actualEnvelope.getSourceInetSocketAddress());
    }
}