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

  private static final String XML_XSL_ROOT_PATH = "/org/extensify/transform/xalan/extensions/groovy/"
  private static final String EXTENSION_TEST_NS = "http://www.xsltextensions.org/extensions"

  Source xmlSource = null
  Source xslSource = null

  @Before
  void setUp() {
  }

  private ClosureExtensibleXalanTransformer createTransformer(xslFileName) {
    def xslPath = XML_XSL_ROOT_PATH + xslFileName
    def xslSource = new StreamSource(ClosureExtensibleXalanTransformerTest.class.getResourceAsStream(xslPath))

    def transformer = TransformerFactory.newInstance().newTransformer(xslSource)
    new ClosureExtensibleXalanTransformer(transformer)
  }

  private Source getXMLSourceFromFile(String xmlFileName) {
    def xmlPath = XML_XSL_ROOT_PATH + xmlFileName
    new StreamSource(ClosureExtensibleXalanTransformerTest.getClass().getResourceAsStream(xmlPath))
  }

  private String getXMLString(xmlFileName) {
    def xmlPath = XML_XSL_ROOT_PATH + xmlFileName
    IOUtils.toString(ClosureExtensibleXalanTransformerTest.getClass().getResourceAsStream(xmlPath))
  }

  def runAndAssertPeopleTransformation(transformer, inputXMLFileName, expectedXMLFileName) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
    StreamResult result = new StreamResult(outputStream)

    def xmlSource = getXMLSourceFromFile("people.xml")
    transformer.transform(xmlSource, result)

    def expectedXML = getXMLString(expectedXMLFileName)
    def transformedXML = outputStream.toString()

    XMLAssert.assertXMLEqual(expectedXML, transformedXML)
  }

  @Test
  void testGroovyExtensions() {
    def closureTransformer = createTransformer("people_xform.xsl")
    def variables = [:]

    closureTransformer.addExtensionFunction(EXTENSION_TEST_NS, "variable") { ExpressionContext context, name ->
      variables[name]
    }
    closureTransformer.addExtensionElement(EXTENSION_TEST_NS, "set-variable") { ElemTemplateElement elemTemplateElement, transformer, stylesheet ->
      def name = elemTemplateElement.getAttribute('name')
      def value = elemTemplateElement.getAttribute('value')
      variables[name] = value
    }

    runAndAssertPeopleTransformation(closureTransformer, "people.xml", "people_expected.xml")
  }

  @Test
  void testGroovyExtensionsNoXalanClassesInClosureParameters() {
    def closureTransformer = createTransformer("people_xform.xsl")
    def variables = [:]

    closureTransformer.addExtensionFunction(EXTENSION_TEST_NS, "variable") { name ->
      variables[name]
    }
    closureTransformer.addExtensionElement(EXTENSION_TEST_NS, "set-variable") { args ->
      variables[args['name']] = args['value']
    }

    runAndAssertPeopleTransformation(closureTransformer, "people.xml", "people_expected.xml")
  }

}