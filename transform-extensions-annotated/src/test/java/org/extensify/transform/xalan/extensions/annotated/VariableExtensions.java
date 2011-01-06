package org.extensify.transform.xalan.extensions.annotated;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.objects.XString;

import java.util.HashMap;
import java.util.Map;

/**
 * This is not a test class but an implementation of an annotated XSLT Extension which can be used
 * by an AnnotatedExtensionHandler.  This is tested in the
 * {@link VariableExtensionsTest} test class.
 */
@XSLTExtension(namespace = "http://www.xsltextensions.org/extensions")
public class VariableExtensions {

    private Map<String, Object> dpVariables = null;

    public VariableExtensions() {
        dpVariables = new HashMap<String, Object>();
    }

    @XSLTExtensionFunction("variable")
    public Object getVariable(ExpressionContext context, XString name) {
        return dpVariables.get(name.toString());
    }

    @XSLTExtensionElement("set-variable")
    public Object setVariable(ElemTemplateElement templateElement, TransformerImpl transformer, Stylesheet stylesheet) {
        String name = templateElement.getAttribute("name");
        Object value = templateElement.getAttribute("value");

        dpVariables.put(name, value);

        return null;
    }

}
