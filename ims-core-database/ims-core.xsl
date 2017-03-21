<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns="http://www.w3.org/2001/XMLSchema" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
	xmlns:tns="http://www.harmoney.com/ims-core">
	<xsl:output method="xml" omit-xml-declaration="no"
		indent="yes" />
	<xsl:strip-space elements="*" />

	<xsl:template match="/">
	
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="xsd:schema">
		<schema>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
 		</schema>
	</xsl:template>
	<xsl:template match="xsd:complexType[@name='shedlock']">
		<!-- ignore schedlock -->
	</xsl:template>
	<!-- 
	<xsl:template match="xsd:complexType[not(@name)]">
		<complexType>
			<xsl:apply-templates />
		</complexType>
	</xsl:template>
	 -->
	<xsl:template match="xsd:complexType">
		<complexType>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</complexType>
	</xsl:template>
	<xsl:template match="xsd:sequence">
		<sequence>
			<xsl:apply-templates />
		</sequence>
	</xsl:template>
	<xsl:template match="xsd:extension">
		<extension>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</extension>
	</xsl:template>
	<xsl:template match="xsd:complexContent">
		<complexContent>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</complexContent>
	</xsl:template>
	<xsl:template match="xsd:element">
		<element>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</element>
	</xsl:template>
	<xsl:template match="xsd:simpleType">
		<simpleType>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</simpleType>
	</xsl:template>
	<xsl:template match="xsd:restriction">
		<restriction>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</restriction>
	</xsl:template>
	<xsl:template match="xsd:enumeration">
		<enumeration>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</enumeration>
	</xsl:template>
	<xsl:template match="xsd:totalDigits">
		<totalDigits>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</totalDigits>
	</xsl:template>
	<xsl:template match="xsd:fractionDigit">
		<fractionDigit>
			<xsl:copy-of select="@*|b/@*" />
			<xsl:apply-templates />
		</fractionDigit>
	</xsl:template>
	<xsl:template match="xsd:annotation" />

</xsl:stylesheet> 