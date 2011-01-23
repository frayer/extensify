package org.extensify.transform.xalan.extensions.groovy

import org.junit.Test

import static org.junit.Assert.*
import org.apache.xalan.templates.ElemTemplateElement
import org.apache.xalan.templates.Stylesheet
import org.apache.xalan.transformer.TransformerImpl;

class GroovyExtensionElementTest {

  @Test
  void testReturnValue() {
    def stringReturnClosure = { ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet ->
      "closure called"
    }

    def extensionElement = new GroovyExtensionElement(stringReturnClosure);
    assertEquals("closure called", extensionElement.call(null, null, null))
  }

}
