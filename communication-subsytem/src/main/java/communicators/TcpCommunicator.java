package communicators;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class TcpCommunicator implements EnvelopeReceiver<byte[]> {

    private Socket socket;

    public TcpCommunicator(Socket socket) {
        if(socket == null || !socket.isConnected()) {
            throw new IllegalArgumentException();
        }

        this.socket = socket;
    }

    public void send(byte[] messageBytes) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(messageBytes);
    }

    public Envelope<byte[]> receive() throws IOException {
        InputStream inputStream = socket.getInputStream();

        byte[] buffer = new byte[5096];
        int numBytesReadIntoBuffer = inputStream.read(buffer);

        byte[] messageBytes = Arrays.copyOf(buffer, numBytesReadIntoBuffer);
        InetSocketAddress sourceSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

        return new Envelope<>(messageBytes, sourceSocketAddress);
    }
}
