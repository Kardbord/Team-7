package matchingengine;


import dispatcher.EnvelopeDispatcher;
import messages.*;
import communicators.*;
import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.Vector;

public class MatchingEngine {
    String symbol;
    short bidPrice;
    short bidQuantity;
    short askPrice;
    short askQuantity;
    short orderIdCounter;
    private final static int PORT = 2000;

    //TODO figure what we need the IP to be
    //private final static String IP = "127.0.0.1";
    private Socket socket;
    private TcpCommunicator tcpCommunicator;
    //sellers from cheapest to most expensive, buyers most expensive to cheapest
    private Vector<Order> buyers = new Vector<>();
    private Vector<Order> sellers = new Vector<>();

    public MatchingEngine(String symbol, short bidPrice){
        this.symbol=symbol;
        this.bidPrice=bidPrice;
        orderIdCounter=0;
        register();
        initMessageListener();
    }


    public void register(){
        RegisterMatchingEngineMessage registerMatchingEngineMessage = new RegisterMatchingEngineMessage(this.symbol);
        try {
            InetAddress address=InetAddress.getLocalHost();
            //TODO change address to IP
            socket=new Socket(address,PORT);
            tcpCommunicator=new TcpCommunicator(socket);
            tcpCommunicator.send(registerMatchingEngineMessage.encode());
            //TODO have a timeout to make this is more reliable
        }catch (Exception ex){
            System.out.println(ex);
        }
    }


    public void initMessageListener(){
        EnvelopeDispatcher<byte[]> envelopeDispatcher = new EnvelopeDispatcher<>(tcpCommunicator, Message::decode);
        envelopeDispatcher.registerForDispatch(TopOfBookRequestMessage.class, this::sendTopOfBook);
        envelopeDispatcher.registerForDispatch(ForwardOrderMessage.class, this::handleOrder);
        envelopeDispatcher.registerForDispatch(CancelOrderMessage.class, this::cancelOrder);
        new Thread(envelopeDispatcher).start();
    }

    private void cancelOrder(Envelope<CancelOrderMessage> envelope) {
        CancelOrderMessage cancelOrderMessage = envelope.getMessage();
        short orderId=cancelOrderMessage.getOrderId();
        short playerId=cancelOrderMessage.getPlayerId();
        short cancelledQuantity=0;
        boolean done=false;
        for(int i=0;i<sellers.size()&&done==false;i++){
            if(sellers.get(i).getPlayerId()==playerId&&sellers.get(i).getOrderID()==orderId){
                cancelledQuantity=sellers.get(i).getQuantity();
                sellers.remove(i);
                done=true;
            }
        }
        for(int i=0;i<buyers.size()&&done==false;i++){
            if(buyers.get(i).getPlayerId()==playerId&&buyers.get(i).getOrderID()==orderId){
                cancelledQuantity=buyers.get(i).getQuantity();
                buyers.remove(i);
                done=true;
            }
        }
        //TODO what message do we send if the order does not exist? or how do we format this message
        CancelConfirmationMessage cancelConfirmationMessage = new CancelConfirmationMessage(playerId,
                orderId,cancelledQuantity,this.symbol);
        try {
            tcpCommunicator.send(cancelConfirmationMessage.encode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTopOfBook(Envelope<TopOfBookRequestMessage> envelope) {
        TopOfBookRequestMessage topOfBookRequestMessage = envelope.getMessage();
        TopOfBookResponseMessage topOfBookResponseMessage=new TopOfBookResponseMessage(this.symbol,this.bidPrice,
                this.bidQuantity,this.askPrice,this.askQuantity);
        try {
            tcpCommunicator.send(topOfBookRequestMessage.encode());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleOrder(Envelope<ForwardOrderMessage> envelope) {
        ForwardOrderMessage forwardOrderMessage = envelope.getMessage();
        short orderPlayerId=forwardOrderMessage.getPlayerId();
        SubmitOrderMessage.OrderType orderOrderType=forwardOrderMessage.getOrderType();
        short orderQuantity= forwardOrderMessage.getQuantity();
        short orderPrice=forwardOrderMessage.getPrice();
        String orderSymbol=forwardOrderMessage.getSymbol();
        orderIdCounter++;
        short executedQty=0;
        short remainingOrderQuantity=orderQuantity;
        boolean orderComplete=false;
        if(orderOrderType == SubmitOrderMessage.OrderType.BUY){
            for(int i=0;i<sellers.size()&&!orderComplete;i++){
                if(orderPrice>=sellers.get(i).getPrice()){
                    if(sellers.get(i).getQuantity()==remainingOrderQuantity){
                        orderComplete=true;
                        OrderConfirmationMessage orderConfirmationMessage1=new OrderConfirmationMessage(
                                sellers.get(i).getPlayerId(),sellers.get(i).getOrderID(),
                                sellers.get(i).getQuantity(), (short)0,sellers.get(i).getPrice(),
                                sellers.get(i).getSymbol()
                        );
                        //TODO should this orderprice be sellers(i) order price?
                        OrderConfirmationMessage orderConfirmationMessage2=new OrderConfirmationMessage(
                                orderPlayerId, orderIdCounter,orderQuantity,(short)0,orderPrice,
                                orderSymbol);
                        try {
                            tcpCommunicator.send(orderConfirmationMessage1.encode());
                            tcpCommunicator.send(orderConfirmationMessage2.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sellers.remove(i);
                        i--;
                    }
                    else if(sellers.get(i).getQuantity()<remainingOrderQuantity){
                        OrderConfirmationMessage orderConfirmationMessage = new OrderConfirmationMessage(
                                sellers.get(i).getPlayerId(),sellers.get(i).getOrderID(),
                                sellers.get(i).getQuantity(),(short)0,sellers.get(i).getPrice(),orderSymbol
                        );
                        try {
                            tcpCommunicator.send(orderConfirmationMessage.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        executedQty+=sellers.get(i).getQuantity();
                        remainingOrderQuantity=(short)(remainingOrderQuantity-sellers.get(i).getQuantity());
                        sellers.remove(i);
                        i--;
                    }
                    else if(sellers.get(i).getQuantity()>remainingOrderQuantity){
                        orderComplete=true;
                        sellers.get(i).setQuantity((short)(sellers.get(i).getQuantity()-remainingOrderQuantity));
                        OrderConfirmationMessage orderConfirmationMessage1 = new OrderConfirmationMessage(
                                orderPlayerId,orderIdCounter,orderQuantity,(short)0,orderPrice,orderSymbol
                        );
                        OrderConfirmationMessage orderConfirmationMessage2 = new OrderConfirmationMessage(
                                sellers.get(i).getPlayerId(),sellers.get(i).getOrderID(),remainingOrderQuantity,
                                sellers.get(i).getQuantity(),sellers.get(i).getPrice(),orderSymbol
                        );
                        try {
                            tcpCommunicator.send(orderConfirmationMessage1.encode());
                            tcpCommunicator.send(orderConfirmationMessage2.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            //if the order didn't finish, add it to the resting book
            //TODO makers sure buyers resting book is ordered by price(greatest to least)
            if(orderComplete==false){
                Order order=new Order(orderPlayerId,orderOrderType,remainingOrderQuantity,orderPrice,
                        orderSymbol,orderIdCounter);
                buyers.add(order);
                Collections.sort(buyers, Collections.reverseOrder());
                OrderConfirmationMessage orderConfirmationMessage = new OrderConfirmationMessage(
                        orderPlayerId,orderIdCounter,executedQty,remainingOrderQuantity,orderPrice,orderSymbol
                );
            }

        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if(orderOrderType == SubmitOrderMessage.OrderType.SELL){
            for(int i=0;i<buyers.size()&&!orderComplete;i++){
                if(orderPrice<=buyers.get(i).getPrice()){
                    if(buyers.get(i).getQuantity()==remainingOrderQuantity){
                        orderComplete=true;
                        OrderConfirmationMessage orderConfirmationMessage1=new OrderConfirmationMessage(
                                buyers.get(i).getPlayerId(),buyers.get(i).getOrderID(),
                                buyers.get(i).getQuantity(), (short)0,buyers.get(i).getPrice(),
                                buyers.get(i).getSymbol()
                        );
                        OrderConfirmationMessage orderConfirmationMessage2=new OrderConfirmationMessage(
                                orderPlayerId, orderIdCounter,orderQuantity,(short)0,orderPrice,
                                orderSymbol);
                        try {
                            tcpCommunicator.send(orderConfirmationMessage1.encode());
                            tcpCommunicator.send(orderConfirmationMessage2.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        buyers.remove(i);
                        i--;
                    }
                    else if(buyers.get(i).getQuantity()<remainingOrderQuantity){
                        OrderConfirmationMessage orderConfirmationMessage = new OrderConfirmationMessage(
                                buyers.get(i).getPlayerId(),buyers.get(i).getOrderID(),
                                buyers.get(i).getQuantity(),(short)0,buyers.get(i).getPrice(),orderSymbol
                        );
                        try {
                            tcpCommunicator.send(orderConfirmationMessage.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        executedQty+=buyers.get(i).getQuantity();
                        remainingOrderQuantity=(short)(remainingOrderQuantity-buyers.get(i).getQuantity());
                        buyers.remove(i);
                        i--;
                    }
                    else if(buyers.get(i).getQuantity()>remainingOrderQuantity){
                        orderComplete=true;
                        buyers.get(i).setQuantity((short)(buyers.get(i).getQuantity()-remainingOrderQuantity));
                        OrderConfirmationMessage orderConfirmationMessage1 = new OrderConfirmationMessage(
                                orderPlayerId,orderIdCounter,orderQuantity,(short)0,orderPrice,orderSymbol
                        );
                        OrderConfirmationMessage orderConfirmationMessage2 = new OrderConfirmationMessage(
                                buyers.get(i).getPlayerId(),buyers.get(i).getOrderID(),remainingOrderQuantity,
                                buyers.get(i).getQuantity(),buyers.get(i).getPrice(),orderSymbol
                        );
                        try {
                            tcpCommunicator.send(orderConfirmationMessage1.encode());
                            tcpCommunicator.send(orderConfirmationMessage2.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            //if the order didn't finish, add it to the resting book
            if(orderComplete==false){
                Order order=new Order(orderPlayerId,orderOrderType,remainingOrderQuantity,orderPrice,
                        orderSymbol,orderIdCounter);
                sellers.add(order);
                Collections.sort(sellers);
                OrderConfirmationMessage orderConfirmationMessage = new OrderConfirmationMessage(
                        orderPlayerId,orderIdCounter,executedQty,remainingOrderQuantity,orderPrice,orderSymbol
                );
            }
        }
    }







    public static void main(String[] args) {
        MatchingEngine matchingEngine=new MatchingEngine("GOOG",(short)45);
    }

}