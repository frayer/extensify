extensify
=========

Overview
--------

extensify is a suite of Java Libraries which aims to make Unit Testing XSLT transformations
containing custom [XSLT Extensions](http://www.w3.org/TR/xslt#extension) possible. Platforms such as
[IBM WebSphere DataPower](http://www-01.ibm.com/software/integration/datapower/) take advantage of
this feature in the XSLT specification and have implemented custom extensions beyond what you would
find standard XSLT or even [EXSTL](http://www.exslt.org/). While these extensions provide the
ability to perform powerful tasks inside the DataPower appliance, it can cause a lot of pain when
testing the functionality of your XSLT.

IBM does not provide a virtualization layer for the DataPower environment, so executing an XSLT
containing DataPower extensions has always meant running it on the DataPower device. More than that,
the XSLT needs to be part of some higher level object such as a Multi-Protocol Gateway or Web
Service Proxy in order to execute. And finally you have to get a message to that object through one
of DataPower's supported protocols such as MQ or HTTP(S). In the end, your test cases end up looking
more like a full integration test rather than being able to test the individual units of your
request or response flow.

The goal of this project is to provide DataPower developers a suite of Java related tools to stub
out the behavior of DataPower extension elements and functions however they choose. The ability to
stub out these extensions provides the same types of benefits developers of other platforms enjoy
with their mocking frameworks. Besides the fact that you can now run DataPower specific XSLT
transformations locally before deploying to DataPower, you can also define predictable and
repeatable extension behavior for your suite of test cases.

While this project has a focus on the DataPower developer, there are still features of it that could
be beneficial to developers of other platforms which have a focus on XSLT or XML messages. In
addition to providing the ability to stub out XSLT extensions, this project contains [Hamcrest
Matchers](http://code.google.com/p/hamcrest/) and [XMLUnit](http://xmlunit.sourceforge.net/)
Difference Listeners which come in handy when comparing expected XML messages against each other.

How it works
------------

extensify uses the Transformation API for XML (TrAX) available in Java. While current versions of
Java ship with a TrAX implementation, extensify uses Xalan 2.7.1 to take advantage of it's
[extension features](http://xml.apache.org/xalan-j/extensions.html). Xalan has a variety of options
for defining and implementing XSLT extensions, however they all involve defining the extension in
the actual stylesheet. This is fine if your production environment will also use Xalan as its XSL
engine. When your target platform is DataPower however, defining the implementation of an extension
using Xalan's proprietary approach means you'll have two versions of an XSL. One that works in
Xalan, and one that works in DataPower.

The approach extensify takes is to notify Xalan of extensions through Xalan API calls rather than
defining them inside the XSL itself. Eventually there will be more documentation on how to do this
in the Wiki, but the following is a taste of what that syntax looks like when using the
[Groovy](http://groovy.codehaus.org/) wrapper around a javax.xml.transformer.Transformer
implementation. This is just pseudo-code, but DataPower developers will be familiar with the
`set-variable` extension element and the `variable` extension function defined here. The
implementation of each extension is defined in a Groovy Closure which is passed as the last argument
to either the `addExtensionFunction` or `addExtensionElement` functions.

    String EXTENSION_TEST_NS = "http://www.datapower.com/extensions" // The Namespace DataPower extensions appear in.
    def variables = [:] // A Java HashMap.

    transformer.addExtensionFunction(EXTENSION_TEST_NS, "variable") { name ->
      return variables[name] // Return the variable from the HashMap.
    }

    transformer.addExtensionElement(EXTENSION_TEST_NS, "set-variable") { attributes ->
      variables[attributes['name']] = attributes['value'] // Set the variable in the HashMap.
      null // "set-variable" doesn't return anything.
    }

    /*
     * Finally, perform the XSL transformation and assert the equality
     * of the result with the expected XML
     */
    transformer.transform(xmlSource, xmlResult)
    assertThat(expectedXML, equalToXMLResultIgnoringWhitespace(xmlResult))

The XSLT document which would invoke either of these closures might look like the following.

    <?xml version="1.0"?>
    <xsl:stylesheet xmlns:xsl="http://www.w3.org/TR/WD-xsl" xmlns:dp="http://www.datapower.com/extensions" extension-element-prefixes="dp">
    	<xsl:template match="/">
    	  <dp:set-variable name="var://context/name" value="'frayer'"/>
    	  <name>
    	    <xsl:value-of select="dp:variable('var://context/name')"/>
    	  </name>
    	</xsl:template>
    </xsl:stylesheet>

What next
---------

There will be more user documentation added to the Wiki. In the meantime, your best source of
examples are in the Unit Tests of the `transform-extensions-groovy` module.

Stay tuned for more.
