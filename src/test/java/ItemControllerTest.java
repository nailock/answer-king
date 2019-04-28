import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.Assert;
import org.junit.Test;

import answer.king.model.Item;

import java.math.BigDecimal;

public class ItemControllerTest extends GenericControllerTest {

    private final static String uri = "/item";

    @Test
    public void testItemCreate() throws Exception {        
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        testitem.setPrice(new BigDecimal(10));

        String itemJson = testitem.toJson();

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

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                              .contentType("application/json")
                              .content(itemJson)).andReturn();

        int status = result.getResponse().getStatus();
        String responseContent = result.getResponse().getContentAsString();

        Assert.assertEquals(400, status);
        Assert.assertEquals(responseContent, "{ \"error\": \"Price cannot be negative.\" }");
    }

}