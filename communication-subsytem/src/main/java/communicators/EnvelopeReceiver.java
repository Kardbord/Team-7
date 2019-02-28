package communicators;

import java.io.IOException;

public interface EnvelopeReceiver {
    Envelope receive() throws IOException;
}
