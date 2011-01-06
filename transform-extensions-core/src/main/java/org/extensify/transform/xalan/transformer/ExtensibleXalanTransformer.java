package org.extensify.transform.xalan.transformer;

import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.extensions.ExtensionNamespaceSupport;
import org.apache.xalan.extensions.ExtensionNamespacesManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.extensify.transform.xalan.ExtensionHandlerRegistrar;
import org.extensify.transform.xalan.ExtensionNamespacesManagerUtil;

import javax.xml.transform.*;
import java.util.Collection;
import java.util.Properties;

public class ExtensibleXalanTransformer extends Transformer {

    private TransformerImpl baseTransformer = null;
    protected ExtensionHandlerRegistrar extensionHandlerRegistrar = null;

    public ExtensibleXalanTransformer(TransformerImpl baseTransformer) {
        this.baseTransformer = baseTransformer;
        extensionHandlerRegistrar = new ExtensionHandlerRegistrar();
    }

    public void registerExtensionHandler(String namespaceUri, ExtensionHandler extensionHandler) {
        extensionHandlerRegistrar.registerExtensionHandler(namespaceUri, extensionHandler);
    }

    @Override
    public void transform(Source source, Result result) throws TransformerException {
        ExtensionNamespacesManager extensionNamespacesManager = baseTransformer.getStylesheet().getExtensionNamespacesManager();
        Collection<ExtensionNamespaceSupport> extensionNamespaceSupportCollection = extensionHandlerRegistrar.generateExtensionNamespaceSupportList();
        ExtensionNamespacesManagerUtil.registerExtensions(extensionNamespacesManager, extensionNamespaceSupportCollection);

        baseTransformer.transform(source, result);
    }

    @Override
    public void setParameter(String name, Object value) {
        baseTransformer.setParameter(name, value);
    }

    @Override
    public Object getParameter(String name) {
        return baseTransformer.getParameter(name);
    }

    @Override
    public void clearParameters() {
        baseTransformer.clearParameters();
    }

    @Override
    public void setURIResolver(URIResolver uriResolver) {
        baseTransformer.setURIResolver(uriResolver);
    }

    @Override
    public URIResolver getURIResolver() {
        return baseTransformer.getURIResolver();
    }

    @Override
    public void setOutputProperties(Properties properties) {
        baseTransformer.setOutputProperties(properties);
    }

    @Override
    public Properties getOutputProperties() {
        return baseTransformer.getOutputProperties();
    }

    @Override
    public void setOutputProperty(String name, String value) throws IllegalArgumentException {
        baseTransformer.setOutputProperty(name, value);
    }

    @Override
    public String getOutputProperty(String name) throws IllegalArgumentException {
        return baseTransformer.getOutputProperty(name);
    }

    @Override
    public void setErrorListener(ErrorListener errorListener) throws IllegalArgumentException {
        baseTransformer.setErrorListener(errorListener);
    }

    @Override
    public ErrorListener getErrorListener() {
        return baseTransformer.getErrorListener();
    }
}