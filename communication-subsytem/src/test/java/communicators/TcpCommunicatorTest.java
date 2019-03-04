package communicators;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class TcpCommunicatorTest {

    private TcpCommunicator victim;
    private Socket socket = mock(Socket.class);

    @Before
    public void setup() {
        when(socket.isConnected()).thenReturn(true);
        victim = new TcpCommunicator(socket);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsExceptionWhenInstantiatedWithDisconnectedSocket() {
        victim = new TcpCommunicator(mock(Socket.class));
    }

    @Test
    public void sendWritesMessageBytesToSocketOutputStream() throws IOException {
        OutputStream outputStream = mock(OutputStream.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        byte[] expectedMessageBytes = new byte[]{'t', 'e', 's', 't'};

        victim.send(expectedMessageBytes);

        verify(outputStream).write(expectedMessageBytes);
    }

    @Test
    public void receivesBytesFromSocketInputStream() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any())).thenReturn(0);
        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getRemoteSocketAddress()).thenReturn(mock(InetSocketAddress.class));

        victim.receive();

        verify(inputStream).read(any());
    }
}