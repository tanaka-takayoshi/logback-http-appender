package jp.co.newrelikk.labs;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppTest
{
    private static final Logger LOG = LoggerFactory.getLogger(AppTest.class);

    @Test
    public void testApp()
    {
        try
        {
            throw new RuntimeException("Oops");
        } catch (Exception e)
        {
            LOG.error("Caught Exception: ", e);
        }
    }
}
