package org.extensify.transform.xalan.extensions;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.functions.FuncExtFunction;
import org.extensify.transform.xalan.extensions.CallableExtensionElement;
import org.extensify.transform.xalan.extensions.CallableExtensionFunction;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Vector;

public abstract class BasicExtensionHandler extends ExtensionHandler {

    protected BasicExtensionHandler(String namespaceUri, String scriptLang) {
        super(namespaceUri, scriptLang);
    }

    @Override
    public Object callFunction(String functionName, Vector vector, Object methodKey, ExpressionContext expressionContext) throws TransformerException {
        CallableExtensionFunction callable = getCallableExtensionFunction(functionName);
        return callable.call(expressionContext, vector.toArray());
    }

    @Override
    public Object callFunction(FuncExtFunction extFunction, Vector args, ExpressionContext expressionContext) throws TransformerException {
        return callFunction(extFunction.getFunctionName(), args, extFunction.getMethodKey(), expressionContext);
    }

    @Override
    public void processElement(String elementName, ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet, Object methodKey) throws TransformerException, IOException {
        CallableExtensionElement callable = getCallableExtensionElement(elementName);
        callable.call(elemTemplateElement, transformer, stylesheet);
    }

    public String getNamespaceUri() {
        return this.m_namespaceUri;
    }

    public String getScriptLanguage() {
        return this.m_scriptLang;
    }

    protected abstract CallableExtensionElement getCallableExtensionElement(String elementName);

    protected abstract CallableExtensionFunction getCallableExtensionFunction(String functionName);

}
