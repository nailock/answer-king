import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.service.ItemService;
import answer.king.service.OrderService;


public class OrderServiceTest extends GenericServiceTest {
    
    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Test
    public void testSaveOrder() {
        Order testorder = new Order();
        testorder.setId(1L);
        testorder.setPaid(false);

        Order returnedOrder = orderService.save(testorder);

        Assert.assertEquals(testorder.getId(), returnedOrder.getId());
        Assert.assertEquals(testorder.getPaid(), returnedOrder.getPaid());
    }
    
    @Test
    public void testAddItem() {
        // Item first
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        Item returnedItem = itemService.save(testitem);

        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        Order testorder = new Order();
        testorder.setId(1L);
        testorder.setPaid(false);

        Order returnedOrder = orderService.save(testorder);

        Assert.assertEquals(testorder.getId(), returnedOrder.getId());
        Assert.assertEquals(testorder.getPaid(), returnedOrder.getPaid());

        orderService.addItem(returnedOrder.getId(), returnedItem.getId(), 1);
        
        Order fetchedOrder = orderService.getOrder(returnedOrder.getId());

        Assert.assertEquals(1, fetchedOrder.getItems().size());

    }

    @Test
    public void testAddItem_duplicateItem() {
        // Item first
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        Item returnedItem = itemService.save(testitem);

        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        Order testorder = new Order();
        testorder.setId(1L);
        testorder.setPaid(false);

        Order returnedOrder = orderService.save(testorder);

        Assert.assertEquals(testorder.getId(), returnedOrder.getId());
        Assert.assertEquals(testorder.getPaid(), returnedOrder.getPaid());

        orderService.addItem(returnedOrder.getId(), returnedItem.getId(), 1);
        
        Order fetchedOrder = orderService.getOrder(returnedOrder.getId());

        Assert.assertEquals(1, fetchedOrder.getItems().size());

        orderService.addItem(returnedOrder.getId(), returnedItem.getId(), 2);

        fetchedOrder = orderService.getOrder(returnedOrder.getId());

        Assert.assertEquals(1, fetchedOrder.getItems().size());
        Assert.assertEquals(3, fetchedOrder.getItems().get(0).getQuantity());

    }

    @Test
    public void testPayment() {
        // Item first
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        Item returnedItem = itemService.save(testitem);

        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        Order testorder = new Order();
        testorder.setId(1L);
        testorder.setPaid(false);

        Order returnedOrder = orderService.save(testorder);

        Assert.assertEquals(testorder.getId(), returnedOrder.getId());
        Assert.assertEquals(testorder.getPaid(), returnedOrder.getPaid());

        // Link it to the order
        orderService.addItem(returnedOrder.getId(), returnedItem.getId(), 1);
        
        Order fetchedOrder = orderService.getOrder(returnedOrder.getId());

        Assert.assertEquals(1, fetchedOrder.getItems().size());

        BigDecimal paymentAmount = new BigDecimal(15);
        Receipt paymentReceipt = orderService.pay(returnedOrder.getId(), paymentAmount);
        
        Assert.assertEquals(0, paymentReceipt.getPayment().compareTo(paymentAmount));
    }

    @Test
    public void testPayment_insufficientFunds() {
        // Item first
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        Item returnedItem = itemService.save(testitem);

        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        Order testorder = new Order();
        testorder.setId(1L);
        testorder.setPaid(false);

        Order returnedOrder = orderService.save(testorder);

        Assert.assertEquals(testorder.getId(), returnedOrder.getId());
        Assert.assertEquals(testorder.getPaid(), returnedOrder.getPaid());

        // Link it to the order
        orderService.addItem(returnedOrder.getId(), returnedItem.getId(), 1);
        
        Order fetchedOrder = orderService.getOrder(returnedOrder.getId());

        Assert.assertEquals(1, fetchedOrder.getItems().size());

        BigDecimal paymentAmount = new BigDecimal(5);
        Receipt paymentreceipt = orderService.pay(returnedOrder.getId(), paymentAmount);
        
        Assert.assertEquals(false, paymentreceipt.getOrder().getPaid());
        Assert.assertEquals("Insufficient funds", paymentreceipt.getText());
        Assert.assertEquals(BigDecimal.ZERO, paymentreceipt.getPayment());
    }
}