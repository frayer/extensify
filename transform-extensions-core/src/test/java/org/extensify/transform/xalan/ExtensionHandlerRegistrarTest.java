package org.extensify.transform.xalan;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.extensions.ExtensionNamespaceSupport;
import org.extensify.transform.xalan.ExtensionHandlerRegistrar;
import org.extensify.transform.xalan.NamespaceExtensionHandlerController;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtensionHandlerRegistrarTest {

    private static final String NS_1 = "namespace-1";
    private static final String NS_2 = "namespace-2";
    private static final String NS_3 = "namespace-3";

    private ExtensionHandlerRegistrar emptyRegistrar = null;
    private ExtensionHandlerRegistrar registrar = null;
    private ExtensionHandler handler1 = null;
    private ExtensionHandler handler2 = null;
    private ExtensionHandler handler3 = null;
    private ExtensionHandler handler4 = null;
    private ExtensionHandler handler5 = null;

    @Before
    public void setUp() throws Exception {
        emptyRegistrar = new ExtensionHandlerRegistrar();
        registrar = new ExtensionHandlerRegistrar();

        handler1 = mock(ExtensionHandler.class);
        handler2 = mock(ExtensionHandler.class);
        handler3 = mock(ExtensionHandler.class);
        handler4 = mock(ExtensionHandler.class);
        handler5 = mock(ExtensionHandler.class);
        
        registrar.registerExtensionHandler(NS_1, handler1);
        registrar.registerExtensionHandler(NS_2, handler2);
        registrar.registerExtensionHandler(NS_3, handler3);
        registrar.registerExtensionHandler(NS_1, handler4);
        registrar.registerExtensionHandler(NS_2, handler5);
    }

    /**
     * Tests that a call to <code>generateExtensionNamespaceSupportList</code> returns an empty
     * <code>List</code> if no ExtensionHandler objects have been registered with the registrar.
     *
     * @throws Exception
     */
    @Test
    public void testEmptyListWhenNothingRegistered() throws Exception {
        List<ExtensionNamespaceSupport> extensionNamespaceSupports = emptyRegistrar.generateExtensionNamespaceSupportList();

        assertNotNull(extensionNamespaceSupports);
        assertEquals(0, extensionNamespaceSupports.size());
    }

    @Test
    public void testListSizeEqualsNumberOfUniqueNamespaces() throws Exception {
        List<ExtensionNamespaceSupport> extensionNamespaceSupports = registrar.generateExtensionNamespaceSupportList();

        assertEquals(3, extensionNamespaceSupports.size());

        /*
         * Assert that the namespaces of the individual ExtensionNamespaceSupport objects are
         * indeed unique by adding them to a Set and checking the size.
         */
        Set uniqueNamespaces = new HashSet();
        for (ExtensionNamespaceSupport ens : extensionNamespaceSupports) {
            uniqueNamespaces.add(ens.getNamespace());
        }
        assertEquals(3, uniqueNamespaces.size());

        /*
         * Assert that the namspaces are what we expected.
         */
        assertTrue(uniqueNamespaces.contains(NS_1));
        assertTrue(uniqueNamespaces.contains(NS_2));
        assertTrue(uniqueNamespaces.contains(NS_3));
    }

    @Test
    public void testCorrectHandlersAssigned() throws Exception {
        List<ExtensionNamespaceSupport> extensionNamespaceSupports = registrar.generateExtensionNamespaceSupportList();

        ExtensionNamespaceSupport enSupport1 = null;
        ExtensionNamespaceSupport enSupport2 = null;
        ExtensionNamespaceSupport enSupport3 = null;

        NamespaceExtensionHandlerController nehGroup1 = null;
        NamespaceExtensionHandlerController nehGroup2 = null;
        NamespaceExtensionHandlerController nehGroup3 = null;

        for (ExtensionNamespaceSupport currentENSupport : extensionNamespaceSupports) {
            if (NS_1.equals(currentENSupport.getNamespace())) {
                enSupport1 = currentENSupport;
            } else if (NS_2.equals(currentENSupport.getNamespace())) {
                enSupport2 = currentENSupport;
            } else if (NS_3.equals(currentENSupport.getNamespace())) {
                enSupport3 = currentENSupport;
            }
        }

        assertEquals(NS_1, enSupport1.getNamespace());
        assertEquals(NS_2, enSupport2.getNamespace());
        assertEquals(NS_3, enSupport3.getNamespace());

        nehGroup1 = (NamespaceExtensionHandlerController) enSupport1.launch();
        nehGroup2 = (NamespaceExtensionHandlerController) enSupport2.launch();
        nehGroup3 = (NamespaceExtensionHandlerController) enSupport3.launch();

        assertEquals("Should be 2 ExtensionHandlers for NS_1", 2, nehGroup1.getExtensionHandlers().size());
        assertEquals("Should be 2 ExtensionHandlers for NS_2", 2, nehGroup2.getExtensionHandlers().size());
        assertEquals("Should be 1 ExtensionHandler for NS_3", 1, nehGroup3.getExtensionHandlers().size());
    }
}
