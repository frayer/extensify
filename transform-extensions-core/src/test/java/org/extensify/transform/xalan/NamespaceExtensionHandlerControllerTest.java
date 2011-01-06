package org.extensify.transform.xalan;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.functions.FuncExtFunction;
import org.extensify.transform.xalan.NamespaceExtensionHandlerController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.LinkedHashSet;
import java.util.Vector;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class NamespaceExtensionHandlerControllerTest {

    private NamespaceExtensionHandlerController nehController = null;
    ExtensionHandler handler1 = null;
    ExtensionHandler handler2 = null;
    ExtensionHandler handler3 = null;

    @Before
    public void setUp() throws Exception {
        handler1 = mock(ExtensionHandler.class);
        handler2 = mock(ExtensionHandler.class);
        handler3 = mock(ExtensionHandler.class);

        when(handler1.isFunctionAvailable("function-a")).thenReturn(Boolean.TRUE);
        when(handler1.isFunctionAvailable("function-b")).thenReturn(Boolean.FALSE);
        when(handler1.isFunctionAvailable("function-c")).thenReturn(Boolean.FALSE);
        when(handler1.isElementAvailable("element-a")).thenReturn(Boolean.TRUE);
        when(handler1.isElementAvailable("element-b")).thenReturn(Boolean.FALSE);
        when(handler1.isElementAvailable("element-c")).thenReturn(Boolean.FALSE);
        when(handler1.callFunction(anyString(), Matchers.<Vector>anyObject(), Matchers.<Object>anyObject(), Matchers.<ExpressionContext>anyObject())).thenReturn("handler1 arity=4 result");
        when(handler1.callFunction(Matchers.<FuncExtFunction>anyObject(), Matchers.<Vector>anyObject(), Matchers.<ExpressionContext>anyObject())).thenReturn("handler1 arity=3 result");

        when(handler2.isFunctionAvailable("function-a")).thenReturn(Boolean.FALSE);
        when(handler2.isFunctionAvailable("function-b")).thenReturn(Boolean.TRUE);
        when(handler2.isFunctionAvailable("function-c")).thenReturn(Boolean.FALSE);
        when(handler2.isElementAvailable("element-a")).thenReturn(Boolean.FALSE);
        when(handler2.isElementAvailable("element-b")).thenReturn(Boolean.TRUE);
        when(handler2.isElementAvailable("element-c")).thenReturn(Boolean.FALSE);
        when(handler2.callFunction(anyString(), Matchers.<Vector>anyObject(), Matchers.<Object>anyObject(), Matchers.<ExpressionContext>anyObject())).thenReturn("handler2 arity=4 result");
        when(handler2.callFunction(Matchers.<FuncExtFunction>anyObject(), Matchers.<Vector>anyObject(), Matchers.<ExpressionContext>anyObject())).thenReturn("handler2 arity=3 result");

        when(handler3.isFunctionAvailable("function-a")).thenReturn(Boolean.TRUE);
        when(handler3.isFunctionAvailable("function-b")).thenReturn(Boolean.TRUE);
        when(handler3.isFunctionAvailable("function-c")).thenReturn(Boolean.FALSE);
        when(handler3.isElementAvailable("element-a")).thenReturn(Boolean.TRUE);
        when(handler3.isElementAvailable("element-b")).thenReturn(Boolean.TRUE);
        when(handler3.isElementAvailable("element-c")).thenReturn(Boolean.FALSE);

        LinkedHashSet<ExtensionHandler> extensionHandlers  = new LinkedHashSet<ExtensionHandler>();
        extensionHandlers.add(handler1);
        extensionHandlers.add(handler2);
        extensionHandlers.add(handler3);

        nehController = new NamespaceExtensionHandlerController("some-namespace", extensionHandlers);
    }

    @Test
    public void testGetNamespaceUri() throws Exception {
        assertEquals("some-namespace", nehController.getNamespaceUri());
    }

    @Test
    public void testExtensionHandlerCount() throws Exception {
        assertEquals(3, nehController.getExtensionHandlers().size());
    }

    /**
     * Tests the <code>NamespaceExtensionHandlerController.isFunctionAvailable</code> method. This
     * should look through its internal collection of ExtensionHandlers and return true if one
     * of them has the function available.
     *
     * @throws Exception
     */
    @Test
    public void testFunctionAvailable() throws Exception {
        assertTrue("\"function-a\" should be available.", nehController.isFunctionAvailable("function-a"));
        assertTrue("\"function-b\" should be available.", nehController.isFunctionAvailable("function-b"));
        assertFalse("\"function-c\" should not be available.", nehController.isFunctionAvailable("function-c"));
    }

    /**
     * Tests the <code>NamespaceExtensionHandlerController.isElementAvailable</code> method. This
     * should look through its internal collection of ExtensionHandlers and return true if one
     * of them has the element available.
     *
     * @throws Exception
     */
    @Test
    public void testElementAvailable() throws Exception {
        assertTrue("\"element-a\" should be available.", nehController.isElementAvailable("element-a"));
        assertTrue("\"element-b\" should be available.", nehController.isElementAvailable("element-b"));
        assertFalse("\"element-c\" should not be available.", nehController.isElementAvailable("element-c"));
    }

    @Test
    public void testCorrectFunctionCalledForHandler1Arity3Signature() throws Exception {
        FuncExtFunction extFunction = mock(FuncExtFunction.class);
        when(extFunction.getFunctionName()).thenReturn("function-a");
        Vector args = mock(Vector.class);
        ExpressionContext context = mock(ExpressionContext.class);

        String functionResult = (String) nehController.callFunction(extFunction, args, context);
        assertEquals("handler1 arity=3 result", functionResult);

        verify(handler1).callFunction(Matchers.<FuncExtFunction>anyObject(), Matchers.<Vector>anyObject(), Matchers.<ExpressionContext>anyObject());
        verify(handler2, never()).callFunction(Matchers.<FuncExtFunction>anyObject(), Matchers.<Vector>anyObject(), Matchers.<ExpressionContext>anyObject());
        /*
         * Even though "element-a" is available in handler3, it should never be called because it was first available
         * in handler1.  The first handler to respond true from calling "isElementAvailable" should process
         * the request.  In this case that is handler1.
         */
        verify(handler3, never()).callFunction(Matchers.<FuncExtFunction>anyObject(), Matchers.<Vector>anyObject(), Matchers.<ExpressionContext>anyObject());
    }

    @Test
    public void testCorrectFunctionCalledForHandler1Arity4Signature() throws Exception {
        Vector args = mock(Vector.class);
        ExpressionContext context = mock(ExpressionContext.class);

        String functionResult = (String) nehController.callFunction("function-a", args, "method-key-a", context);
        assertEquals("handler1 arity=4 result", functionResult);

        verify(handler1).callFunction(Matchers.eq("function-a"), Matchers.<Vector>anyObject(), Matchers.eq("method-key-a"), Matchers.<ExpressionContext>anyObject());
        verify(handler2, never()).callFunction(Matchers.<String>anyObject(), Matchers.<Vector>anyObject(), Matchers.<Object>anyObject(), Matchers.<ExpressionContext>anyObject());
        /*
         * Even though "element-a" is available in handler3, it should never be called because it was first available
         * in handler1.  The first handler to respond true from calling "isElementAvailable" should process
         * the request.  In this case that is handler1.
         */
        verify(handler3, never()).callFunction(Matchers.<String>anyObject(), Matchers.<Vector>anyObject(), Matchers.<Object>anyObject(), Matchers.<ExpressionContext>anyObject());
    }

    @Test
    public void testCorrectFunctionCalledForHandler2Arity3Signature() throws Exception {
        FuncExtFunction extFunction = mock(FuncExtFunction.class);
        when(extFunction.getFunctionName()).thenReturn("function-b");
        Vector args = mock(Vector.class);
        ExpressionContext context = mock(ExpressionContext.class);

        String functionResult = (String) nehController.callFunction(extFunction, args, context);
        assertEquals("handler2 arity=3 result", functionResult);

        verify(handler2).callFunction(Matchers.<FuncExtFunction>anyObject(), Matchers.<Vector>anyObject(), Matchers.<ExpressionContext>anyObject());
        verify(handler1, never()).callFunction(Matchers.<FuncExtFunction>anyObject(), Matchers.<Vector>anyObject(), Matchers.<ExpressionContext>anyObject());
        verify(handler3, never()).callFunction(Matchers.<FuncExtFunction>anyObject(), Matchers.<Vector>anyObject(), Matchers.<ExpressionContext>anyObject());
    }

    @Test
    public void testCorrectFunctionCalledForHandler2Arity4Signature() throws Exception {
        Vector args = mock(Vector.class);
        ExpressionContext context = mock(ExpressionContext.class);

        String functionResult = (String) nehController.callFunction("function-b", args, "method-key-b", context);
        assertEquals("handler2 arity=4 result", functionResult);

        verify(handler2).callFunction(Matchers.eq("function-b"), Matchers.<Vector>anyObject(), Matchers.eq("method-key-b"), Matchers.<ExpressionContext>anyObject());
        verify(handler1, never()).callFunction(Matchers.<String>anyObject(), Matchers.<Vector>anyObject(), Matchers.<Object>anyObject(), Matchers.<ExpressionContext>anyObject());
        /*
         * Even though "element-a" is available in handler3, it should never be called because it was first available
         * in handler1.  The first handler to respond true from calling "isElementAvailable" should process
         * the request.  In this case that is handler1.
         */
        verify(handler3, never()).callFunction(Matchers.<String>anyObject(), Matchers.<Vector>anyObject(), Matchers.<Object>anyObject(), Matchers.<ExpressionContext>anyObject());
    }

    @Test
    public void testCorrectElementCalled_handler1() throws Exception {
        nehController.processElement("element-a", mock(ElemTemplateElement.class), mock(TransformerImpl.class), mock(Stylesheet.class), "element-a-key");

        verify(handler1).processElement(Matchers.eq("element-a"), Matchers.<ElemTemplateElement>anyObject(), Matchers.<TransformerImpl>anyObject(), Matchers.<Stylesheet>anyObject(), Matchers.eq("element-a-key"));
        verify(handler2, never()).processElement(Matchers.<String>anyObject(), Matchers.<ElemTemplateElement>anyObject(), Matchers.<TransformerImpl>anyObject(), Matchers.<Stylesheet>anyObject(), Matchers.<Object>anyObject());
        /*
         * Even though "element-a" is available in handler3, it should never be called because it was first available
         * in handler1.  The first handler to respond true from calling "isElementAvailable" should process
         * the request.  In this case that is handler1.
         */
        verify(handler3, never()).processElement(Matchers.<String>anyObject(), Matchers.<ElemTemplateElement>anyObject(), Matchers.<TransformerImpl>anyObject(), Matchers.<Stylesheet>anyObject(), Matchers.<Object>anyObject());
    }

    @Test
    public void testCorrectElementCalled_handler2() throws Exception {
        nehController.processElement("element-b", mock(ElemTemplateElement.class), mock(TransformerImpl.class), mock(Stylesheet.class), "element-b-key");

        verify(handler2).processElement(Matchers.eq("element-b"), Matchers.<ElemTemplateElement>anyObject(), Matchers.<TransformerImpl>anyObject(), Matchers.<Stylesheet>anyObject(), Matchers.eq("element-b-key"));
        verify(handler1, never()).processElement(Matchers.<String>anyObject(), Matchers.<ElemTemplateElement>anyObject(), Matchers.<TransformerImpl>anyObject(), Matchers.<Stylesheet>anyObject(), Matchers.<Object>anyObject());
        /*
         * Even though "element-b" is available in handler3, it should never be called because it was first available
         * in handler2.  The first handler to respond true from calling "isElementAvailable" should process
         * the request.  In this case that is handler2.
         */
        verify(handler3, never()).processElement(Matchers.<String>anyObject(), Matchers.<ElemTemplateElement>anyObject(), Matchers.<TransformerImpl>anyObject(), Matchers.<Stylesheet>anyObject(), Matchers.<Object>anyObject());
    }

}