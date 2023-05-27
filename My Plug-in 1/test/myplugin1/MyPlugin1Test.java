package myplugin1;

import com.nomagic.magicdraw.tests.MagicDrawTestCase;

public class MyPlugin1Test extends MagicDrawTestCase{

	public void testPluginInitialized() {
		assertTrue(MyPlugin1.initialized);
	}
}
