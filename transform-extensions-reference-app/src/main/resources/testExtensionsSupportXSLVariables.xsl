<?xml version="1.0" encoding="UTF-8"?>
<!--
  Tests that XSL variables can be passed to Extension Functions.
-->
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:ext1="ext1"
  xmlns:ext2="ext2"
  extension-element-prefixes="ext1 ext2"
  exclude-result-prefixes="ext1 ext2"
  version="1.0">
  <xsl:output encoding="UTF-8" indent="yes" method="xml"/>
  <xsl:template match="/">
    <xsl:variable name="helloWorld">Hello World</xsl:variable>
    
    <testResults>
      <testExtensionsSupportXSLVariables>
        <ext1-echo><xsl:value-of select="ext1:echo($helloWorld)"/></ext1-echo>
        <ext2-echo><xsl:value-of select="ext2:echo($helloWorld)"/></ext2-echo>
        <!-- Passing variables to Extension Element attribute values is not supported. -->
        <!--
          <ext1-echo-attributes><ext1:echo-attributes attr1="$hello" attr2="$world"/></ext1-echo-attributes>
          <ext2-echo-attributes><ext2:echo-attributes attr1="$hello" attr2="$world"/></ext2-echo-attributes>
        -->
      </testExtensionsSupportXSLVariables>
    </testResults>
  </xsl:template>
</xsl:stylesheet>
