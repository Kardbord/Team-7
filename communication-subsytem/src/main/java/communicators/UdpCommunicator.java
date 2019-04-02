package communicators;

import dispatcher.EnvelopeDispatcher;
import messages.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

public class UdpCommunicator extends EnvelopeDispatcher implements Runnable {

    private DatagramChannel datagramChannel;

    public UdpCommunicator(DatagramChannel datagramChannel, InetSocketAddress address) throws IOException{
        this.datagramChannel = datagramChannel;
        if (address.getPort() != 0)
            this.datagramChannel.bind(address);
        else
            this.datagramChannel.bind(null);
    }

    public void send(byte[] messageBytes, InetAddress address, int port) throws IOException {
        datagramChannel.send(ByteBuffer.wrap(messageBytes), new InetSocketAddress(address, port));
    }

    public void send(Message messageToSend, InetAddress address, int port) throws IOException {
        datagramChannel.send(ByteBuffer.wrap(messageToSend.encode()), new InetSocketAddress(address, port));
    }

    public <T extends Message> void sendReliably(Message messageToSend, InetAddress address,
                                 int port, Class<T> expectedResponse) throws IOException {
        sendReliably(messageToSend, address, port, expectedResponse,
                RetyPolicies.DEFAULT_MAX_RETRIES, RetyPolicies.DEFAULT_MILLISECONDS_BETWEEN_RETRIES);
    }

    public <T extends Message> void sendReliably(Message messageToSend, InetAddress address, int port,
                                 Class<T> expectedResponse, int maxRetries, long millisecondsBetweenRetries) throws IOException {

        final boolean[] receivedResponse = {false};

        Consumer<Envelope<T>> responseInterceptor = (envelope) -> {
            Message receivedMessage = envelope.getMessage();
            UUID receivedConversationId = receivedMessage.getConversationId();
            UUID expectedConversationId = messageToSend.getConversationId();
            if(receivedConversationId.equals(expectedConversationId)) {
                receivedResponse[0] = true;
            }
        };
        registerForDispatch(expectedResponse, responseInterceptor);

        while(!receivedResponse[0] && maxRetries > 0) {
            send(messageToSend, address, port);

            try {
                Thread.sleep(millisecondsBetweenRetries);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            maxRetries--;
        }

        typeToConsumerMap.get(expectedResponse).remove(responseInterceptor);

        if(!receivedResponse[0]) {
            throw new IOException("Failed to send reliably.");
        }
    }

    public Envelope<byte[]> receive() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(5096);
        InetSocketAddress sourceSocketAddress = (InetSocketAddress) datagramChannel.receive(buffer);

        byte[] messageBytes = Arrays.copyOf(buffer.array(), buffer.position());

        return new Envelope<>(messageBytes, sourceSocketAddress);
    }

    @Override
    public void run() {
        while (true) {
            try {
                dispatch(receive());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
