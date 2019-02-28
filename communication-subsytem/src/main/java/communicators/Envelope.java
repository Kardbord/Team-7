package communicators;

import java.net.InetSocketAddress;

public class Envelope<T> {
    private T message;
    private InetSocketAddress inetSocketAddress;

    public Envelope(T message, InetSocketAddress inetSocketAddress) {
        this.message = message;
        this.inetSocketAddress = inetSocketAddress;
    }

    public T getMessage() {
        return message;
    }

    public InetSocketAddress getSourceInetSocketAddress() {
        return inetSocketAddress;
    }
}
