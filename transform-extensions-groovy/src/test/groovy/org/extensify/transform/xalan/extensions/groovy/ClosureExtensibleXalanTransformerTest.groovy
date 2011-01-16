package org.extensify.transform.xalan.extensions.groovy

import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import org.apache.commons.io.IOUtils
import org.apache.xalan.extensions.ExpressionContext
import org.apache.xalan.templates.ElemTemplateElement
import org.custommonkey.xmlunit.XMLAssert
import org.junit.Before
import org.junit.Test
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNotNull

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
    closureTransformer.addExtensionElement(EXTENSION_TEST_NS, "set-variable") { attributes ->
      variables[attributes['name']] = attributes['value']
    }

    runAndAssertPeopleTransformation(closureTransformer, "people.xml", "people_expected.xml")
  }

  @Test
  void testNodeListOfExtensionElement() {
    def closureTransformer = createTransformer("people_xform_testNodeListOfExtensionElement.xsl")

    def variables = [:]
    NodeList extensionWithChildElementNodeList = null
    def extensionWithChildElementNameAttribute
    def extensionWithChildElementValueAttribute

    closureTransformer.addExtensionFunction(EXTENSION_TEST_NS, "variable") { name ->
      variables[name]
    }
    closureTransformer.addExtensionElement(EXTENSION_TEST_NS, "set-variable") { attributes ->
      variables[attributes['name']] = attributes['value']
    }
    closureTransformer.addExtensionElement(EXTENSION_TEST_NS, "extension-with-childElement") { attributes, nodeList ->
      extensionWithChildElementNameAttribute = attributes['name']
      extensionWithChildElementValueAttribute = attributes['value']
      extensionWithChildElementNodeList = nodeList
    }

    runAndAssertPeopleTransformation(closureTransformer, "people.xml", "people_expected.xml")

    // Validate details about the NodeList passed into the "set-variable" Extension Element.
    assertEquals("var://context/foo3", extensionWithChildElementNameAttribute)
    assertEquals("third set variable", extensionWithChildElementValueAttribute)

    assertNotNull(extensionWithChildElementNodeList)
    assertEquals(1, extensionWithChildElementNodeList.length)

    Node firstLevelNode = extensionWithChildElementNodeList.item(0)
    assertEquals("firstLevel", firstLevelNode.nodeName)

    NodeList firstLevelChildren = firstLevelNode.getChildNodes();
    assertEquals(1, firstLevelChildren.length)

    Node secondLevelNode = firstLevelChildren.item(0)
    assertEquals("secondLevel", secondLevelNode.nodeName)
    assertEquals(1, secondLevelNode.getChildNodes().length)

    Node textNode = secondLevelNode.getFirstChild()
    assertEquals("text node value", textNode.getTextContent())
  }

}