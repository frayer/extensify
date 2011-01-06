package org.extensify.transform.xalan.extensions.groovy;

import groovy.lang.Closure;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;
import org.extensify.transform.xalan.extensions.CallableExtensionElement;

public class GroovyExtensionElement implements CallableExtensionElement {

    private Closure closure = null;

    public GroovyExtensionElement(Closure closure) {
        this.closure = closure;
    }

    public Object call(ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet) {
        return closure.call( new Object[] { elemTemplateElement, transformer, stylesheet } );
    }

}
