package sqlancer.dbms;

import org.junit.jupiter.api.Test;
import sqlancer.Main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class TestFeldera {
    @Test
    public void testFelderaNoREC() {
        assumeTrue(TestConfig.isEnvironmentTrue(TestConfig.FELDERA_ENV));
        assertEquals(0, Main.executeMain("--num-threads", "16",
                "--timeout-seconds", TestConfig.SECONDS,
                "feldera",
                "--oracle", "NOREC"));
    }
}
