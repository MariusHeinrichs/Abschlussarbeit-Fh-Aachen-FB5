package myplugin1;

import com.nomagic.magicdraw.tests.MagicDrawTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(MagicDrawTestRunner.class)
public class MyPlugin1JUnit4Test
{
    @Test
    public void testPluginInitialized()
    {
        assertTrue(MyPlugin1.initialized);
    }
}
