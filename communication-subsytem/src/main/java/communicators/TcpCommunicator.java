package communicators;

import dispatcher.EnvelopeDispatcher;
import messages.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.function.Consumer;

public class TcpCommunicator extends EnvelopeDispatcher implements Runnable {

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

    public void send(Message messageToSend) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(messageToSend.encode());
    }

    public <T> void sendReliably(Message messageToSend, Class<T> expectedResponse) throws IOException {
        sendReliably(messageToSend, expectedResponse, RetyPolicies.DEFAULT_MAX_RETRIES, RetyPolicies.DEFAULT_MILLISECONDS_BETWEEN_RETRIES);
    }

    public <T> void sendReliably(Message messageToSend, Class<T> expectedResponse, int maxRetries, long millisecondsBetweenRetries) throws IOException {

        final boolean[] receivedResponse = {false};

        Consumer<Envelope<T>> responseInterceptor = (ignored) -> receivedResponse[0] = true;
        registerForDispatch(expectedResponse, responseInterceptor);

        while(!receivedResponse[0] && maxRetries > 0) {
            send(messageToSend);

            try {
                Thread.sleep(millisecondsBetweenRetries);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            maxRetries--;
        }

        typeToConsumerMap.get(expectedResponse).remove(responseInterceptor);

        if(!receivedResponse[0]) {
            throw new IOException("Timeout Exceeded");
        }
    }

    public Envelope<byte[]> receive() throws IOException {
        InputStream inputStream = socket.getInputStream();

        byte[] buffer = new byte[5096];
        int numBytesReadIntoBuffer = inputStream.read(buffer);

        byte[] messageBytes = Arrays.copyOf(buffer, numBytesReadIntoBuffer);
        InetSocketAddress sourceSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

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
