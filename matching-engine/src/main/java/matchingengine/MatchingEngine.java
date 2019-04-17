package matchingengine;


import communicators.Envelope;
import communicators.TcpCommunicator;
import messages.*;
import messages.SubmitOrderMessage.OrderType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class MatchingEngine {
    private TreeSet<Order> asks = new TreeSet<>(Comparator.reverseOrder()); // sorted from high to low
    private TreeSet<Order> bids = new TreeSet<>((o1, o2) -> {
        if(o1.getPrice() == o2.getPrice()){
            return o2.getOrderID() - o1.getOrderID();
        }

        return o1.getPrice() - o2.getPrice();
    }); // sorted from low to high
    private Map<Short, Order> orderIdToRestingOrderMap = new HashMap<>();

    private String symbol;
    private short orderIdCounter;
    private final static int PORT = 2000;
    private static final Logger LOG = LogManager.getFormatterLogger(MatchingEngine.class.getName());
    private TcpCommunicator tcpCommunicator;

    public MatchingEngine(String symbol, TcpCommunicator tcpCommunicator){
        this.symbol = symbol;
        this.tcpCommunicator = tcpCommunicator;
        initMessageListeners();
        register();
    }

    public void register(){
        RegisterMatchingEngineMessage registerMatchingEngineMessage = new RegisterMatchingEngineMessage(this.symbol);
        try {
            tcpCommunicator.sendReliably(registerMatchingEngineMessage, AckMessage.class, 10, 1000);
            LOG.info("%s sent registerMatchingEngineMessage to Gateway",this.symbol);
        }catch (IOException e) {
            LOG.error("Failure in %s constructor, couldn't send registerMatchingEngineMessage",
                    symbol, e.getMessage());
        }
    }

    public void initMessageListeners(){
        tcpCommunicator.registerForDispatch(TopOfBookRequestMessage.class, this::sendTopOfBook);
        LOG.info("%s Initialized TopOfBookRequestMessage listener",this.symbol);
        tcpCommunicator.registerForDispatch(ForwardOrderMessage.class, this::handleOrder);
        LOG.info("%s Initialized ForwardOrderMessage listener",this.symbol);
        tcpCommunicator.registerForDispatch(ForwardCancelMessage.class, this::cancelOrder);
        LOG.info("%s Initialized ForwardCancelMessage listener",this.symbol);
        new Thread(tcpCommunicator).start();
    }

    private synchronized void cancelOrder(Envelope<ForwardCancelMessage> envelope) {
        ForwardCancelMessage cancelOrderMessage = envelope.getMessage();
        short orderId=cancelOrderMessage.getOrderId();
        short playerId=cancelOrderMessage.getPlayerId();
        short cancelledQuantity = 0;

        if(orderIdToRestingOrderMap.containsKey(orderId)){
            Order restingOrderToCancel = orderIdToRestingOrderMap.get(orderId);
            bids.remove(restingOrderToCancel);
            asks.remove(restingOrderToCancel);
            cancelledQuantity = restingOrderToCancel.getQuantity();
            orderIdToRestingOrderMap.remove(orderId);
        }

        LOG.info("%s received ForwardCancelMessage from gateway for order %d from player %d ", symbol, orderId, playerId);

        CancelConfirmationMessage cancelConfirmationMessage = new CancelConfirmationMessage(
                cancelOrderMessage.getConversationId(),
                playerId,
                orderId,
                cancelledQuantity,
                symbol
            );
        try {
            tcpCommunicator.send(cancelConfirmationMessage);
            LOG.info("%s sent cancelConfirmationMessage to Gateway for order %d", symbol, orderId);
        } catch (IOException e) {
            LOG.error("Failure in %s ME while trying to send cancelConfirmationMessage to Gateway",
                    symbol, e.getMessage());
        }
    }

    private synchronized void sendTopOfBook(Envelope<TopOfBookRequestMessage> envelope) {
        int askPrice = !asks.isEmpty() ? asks.last().getPrice() : 0;
        int bidPrice = !bids.isEmpty() ? bids.last().getPrice() : 0;

        TopOfBookRequestMessage msg = envelope.getMessage();

        List<TopOfBookResponseMessage.PriceQuantityPair> asksList = new ArrayList<>();
        asksList.add(new TopOfBookResponseMessage.PriceQuantityPair(askPrice, (short)0));

        Iterator<Order> askIter = asks.descendingIterator();
        while(askIter.hasNext() && asksList.size() < 5){
            Order restingOrder = askIter.next();
            if(restingOrder.getPrice() == askPrice){
                asksList.get(asksList.size()-1).qty += restingOrder.getQuantity();
            }else{
                askPrice = restingOrder.getPrice();
                asksList.add(new TopOfBookResponseMessage.PriceQuantityPair(askPrice, restingOrder.getQuantity()));
            }
        }

        List<TopOfBookResponseMessage.PriceQuantityPair> bidsList = new ArrayList<>();
        bidsList.add(new TopOfBookResponseMessage.PriceQuantityPair(bidPrice, (short)0));

        Iterator<Order> bidIter = bids.descendingIterator();
        while(bidIter.hasNext() && bidsList.size() < 5){
            Order restingOrder = bidIter.next();
            if(restingOrder.getPrice() == bidPrice){
                bidsList.get(bidsList.size()-1).qty += restingOrder.getQuantity();
            }else{
                bidPrice = restingOrder.getPrice();
                bidsList.add(new TopOfBookResponseMessage.PriceQuantityPair(bidPrice, restingOrder.getQuantity()));
            }
        }

        TopOfBookResponseMessage topOfBookResponseMessage = new TopOfBookResponseMessage(
                msg.getConversationId(), symbol, asksList, bidsList);
        try {
            tcpCommunicator.send(topOfBookResponseMessage);
            LOG.info("%s sent topOfBookResponseMessage to Gateway",this.symbol);
        } catch (IOException e) {
            LOG.error("Failure in %s while trying to send topOfBookResponseMessage to Gateway",
                    symbol, e.getMessage());
        }
    }

    synchronized void handleOrder(Envelope<ForwardOrderMessage> envelope){
        LOG.info("Received ForwardOrderMessage from Gateway for player ID %d",
                envelope.getMessage().getPlayerId());

        ForwardOrderMessage msg = envelope.getMessage();

        TreeSet<Order> orderBookToQuoteOn = new TreeSet<>();
        TreeSet<Order> orderBookToExecuteAgainst = new TreeSet<>();

        if(msg.getOrderType() == OrderType.SELL){
            orderBookToQuoteOn = asks;
            orderBookToExecuteAgainst = bids;
        }else if(msg.getOrderType() == OrderType.BUY){
            orderBookToQuoteOn = bids;
            orderBookToExecuteAgainst = asks;
        }

        short remainingQty = msg.getQuantity();

        while(remainingQty > 0 && !orderBookToExecuteAgainst.isEmpty() && isMatchableOrder(orderBookToExecuteAgainst.last(), msg)){
            Order restingOrder = orderBookToExecuteAgainst.last();
            short executedQty = restingOrder.executeQty(remainingQty);

            OrderConfirmationMessage restingOrderConfirmation = new OrderConfirmationMessage(
                    UUID.randomUUID(),
                    restingOrder.getPlayerId(),
                    restingOrder.getOrderID(),
                    executedQty,
                    restingOrder.getQuantity(),
                    msg.getPrice(), // price improvement (matching at submitted price rather than resting price to accommodate lapse in protocol)
                    restingOrder.getSymbol(),
                    restingOrder.getOrderType()
            );
            try {
                tcpCommunicator.send(restingOrderConfirmation);
                LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d", symbol, restingOrder.getPlayerId());
            } catch (IOException e) {
                LOG.info("Failed to send order confirmation to Player ID %d. Resting Order: %s ", restingOrder.getPlayerId(), restingOrder);
                e.printStackTrace();
            }

            if(restingOrder.getQuantity() == 0){
                orderBookToExecuteAgainst.pollLast();
                orderIdToRestingOrderMap.remove(restingOrder.getOrderID());
            }

            remainingQty -= executedQty;
        }

        Order newRestingOrder = new Order(msg.getPlayerId(), msg.getOrderType(), remainingQty, msg.getPrice(), msg.getSymbol(), orderIdCounter++);

        if(remainingQty > 0){
            orderBookToQuoteOn.add(newRestingOrder);
            orderIdToRestingOrderMap.put(newRestingOrder.getOrderID(), newRestingOrder);
        }

        OrderConfirmationMessage restingOrderConfirmation = new OrderConfirmationMessage(
                msg.getConversationId(),
                msg.getPlayerId(),
                newRestingOrder.getOrderID(),
                (short) (msg.getQuantity() - remainingQty),
                newRestingOrder.getQuantity(),
                msg.getPrice(),
                newRestingOrder.getSymbol(),
                msg.getOrderType()
        );
        try {
            tcpCommunicator.send(restingOrderConfirmation);
            LOG.info("%s sent OrderConfirmationMessage to Gateway for player ID %d", symbol, msg.getPlayerId());
        } catch (IOException e) {
            LOG.info("Failed to send order confirmation to Player ID %d. Resting Order: %s ", newRestingOrder.getPlayerId(), newRestingOrder);
            e.printStackTrace();
        }
    }

    private boolean isMatchableOrder(Order restingOrder, ForwardOrderMessage msg) {
        if(msg.getOrderType() == OrderType.SELL){
            return restingOrder.getPrice() >= msg.getPrice();
        }else if(msg.getOrderType() == OrderType.BUY){
            return restingOrder.getPrice() <= msg.getPrice();
        }

        return false;
    }

    public static void main(String[] args) throws UnknownHostException {
        // TODO: change this string to the appropriate AWS public DNS
//        String address = "ec2-34-216-105-242.us-west-2.compute.amazonaws.com";
        //TODO change address to address above if using AWS
        InetAddress address = InetAddress.getLocalHost();
        Socket socket = null;
        while(socket == null || !socket.isConnected()) {
            try {
                socket = new Socket(address, PORT);
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        TcpCommunicator tcpCommunicator=new TcpCommunicator(socket);
        MatchingEngine matchingEngine=new MatchingEngine(args.length > 0 ? args[0] : "GOOG", tcpCommunicator);
    }
}
