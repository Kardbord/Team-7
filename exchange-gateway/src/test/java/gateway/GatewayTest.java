package gateway;

import communicators.UdpCommunicator;
import messages.CancelOrderMessage;
import messages.RegisterPlayerMessage;
import messages.SubmitOrderMessage;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GatewayTest {
    @Test
    public void testGatewayConstructor() throws IOException {
        UdpCommunicator udpCommunicator = mock(UdpCommunicator.class);
        new Gateway(udpCommunicator);

        verify(udpCommunicator).registerForDispatch(
                eq(RegisterPlayerMessage.class),
                any()
        );
        verify(udpCommunicator).registerForDispatch(
                eq(SubmitOrderMessage.class),
                any()
        );
        verify(udpCommunicator).registerForDispatch(
                eq(CancelOrderMessage.class),
                any()
        );
        verify(udpCommunicator).run();
    }
}