/**
 * 
 */
package com.harmoney.ims.core.queuehandler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import com.harmoney.ims.core.queuehandler.unpacker.Unpacker;

/**
 * @author Roger Parkinson
 *
 */
public class ConvertXSDVariables {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		org.jdom2.Document doc = builder.build(new FileReader("../ims-core-database/src/main/resources/ims-core.xsd"));
		Element root = doc.getDocument().getRootElement();
		org.jdom2.Namespace namespace = root.getNamespace();
		for (Element complexType: root.getChildren("complexType",namespace)) {
			complexType.toString();
			for (Element sequence: complexType.getChildren("sequence",namespace)) {
				sequence.toString();
				for (Element element: sequence.getChildren("element",namespace)) {
					Attribute nameAttribute = element.getAttribute("name");
					if (nameAttribute != null) {
						String sfValue = nameAttribute.getValue();
						nameAttribute.setValue(Unpacker.fixVariableFormat(sfValue));
						if (!sfValue.equals(nameAttribute.getValue())) {
							Element annotation = new Element("annotation",namespace);
							Element documentation = new Element("documentation",namespace);
							documentation.setText("Salesforce name: "+sfValue);
							annotation.addContent(documentation);
							element.addContent(annotation);
						}
					}
				}
				
			}
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.output(doc, System.out);
	}

}
