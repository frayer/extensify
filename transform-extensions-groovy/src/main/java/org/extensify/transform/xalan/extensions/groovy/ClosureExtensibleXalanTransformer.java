package org.extensify.transform.xalan.extensions.groovy;

import groovy.lang.Closure;
import org.extensify.transform.xalan.transformer.ExtensibleXalanTransformer;
import org.apache.xalan.transformer.TransformerImpl;

public class ClosureExtensibleXalanTransformer extends ExtensibleXalanTransformer {

    public ClosureExtensibleXalanTransformer(TransformerImpl baseTransformer) {
        super(baseTransformer);
    }

    public void addExtensionFunction(String namespaceUri, String name, Closure closure) {
        GroovyClosureExtensionHandler handler = new GroovyClosureExtensionHandler(namespaceUri);
        handler.addFunctionClosure(name, closure);

        this.registerExtensionHandler(namespaceUri, handler);
    }

    public void addExtensionElement(String namespaceUri, String name, Closure closure) {
        GroovyClosureExtensionHandler handler = new GroovyClosureExtensionHandler(namespaceUri);
        handler.addElementClosure(name, closure);

        this.registerExtensionHandler(namespaceUri, handler);
    }

}
