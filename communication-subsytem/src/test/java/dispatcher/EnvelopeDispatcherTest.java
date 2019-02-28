package dispatcher;

import communicators.Envelope;
import communicators.EnvelopeReceiver;
import messages.AckMessage;
import messages.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

public class EnvelopeDispatcherTest {

    private EnvelopeDispatcher<byte[]> victim;
    private EnvelopeReceiver<byte[]> receiver = Mockito.mock(EnvelopeReceiver.class);

    @Before
    public void setup() {
        victim = new EnvelopeDispatcher<>(receiver, Message::decode);
    }

    @Test
    public void callingDispatchShouldDispatchEnvelopeToRegisteredMethodWithMatchingType() throws IOException {
        Consumer<Envelope<AckMessage>> methodToDispatch = Mockito.mock(Consumer.class);
        victim.registerForDispatch(AckMessage.class, methodToDispatch);

        byte[] ackMessageBytes = new AckMessage().encode();
        InetSocketAddress inetSocketAddress = Mockito.mock(InetSocketAddress.class);
        Envelope<byte[]> envelopeToDispatch = new Envelope<>(ackMessageBytes, inetSocketAddress);

        victim.dispatch(envelopeToDispatch);

        Envelope<AckMessage> expectedEnvelope = new Envelope<>(new AckMessage(), inetSocketAddress);
        Mockito.verify(methodToDispatch).accept(expectedEnvelope);
    }

}