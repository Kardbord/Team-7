package communicators;

import java.io.IOException;

public interface EnvelopeReceiver<T> {
    Envelope<T> receive() throws IOException;
}
