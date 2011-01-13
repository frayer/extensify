package org.extensify.transform.xalan.extensions.groovy;

import groovy.lang.Closure;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;
import org.extensify.transform.xalan.extensions.CallableExtensionElement;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class GroovyExtensionElement implements CallableExtensionElement {

    private Closure closure = null;

    public GroovyExtensionElement(Closure closure) {
        this.closure = closure;
    }

    public Object call(ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet) {
        /*
         * If the number of parameters available in the Closure is 3, the convention is that the
         * Closure will be called with the following 3 types:
         *      ElemTemplateElement
         *      TransformerImpl
         *      Stylesheet
         *
         * Otherwise a map of element attributes passed available in ElemTemplateElement will be
         * collected and sent to the Closure as a single argument.
         */
        if (closure.getMaximumNumberOfParameters() == 3) {
            return closure.call(new Object[]{elemTemplateElement, transformer, stylesheet});
        } else if (closure.getMaximumNumberOfParameters() == 1) {
            NamedNodeMap namedNodeMap = elemTemplateElement.getAttributes();
            Map<String, Object> arguments = new HashMap<String, Object>(namedNodeMap.getLength());
            for (int item = 0; item < namedNodeMap.getLength(); item++) {
                Node node = namedNodeMap.item(item);
                String argumentName = node.getNodeName();
                Object argumentValue = node.getNodeValue();
                arguments.put(argumentName, argumentValue);
            }

            return closure.call(arguments);
        } else {
            // TODO: Throw a InvalidClosureParameter Exception here.
            return null;
        }
    }

}
