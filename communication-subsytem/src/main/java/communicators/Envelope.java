package communicators;

import java.net.InetSocketAddress;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Envelope<?> envelope = (Envelope<?>) o;
        return Objects.equals(message, envelope.message) &&
                Objects.equals(inetSocketAddress, envelope.inetSocketAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, inetSocketAddress);
    }
}
