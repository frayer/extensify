package org.extensify.transform.xalan.extensions.groovy

import org.junit.Test
import static org.junit.Assert.*
import org.apache.xalan.extensions.ExpressionContext

class GroovyExtensionFunctionTest {

  @Test
  void testReturnValue() {
    def stringReturnClosure = { expressionContext, arg1, arg2, arg3 ->
      "closure called"
    }

    def additionClosure = { expressionContext, arg1, arg2, arg3 ->
      arg1 + arg2 + arg3
    }

    def extensionFunction1 = new GroovyExtensionFunction(stringReturnClosure)
    def extensionFunction2 = new GroovyExtensionFunction(additionClosure)

    assertEquals("closure called", extensionFunction1.call(null, [1, 2, 3].toArray()))
    assertEquals(6, extensionFunction2.call(null, [1, 2, 3].toArray()))
  }

}
