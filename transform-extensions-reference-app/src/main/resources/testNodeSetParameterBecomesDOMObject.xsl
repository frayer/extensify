<?xml version="1.0" encoding="UTF-8"?>
<!--
  Tests that a Node Set passed to a function is converted to a Java
  org.w3c.dom.traversal.NodeIterator
-->
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:ext1="ext1"
        extension-element-prefixes="ext1"
        exclude-result-prefixes="ext1"
        version="1.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>
    <xsl:template match="/">
        <testResults>
            <testNodeSetParameterBecomesNodeIterator>
                <firstNames><xsl:value-of select="ext1:first-names-by-person(/people/person)"/></firstNames>
                <lastNames><xsl:value-of select="ext1:last-names(/people/person/lastName)"/></lastNames>
            </testNodeSetParameterBecomesNodeIterator>
        </testResults>
    </xsl:template>
</xsl:stylesheet>
