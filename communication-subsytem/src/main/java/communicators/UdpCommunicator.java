package communicators;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class UdpCommunicator implements EnvelopeReceiver<byte[]> {

    private DatagramChannel datagramChannel;

    public UdpCommunicator(DatagramChannel datagramChannel) {
        this.datagramChannel = datagramChannel;
    }

    public void send(byte[] messageBytes, InetAddress address, int port) throws IOException {
        datagramChannel.send(ByteBuffer.wrap(messageBytes), new InetSocketAddress(address, port));
    }

    public Envelope<byte[]> receive() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5096);
        InetSocketAddress sourceSocketAddress = (InetSocketAddress) datagramChannel.receive(buffer);

        byte[] messageBytes = Arrays.copyOf(buffer.array(), buffer.position());

        return new Envelope<>(messageBytes, sourceSocketAddress);
    }
}
