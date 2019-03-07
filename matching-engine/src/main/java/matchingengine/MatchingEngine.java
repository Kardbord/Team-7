package matchingengine;


import dispatcher.EnvelopeDispatcher;
import messages.*;
import communicators.*;
import java.io.*;
import java.net.*;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
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

    //private HashMap orders = new HashMap(100);




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
        short restingQty=orderQuantity;
        //keeps track of who needs a message
        short[] involvedPlayerIds=new short[orderQuantity];
        int numPlayerIds=0;
        if(orderOrderType == SubmitOrderMessage.OrderType.BUY){
            boolean orderComplete=false;
            for(int i=0;i<sellers.size()&&orderComplete==false;i++){
                //starting at cheapest price, if we match, go through as many in the order quantity as possible
                if(orderPrice==sellers.get(i).getPrice()){
                    involvedPlayerIds[numPlayerIds]=sellers.get(i).getPlayerId();
                    numPlayerIds++;
                    //if seller order has higher quantity, decrement with the amount of the order, complete the
                    //order, and send your messages
                    if(sellers.get(i).getQuantity()>orderQuantity){
                        short sellerQuantity=sellers.get(i).getQuantity();
                        sellers.get(i).setQuantity((short)(sellerQuantity-orderQuantity));
                        executedQty=orderQuantity;
                        restingQty=0;
                        orderComplete=true;
                        //TODO order messages need to be redone for ids,executed quantity,resting qty,
                        OrderConfirmationMessage orderConfirmationMessage=new OrderConfirmationMessage(orderPlayerId,sellers.get(i).getPlayerId(),orderIdCounter,executedQty,restingQty,orderPrice,orderSymbol);
                        try {
                            tcpCommunicator.send(orderConfirmationMessage.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //if seller order quantity is less than orderQuantity, use whole quantity of seller i,
                    //and move on to next seller.
                    else if(sellers.get(i).getQuantity()<orderQuantity){
                        executedQty+=sellers.get(i).getQuantity();
                        restingQty-=sellers.get(i).getQuantity();
                        orderQuantity=(short)(orderQuantity-sellers.get(i).getQuantity());
                        //sellers.get(i).setQuantity((short)0);
                        sellers.remove(i);
                        i--;
                    }
                    else if(sellers.get(i).getQuantity()==orderQuantity){
                        executedQty+=orderQuantity;
                        restingQty=0;
                        sellers.remove(i);
                        i--;
                        orderComplete=true;
                        OrderConfirmationMessage orderConfirmationMessage=new OrderConfirmationMessage(orderPlayerId,sellers.get(i).getPlayerId(),orderIdCounter,executedQty,restingQty,orderPrice,orderSymbol);
                        try {
                            tcpCommunicator.send(orderConfirmationMessage.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }else if (orderOrderType == SubmitOrderMessage.OrderType.SELL){
            boolean orderComplete=false;
            for(int i=0;i<buyers.size()&&orderComplete==false;i++){
                //starting at cheapest price, if we match, go through as many in the order quantity as possible
                if(orderPrice==buyers.get(i).getPrice()){
                    involvedPlayerIds[numPlayerIds]=buyers.get(i).getPlayerId();
                    numPlayerIds++;
                    //if seller order has higher quantity, decrement with the amount of the order, complete the
                    //order, and send your messages
                    if(buyers.get(i).getQuantity()>orderQuantity){
                        short sellerQuantity=buyers.get(i).getQuantity();
                        buyers.get(i).setQuantity((short)(sellerQuantity-orderQuantity));
                        executedQty=orderQuantity;
                        restingQty=0;
                        orderComplete=true;
                        OrderConfirmationMessage orderConfirmationMessage=new OrderConfirmationMessage(
                                buyers.get(i).getPlayerId(),orderPlayerId,orderIdCounter
                                ,executedQty,restingQty,orderPrice,orderSymbol);
                        try {
                            tcpCommunicator.send(orderConfirmationMessage.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //if seller order quantity is less than orderQuantity, use whole quantity of seller i,
                    //and move on to next seller.
                    else if(buyers.get(i).getQuantity()<orderQuantity){
                        executedQty+=buyers.get(i).getQuantity();
                        restingQty-=buyers.get(i).getQuantity();
                        orderQuantity=(short)(orderQuantity-buyers.get(i).getQuantity());
                        //sellers.get(i).setQuantity((short)0);
                        buyers.remove(i);
                        i--;
                    }
                    else if(buyers.get(i).getQuantity()==orderQuantity){
                        executedQty+=orderQuantity;
                        restingQty=0;
                        buyers.remove(i);
                        i--;
                        orderComplete=true;
                        OrderConfirmationMessage orderConfirmationMessage=new OrderConfirmationMessage(
                                buyers.get(i).getPlayerId(),orderPlayerId,orderIdCounter
                                ,executedQty,restingQty,orderPrice,orderSymbol);
                        try {
                            tcpCommunicator.send(orderConfirmationMessage.encode());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        }
        //if there is still more quantity, add the order to the resting book
        if (orderQuantity>0){
            Order order=new Order(orderPlayerId, orderOrderType, orderQuantity, orderPrice, orderSymbol,orderIdCounter);
            if(orderOrderType== SubmitOrderMessage.OrderType.SELL){
                sellers.add(order);
                //TODO sort all orders
            }
            else if (orderOrderType== SubmitOrderMessage.OrderType.BUY){
                buyers.add(order);
                //TODO sort all orders
            }
            OrderConfirmationMessage orderConfirmationMessage=new OrderConfirmationMessage(orderPlayerId,(short)-1,orderIdCounter,executedQty,restingQty,orderPrice,orderSymbol);
            try {
                tcpCommunicator.send(orderConfirmationMessage.encode());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //TODO Send confirmation messages
        //TODO we may need a redo in structure here...
//        Order Confirmation: When an order does not match, the ME sends an Order Confirmation
//        Message with executedQty = 0 and fills in one of the PlayerIDs with a reserved playerID
//        (something like 0 or -1)



    }





    public static void main(String[] args) {
        MatchingEngine matchingEngine=new MatchingEngine("GOOG",(short)45);

    }

    }
