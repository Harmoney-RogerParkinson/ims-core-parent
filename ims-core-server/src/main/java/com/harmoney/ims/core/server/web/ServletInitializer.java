package com.harmoney.ims.core.server.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	public void onStartup(ServletContext container) {
		container.setInitParameter("spring.profiles.active", "message-processor-prod,server-prod,queue-handler-prod");
		// Creates the root application context
		WebApplicationContext appContext = createServletApplicationContext();

		// Register and map the dispatcher servlet
		MessageDispatcherServlet messageDispatcherServlet = new MessageDispatcherServlet(appContext);
		messageDispatcherServlet.setTransformSchemaLocations(true);
		ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", messageDispatcherServlet);
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/endpoints/*");
		dispatcher.addMapping("*.wsdl");
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { WebConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}
}