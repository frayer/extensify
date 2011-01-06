package org.extensify.transform.xalan.extensions.annotated;

import org.apache.commons.io.IOUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.custommonkey.xmlunit.XMLAssert;
import org.extensify.transform.xalan.transformer.ExtensibleXalanTransformer;
import org.junit.Before;
import org.junit.Test;
import org.extensify.transform.xalan.extensions.BasicExtensionHandler;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;

public class VariableExtensionsTest {

    public static final String XSL_PATH = "/org/extensify/transform/xalan/extensions/annotated/people_xform.xsl";
    public static final String XML_PATH = "/org/extensify/transform/xalan/extensions/annotated/people.xml";
    public static final String EXPECTED_XML_PATH = "/org/extensify/transform/xalan/extensions/annotated/people_expected.xml";

    private ExtensibleXalanTransformer transformer = null;
    private Source xslSource = null;
    private Source xmlSource = null;

    @Before
    public void setUp() throws Exception {
        xslSource = new StreamSource(VariableExtensionsTest.class.getResourceAsStream(XSL_PATH));
        xmlSource = new StreamSource(VariableExtensionsTest.class.getResourceAsStream(XML_PATH));

        Transformer jaxpTransformer = TransformerFactory.newInstance().newTransformer(xslSource);
        transformer = new ExtensibleXalanTransformer((TransformerImpl) jaxpTransformer);

        BasicExtensionHandler extensionHandler = new AnnotatedExtensionHandler<VariableExtensions>(new VariableExtensions());

        transformer.registerExtensionHandler(extensionHandler.getNamespaceUri(), extensionHandler);
    }

    @Test
    public void testTransform() throws Exception {
        String expectedXML = IOUtils.toString(this.getClass().getResourceAsStream(EXPECTED_XML_PATH));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Result result = new StreamResult(outputStream);

        transformer.transform(xmlSource, result);

        String transformedXML = outputStream.toString();
        System.out.println(transformedXML);
        
        XMLAssert.assertXMLEqual(expectedXML, transformedXML);
    }

}
