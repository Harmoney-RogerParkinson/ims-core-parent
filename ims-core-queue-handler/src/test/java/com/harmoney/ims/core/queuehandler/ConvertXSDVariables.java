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
		org.jdom2.Namespace annoxNamespace = root.getNamespace("annox");
		org.jdom2.Namespace imsNamespace = root.getNamespace("ims");
		org.jdom2.Namespace namespace = root.getNamespace();
		for (Element complexType: root.getChildren("complexType",namespace)) {
			complexType.toString();
			for (Element sequence: complexType.getChildren("sequence",namespace)) {
				sequence.toString();
				for (Element element: sequence.getChildren("element",namespace)) {
					for (Element annotation: element.getChildren("annotation",namespace)) {
						String salesforceName = null;
						for (Element documentation: annotation.getChildren("documentation",namespace)) {
							String documentationText = documentation.getTextTrim();
							if (documentationText.startsWith("Salesforce name: ")) {
								salesforceName = documentationText.substring(17);
							}
						}
						if (salesforceName != null) {
							Element appinfo = null;
							for (Element e: annotation.getChildren("appinfo",namespace)) {
								appinfo = e;
							}
							if (appinfo == null) {
								appinfo = new Element("appinfo",namespace);
								annotation.addContent(appinfo);
							}
							Element annotate = new Element("annotate",annoxNamespace);
							Element sfname = new Element("SalesforceName",imsNamespace);
							sfname.setAttribute("name", salesforceName, imsNamespace);
							annotate.addContent(sfname);
							appinfo.addContent(annotate);

						}
					}
				}
				
			}
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.output(doc, System.out);
	}

}
