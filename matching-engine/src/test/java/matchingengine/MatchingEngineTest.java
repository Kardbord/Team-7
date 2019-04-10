package matchingengine;

import communicators.Envelope;
import communicators.TcpCommunicator;
import messages.ForwardOrderMessage;
import messages.OrderConfirmationMessage;
import messages.SubmitOrderMessage;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class MatchingEngineTest {
    private final String symbol = "GOOG";
    private TcpCommunicator tcpCommunicator = mock(TcpCommunicator.class);
    private MatchingEngine victim = new MatchingEngine(symbol, tcpCommunicator);

    @Test
    public void shouldSendOrderConfirmationToBothMatchedPartiesWhenSingleOrderMatched() throws IOException {
        short restingPlayerId = 1;
        short restingQty = 10;
        short restingPrice = 100;
        ForwardOrderMessage restingOrder = new ForwardOrderMessage(UUID.randomUUID(), restingPlayerId, SubmitOrderMessage.OrderType.BUY, restingQty, restingPrice, symbol);
        Envelope<ForwardOrderMessage> restingOrderEnvelope = new Envelope<>(restingOrder, mock(InetSocketAddress.class));

        victim.handleOrder(restingOrderEnvelope);

        OrderConfirmationMessage expectedRestingPlayerOrderConfirmation = new OrderConfirmationMessage(UUID.randomUUID(), restingPlayerId, (short)0, (short)0, restingQty, restingPrice, symbol, restingOrder.getOrderType());

        verify(tcpCommunicator).send(expectedRestingPlayerOrderConfirmation);

        short incomingPlayerId = 2;
        short incomingQty = 5;
        short incomingPrice = 100;
        ForwardOrderMessage incomingOrder = new ForwardOrderMessage(UUID.randomUUID(), incomingPlayerId, SubmitOrderMessage.OrderType.SELL, incomingQty, incomingPrice, symbol);
        Envelope<ForwardOrderMessage> incomingOrderEnvelope = new Envelope<>(incomingOrder, mock(InetSocketAddress.class));

        victim.handleOrder(incomingOrderEnvelope);

        expectedRestingPlayerOrderConfirmation = new OrderConfirmationMessage(UUID.randomUUID(), restingPlayerId, (short)0, incomingQty, incomingQty, incomingPrice, symbol, SubmitOrderMessage.OrderType.BUY);
        OrderConfirmationMessage expectedIncomingPlayerOrderConfirmation = new OrderConfirmationMessage(UUID.randomUUID(), incomingPlayerId, (short)1, incomingQty, (short)0, incomingPrice, symbol, incomingOrder.getOrderType());

        verify(tcpCommunicator).send(expectedRestingPlayerOrderConfirmation);
        verify(tcpCommunicator).send(expectedIncomingPlayerOrderConfirmation);
    }
}
