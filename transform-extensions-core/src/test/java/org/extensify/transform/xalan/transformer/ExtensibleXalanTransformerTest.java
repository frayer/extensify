package org.extensify.transform.xalan.transformer;

import org.apache.xalan.transformer.TransformerImpl;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExtensibleXalanTransformerTest {

    private ExtensibleXalanTransformer transformer = null;
    private TransformerImpl wrappedTransformer = null;

    @Before
    public void setUp() throws Exception {
        wrappedTransformer = mock(TransformerImpl.class);
        transformer = new ExtensibleXalanTransformer(wrappedTransformer);
    }

    /**
     * Validates that the behavior of delegating to the underlying <code>TransformerImpl</code>
     * instance is working for the necessary methods.
     *
     * @throws Exception
     */
    @Test
    public void testDelegatingMethods() throws Exception {
        URIResolver uriResolver = mock(URIResolver.class);
        Properties properties = mock(Properties.class);
        ErrorListener errorListener = mock(ErrorListener.class);

        transformer.setParameter("paramName", "paramValue");
        transformer.getParameter("paramName");
        transformer.clearParameters();
        transformer.setURIResolver(uriResolver);
        transformer.getURIResolver();
        transformer.setOutputProperties(properties);
        transformer.getOutputProperties();
        transformer.setOutputProperty("propertyName", "propertyValue");
        transformer.getOutputProperty("propertyName");
        transformer.setErrorListener(errorListener);
        transformer.getErrorListener();

        verify(wrappedTransformer).setParameter("paramName", "paramValue");
        verify(wrappedTransformer).getParameter("paramName");
        verify(wrappedTransformer).clearParameters();
        verify(wrappedTransformer).setURIResolver(uriResolver);
        verify(wrappedTransformer).getURIResolver();
        verify(wrappedTransformer).setOutputProperties(properties);
        verify(wrappedTransformer).getOutputProperties();
        verify(wrappedTransformer).setOutputProperty("propertyName", "propertyValue");
        verify(wrappedTransformer).getOutputProperty("propertyName");
        verify(wrappedTransformer).setErrorListener(errorListener);
        verify(wrappedTransformer).getErrorListener();
    }

}
