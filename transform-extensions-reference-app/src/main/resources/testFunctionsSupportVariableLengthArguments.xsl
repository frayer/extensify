<?xml version="1.0" encoding="UTF-8"?>
<!--
  Test that extension functions can take a variable number of arguments.
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
      <testFunctionsSupportVariableLengthArguments>
        <ext1-concat-1><xsl:value-of select="ext1:concat('Hello')"/></ext1-concat-1>
        <ext1-concat-2><xsl:value-of select="ext1:concat('Hello ', 'World')"/></ext1-concat-2>
        <ext1-concat-3><xsl:value-of select="ext1:concat('Hello ', 'World', '!')"/></ext1-concat-3>
      </testFunctionsSupportVariableLengthArguments>
    </testResults>
  </xsl:template>
</xsl:stylesheet>
