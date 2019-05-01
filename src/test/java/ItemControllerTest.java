import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.Assert;
import org.junit.Test;

import answer.king.model.Item;

import java.math.BigDecimal;

public class ItemControllerTest extends GenericControllerTest {

    // Test class for our item controller

    private final static String uri = "/item";

    @Test
    public void testItemCreate() throws Exception {        
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        testitem.setPrice(new BigDecimal(10));

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();
        Item returnedItem = mapFromJson(responseContent, Item.class);

        Assert.assertEquals(200, status);
        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());
    }

    @Test
    public void testItemCreate_badName() throws Exception {
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("@@??>><<");
        testitem.setPrice(new BigDecimal(10));

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();

        Assert.assertEquals(400, status);
        Assert.assertEquals(responseContent, "{ \"error\": \"Only letters, numbers and spaces are accepted in the item name.\" }");
    }

    @Test
    public void testItemCreate_badPrice() throws Exception {
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        testitem.setPrice(new BigDecimal(-1));

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();

        Assert.assertEquals(400, status);
        Assert.assertEquals(responseContent, "{ \"error\": \"Price cannot be negative.\" }");
    }

    @Test
    public void testUpdatePrice() throws Exception {
        // Prerequisite - we have an item
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        testitem.setPrice(new BigDecimal(10));

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();
        Item returnedItem = mapFromJson(responseContent, Item.class);

        Assert.assertEquals(200, status);
        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        BigDecimal newPrice = new BigDecimal(20);

        String updatePriceUri = uri + "/" + testitem.getId() + "/updatePrice/" + newPrice;
        
        // PUT our updated item price
        MvcResult updateResult = mockMvc.perform(MockMvcRequestBuilders.put(updatePriceUri))
                         .andReturn();

        int updateStatus = updateResult.getResponse().getStatus();

        Assert.assertEquals(200, updateStatus);

        String fetchUri = uri + "/" + returnedItem.getId();

        // GET our item to check the price
        MvcResult fetchResult = mockMvc.perform(MockMvcRequestBuilders.get(fetchUri))
                              .andReturn();

        int fetchStatus = fetchResult.getResponse().getStatus();
        String fetchResponseContent = fetchResult.getResponse().getContentAsString();
        Item fetchedItem = mapFromJson(fetchResponseContent, Item.class);

        Assert.assertEquals(200, fetchStatus);
        Assert.assertEquals(testitem.getId(), fetchedItem.getId());
        Assert.assertEquals(testitem.getName(), fetchedItem.getName());
        Assert.assertEquals(0, newPrice.compareTo(fetchedItem.getPrice()));
    }

    @Test
    public void testUpdatePrice_badPrice() throws Exception {
        // Prerequisite - we have an item
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        testitem.setPrice(new BigDecimal(10));

        String itemJson = testitem.toJson();

        // POST our new item
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();
        Item returnedItem = mapFromJson(responseContent, Item.class);

        Assert.assertEquals(200, status);
        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());

        BigDecimal newPrice = new BigDecimal(-1);

        String updatePriceUri = uri + "/" + testitem.getId() + "/updatePrice/" + newPrice;
        
        // PUT our updated item price
        MvcResult updateResult = mockMvc.perform(MockMvcRequestBuilders.put(updatePriceUri))
                         .andReturn();

        int updateStatus = updateResult.getResponse().getStatus();
        String updateResponseContent = updateResult.getResponse().getContentAsString();

        Assert.assertEquals(400, updateStatus);
        Assert.assertEquals(updateResponseContent, "{ \"error\": \"Price cannot be negative.\" }");
    }

}