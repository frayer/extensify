package org.extensify.transform.xalan.extensions.groovy

import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import org.apache.commons.io.IOUtils
import org.apache.xalan.templates.ElemTemplateElement
import org.custommonkey.xmlunit.XMLAssert
import org.junit.Before
import org.junit.Test
import org.apache.xalan.extensions.ExpressionContext

class ClosureExtensibleXalanTransformerTest {

  private static final String EXTENSION_TEST_NS = "http://www.xsltextensions.org/extensions"
  private static final String XSL_PATH = "/org/extensify/transform/xalan/extensions/groovy/people_xform.xsl"
  private static final String XML_PATH = "/org/extensify/transform/xalan/extensions/groovy/people.xml"
  private static final String EXPECTED_XML_PATH = "/org/extensify/transform/xalan/extensions/groovy/people_expected.xml"

  ClosureExtensibleXalanTransformer closureTransformer = null
  Source xmlSource = null
  Source xslSource = null

  @Before
  void setUp() {
    xmlSource = new StreamSource(ClosureExtensibleXalanTransformerTest.class.getResourceAsStream(XML_PATH))
    xslSource = new StreamSource(ClosureExtensibleXalanTransformerTest.class.getResourceAsStream(XSL_PATH))

    def transformer = TransformerFactory.newInstance().newTransformer(xslSource)
    closureTransformer = new ClosureExtensibleXalanTransformer(transformer)
  }

  def runAndAssertPeopleTransformation() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
    StreamResult result = new StreamResult(outputStream)

    closureTransformer.transform(xmlSource, result)

    def expectedXML = IOUtils.toString(ClosureExtensibleXalanTransformerTest.class.getResourceAsStream(EXPECTED_XML_PATH))
    def transformedXML = outputStream.toString()

    XMLAssert.assertXMLEqual(expectedXML, transformedXML)
  }

  @Test
  void testGroovyExtensions() {
    def variables = [:]

    closureTransformer.addExtensionFunction(EXTENSION_TEST_NS, "variable") { ExpressionContext context, name ->
      variables[name]
    }
    closureTransformer.addExtensionElement(EXTENSION_TEST_NS, "set-variable") { ElemTemplateElement elemTemplateElement, transformer, stylesheet ->
      def name = elemTemplateElement.getAttribute('name')
      def value = elemTemplateElement.getAttribute('value')
      variables[name] = value
    }

    runAndAssertPeopleTransformation()
  }

  @Test
  void testGroovyExtensionsNoXalanClassesInClosureParameters() {
    def variables = [:]

    closureTransformer.addExtensionFunction(EXTENSION_TEST_NS, "variable") { name ->
      variables[name]
    }
    closureTransformer.addExtensionElement(EXTENSION_TEST_NS, "set-variable") { args ->
      variables[args['name']] = args['value']
    }

    runAndAssertPeopleTransformation()
  }

}