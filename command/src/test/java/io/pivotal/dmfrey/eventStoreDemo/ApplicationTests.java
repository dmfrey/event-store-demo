package io.pivotal.dmfrey.eventStoreDemo;

import io.pivotal.dmfrey.eventStoreDemo.config.UnitTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith( SpringRunner.class )
@SpringBootTest( properties = {
        "--spring.cloud.service-registry.auto-registration.enabled=false"
})
@Import( UnitTestConfig.class )
public class ApplicationTests {

    @Test
    public void contextLoads() {
    }

}
