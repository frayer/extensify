package org.extensify.transform.xalan.extensions.groovy

import org.junit.Test
import static org.junit.Assert.*
import org.apache.xalan.extensions.ExpressionContext
import org.w3c.dom.NodeList
import org.w3c.dom.Node
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when
import static org.mockito.Mockito.verify
import org.apache.xpath.objects.XNodeSet
import org.apache.xpath.objects.XObject

class GroovyExtensionFunctionTest {

  @Test
  void testReturnValue() {
    def stringReturnClosure = { ExpressionContext expressionContext, arg1, arg2, arg3 ->
      "closure called"
    }

    def additionClosure = { ExpressionContext expressionContext, arg1, arg2, arg3 ->
      arg1 + arg2 + arg3
    }

    def extensionFunction1 = new GroovyExtensionFunction(stringReturnClosure)
    def extensionFunction2 = new GroovyExtensionFunction(additionClosure)

    assertEquals("closure called", extensionFunction1.call(null, [1, 2, 3].toArray()))
    assertEquals(6, extensionFunction2.call(null, [1, 2, 3].toArray()))
  }

  @Test
  void testFunctionParameterTypeCoercion() {
    /*
     * Assert that a Closure parameter with no explicit type receives the exact
     * type that is passed in on the call to the Extension Function.
     */
    def noTypeDefClosure = { arg ->
      assertEquals(String.class, arg.class)
    }
    def extensionFunction1 = new GroovyExtensionFunction(noTypeDefClosure)
    extensionFunction1.call(null, 'String class type')

    /*
     * Verify that a XNodeSet gets converted to an org.w3c.dom.NodeList when
     * the type is defined on the Closure parameter.
     */
    def typeDefClosure = { NodeList arg ->
      assertNotNull(arg)
      Node node = arg.item(0)
      assertEquals('test-node-name', node.getLocalName())
    }
    def extensionFunction2 = new GroovyExtensionFunction(typeDefClosure)

    def mockXNodeSet = mock(XNodeSet)
    def mockNodeList = mock(NodeList)
    def mockNode = mock(Node)
    when(mockXNodeSet.getType()).thenReturn(XObject.CLASS_NODESET);
    when(mockXNodeSet.nodelist()).thenReturn(mockNodeList)
    when(mockNodeList.item(0)).thenReturn(mockNode)
    when(mockNode.getLocalName()).thenReturn('test-node-name')

    extensionFunction2.call(null, mockXNodeSet)

    verify(mockNodeList).item(0)
    verify(mockNode).getLocalName()
  }

}
