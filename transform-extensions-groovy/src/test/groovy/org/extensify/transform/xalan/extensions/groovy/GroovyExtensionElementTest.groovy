package org.extensify.transform.xalan.extensions.groovy

import org.junit.Test

import static org.junit.Assert.*;

class GroovyExtensionElementTest {

  @Test
  void testReturnValue() {
    def stringReturnClosure = {elemTemplateElement, transformer, stylesheet ->
      "closure called"
    }

    def extensionElement = new GroovyExtensionElement(stringReturnClosure);
    assertEquals("closure called", extensionElement.call(null, null, null))
  }

}
