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
         * If the number of parameters available in the Closure is 2, the convention is that the
         * Closure will be called with a map of attribute name/value pairs which are available in
         * ElemTemplateElement, and also the NodeList of any child elements of the extension
         * element.
         *
         * If the number of parameters available in the Closure is 1, the convention is that the
         * Closure will be called with just the map of attribute name/value pairs which are
         * available in ElemTemplateElement.
         */
        int maximumNumberOfParameters = closure.getMaximumNumberOfParameters();
        if (isUsingXalanSignature(closure.getParameterTypes())) {
            return closure.call(new Object[]{elemTemplateElement, transformer, stylesheet});
        } else if (maximumNumberOfParameters == 2) {
            Map<String, Object> attributeMap = buildAttributeMap(elemTemplateElement);
            return closure.call(new Object[]{attributeMap, elemTemplateElement.getChildNodes()});
        } else if (maximumNumberOfParameters == 1) {
            Map<String, Object> attributeMap = buildAttributeMap(elemTemplateElement);
            return closure.call(attributeMap);
        } else {
            // TODO: Throw a InvalidClosureParameter Exception here.
            return null;
        }
    }

    /**
     * Returns true if the types in the Class array are the following 3 types in the given order.
     * Otherwise it returns false.
     *
     * ElemTemplateElement
     * TransformerImpl
     * Stylesheet
     *
     * @param types
     * @return
     */
    private boolean isUsingXalanSignature(Class[] types) {
        if ((types.length == 3)
                && ElemTemplateElement.class.isAssignableFrom(types[0])
                && TransformerImpl.class.isAssignableFrom(types[1])
                && Stylesheet.class.isAssignableFrom(types[2])) {
            return true;
        }
        return false;
    }

    /**
     * Builds a map of name/value pairs for attributes available on the elemTemplateElement passed
     * in.
     *
     * @param elemTemplateElement the object to gather attributes from.
     * @return the Map of attribute name/value pairs.
     */
    private Map<String, Object> buildAttributeMap(ElemTemplateElement elemTemplateElement) {
        NamedNodeMap namedNodeMap = elemTemplateElement.getAttributes();
        Map<String, Object> attributeMap = new HashMap<String, Object>(namedNodeMap.getLength());
        for (int item = 0; item < namedNodeMap.getLength(); item++) {
            Node node = namedNodeMap.item(item);
            String attributeName = node.getNodeName();
            Object attributeValue = node.getNodeValue();
            attributeMap.put(attributeName, attributeValue);
        }

        return attributeMap;
    }

}
