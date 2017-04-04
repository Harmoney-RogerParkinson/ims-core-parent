package com.harmoney.ims.core.server.web;

import java.io.InputStream;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	
    private static final Logger log = LoggerFactory.getLogger(ServletInitializer.class);

	@Override
	public void onStartup(ServletContext container) {
		
		String implementationBuild = "";
		Manifest manifest;
		try {
			InputStream inputStream = container.getResourceAsStream("/META-INF/MANIFEST.MF");
			manifest = new Manifest(inputStream);
			implementationBuild = manifest.getAttributes("implementation-build").toString();
			inputStream.close();
		} catch (Exception e) {
		}
		
		log.info("Starting application...{}-{} {}",
				this.getClass().getPackage().getImplementationTitle(),
				this.getClass().getPackage().getImplementationVersion(),
				implementationBuild,
				container.getServerInfo());
		
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