import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestMain {
    @Test
    public void startApp() throws Exception {
        while (true) {
            TimeUnit.SECONDS.sleep(2);
        }
    }
}
