package myplugin1;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.plugins.Plugin;
import myplugin1.actions.DiagramAction;
import myplugin1.actions.DiagramConfiguration;

public class MyPlugin1 extends Plugin
{
	public static boolean initialized;
	
	@Override
	public void init()
	{
		initialized = true;
		createDiagramAction();
		Application.getInstance().getGUILog().showMessage("My Plug-in 1 initialized.");
	}

	@Override
	public boolean close()
	{
		return true;
	}

	@Override
	public boolean isSupported()
	{
		return true;
	}

	private void createDiagramAction(){
		DiagramAction action = new DiagramAction("MyPlugin1DiagramAction", "My Plugin 1 Browser Action");
		DiagramConfiguration configurator = new DiagramConfiguration(action);
		ActionsConfiguratorsManager.getInstance().addAnyDiagramCommandBarConfigurator(configurator);
	}
}
