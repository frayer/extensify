<?xml version="1.0" encoding="UTF-8"?>
<!--
  Test that more than one extension namespace is supported in a single XSLT.
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
    <testResults>
      <testMultipleExtensionNamespacesSupported>
        <ext1-echo><xsl:value-of select="ext1:echo('Hello World')"/></ext1-echo>
        <ext2-echo><xsl:value-of select="ext2:echo('Hello World')"/></ext2-echo>
        <ext1-echo-attributes><ext1:echo-attributes attr1="Hello" attr2="World"/></ext1-echo-attributes>
        <ext2-echo-attributes><ext2:echo-attributes attr1="Hello" attr2="World"/></ext2-echo-attributes>
      </testMultipleExtensionNamespacesSupported>
    </testResults>
  </xsl:template>
</xsl:stylesheet>
