package org.extensify.transform.xalan.extensions.groovy

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*
import org.apache.xalan.templates.ElemTemplateElement
import static org.mockito.Mockito.*
import org.apache.xalan.extensions.ExpressionContext
import org.apache.xalan.transformer.TransformerImpl
import org.apache.xalan.templates.Stylesheet

class GroovyClosureExtensionHandlerTest {

  GroovyClosureExtensionHandler handler = null

  @Before
  void setUp() {
    handler = new GroovyClosureExtensionHandler("http://test-namespace")
  }

  @Test
  void testNamespace() {
    assertEquals("http://test-namespace", handler.getNamespaceUri())
  }

  @Test
  void testAddFunctionClosure() {
    handler.addFunctionClosure("function1") { null }
    handler.addFunctionClosure("function2") { null }

    assertTrue("function1 should be available.", handler.isFunctionAvailable("function1"))
    assertTrue("function2 should be available.", handler.isFunctionAvailable("function2"))
  }

  @Test
  void testAddElementClosure() {
    handler.addElementClosure("element1") {}
    handler.addElementClosure("element2") {}

    assertTrue("element1 should be available.", handler.isElementAvailable("element1"))
    assertTrue("element2 should be available.", handler.isElementAvailable("element2"))
  }

  @Test
  void testCallingClosures() {
    def vars = [:]

    handler.addElementClosure("set-variable") { ElemTemplateElement templateElement, TransformerImpl transformer, Stylesheet stylesheet ->
      vars[templateElement.getAttribute("name")] = templateElement.getAttribute("value")
    }
    handler.addFunctionClosure("variable") { ExpressionContext expressionContext, varName ->
      vars[varName]
    }

    ElemTemplateElement templateElement = mock(ElemTemplateElement.class)
    when(templateElement.getAttribute("name")).thenReturn("foo_1")
    when(templateElement.getAttribute("value")).thenReturn("bar_1")
    handler.processElement("set-variable", templateElement, null, null, null)

    templateElement = mock(ElemTemplateElement.class)
    when(templateElement.getAttribute("name")).thenReturn("foo_2")
    when(templateElement.getAttribute("value")).thenReturn("bar_2")
    handler.processElement("set-variable", templateElement, null, null, null)

    Vector functionArgs = new Vector()
    functionArgs.add("foo_1") // Variable name which is the only argument to the "variable" function.
    def value = handler.callFunction("variable", functionArgs, null, null)
    assertEquals("bar_1", value)

    functionArgs = new Vector()
    functionArgs.add("foo_2") // Variable name which is the only argument to the "variable" function.
    value = handler.callFunction("variable", functionArgs, null, null)
    assertEquals("bar_2", value)
  }

}
