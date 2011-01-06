package org.extensify.transform.xalan.extensions.groovy;

import groovy.lang.Closure;
import org.extensify.transform.xalan.extensions.BasicExtensionHandler;
import org.extensify.transform.xalan.extensions.CallableExtensionElement;
import org.extensify.transform.xalan.extensions.CallableExtensionFunction;

import java.util.HashMap;
import java.util.Map;

public class GroovyClosureExtensionHandler extends BasicExtensionHandler {

    private Map<String, CallableExtensionFunction> functionClosures = null;
    private Map<String, CallableExtensionElement> elementClosures = null;

    public GroovyClosureExtensionHandler(String namespaceUri) {
        super(namespaceUri, "javaclass");
        functionClosures = new HashMap<String, CallableExtensionFunction>();
        elementClosures = new HashMap<String, CallableExtensionElement>();
    }

    @Override
    protected CallableExtensionElement getCallableExtensionElement(String elementName) {
        return elementClosures.get(elementName);
    }

    @Override
    protected CallableExtensionFunction getCallableExtensionFunction(String functionName) {
        return functionClosures.get(functionName);
    }

    @Override
    public boolean isFunctionAvailable(String name) {
        return functionClosures.containsKey(name);
    }

    @Override
    public boolean isElementAvailable(String name) {
        return elementClosures.containsKey(name);
    }

    public void addFunctionClosure(String name, Closure closure) {
        functionClosures.put(name, new GroovyExtensionFunction(closure));
    }

    public void addElementClosure(String name, Closure closure) {
        elementClosures.put(name, new GroovyExtensionElement(closure));
    }

}
