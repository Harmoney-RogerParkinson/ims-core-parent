/**
 * 
 */
package com.salesforce.emp.connector;

import java.net.URL;

/**
 * Not actually used but this is a good place to copy/paste the enterprise settings from
 * 
 * @author Roger Parkinson
 *
 */
public class EnterpriseLoginHelper extends LoginHelper {

    static final String LOGIN_ENDPOINT = "https://login.salesforce.com";
    private static final String ENV_START = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' "
            + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
            + "xmlns:urn='urn:enterprise.soap.sforce.com'><soapenv:Body>";

    // The enterprise SOAP API endpoint used for the login call
    private static final String SERVICES_SOAP_ENTERPRISE_ENDPOINT = "/services/Soap/c/38.0/0DFN0000000CbeW";

    public static BayeuxParameters login(String username, String password) throws Exception {
        return login(new URL(LOGIN_ENDPOINT), username, password);
    }

    public static BayeuxParameters login(String username, String password, BayeuxParameters params) throws Exception {
        return login(new URL(LOGIN_ENDPOINT), username, password, params);
    }
    protected static String getSoapUri() {
        return SERVICES_SOAP_ENTERPRISE_ENDPOINT;
    }
    protected static String getEnvStart() {
        return ENV_START;
    }
	public EnterpriseLoginHelper() {
		// TODO Auto-generated constructor stub
	}

}
