import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.Receipt;

import answer.king.service.ItemService;
import answer.king.service.OrderService;
import answer.king.service.ReceiptService;

public class ReceiptServiceTest extends GenericServiceTest {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private OrderService orderService;

    @Test
    public void testReceiptCreate() throws Exception {
        Receipt testReceipt = new Receipt();
        testReceipt.setId(1L);
        testReceipt.setText("Test");
        testReceipt.setPayment(new BigDecimal(10));
        
        Receipt returnedReceipt = receiptService.save(testReceipt);

        Assert.assertEquals(testReceipt.getText(), returnedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(returnedReceipt.getPayment()));
    }

    @Test
    public void testReceiptGetById() throws Exception {
        // Prerequisite - we've got a receipt
        Receipt testReceipt = new Receipt();
        testReceipt.setId(1L);
        testReceipt.setText("Test");
        testReceipt.setPayment(new BigDecimal(10));
        
        Receipt returnedReceipt = receiptService.save(testReceipt);

        Assert.assertEquals(testReceipt.getText(), returnedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(returnedReceipt.getPayment()));

        Receipt fetchedReceipt = receiptService.getReceipt(testReceipt.getId());

        Assert.assertEquals(testReceipt.getText(), fetchedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(fetchedReceipt.getPayment()));
    }

    @Test
    public void testReceiptGetByOrderId() throws Exception {
        // Prerequisite - we've got a receipt with an order attached
        Receipt testReceipt = new Receipt();
        testReceipt.setId(1L);
        testReceipt.setText("Test");
        testReceipt.setPayment(new BigDecimal(10));
        
        Receipt returnedReceipt = receiptService.save(testReceipt);

        Assert.assertEquals(testReceipt.getText(), returnedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(returnedReceipt.getPayment()));


        Order returnedOrder = orderService.save(new Order());

        receiptService.addOrder(returnedReceipt.getId(), returnedOrder.getId());

        Receipt fetchedReceipt = receiptService.getReceiptByOrder(returnedOrder.getId());

        Assert.assertEquals(testReceipt.getText(), fetchedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(fetchedReceipt.getPayment()));
    }



}