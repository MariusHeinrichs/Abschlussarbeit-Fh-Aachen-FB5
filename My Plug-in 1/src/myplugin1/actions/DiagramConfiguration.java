package myplugin1.actions;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.MDActionsCategory;

public class DiagramConfiguration implements AMConfigurator {

    private final DiagramAction diagramAction;

    public DiagramConfiguration(DiagramAction diagramAction) {
        this.diagramAction = diagramAction;
    }

    @Override
    public void configure(ActionsManager actionsManager) {
        MDActionsCategory category = new MDActionsCategory("MyPlugin1DiagramToolbar", "My Plugin 1 Diagram Toolbar");
        category.addAction(diagramAction);
        actionsManager.addCategory(category);
    }

    @Override
    public int getPriority() {
        return MEDIUM_PRIORITY;
    }
}
