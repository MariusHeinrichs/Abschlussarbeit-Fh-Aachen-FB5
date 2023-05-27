package myplugin1.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.paths.PathConnector;
import com.nomagic.magicdraw.uml.symbols.paths.PathElement;
import com.nomagic.ui.ScalableImageIcon;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.Interface;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiagramAction extends DefaultDiagramAction {

    public DiagramAction(String id, String name) {
        super(id, name, null, null);

        URL url = getClass().getResource("../../icons/coffee-icon.png");
        setSmallIcon(new ScalableImageIcon(url));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        List<PresentationElement> presentationElements = getSelected();


        if(presentationElements != null && presentationElements.size() > 0) {
            PresentationElement presentationElement = presentationElements.get(0);
            DiagramPresentationElement diagramPresentationElement = presentationElement.getDiagramPresentationElement();

            if(diagramPresentationElement.getHumanType().equals("SysML Internal Block Diagram") && presentationElement.getHumanType().equals("Part Property"))
                checkBlockInIBD(presentationElement);
            else if(diagramPresentationElement.getHumanType().equals("SysML Block Definition Diagram") && presentationElement.getHumanType().equals("Block"))
                checkBlockInBDD(presentationElement);
            else
                Application.getInstance().getGUILog().showMessage("Selected Element is either not in a BDD/IBD or is not a Block");
        }
    }

    // Returns List of all ProxyPorts from the given PresentationElement
    public List<PresentationElement> getAllProxyPortsFromPresentationElement(PresentationElement presentationElement){
        List<PresentationElement> proxyPorts = new ArrayList<PresentationElement>();
        List<PresentationElement> children = presentationElement.getPresentationElements();

        for(int i = 0; i < children.size(); i++){
            if(children.get(i).getHumanType().equals("Proxy Port"))
                proxyPorts.add(children.get(i));
        }
        return proxyPorts;
    }

    // Returns List of all FullPorts from the given PresentationElement
    public List<PresentationElement> getAllFullPortsFromPresentationElement(PresentationElement presentationElement){
        List<PresentationElement> fullPorts = new ArrayList<PresentationElement>();
        List<PresentationElement> children = presentationElement.getPresentationElements();

        for(int i = 0; i < children.size(); i++){
            if(children.get(i).getHumanType().equals("Full Port"))
                fullPorts.add(children.get(i));
        }
        return fullPorts;
    }

    // Returns List of all Ports from the given PresentationElement
    public List<PresentationElement> getAllPortsFromPresentationElement(PresentationElement presentationElement){
        List<PresentationElement> ports = new ArrayList<PresentationElement>();
        List<PresentationElement> children = presentationElement.getPresentationElements();

        for(int i = 0; i < children.size(); i++){
            if(children.get(i).getHumanType().equals("Port"))
                ports.add(children.get(i));
        }
        return ports;
    }

    // Checks if all Ports of a given List of ProxyPorts do have at least one ConnectorLine
    // Returns True if all Ports have at least one ConnectorLine
    // Returns False if one Ports does not have a ConnectorLine
    public boolean checkIfAllProxyPortsAreConnected(List<PresentationElement> listProxyPorts) {

        for(int i = 0; i < listProxyPorts.size(); i++){
            boolean hasConnector = false;
            List<PathElement> pathElements = getAllPathsFromPresentationElement(listProxyPorts.get(i));

            if(pathElements.size() == 0)
                return false;

            for(int j = 0; j < pathElements.size(); j++) {
                if(pathElements.get(j).getHumanType().equals("Connector"))
                    hasConnector = true;
            }

            if(!hasConnector)
                return false;
        }
        return true;
    }

    // Checks if all Ports of a given List of ProxyPorts do have a specified Type
    // Returns True if all Ports have a specified Type
    // Returns False if one Ports does not have a specified Type
    public boolean checkIfAllProxyPortsHaveAType(List<PresentationElement> listProxyPorts) {

        for(int i = 0; i < listProxyPorts.size(); i++) {

            Element proxyPort = listProxyPorts.get(i).getElement();

            String name = listProxyPorts.get(i).getName();
            if(!name.contains(" : "))
                return false;
        }
        return true;
    }

    // Returns List of all PathElements from the given PresentationElement
    public List<PathElement> getAllPathsFromPresentationElement(PresentationElement presentationElement){
        PathConnector pathConnector = (PathConnector) presentationElement;
        return pathConnector.getConnectedPathElements();
    }

    // Returns a List of all PathElements going down from the given presentationElement
    public List<PathElement> getPathsGoingDown(PresentationElement presentationElement){
        List<PathElement> pathElementsGoingDown = new ArrayList<PathElement>();
        List<PathElement> pathElements = getAllPathsFromPresentationElement(presentationElement);

        for(int i = 0; i < pathElements.size(); i++){
            if(pathElements.get(i).getClient().getHumanName().equals(presentationElement.getHumanName())){
                pathElementsGoingDown.add(pathElements.get(i));
            }
        }
        return pathElementsGoingDown;
    }

    // Recursive tree traversal in DFS to find the deepest connected PresentationElement, starting from the given PresentationElement
    // Returns level of the deepest PresentationElement
    public int getNestingDepthFirstSearch(PresentationElement presentationElement){
        List<PathElement> pathElementsGoingDown = getPathsGoingDown(presentationElement);

        // Best case
        if(pathElementsGoingDown.size() == 0)
            return 0;

        int levelOfDeepestPresentationElement = 0;

        for(int i = 0; i < pathElementsGoingDown.size(); i++) {
            PresentationElement child = pathElementsGoingDown.get(i).getSupplier();
            levelOfDeepestPresentationElement = Math.max(levelOfDeepestPresentationElement, getNestingDepthFirstSearch(child));

        }
        return levelOfDeepestPresentationElement + 1;
    }

    // Returns True, if the given PresentationElement Starts with a Lower Case
    // Else Returns False
    public boolean checkIfPresentationElementStartsWithLowerCase(PresentationElement presentationElement){
        char first = presentationElement.getName().charAt(0);

        if(first == Character.toLowerCase(first) && first != Character.toUpperCase(first))
            return true;

        return false;
    }


    // Returns True if every ProxyPort from the given List starts with a Lower Case
    // Else Returns False
    public boolean checkIfProxyPortsStartWithLowerCase(List<PresentationElement> listProxyPorts){
        for(int i = 0; i < listProxyPorts.size(); i++) {
            if (!checkIfPresentationElementStartsWithLowerCase(listProxyPorts.get(i)))
                return false;
        }
        return true;
    }

    // Counts the Different Directions of a given List of proxyPorts
    // Returns an Array
    public int[] countProxyInputAndOutput(List<PresentationElement> proxyPorts){
        // count[0] : no Direction
        // count[1] : Input Port
        // count[2] : Output Port
        int count[] = new int[3];

        for(int i = 0; i < proxyPorts.size(); i++) {
            Port port = (Port) proxyPorts.get(i).getElement();

            Class interfaceBlock = (Class) port.getType();

            List<Property> interfaceBlockOwnedAttribute = interfaceBlock.getOwnedAttribute();

            for(Property prop : interfaceBlockOwnedAttribute){

            }

            Property flowProperty = (Property) interfaceBlockOwnedAttribute.get(0);

        }
        return count;
    }

    public void checkBlockInBDD(PresentationElement presentationElement){
        int levelOfDeepestPresentationElement = 0;
        StringBuffer buffer = new StringBuffer();
        String name = presentationElement.getElement().getHumanName();
        levelOfDeepestPresentationElement = getNestingDepthFirstSearch(presentationElement);
        List<PresentationElement> proxyPorts = getAllProxyPortsFromPresentationElement(presentationElement);
        List<PresentationElement> fullPorts = getAllFullPortsFromPresentationElement(presentationElement);
        List<PresentationElement> ports = getAllPortsFromPresentationElement(presentationElement);

        boolean blockStartsWithLowerCase = checkIfPresentationElementStartsWithLowerCase(presentationElement);
        boolean allProxyPortsStartWithLowerCase = checkIfProxyPortsStartWithLowerCase(proxyPorts);
        boolean allProxyPortsHaveAType = checkIfAllProxyPortsHaveAType(proxyPorts);

        buffer.append("Selected element: " + name + "\n");

        if(!blockStartsWithLowerCase)
            buffer.append("Smell: Block should start with lower case! \n");
        if(!allProxyPortsStartWithLowerCase)
            buffer.append("Smell: Proxy ports should start with lower case! \n");
        if(!allProxyPortsHaveAType)
            buffer.append("Warning: Not all Proxy ports have a specified Type! \n");
        if(fullPorts.size() > 0 || ports.size() > 0)
            buffer.append("Smell: Every Port in a BDD should be a Proxy port! \n");
        if(proxyPorts.size() > 0 && fullPorts.size() > 0 || proxyPorts.size() > 0 && ports.size() > 0 || fullPorts.size() > 0 && ports.size() > 0)
            buffer.append("Error: Different types of Ports have been used! \n");
        if(levelOfDeepestPresentationElement >= 7)
            buffer.append("Error: Depth of composed associations exceeded 6! \n");
        else if(levelOfDeepestPresentationElement >= 5)
            buffer.append("Warning: Depth of composed associations exceeded 4! \n");
        else if(levelOfDeepestPresentationElement >= 3)
            buffer.append("Smell: Depth of composed associations exceeded 2! \n");

        Application.getInstance().getGUILog().showMessage(buffer.toString());
    }

    public void checkBlockInIBD(PresentationElement presentationElement){
        int levelOfDeepestPresentationElement = 0;
        String name = presentationElement.getElement().getHumanName();
        StringBuffer buffer = new StringBuffer();
        levelOfDeepestPresentationElement = getNestingDepthFirstSearch(presentationElement);
        List<PresentationElement> proxyPorts = getAllProxyPortsFromPresentationElement(presentationElement);
        List<PresentationElement> fullPorts = getAllFullPortsFromPresentationElement(presentationElement);
        List<PresentationElement> ports = getAllPortsFromPresentationElement(presentationElement);

        boolean blockStartsWithLowerCase = checkIfPresentationElementStartsWithLowerCase(presentationElement);
        boolean allProxyPortsStartWithLowerCase = checkIfProxyPortsStartWithLowerCase(proxyPorts);
        boolean allProxyPortsConnected = checkIfAllProxyPortsAreConnected(proxyPorts);
        boolean allProxyPortsHaveAType = checkIfAllProxyPortsHaveAType(proxyPorts);
        int[] counterOfDifferentDirectionalProxyPorts = countProxyInputAndOutput(proxyPorts);

        buffer.append("Selected element: " + name + "\n");

        if(!blockStartsWithLowerCase)
            buffer.append("Smell: Part Property should start with lower case! \n");
        if(!allProxyPortsStartWithLowerCase)
            buffer.append("Smell: Proxy ports should start with lower case! \n");
        if(!allProxyPortsHaveAType)
            buffer.append("Warning: Not all Proxy ports have a specified Type! \n");
        if(!allProxyPortsConnected)
            buffer.append("Warning: Not all Proxy ports have a Connector \n");
        if(fullPorts.size() > 0 || ports.size() > 0)
            buffer.append("Smell: Every Port in a IBD should be a Proxy port! \n");
        if(proxyPorts.size() > 0 && fullPorts.size() > 0 || proxyPorts.size() > 0 && ports.size() > 0 || fullPorts.size() > 0 && ports.size() > 0)
            buffer.append("Error: Different types of Ports have been used! \n");
        if(counterOfDifferentDirectionalProxyPorts[0] > 0)
            buffer.append("Warning: Not every Proxy port has a direction \n");
        if(counterOfDifferentDirectionalProxyPorts[1] >= 5)
            buffer.append("Error: Number of input Ports exceeded 4 \n");
        else if(counterOfDifferentDirectionalProxyPorts[1] >= 4)
            buffer.append("Warning: Number of Input Ports exceeded 3 \n");
        else if(counterOfDifferentDirectionalProxyPorts[1] >= 3)
            buffer.append("Smell: Number of Input Ports exceeded 2 \n");
        if(counterOfDifferentDirectionalProxyPorts[2] >= 4)
            buffer.append("Error: Number of output Ports exceeded 3 \n");
        else if(counterOfDifferentDirectionalProxyPorts[2] >= 3)
            buffer.append("Warning: Number of output Ports exceeded 2 \n");
        else if(counterOfDifferentDirectionalProxyPorts[2] >= 2)
            buffer.append("Smell: Number of output Ports exceeded 1 \n");
        if(levelOfDeepestPresentationElement >= 7)
            buffer.append("Error: Depth of composed associations exceeded 6! \n");
        else if(levelOfDeepestPresentationElement >= 5)
            buffer.append("Warning: Depth of composed associations exceeded 4! \n");
        else if(levelOfDeepestPresentationElement >= 3)
            buffer.append("Smell: Depth of composed associations exceeded 2! \n");

        Application.getInstance().getGUILog().showMessage(buffer.toString());
    }
}
