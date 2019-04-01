package matchingengine;

import communicators.TcpCommunicator;
import messages.ForwardOrderMessage;
import messages.OrderConfirmationMessage;
import messages.SubmitOrderMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;

public class OrderHandler {
    short orderIdCounter;
    private RestingBook restingBook;
    private String symbol;
    private TcpCommunicator tcpCommunicator;
    private static final Logger LOG = LogManager.getFormatterLogger(MatchingEngine.class.getName());
    public OrderHandler(RestingBook restingBook,String symbol,TcpCommunicator tcpCommunicator){
        this.restingBook=restingBook;
        this.symbol = symbol;
        this.tcpCommunicator=tcpCommunicator;
        orderIdCounter=0;
    }
    public void handleOrder(ForwardOrderMessage forwardOrderMessage) {
        short orderPlayerId=forwardOrderMessage.getPlayerId();
        SubmitOrderMessage.OrderType orderOrderType=forwardOrderMessage.getOrderType();
        short orderQuantity= forwardOrderMessage.getQuantity();
        int orderPrice=forwardOrderMessage.getPrice();
        String orderSymbol=forwardOrderMessage.getSymbol();
        orderIdCounter++;
        short executedQty=0;
        short remainingOrderQuantity=orderQuantity;
        boolean orderComplete=false;
        if(orderOrderType == SubmitOrderMessage.OrderType.BUY){
            for(int i=0;i<restingBook.sellers.size()&&!orderComplete;i++){
                if(orderPrice>=restingBook.sellers.get(i).getPrice()){
                    if(restingBook.sellers.get(i).getQuantity()==remainingOrderQuantity){
                        orderComplete=true;
                        OrderConfirmationMessage orderConfirmationMessage1=new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                restingBook.sellers.get(i).getPlayerId(),restingBook.sellers.get(i).getOrderID(),
                                restingBook.sellers.get(i).getQuantity(), (short)0,restingBook.sellers.get(i).getPrice(),
                                restingBook.sellers.get(i).getSymbol()
                        );
                        OrderConfirmationMessage orderConfirmationMessage2=new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                orderPlayerId, orderIdCounter,orderQuantity,(short)0,
                                restingBook.sellers.get(i).getPrice(), orderSymbol);
                        try {
                            tcpCommunicator.send(orderConfirmationMessage1.encode());
                            tcpCommunicator.send(orderConfirmationMessage2.encode());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol, restingBook.sellers.get(i).getPlayerId());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol, orderPlayerId);
                        } catch (IOException e) {
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                    "to Gateway for player ID %d", symbol, orderPlayerId, e.getMessage());
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                            "to Gateway for player ID %d", symbol, restingBook.sellers.get(i).getPlayerId(),
                                    e.getMessage());
                        }
                        restingBook.sellers.remove(i);
                        i--;
                    }
                    else if(restingBook.sellers.get(i).getQuantity()<remainingOrderQuantity){
                        OrderConfirmationMessage orderConfirmationMessage = new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                restingBook.sellers.get(i).getPlayerId(),restingBook.sellers.get(i).getOrderID(),
                                restingBook.sellers.get(i).getQuantity(),(short)0,restingBook.sellers.get(i).getPrice(),orderSymbol
                        );
                        try {
                            tcpCommunicator.send(orderConfirmationMessage.encode());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol, restingBook.sellers.get(i).getPlayerId());
                        } catch (IOException e) {
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                            "to Gateway for player ID %d", symbol, restingBook.sellers.get(i).getPlayerId(),
                                    e.getMessage());
                        }
                        executedQty+=restingBook.sellers.get(i).getQuantity();
                        remainingOrderQuantity=(short)(remainingOrderQuantity-restingBook.sellers.get(i).getQuantity());
                        restingBook.sellers.remove(i);
                        i--;
                    }
                    else if(restingBook.sellers.get(i).getQuantity()>remainingOrderQuantity){
                        orderComplete=true;
                        restingBook.sellers.get(i).setQuantity((short)(restingBook.sellers.get(i).getQuantity()-remainingOrderQuantity));
                        OrderConfirmationMessage orderConfirmationMessage1 = new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                orderPlayerId,orderIdCounter,orderQuantity,(short)0,
                                restingBook.sellers.get(i).getPrice(),orderSymbol
                        );
                        OrderConfirmationMessage orderConfirmationMessage2 = new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                restingBook.sellers.get(i).getPlayerId(),restingBook.sellers.get(i).getOrderID(),remainingOrderQuantity,
                                restingBook.sellers.get(i).getQuantity(),restingBook.sellers.get(i).getPrice(),orderSymbol
                        );
                        try {
                            tcpCommunicator.send(orderConfirmationMessage1.encode());
                            tcpCommunicator.send(orderConfirmationMessage2.encode());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol, restingBook.sellers.get(i).getPlayerId());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol, orderPlayerId);
                        } catch (IOException e) {
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                    "to Gateway for player ID %d", symbol, orderPlayerId, e.getMessage());
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                            "to Gateway for player ID %d", symbol, restingBook.sellers.get(i).getPlayerId(),
                                    e.getMessage());
                        }
                    }
                }

            }
            //if the order didn't finish, add it to the resting book
            if(orderComplete==false){
                Order order=new Order(orderPlayerId,orderOrderType,remainingOrderQuantity,orderPrice,
                        orderSymbol,orderIdCounter);
                restingBook.buyers.add(order);
                Collections.sort(restingBook.buyers, Collections.reverseOrder());
                OrderConfirmationMessage orderConfirmationMessage = new OrderConfirmationMessage(
                        forwardOrderMessage.getConversationId(),
                        orderPlayerId,orderIdCounter,executedQty,remainingOrderQuantity,orderPrice,orderSymbol
                );
                try {
                    tcpCommunicator.send(orderConfirmationMessage.encode());
                    LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d", symbol,
                            orderPlayerId);
                } catch (IOException e) {
                    LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                            "to Gateway for player ID %d", symbol, orderPlayerId, e.getMessage());
                }
            }

        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if(orderOrderType == SubmitOrderMessage.OrderType.SELL){
            for(int i=0;i<restingBook.buyers.size()&&!orderComplete;i++){
                if(orderPrice<=restingBook.buyers.get(i).getPrice()){
                    if(restingBook.buyers.get(i).getQuantity()==remainingOrderQuantity){
                        orderComplete=true;
                        OrderConfirmationMessage orderConfirmationMessage1=new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                restingBook.buyers.get(i).getPlayerId(),restingBook.buyers.get(i).getOrderID(),
                                restingBook.buyers.get(i).getQuantity(), (short)0,orderPrice,
                                restingBook.buyers.get(i).getSymbol()
                        );
                        //on this one we're keeping it with orderPrice, as the sale price should always be what it goes for
                        OrderConfirmationMessage orderConfirmationMessage2=new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                orderPlayerId, orderIdCounter,orderQuantity,(short)0,orderPrice,
                                orderSymbol);
                        try {
                            tcpCommunicator.send(orderConfirmationMessage1.encode());
                            tcpCommunicator.send(orderConfirmationMessage2.encode());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol,restingBook.buyers.get(i).getPlayerId());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol,orderPlayerId);
                        } catch (IOException e) {
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                    "to Gateway for player ID %d", symbol, orderPlayerId, e.getMessage());
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                            "to Gateway for player ID %d", symbol, restingBook.buyers.get(i).getPlayerId(),
                                    e.getMessage());
                        }
                        restingBook.buyers.remove(i);
                        i--;
                    }
                    else if(restingBook.buyers.get(i).getQuantity()<remainingOrderQuantity){
                        //similarly, this one should now be orderPrice
                        OrderConfirmationMessage orderConfirmationMessage = new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                restingBook.buyers.get(i).getPlayerId(),restingBook.buyers.get(i).getOrderID(),
                                restingBook.buyers.get(i).getQuantity(),(short)0,orderPrice,orderSymbol
                        );
                        try {
                            tcpCommunicator.send(orderConfirmationMessage.encode());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol,restingBook.buyers.get(i).getPlayerId());
                        } catch (IOException e) {
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                            "to Gateway for player ID %d", symbol, restingBook.buyers.get(i).getPlayerId(),
                                    e.getMessage());
                        }
                        executedQty+=restingBook.buyers.get(i).getQuantity();
                        remainingOrderQuantity=(short)(remainingOrderQuantity-restingBook.buyers.get(i).getQuantity());
                        restingBook.buyers.remove(i);
                        i--;
                    }
                    else if(restingBook.buyers.get(i).getQuantity()>remainingOrderQuantity){
                        orderComplete=true;
                        restingBook.buyers.get(i).setQuantity((short)(restingBook.buyers.get(i).getQuantity()-remainingOrderQuantity));
                        OrderConfirmationMessage orderConfirmationMessage1 = new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                orderPlayerId,orderIdCounter,orderQuantity,(short)0,orderPrice,orderSymbol
                        );
                        OrderConfirmationMessage orderConfirmationMessage2 = new OrderConfirmationMessage(
                                forwardOrderMessage.getConversationId(),
                                restingBook.buyers.get(i).getPlayerId(),restingBook.buyers.get(i).getOrderID(),remainingOrderQuantity,
                                restingBook.buyers.get(i).getQuantity(),orderPrice,orderSymbol
                        );
                        try {
                            tcpCommunicator.send(orderConfirmationMessage1.encode());
                            tcpCommunicator.send(orderConfirmationMessage2.encode());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol,restingBook.buyers.get(i).getPlayerId());
                            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d",
                                    symbol,orderPlayerId);
                        } catch (IOException e) {
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                    "to Gateway for player ID %d", symbol, orderPlayerId, e.getMessage());
                            LOG.error("Failure in %s while trying to send OrderConfirmationMessage " +
                                            "to Gateway for player ID %d", symbol, restingBook.buyers.get(i).getPlayerId(),
                                    e.getMessage());
                        }
                    }
                }

            }
            //if the order didn't finish, add it to the resting book
            if(orderComplete==false){
                Order order=new Order(orderPlayerId,orderOrderType,remainingOrderQuantity,orderPrice,
                        orderSymbol,orderIdCounter);
                restingBook.sellers.add(order);
                Collections.sort(restingBook.sellers);
                OrderConfirmationMessage orderConfirmationMessage = new OrderConfirmationMessage(
                        forwardOrderMessage.getConversationId(),
                        orderPlayerId,orderIdCounter,executedQty,remainingOrderQuantity,orderPrice,orderSymbol
                );
                try {
                    tcpCommunicator.send(orderConfirmationMessage.encode());
                    LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d", symbol,
                            orderPlayerId);
                } catch (IOException e) {
                    LOG.error("Failure in %s ME while trying to send OrderConfirmationMessage " +
                            "to Gateway for player ID %d", symbol, orderPlayerId, e.getMessage());
                }
            }
        }
    }
}
