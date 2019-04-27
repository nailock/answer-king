import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.Assert;
import org.junit.Test;

import answer.king.model.Item;

import java.math.BigDecimal;

public class ItemControllerTest extends GenericControllerTest {

    @Test
    public void testItemCreate() throws Exception {
        String uri = "/item";
        
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

}