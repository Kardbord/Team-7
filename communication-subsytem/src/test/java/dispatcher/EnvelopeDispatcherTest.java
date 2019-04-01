package dispatcher;

import communicators.Envelope;
import messages.AckMessage;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EnvelopeDispatcherTest {

    private EnvelopeDispatcher victim;

    @Before
    public void setup() {
        victim = new EnvelopeDispatcher() {};
    }

    @Test
    public void callingDispatchShouldDispatchEnvelopeToRegisteredMethodWithMatchingType() throws IOException {
        Consumer<Envelope<AckMessage>> methodToDispatch = mock(Consumer.class);
        victim.registerForDispatch(AckMessage.class, methodToDispatch);

        AckMessage ackMessage = new AckMessage(UUID.randomUUID());
        byte[] ackMessageBytes = ackMessage.encode();
        InetSocketAddress inetSocketAddress = mock(InetSocketAddress.class);
        Envelope<byte[]> envelopeToDispatch = new Envelope<>(ackMessageBytes, inetSocketAddress);

        victim.dispatch(envelopeToDispatch);

        Envelope<AckMessage> expectedEnvelope = new Envelope<>(ackMessage, inetSocketAddress);
        verify(methodToDispatch).accept(expectedEnvelope);
    }

}