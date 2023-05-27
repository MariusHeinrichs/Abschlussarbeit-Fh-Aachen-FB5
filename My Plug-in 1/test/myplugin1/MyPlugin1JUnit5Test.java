package myplugin1;

import com.nomagic.magicdraw.tests.MagicDrawApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MagicDrawApplication.class)
public class MyPlugin1JUnit5Test
{
    @Test
    void testPluginInitialized()
    {
        assertTrue(MyPlugin1.initialized);
    }
}
