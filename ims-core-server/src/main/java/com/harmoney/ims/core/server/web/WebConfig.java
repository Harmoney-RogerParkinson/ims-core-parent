package com.harmoney.ims.core.server.web;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbc.JdbcLockProvider;
import net.javacrumbs.shedlock.spring.SpringLockableTaskSchedulerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

import com.harmoney.ims.core.database.DatabaseSpringConfig;
import com.harmoney.ims.core.messages.MessageProcessorSpringConfig;
import com.harmoney.ims.core.partner.PartnerConnectionSpringConfig;
import com.harmoney.ims.core.queries.QuerySpringConfig;
import com.harmoney.ims.core.queuehandler.QueueHandlerSpringConfig;
import com.harmoney.ims.core.server.scheduled.SchedulerConfig;

@Configuration
@EnableWs
@PropertySource("classpath:default.properties")
@ComponentScan({ "com.harmoney.ims.core.server.web","com.harmoney.ims.core.server.endpoints" })
@Import({MessageProcessorSpringConfig.class,
		PartnerConnectionSpringConfig.class,
		QueueHandlerSpringConfig.class,
		DatabaseSpringConfig.class,
//		SchedulerConfig.class,
		QuerySpringConfig.class})
public class WebConfig extends WsConfigurerAdapter {

	@Autowired
	DataSource dataSource;
	@Autowired
	private ServletContext context;

	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcLockProvider(dataSource);
	}

	@Bean
	public TaskScheduler taskScheduler(LockProvider lockProvider) {
		int poolSize = 10;
		return SpringLockableTaskSchedulerFactory.newLockableTaskScheduler(
				poolSize, lockProvider);
	}
	// The bean name here has to match the PortTypeName
	@Bean(name="IMSService")
	public DefaultWsdl11Definition getWsdl() throws IOException {
		DefaultWsdl11Definition ret = new DefaultWsdl11Definition();
		ret.setSchemaCollection(xsdSchema());
		ret.setPortTypeName("IMSService");
		ret.setServiceName("IMSServices");
		ret.setLocationUri("/endpoints");
		return ret;
	}
	
	@Bean
	public CommonsXsdSchemaCollection xsdSchema() {
		CommonsXsdSchemaCollection ret = new CommonsXsdSchemaCollection();
		ret.setInline(true);
		ret.setXsds(new Resource[]{new ClassPathResource("ims-core.xsd")});
		return ret;
	}
	
}
