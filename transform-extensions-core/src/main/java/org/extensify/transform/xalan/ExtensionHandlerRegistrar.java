package org.extensify.transform.xalan;

import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.extensions.ExtensionNamespaceSupport;

import java.util.*;

/**
 * Allows for the registration of 1 or more <code>ExtensionHandler</code> instances which are
 * tied to a namespace.  From the instances registered, a <code>List</code> of
 * <code>ExtensionNamespaceSupport</code> objects can then be generated which could be used
 * to register these <code>ExtensionHandler's</code> with a Xalan Transformer.
 */
public class ExtensionHandlerRegistrar {

    public static final String DEFAULT_HANDLER_CLASS = NamespaceExtensionHandlerController.class.getName();

    private Map<String, Set<ExtensionHandler>> extensionHandlersForNamespace = null;

    public ExtensionHandlerRegistrar() {
        extensionHandlersForNamespace = new HashMap<String, Set<ExtensionHandler>>();
    }

    /**
     * Registers the given <code>ExtensionHandler</code> under the given namespace.
     *
     * @param namespaceUri the Namespace URI to register the handler to.
     * @param extensionHandler the handler to register.
     */
    public void registerExtensionHandler(String namespaceUri, ExtensionHandler extensionHandler) {
        if (extensionHandlersForNamespace.containsKey(namespaceUri)) {
            extensionHandlersForNamespace.get(namespaceUri).add(extensionHandler);
        } else {
            Set<ExtensionHandler> extensionHandlers = new LinkedHashSet<ExtensionHandler>();
            extensionHandlers.add(extensionHandler);
            extensionHandlersForNamespace.put(namespaceUri, extensionHandlers);
        }
    }

    public List<ExtensionNamespaceSupport> generateExtensionNamespaceSupportList() {
        List<ExtensionNamespaceSupport> result = new ArrayList<ExtensionNamespaceSupport>(extensionHandlersForNamespace.size());

        for (String namespace : extensionHandlersForNamespace.keySet()) {
            Set<ExtensionHandler> extensionHandlers = extensionHandlersForNamespace.get(namespace);
            Object[] constructorArgs = new Object[] { namespace, extensionHandlers };
            ExtensionNamespaceSupport enSupport = new ExtensionNamespaceSupport(namespace, DEFAULT_HANDLER_CLASS, constructorArgs);
            result.add(enSupport);
        }

        return result;
    }

}
