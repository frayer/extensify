<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ext="http://www.xsltextensions.org/extensions" extension-element-prefixes="ext"
                exclude-result-prefixes="ext">
    <xsl:output indent="yes"/>

    <xsl:template match="/">
        <ext:set-variable name="var://context/foo1" value="first set variable"/>
        <ext:set-variable name="var://context/foo2" value="second set variable"/>
        <ext:extension-with-childElement name="var://context/foo3" value="third set variable">
            <firstLevel>
                <secondLevel>text node value</secondLevel>
            </firstLevel>
        </ext:extension-with-childElement>
        <names>
            <dpVariables>
                <foo1>
                    <xsl:value-of select="ext:variable('var://context/foo1')"/>
                </foo1>
                <foo2>
                    <xsl:value-of select="ext:variable('var://context/foo2')"/>
                </foo2>
            </dpVariables>
            <firstNames>
                <xsl:apply-templates select="people/person" mode="firstNames"/>
            </firstNames>
            <lastNames>
                <xsl:apply-templates select="people/person" mode="lastNames"/>
            </lastNames>
        </names>
    </xsl:template>

    <xsl:template match="person" mode="firstNames">
        <firstName>
            <xsl:value-of select="firstName"/>
        </firstName>
    </xsl:template>

    <xsl:template match="person" mode="lastNames">
        <lastName>
            <xsl:value-of select="lastName"/>
        </lastName>
    </xsl:template>

</xsl:stylesheet>