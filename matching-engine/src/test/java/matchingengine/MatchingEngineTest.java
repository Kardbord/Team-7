package matchingengine;

import communicators.UdpCommunicator;
import gateway.Gateway;
import messages.ForwardCancelMessage;
import messages.ForwardOrderMessage;
import messages.SubmitOrderMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class MatchingEngineTest {
    String symbol="GOOG";
    short bidPrice =1;
    short askPrice =1;
    MatchingEngine victim = null;
    Gateway gateway = null;

    //Tests cannot be run concurrently at the moment, they have to be run individually

    @Before
    public void setUp() {
        try {
            this.gateway = new Gateway(new UdpCommunicator(
                    DatagramChannel.open(),
                    new InetSocketAddress("0.0.0.0", Gateway.PORT)
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        victim=new MatchingEngine(symbol,bidPrice,askPrice);
    }

    @After
    public void cleanUp(){
        gateway=null;
        victim=null;
    }

    @Test
    public void createMatchingEngineTest() {
        assertEquals(this.symbol,victim.symbol);
        assertEquals(bidPrice,victim.bidPrice);
        assertEquals(askPrice,victim.askPrice);
    }


    @Test
    public void handleOrderTest(){
        //sellers should be empty to start with
        assertEquals(0,victim.restingBook.sellers.size());
        ForwardOrderMessage forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.SELL, (short)3,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        //sellers should now contain 1
        assertEquals(1,victim.restingBook.sellers.size());


//        (playerId, orderType, quantity, price, symbol)

        assertEquals(0,victim.restingBook.buyers.size());
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.BUY, (short)3,(short)7,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        //sellers should now contain 1


        ///////////equal quantities and a match
        assertEquals(1,victim.restingBook.buyers.size());
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2, SubmitOrderMessage.OrderType.SELL,
                (short)3,(short)7,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);

        //A match should have occured, reducing buyers.size back down to zero
        assertEquals(0,victim.restingBook.buyers.size());


        ////////different order quantities and a match
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.BUY, (short)2,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);

        //sellers should still have one in it
        assertEquals(1,victim.restingBook.sellers.size());
        //that ones quanitity should now be reduced to 1
        assertEquals(1,victim.restingBook.sellers.get(0).getQuantity());



        //////////multiple orders matching to one order
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.BUY, (short)1,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);

        ///^that just got rid of the remaining buy order
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.BUY, (short)1,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.BUY, (short)1,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.BUY, (short)1,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        ///now 3 buy order of quantity one should match with one sell of quantity 4
        assertEquals(victim.restingBook.buyers.size(),3);
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.SELL, (short)4,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        assertEquals(victim.restingBook.sellers.size(),1);
        assertEquals(victim.restingBook.buyers.size(),0);
    }

    @Test
    public void cancelOrderTest(){
        ForwardOrderMessage forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.SELL, (short)3,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        assertEquals(1,victim.restingBook.sellers.size());
        ForwardCancelMessage cancelOrderMessage = new ForwardCancelMessage(UUID.randomUUID(), (short)2,(short)3,"GOOG");
        victim.cancelOrderTester(cancelOrderMessage);
        //Nothing should have changed as wrong ID was provided
        assertEquals(1,victim.restingBook.sellers.size());
        cancelOrderMessage = new ForwardCancelMessage(UUID.randomUUID(), (short)2,(short)1,"GOOG");
        victim.cancelOrderTester(cancelOrderMessage);
        //correct ID was provided, now sellers.size should be 0
        assertEquals(0,victim.restingBook.sellers.size());


    }

    @Test
    public void topOfBookTest(){
        assertEquals(1,victim.askPrice);
        assertEquals(1,victim.bidPrice);
        ForwardOrderMessage forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.SELL, (short)3,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        //sleep for a while to make sure gateway has refreshed top of book
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(10,victim.askPrice);
        assertEquals(1,victim.bidPrice);
        forwardOrderMessage = new ForwardOrderMessage(UUID.randomUUID(), (short)2,
                SubmitOrderMessage.OrderType.BUY, (short)3,(short)10,"GOOG");
        victim.orderHandler.handleOrder(forwardOrderMessage);
        //sleep for a while to make sure gateway has refreshed top of book
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1,victim.askPrice);
        assertEquals(1,victim.bidPrice);
    }







}
