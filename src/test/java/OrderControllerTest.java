import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.Assert;
import org.junit.Test;

import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.model.Item;

import java.math.BigDecimal;

public class OrderControllerTest extends GenericControllerTest {

    // Test class for our order controller

    private final static String orderuri = "/order";
    private final static String itemuri = "/item";

    @Test
    public void testOrderCreate() throws Exception {
        // POST to create a new order        
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(orderuri))
                         .andReturn();

        int status = result.getResponse().getStatus();

        Assert.assertEquals(200, status);
    }

    @Test
    public void testOrderAddItem() throws Exception {
        // Prerequisite - we have an item
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult itemresult = mockMvc.perform(MockMvcRequestBuilders.post(itemuri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int itemstatus = itemresult.getResponse().getStatus();
        String itemResponseContent = itemresult.getResponse().getContentAsString();
        Item returnedItem = mapFromJson(itemResponseContent, Item.class);

        Assert.assertEquals(200, itemstatus);
        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        // Prerequisite - we have an order
        // POST to create a new order
        MvcResult orderresult = mockMvc.perform(MockMvcRequestBuilders.post(orderuri))
                         .andReturn();

        int orderstatus = orderresult.getResponse().getStatus();
        String orderResponseContent = orderresult.getResponse().getContentAsString();
        Order returnedOrder = mapFromJson(orderResponseContent, Order.class);

        Assert.assertEquals(200, orderstatus);

        // Now we can add the item
        String adduri = orderuri + "/" + returnedOrder.getId() + "/addItem/" + returnedItem.getId()
                        + "/quantity/1";

        // PUT our item and it's desired quantity onto our order
        MvcResult addresult = mockMvc.perform(MockMvcRequestBuilders.put(adduri))
                            .andReturn();

        int addstatus = addresult.getResponse().getStatus();

        Assert.assertEquals(200, addstatus);
    }

    @Test
    public void testOrderAddItem_invalidQuantity() throws Exception {
        // Prerequisite - we have an item
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult itemresult = mockMvc.perform(MockMvcRequestBuilders.post(itemuri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int itemstatus = itemresult.getResponse().getStatus();
        String itemResponseContent = itemresult.getResponse().getContentAsString();
        Item returnedItem = mapFromJson(itemResponseContent, Item.class);

        Assert.assertEquals(200, itemstatus);
        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        // Prerequisite - we have an order
        // POST to create a new order
        MvcResult orderresult = mockMvc.perform(MockMvcRequestBuilders.post(orderuri))
                         .andReturn();

        int orderstatus = orderresult.getResponse().getStatus();
        String orderResponseContent = orderresult.getResponse().getContentAsString();
        Order returnedOrder = mapFromJson(orderResponseContent, Order.class);

        Assert.assertEquals(200, orderstatus);

        // Now we can add the item
        String adduri = orderuri + "/" + returnedOrder.getId() + "/addItem/" + returnedItem.getId()
                        + "/quantity/-1";

        // PUT our item and it's desired quantity onto our order
        MvcResult addresult = mockMvc.perform(MockMvcRequestBuilders.put(adduri))
                            .andReturn();

        int addstatus = addresult.getResponse().getStatus();
        String addResponseContent = addresult.getResponse().getContentAsString();

        Assert.assertEquals(400, addstatus);
        Assert.assertEquals("{ \"error\": \"Quantity cannot be negative.\" }", addResponseContent);
    }

    @Test
    public void testOrderPay() throws Exception {
        // Prerequisite - we have an item
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult itemresult = mockMvc.perform(MockMvcRequestBuilders.post(itemuri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int itemstatus = itemresult.getResponse().getStatus();
        String itemResponseContent = itemresult.getResponse().getContentAsString();
        Item returnedItem = mapFromJson(itemResponseContent, Item.class);

        Assert.assertEquals(200, itemstatus);
        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        // Prerequisite - we have an order
        // POST to create a new order
        MvcResult orderresult = mockMvc.perform(MockMvcRequestBuilders.post(orderuri))
                         .andReturn();

        int orderstatus = orderresult.getResponse().getStatus();
        String orderResponseContent = orderresult.getResponse().getContentAsString();
        Order returnedOrder = mapFromJson(orderResponseContent, Order.class);

        Assert.assertEquals(200, orderstatus);

        // Prerequisite - the item's attached to the order
        String adduri = orderuri + "/" + returnedOrder.getId() + "/addItem/" + returnedItem.getId()
                        + "/quantity/1";

        // PUT our item and it's desired quantity onto our order
        MvcResult addresult = mockMvc.perform(MockMvcRequestBuilders.put(adduri))
                            .andReturn();

        int addstatus = addresult.getResponse().getStatus();

        Assert.assertEquals(200, addstatus);

        // Now let's pay
        String payuri = orderuri + "/" + returnedOrder.getId() + "/pay";
        // We can overpay to test the change functionality
        String paymentAmount = "15";
        
        // PUT our payment for our order
        MvcResult paymentresult = mockMvc.perform(MockMvcRequestBuilders.put(payuri)
                                .contentType("application/json")
                                .content(paymentAmount))
                                .andReturn();

        int paymentstatus = paymentresult.getResponse().getStatus();
        String paymentResponseContent = paymentresult.getResponse().getContentAsString();
        Receipt paymentreceipt = mapFromJson(paymentResponseContent, Receipt.class);

        BigDecimal expectedAmount = new BigDecimal(paymentAmount);
        BigDecimal expectedChange = expectedAmount.subtract(itemAmount);
        

        Assert.assertEquals(200, paymentstatus);
        Assert.assertEquals(true, paymentreceipt.getOrder().getPaid());
        Assert.assertEquals("Approved", paymentreceipt.getText());
        Assert.assertEquals(0, expectedChange.compareTo(paymentreceipt.getChange()));
        Assert.assertEquals(0, expectedAmount.compareTo(paymentreceipt.getPayment()));
    }

    @Test
    public void testOrderPay_insufficientFunds() throws Exception {
        // Prerequisite - we have an item
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult itemresult = mockMvc.perform(MockMvcRequestBuilders.post(itemuri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int itemstatus = itemresult.getResponse().getStatus();
        String itemResponseContent = itemresult.getResponse().getContentAsString();
        Item returnedItem = mapFromJson(itemResponseContent, Item.class);

        Assert.assertEquals(200, itemstatus);
        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        // Prerequisite - we have an order
        // POST to create a new order
        MvcResult orderresult = mockMvc.perform(MockMvcRequestBuilders.post(orderuri))
                         .andReturn();

        int orderstatus = orderresult.getResponse().getStatus();
        String orderResponseContent = orderresult.getResponse().getContentAsString();
        Order returnedOrder = mapFromJson(orderResponseContent, Order.class);

        Assert.assertEquals(200, orderstatus);

        // Prerequisite - the item's attached to the order
        String adduri = orderuri + "/" + returnedOrder.getId() + "/addItem/" + returnedItem.getId()
                        + "/quantity/1";

        // PUT our item and it's desired quantity onto our order
        MvcResult addresult = mockMvc.perform(MockMvcRequestBuilders.put(adduri))
                            .andReturn();

        int addstatus = addresult.getResponse().getStatus();

        Assert.assertEquals(200, addstatus);

        // Now let's pay
        String payuri = orderuri + "/" + returnedOrder.getId() + "/pay";

        String paymentAmount = "5";

        // PUT our payment for our order
        MvcResult paymentresult = mockMvc.perform(MockMvcRequestBuilders.put(payuri)
                                .contentType("application/json")
                                .content(paymentAmount))
                                .andReturn();

        int paymentstatus = paymentresult.getResponse().getStatus();
        String paymentResponseContent = paymentresult.getResponse().getContentAsString();
        Receipt paymentreceipt = mapFromJson(paymentResponseContent, Receipt.class);

        BigDecimal expectedAmount = new BigDecimal(paymentAmount);
        BigDecimal expectedChange = expectedAmount.subtract(itemAmount);
        

        Assert.assertEquals(200, paymentstatus);
        Assert.assertEquals(false, paymentreceipt.getOrder().getPaid());
        Assert.assertEquals("Insufficient funds", paymentreceipt.getText());
        Assert.assertEquals(BigDecimal.ZERO, paymentreceipt.getChange());
        Assert.assertEquals(BigDecimal.ZERO, paymentreceipt.getPayment());
    }

    @Test
    public void testOrderPay_negativePayment() throws Exception {
        // Prerequisite - we have an item
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        BigDecimal itemAmount = new BigDecimal(10);
        testitem.setPrice(itemAmount);

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult itemresult = mockMvc.perform(MockMvcRequestBuilders.post(itemuri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int itemstatus = itemresult.getResponse().getStatus();
        String itemResponseContent = itemresult.getResponse().getContentAsString();
        Item returnedItem = mapFromJson(itemResponseContent, Item.class);

        Assert.assertEquals(200, itemstatus);
        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        // Prerequisite - we have an order
        // POST to create a new order
        MvcResult orderresult = mockMvc.perform(MockMvcRequestBuilders.post(orderuri))
                         .andReturn();

        int orderstatus = orderresult.getResponse().getStatus();
        String orderResponseContent = orderresult.getResponse().getContentAsString();
        Order returnedOrder = mapFromJson(orderResponseContent, Order.class);

        Assert.assertEquals(200, orderstatus);

        // Prerequisite - the item's attached to the order
        String adduri = orderuri + "/" + returnedOrder.getId() + "/addItem/" + returnedItem.getId()
                        + "/quantity/1";

        // PUT our item and it's desired quantity onto our order
        MvcResult addresult = mockMvc.perform(MockMvcRequestBuilders.put(adduri))
                            .andReturn();

        int addstatus = addresult.getResponse().getStatus();

        Assert.assertEquals(200, addstatus);

        // Now let's pay
        String payuri = orderuri + "/" + returnedOrder.getId() + "/pay";

        String paymentAmount = "-1";

        // PUT our payment for our order
        MvcResult paymentresult = mockMvc.perform(MockMvcRequestBuilders.put(payuri)
                                .contentType("application/json")
                                .content(paymentAmount))
                                .andReturn();

        int paymentstatus = paymentresult.getResponse().getStatus();
        String paymentResponseContent = paymentresult.getResponse().getContentAsString();
        
        Assert.assertEquals(400, paymentstatus);
        Assert.assertEquals("{ \"error\": \"Payment cannot be negative.\" }", paymentResponseContent);
    }
}