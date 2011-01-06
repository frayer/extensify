package org.extensify.transform.xalan;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.functions.FuncExtFunction;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

/**
 * This Xalan <code>ExtensionHandler</code> implementation holds a collection of
 * <code>ExtensionHandler</code> implementations which are specific to a given namespace.
 * This is useful in scenarios where all the implementations of your extension elements
 * and functions are not implemented in a single ExtensionHandler class.
 * <p/>
 * Individual ExtensionHandler implementations can be added to this class through
 * the <code>addExtensionHandler</code> method.  Calls to <code>callFunction</code>
 * or <code>processElement</code> will be delegated to the appropriate
 * <code>ExtensionHandler</code> by looking through the internal Set of available
 * <code>ExtensionHandler</code> implementations and calling the first one which
 * response true to <code>isFunctionAvailable</code> or <code>isElementAvailable</code>.
 */
public class NamespaceExtensionHandlerController extends ExtensionHandler {

    private Set<ExtensionHandler> extensionHandlers = null;

    /**
     * This constructor enforces a requirement that the collection of ExtensionHandlers this class
     * abstracts must be both unique and have a predictable iteration order through the use of a
     * <code>LinkedHashSet</code>.
     *
     * @param namespaceUri
     * @param extensionHandlers
     */
    public NamespaceExtensionHandlerController(String namespaceUri, LinkedHashSet<ExtensionHandler> extensionHandlers) {
        super(namespaceUri, "javaclass");

        this.extensionHandlers = extensionHandlers;
    }

    public String getNamespaceUri() {
        return this.m_namespaceUri;
    }

    public Set getExtensionHandlers() {
        return extensionHandlers;
    }

    @Override
    public boolean isFunctionAvailable(String functionName) {
        return (findExtensionHandlerForFunction(functionName) != null);
    }

    @Override
    public boolean isElementAvailable(String elementName) {
        return (findExtensionHandlerForElement(elementName) != null);
    }

    @Override
    public Object callFunction(String functionName, Vector args, Object methodKey, ExpressionContext expressionContext) throws TransformerException {
        ExtensionHandler extensionHandler = findExtensionHandlerForFunction(functionName);
        return extensionHandler.callFunction(functionName, args, methodKey, expressionContext);
    }

    @Override
    public Object callFunction(FuncExtFunction extFunction, Vector args, ExpressionContext expressionContext) throws TransformerException {
        ExtensionHandler extensionHandler = findExtensionHandlerForFunction(extFunction.getFunctionName());
        return extensionHandler.callFunction(extFunction, args, expressionContext);
    }

    @Override
    public void processElement(String s, ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet, Object methodKey) throws TransformerException, IOException {
        ExtensionHandler extensionHandler = findExtensionHandlerForElement(s);
        extensionHandler.processElement(s, elemTemplateElement, transformer, stylesheet, methodKey);
    }

    protected ExtensionHandler findExtensionHandlerForFunction(String functionName) {
        ExtensionHandler extensionHandler = null;

        Iterator<ExtensionHandler> handlerIterator = extensionHandlers.iterator();
        while (handlerIterator.hasNext() && (extensionHandler == null)) {
            ExtensionHandler handler = handlerIterator.next();
            if (handler.isFunctionAvailable(functionName)) {
                extensionHandler = handler;
            }
        }

        return extensionHandler;
    }

    protected ExtensionHandler findExtensionHandlerForElement(String elementName) {
        ExtensionHandler extensionHandler = null;

        Iterator<ExtensionHandler> handlerIterator = extensionHandlers.iterator();
        while (handlerIterator.hasNext() && (extensionHandler == null)) {
            ExtensionHandler handler = handlerIterator.next();
            if (handler.isElementAvailable(elementName)) {
                extensionHandler = handler;
            }
        }

        return extensionHandler;
    }

}
