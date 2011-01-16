package org.extensify.transform.xalan;

import org.apache.xalan.extensions.ExtensionNamespaceSupport;
import org.apache.xalan.extensions.ExtensionNamespacesManager;

import java.util.Collection;
import java.util.Vector;

public class ExtensionNamespacesManagerUtil {

    /**
     * Private constructor since this is a utility class.
     */
    private ExtensionNamespacesManagerUtil() {
    }

    /**
     * Re-registers an ExtensionNamespaceSupport object even if the namespace it supports has
     * already been registered previously. Effectively this overrides any previous
     * <code>ExtensionNamespaceSupport</code> object defined for a given namespace.
     *
     * @param manager
     * @param extNSSupport
     */
    public static void registerExtension(ExtensionNamespacesManager manager, ExtensionNamespaceSupport extNSSupport) {
        Vector extensions = manager.getExtensions();
        String namespace = extNSSupport.getNamespace();

        int namespaceIndex = manager.namespaceIndex(namespace, extensions);
        if (namespaceIndex >= 0) {
            extensions.remove(namespaceIndex);
        }

        manager.registerExtension(extNSSupport);
    }

    /**
     *
     * @param manager
     * @param extNSSupportCollection
     */
    public static void registerExtensions(ExtensionNamespacesManager manager, Collection<ExtensionNamespaceSupport> extNSSupportCollection) {
        for (ExtensionNamespaceSupport extNSSupport : extNSSupportCollection) {
            ExtensionNamespacesManagerUtil.registerExtension(manager, extNSSupport);
        }
    }

}
