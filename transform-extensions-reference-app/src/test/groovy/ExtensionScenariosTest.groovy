import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import org.apache.commons.io.IOUtils
import org.extensify.transform.xalan.extensions.groovy.ClosureExtensibleXalanTransformer
import org.junit.Before
import org.junit.Test
import org.w3c.dom.Element
import org.w3c.dom.traversal.NodeIterator
import static junit.framework.Assert.assertTrue
import static org.extensify.matchers.XMLStringMatcher.equalToXMLStringIgnoringWhitespace
import static org.hamcrest.MatcherAssert.assertThat

class ExtensionScenariosTest {

  private static final String EXT1_NS = "ext1"
  private static final String EXT2_NS = "ext2"

  Source xmlSource = null
  Source xslSource = null

  @Before
  void setUp() {
  }

  private ClosureExtensibleXalanTransformer createTransformer(xslFileResourcePath) {
    def xslSource = new StreamSource(ExtensionScenariosTest.getClass().getResourceAsStream(xslFileResourcePath))

    def transformer = TransformerFactory.newInstance().newTransformer(xslSource)
    new ClosureExtensibleXalanTransformer(transformer)
  }

  private Source getXMLSource(String xmlFileResourcePath) {
    new StreamSource(ExtensionScenariosTest.getClass().getResourceAsStream(xmlFileResourcePath))
  }

  private String getXMLString(xmlFileResourcePath) {
    IOUtils.toString(ExtensionScenariosTest.getClass().getResourceAsStream(xmlFileResourcePath))
  }

  def runAndAssertTransformation(transformer, inputXMLPath, expectedXMLPath) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
    StreamResult result = new StreamResult(outputStream)

    def xmlSource = getXMLSource(inputXMLPath)
    transformer.transform(xmlSource, result)

    def expectedXML = getXMLString(expectedXMLPath)
    def transformedXML = outputStream.toString()

    assertThat(expectedXML, equalToXMLStringIgnoringWhitespace(transformedXML))
  }

  /**
   * Test that more than one extension namespace is supported in a single XSLT.
   */
  @Test
  void testMultipleExtensionNamespacesSupported() {
    def closureTransformer = createTransformer('/testMultipleExtensionNamespacesSupported.xsl')
    
    closureTransformer.addExtensionFunction(EXT1_NS, 'echo') { message ->
      "$message from ext1".toString()
    }
    
    closureTransformer.addExtensionFunction(EXT2_NS, 'echo') { message ->
      "$message from ext2".toString()
    }

    closureTransformer.addExtensionElement(EXT1_NS, "echo-attributes") { attributes ->
      def attr1 = attributes['attr1']
      def attr2 = attributes['attr2']
      "$attr1 $attr2 from ext1".toString()
    }
    
    closureTransformer.addExtensionElement(EXT2_NS, "echo-attributes") { attributes ->
      def attr1 = attributes['attr1']
      def attr2 = attributes['attr2']
      "$attr1 $attr2 from ext2".toString()
    }
    
    runAndAssertTransformation(closureTransformer, '/people-input.xml', '/expected-testMultipleExtensionNamespacesSupported.xml')
  }

  /**
   * Tests that XSL variables can be passed to Extension Functions.
   */  
  @Test
  void testExtensionsSupportXSLVariables() {
    def closureTransformer = createTransformer('/testExtensionsSupportXSLVariables.xsl')
    
    closureTransformer.addExtensionFunction(EXT1_NS, 'echo') { message ->
      "$message from ext1".toString()
    }
    
    closureTransformer.addExtensionFunction(EXT2_NS, 'echo') { message ->
      "$message from ext2".toString()
    }
    
    runAndAssertTransformation(closureTransformer, '/people-input.xml', '/expected-testExtensionsSupportXSLVariables.xml')
  }

  /**
   * Test that extension functions can take a variable number of arguments.
   */
  @Test
  void testFunctionsSupportVariableLengthArguments() {
    def closureTransformer = createTransformer('/testFunctionsSupportVariableLengthArguments.xsl')
    
    /*
     * Extension Functions with optional parameters MUST be defined with a
     * default value in the Closure parameter definition
     */
    closureTransformer.addExtensionFunction(EXT1_NS, 'concat') { arg1, arg2='', arg3='' ->
      "${arg1}${arg2}${arg3}".toString()
    }
    
    runAndAssertTransformation(closureTransformer, '/people-input.xml', '/expected-testFunctionsSupportVariableLengthArguments.xml')
  }

  @Test
  void testNodeSetParameterBecomesNodeIterator() {
    def closureTransformer = createTransformer('/testNodeSetParameterBecomesDOMObject.xsl')

    /*
     * By default, when an XSLT Node Set is passed as an argument to an
     * extension function, that argument is converted to a org.w3c.dom.traversal.NodeIterator
     * type before being passed to the Closure if the Closure parameter has no
     * type definition.
     */
    closureTransformer.addExtensionFunction(EXT1_NS, 'first-names-by-person') { personNodeIterator ->
      assertTrue personNodeIterator instanceof NodeIterator

      def firstNames = []
      Element person
      while (person = personNodeIterator.nextNode()) {
        /*
         * The following JAXP syntax can be used to gain access to the text
         * value of the "person/firstName" element.
         */
        // firstNames << person.getElementsByTagName("firstName").item(0).childNodes.item(0).textContent

        /*
         * The following syntax takes advantage of the DOMCategory Groovy
         * Category which makes working with the DOM a little easier.
         */
        use (groovy.xml.dom.DOMCategory) {
          firstNames << person.firstName.text()
        }
      }

      return firstNames.join(', ')
    }

    /*
     * By default, when an XSLT Node Set is passed as an argument to an
     * extension function, that argument is converted to a org.w3c.dom.traversal.NodeIterator
     * type before being passed to the Closure if the Closure parameter has no
     * type definition.
     */
    closureTransformer.addExtensionFunction(EXT1_NS, 'last-names') { lastNameNodeIterator ->
      assertTrue lastNameNodeIterator instanceof NodeIterator

      def lastNames = []
      Element lastName
      while (lastName = lastNameNodeIterator.nextNode()) {
        /*
         * The following JAXP syntax can be used to gain access to the text
         * value of the "lastName" element.
         */
        // lastNames << lastName.childNodes.item(0).textContent

        /*
         * The following syntax takes advantage of the DOMCategory Groovy
         * Category which makes working with the DOM a little easier.
         */
        use (groovy.xml.dom.DOMCategory) {
          lastNames << lastName.text()
        }
      }

      return lastNames.join(', ')
    }

    runAndAssertTransformation(closureTransformer, '/people-input.xml', '/expected-testNodeSetParameterBecomesDOMObject.xml')
  }

  @Test
  void testNodeSetParameterBecomesNodeList() {
    def closureTransformer = createTransformer('/testNodeSetParameterBecomesDOMObject.xsl')

    /*
     * If an XSLT Node Set is passed as an argument to an extension function,
     * that argument is converted to a "org.w3c.dom.NodeList" type before being
     * passed to the Closure if the Closure parameter is also defined as a
     * "org.w3c.dom.NodeList" type.
     */
    closureTransformer.addExtensionFunction(EXT1_NS, 'first-names-by-person') { org.w3c.dom.NodeList personNodeList ->
      assertTrue personNodeList instanceof org.w3c.dom.NodeList

      def firstNames = []
      for (Element person : personNodeList) {
        use (groovy.xml.dom.DOMCategory) {
          firstNames << person.firstName.text()
        }
      }
      return firstNames.join(', ')
    }

    /*
     * If an XSLT Node Set is passed as an argument to an extension function,
     * that argument is converted to a "org.w3c.dom.NodeList" type before being
     * passed to the Closure if the Closure parameter is also defined as a
     * "org.w3c.dom.NodeList" type.
     */
    closureTransformer.addExtensionFunction(EXT1_NS, 'last-names') { org.w3c.dom.NodeList lastNameNodeList ->
      assertTrue lastNameNodeList instanceof org.w3c.dom.NodeList

      def lastNames = []
      for (Element lastName : lastNameNodeList) {
        use (groovy.xml.dom.DOMCategory) {
          lastNames << lastName.text()
        }
      }
      return lastNames.join(', ')
    }

    runAndAssertTransformation(closureTransformer, '/people-input.xml', '/expected-testNodeSetParameterBecomesDOMObject.xml')
  }

}