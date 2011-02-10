import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import org.apache.commons.io.IOUtils
import org.apache.xalan.extensions.ExpressionContext
import org.apache.xalan.templates.ElemTemplateElement
import org.apache.xalan.templates.Stylesheet
import org.apache.xalan.transformer.TransformerImpl
import org.junit.Before
import org.junit.Test
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.extensify.transform.xalan.extensions.groovy.ClosureExtensibleXalanTransformer
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNotNull
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
    def variables = [:]
    
    /*
     * Extension Functions with optional parameters MUST be defined with a
     * default value in the Closure parameter definition
     */
    closureTransformer.addExtensionFunction(EXT1_NS, 'concat') { arg1, arg2='', arg3='' ->
      "${arg1}${arg2}${arg3}".toString()
    }
    
    runAndAssertTransformation(closureTransformer, '/people-input.xml', '/expected-testFunctionsSupportVariableLengthArguments.xml')
  }

}