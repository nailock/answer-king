import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import answer.king.Application;

// JUnit4 testing the Spring app
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Application.class)
public abstract class GenericServiceTest {
    // Generic abstract service test class, to be implemented by our
    // service tests

}