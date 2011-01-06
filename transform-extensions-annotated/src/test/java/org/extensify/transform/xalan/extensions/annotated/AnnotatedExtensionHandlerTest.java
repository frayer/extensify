package org.extensify.transform.xalan.extensions.annotated;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AnnotatedExtensionHandlerTest {

    AnnotatedExtensionHandler handler = null;

    @Before
    public void setUp() {
        AnnotatedExtension annotatedExtension = new AnnotatedExtension();
        handler = new AnnotatedExtensionHandler<AnnotatedExtension>(annotatedExtension);
    }

    @Test
    public void testNamespaceUri() {
        assertEquals("http://test-namespace", handler.getNamespaceUri());
    }

    @Test
    public void testScriptLanguage() {
        assertEquals("javaclass", handler.getScriptLanguage());
    }

    @Test
    public void testExtensionElementAvailable() {
        assertTrue("'ext-element-1' element should be available.", handler.isElementAvailable("ext-element-1"));
        assertTrue("'ext-element-2' element should be available.", handler.isElementAvailable("ext-element-2"));
        assertFalse("'non-existant' element should not be available.", handler.isElementAvailable("non-existant"));
    }

    @Test
    public void testExtensionFunctionAvailable() {
        assertTrue("'ext-function-1' function should be available.", handler.isFunctionAvailable("ext-function-1"));
        assertTrue("'ext-function-2' function should be available.", handler.isFunctionAvailable("ext-function-2"));
        assertFalse("'non-existant' function should not be available.", handler.isFunctionAvailable("non-existant"));
    }

    @Test
    public void testCorrectElementsCalled() throws Exception {
        ElemTemplateElement templateElement1 = mock(ElemTemplateElement.class);
        ElemTemplateElement templateElement2 = mock(ElemTemplateElement.class);

        handler.processElement("ext-element-1", templateElement1, null, null, "ext-element-1-object-key");
        handler.processElement("ext-element-2", templateElement2, null, null, "ext-element-2-object-key");

        verify(templateElement1).getAttribute("attribute1");
        verify(templateElement2).getAttribute("attribute2");
    }

    @Test
    public void testCorrectFunctionsCalled() throws Exception {
        ExpressionContext context1 = mock(ExpressionContext.class);
        ExpressionContext context2 = mock(ExpressionContext.class);
        ExpressionContext context3 = mock(ExpressionContext.class);

        Vector vector = new Vector(0);
        assertEquals("extensionFunction1 called", handler.callFunction("ext-function-1", vector, "method-key", context1));
        assertEquals("extensionFunction2 called", handler.callFunction("ext-function-2", vector, "method-key", context2));

        Vector additionVector = new Vector();
        additionVector.add(Integer.valueOf(5));
        additionVector.add(Integer.valueOf(3));
        assertEquals(Integer.valueOf(8), handler.callFunction("add", additionVector, "method-key", context3));

        verify(context1).getContextNode();
        verify(context2).getContextNode();
        verify(context3).getContextNode();
    }

    @XSLTExtension(namespace = "http://test-namespace")
    public class AnnotatedExtension {
        @XSLTExtensionElement("ext-element-1")
        public Object extensionElement1(ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet) {
            elemTemplateElement.getAttribute("attribute1");
            return "extensionElement1 called";
        }

        @XSLTExtensionElement("ext-element-2")
        public Object extensionElement2(ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet) {
            elemTemplateElement.getAttribute("attribute2");
            return "extensionElement2 called";
        }

        @XSLTExtensionFunction("ext-function-1")
        public Object extensionFunction1(ExpressionContext context) {
            context.getContextNode(); // Just calling this to verify it's picked up by the mock.
            return "extensionFunction1 called";
        }

        @XSLTExtensionFunction("ext-function-2")
        public Object extensionFunction2(ExpressionContext context) {
            context.getContextNode(); // Just calling this to verify it's picked up by the mock.
            return "extensionFunction2 called";
        }

        @XSLTExtensionFunction("add")
        public Object add(ExpressionContext context, Integer a, Integer b) {
            context.getContextNode(); // Just calling this to verify it's picked up by the mock.
            return Integer.valueOf(a.intValue() + b.intValue());
        }
    }

}
