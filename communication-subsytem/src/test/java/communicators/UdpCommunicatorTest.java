package communicators;

import messages.AckMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import security.Decrypter;
import security.Encrypter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UdpCommunicatorTest {

    private UdpCommunicator victim;
    private DatagramChannel datagramChannel = mock(DatagramChannel.class);

    @Before
    public void setup() throws IOException{
        victim = new UdpCommunicator(datagramChannel, new InetSocketAddress(0), new Decrypter(), new Encrypter());
    }

    @Test
    public void sendShouldDelegateToDatagramChannelWithEncryptedBytes() throws IOException {
        byte[] messageBytesToTest = new byte[]{'t', 'e', 's', 't'};
        byte[] expectedMessageBytes = new Encrypter().encrypt(messageBytesToTest);
        InetAddress inetAddress = mock(InetAddress.class);
        int port = 1;
        InetSocketAddress expectedSocketAddress = new InetSocketAddress(inetAddress, port);

        victim.send(messageBytesToTest, inetAddress, port);

        verify(datagramChannel).send(ByteBuffer.wrap(expectedMessageBytes), expectedSocketAddress);
    }

    @Test
    public void reliableSendShouldTryMaxTimesAndThrowIOExceptionWhenNoResponse() throws IOException {

        try{
            victim.sendReliably(new AckMessage(UUID.randomUUID()), InetAddress.getLocalHost(), 1, AckMessage.class);
        }catch(IOException e){
            verify(datagramChannel, times(RetyPolicies.DEFAULT_MAX_RETRIES)).send(any(), any());
            return;
        }

        throw new Error("Expected exception but received none");
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