<?xml version="1.0" encoding="UTF-8"?>
<!--
  Tests that Xalan X* classes such as XString and XBoolean get converted to
  their corresponding Java classes like "java.lang.String" and
  "java.lang.Boolean".
-->
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:ext1="ext1"
        extension-element-prefixes="ext1"
        exclude-result-prefixes="ext1"
        version="1.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml"/>
    <xsl:template match="/">
        <xsl:variable name="firstPersonName" select="/people/person[1]/firstName/text()"/>
        <xsl:variable name="numberOfPeople" select="count(/people/person)"/>
        <xsl:variable name="trueValue" select="true()"/>
        <xsl:variable name="falseValue" select="false()"/>
        <testResults>
            <firstPersonName><xsl:value-of select="ext1:expect-string($firstPersonName)"/></firstPersonName>
            <numberOfPeople><xsl:value-of select="ext1:expect-integer($numberOfPeople)"/></numberOfPeople>
            <trueValue><xsl:value-of select="ext1:expect-boolean($trueValue)"/></trueValue>
            <falseValue><xsl:value-of select="ext1:expect-boolean($falseValue)"/></falseValue>
        </testResults>
    </xsl:template>
</xsl:stylesheet>
