package matchingengine;

import messages.SubmitOrderMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderTest {

    @Test
    public void createOrderTest(){
        short playerId = 1909;
        SubmitOrderMessage.OrderType orderType = SubmitOrderMessage.OrderType.SELL;
        short quantity = 3;
        int price = 210;
        String symbol = "GOOG";
        short orderID = 31;
        Order order = new Order(playerId, orderType, quantity, price, symbol, orderID);
        assertEquals(playerId,order.getPlayerId());
        assertEquals(orderType,order.getOrderType());
        assertEquals(quantity,order.getQuantity());
        assertEquals(price,order.getPrice());
        assertEquals(symbol,order.getSymbol());
        assertEquals(orderID,order.getOrderID());
    }

    @Test
    public void setValuesTest(){
        short playerId = 1909;
        SubmitOrderMessage.OrderType orderType = SubmitOrderMessage.OrderType.SELL;
        short quantity = 3;
        int price = 210;
        String symbol = "GOOG";
        short orderID = 31;
        Order order = new Order(playerId, orderType, quantity, price, symbol, orderID);
        order.setPlayerId((short)3);
        order.setOrderType(SubmitOrderMessage.OrderType.BUY);
        order.setQuantity((short)14);
        order.setPrice((short)21);
        order.setSymbol("AAPL");
        order.setOrderID((short)1);

        assertEquals((short)3,order.getPlayerId());
        assertEquals(SubmitOrderMessage.OrderType.BUY,order.getOrderType());
        assertEquals((short)14,order.getQuantity());
        assertEquals((short)21,order.getPrice());
        assertEquals("AAPL",order.getSymbol());
        assertEquals((short)1,order.getOrderID());
    }


}