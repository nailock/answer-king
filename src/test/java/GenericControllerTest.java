import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import answer.king.Application;

// JUnit4 testing the Spring app
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Application.class)
@AutoConfigureMockMvc
public abstract class GenericControllerTest {
   // Generic abstract controller test class, to be implemented by our
   // controller tests
    
   // Mock MVC to build and send RESTful API calls
   @Autowired
   protected MockMvc mockMvc;

   // Deserialisation function
   protected <T> T mapFromJson(String json, Class<T> clazz)
      throws JsonParseException, JsonMappingException, IOException {
      
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, clazz);
   }
}