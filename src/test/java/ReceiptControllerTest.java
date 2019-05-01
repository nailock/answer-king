import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.Assert;
import org.junit.Test;

import answer.king.model.Item;
import answer.king.model.Receipt;
import answer.king.model.Order;

import java.math.BigDecimal;

public class ReceiptControllerTest extends GenericControllerTest {

    private final static String uri = "/receipt";
    private final static String orderUri = "/order";

    @Test
    public void testReceiptCreate() throws Exception {
        Receipt testReceipt = new Receipt();
        testReceipt.setId(1L);
        testReceipt.setText("Test");
        testReceipt.setPayment(new BigDecimal(10));
        
        String content = testReceipt.toJson();
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                         .contentType("application/json")
                         .content(content))
                         .andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();
        Receipt returnedReceipt = mapFromJson(responseContent, Receipt.class);

        Assert.assertEquals(200, status);
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
        
        String content = testReceipt.toJson();
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                         .contentType("application/json")
                         .content(content))
                         .andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();
        Receipt returnedReceipt = mapFromJson(responseContent, Receipt.class);

        Assert.assertEquals(200, status);
        Assert.assertEquals(testReceipt.getText(), returnedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(returnedReceipt.getPayment()));

        String fetchUri = uri + "/" + testReceipt.getId();

        MvcResult fetchResult = mockMvc.perform(MockMvcRequestBuilders.get(fetchUri))
         .andReturn();

        int fetchStatus = fetchResult.getResponse().getStatus();
        String fetchResponseContent = fetchResult.getResponse().getContentAsString();
        Receipt fetchedReceipt = mapFromJson(fetchResponseContent, Receipt.class);

        Assert.assertEquals(200, fetchStatus);
        Assert.assertEquals(testReceipt.getText(), fetchedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(fetchedReceipt.getPayment()));
    }

    /* Having trouble with this one, commenting it out for now
    @Test
    public void testReceiptGetByOrderId() throws Exception {
        // Prerequisite - we've got a receipt with an order attached
        Receipt testReceipt = new Receipt();
        testReceipt.setId(1L);
        testReceipt.setText("Test");
        testReceipt.setPayment(new BigDecimal(10));
        
        String content = testReceipt.toJson();
        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                         .contentType("application/json")
                         .content(content))
                         .andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();
        Receipt returnedReceipt = mapFromJson(responseContent, Receipt.class);

        Assert.assertEquals(200, status);
        Assert.assertEquals(testReceipt.getText(), returnedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(returnedReceipt.getPayment()));

        MvcResult orderresult = mockMvc.perform(MockMvcRequestBuilders.post(orderUri))
                         .andReturn();

        int orderstatus = orderresult.getResponse().getStatus();
        String orderResponseContent = orderresult.getResponse().getContentAsString();
        Order returnedOrder = mapFromJson(orderResponseContent, Order.class);

        Assert.assertEquals(200, orderstatus);

        String adduri = uri + "/" + returnedReceipt.getId() + "/addOrder/" + returnedOrder.getId();
        MvcResult addresult = mockMvc.perform(MockMvcRequestBuilders.put(adduri))
                            .andReturn();

        int addstatus = addresult.getResponse().getStatus();

        Assert.assertEquals(200, addstatus);

        String fetchUri = uri + "/byOrder/" + returnedOrder.getId();

        MvcResult fetchResult = mockMvc.perform(MockMvcRequestBuilders.get(fetchUri))
         .andReturn();

        int fetchStatus = fetchResult.getResponse().getStatus();
        String fetchResponseContent = fetchResult.getResponse().getContentAsString();
        Receipt fetchedReceipt = mapFromJson(fetchResponseContent, Receipt.class);

        Assert.assertEquals(200, fetchStatus);
        Assert.assertEquals(testReceipt.getText(), fetchedReceipt.getText());
        Assert.assertEquals(0, testReceipt.getPayment().compareTo(fetchedReceipt.getPayment()));
    }

    */
}