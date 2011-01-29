extensify
=========

Overview
--------
A suite of Java Libraries which aim to make Unit Testing XSLT transformations
containing proprietary [XSLT Extensions](http://www.w3.org/TR/xslt#extension)
possible.  Specifically this project will give DataPower developers the ability
to mock out the behavior of DataPower extension functions and elements and run
their transformations outside of the DataPower container.  With the ability to
mock the behavior of extensions, developers can write Unit Tests for their
transformations and run them in Unit Testing frameworks like
[JUnit](http://www.junit.org/).

How it works
------------
extensify uses the Transformation API for XML (TrAX) available in
Java.  While current versions of Java ship with a TrAX implementation, extensify
uses Xalan 2.7.1 to take advantage of it's [extension features](http://xml.apache.org/xalan-j/extensions.html).
Xalan has a variety of options for defining and implementing XSLT extensions,
however they all involve defining the extension in the actual stylesheet.  This
is fine if your production environment will also use Xalan as its XSL engine.
When your target platform is DataPower however, defining the implementation of
an extension using Xalan's proprietary approach means you'll have two versions
of an XSL.  One that works in Xalan, and one that works in DataPower.

The approach extensify takes is to notify Xalan of extensions through Java API
calls rather than defining them inside the XSL itself.  Providing the ability
to define and implement extensions in Groovy is a feature of this library and
it ends up looking something like the following (this is pseudo-code, but I
hope to get the point across).  DataPower developers will be familiar with the
"set-variable" extension element and the "variable" extension function.

    String EXTENSION_TEST_NS = "http://www.xsltextensions.org/extensions" // The Namespace DataPower extensions appear in.
    def variables = [:] // A Java HashMap.

    transformer.addExtensionFunction(EXTENSION_TEST_NS, "variable") { name ->
      return variables[name] // Return the variable from the HashMap.
    }

    transformer.addExtensionElement(EXTENSION_TEST_NS, "set-variable") { attributes ->
      variables[attributes['name']] = attributes['value'] // Set the variable in the HashMap.
      null // "set-variable" doesn't return anything.
    }

    transformer.transform(xmlSource, xmlResult)
    assertThat(expectedXML, equalToXMLResultIgnoringWhitespace(xmlResult))


What next
---------
Over time I'll begin adding more user documentation to the Wiki. In the
meantime, your best source of examples are in the Unit Tests of the
"transform-extensions-groovy" module.

Stay tuned for more.
