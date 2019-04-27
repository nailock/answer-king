import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import answer.king.model.Item;
import answer.king.service.ItemService;

public class ItemServiceTest extends GenericServiceTest {

    @Autowired
    private ItemService itemService;

    @Test
    public void testSaveItem() {
        
        Item testitem = new Item();
        testitem.setId(1L);
        testitem.setName("Test Item");
        testitem.setPrice(new BigDecimal(10));

        Item returnedItem = itemService.save(testitem);

        Assert.assertEquals(testitem.getId(), returnedItem.getId());
        Assert.assertEquals(testitem.getName(), returnedItem.getName());
        Assert.assertEquals(testitem.getPrice(), returnedItem.getPrice());
    }


}