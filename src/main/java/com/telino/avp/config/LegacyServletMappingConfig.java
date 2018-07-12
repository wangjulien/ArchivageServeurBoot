package com.telino.avp.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

@Configuration
public class LegacyServletMappingConfig {
	
	@Bean
	public ServletWrappingController startCdmsMailController() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsMail.class);
		controller.setBeanName("startCdmsMailController");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController resetLogger() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.ResetLogger.class);
		controller.setBeanName("resetLogger");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsMailService() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsMailService.class);
		controller.setBeanName("startCdmsMailService");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController getStructure() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.getStructure.class);
		controller.setBeanName("getStructure");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsWriteSocket() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsWriteSocket.class);
		controller.setBeanName("startCdmsWriteSocket");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsSql() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsSql.class);
		controller.setBeanName("startCdmsSql");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsSql2() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsSql2.class);
		controller.setBeanName("startCdmsSql2");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsSqlInfo() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsSqlInfo.class);
		controller.setBeanName("startCdmsSqlInfo");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsSqlInsert() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsSqlInsert.class);
		controller.setBeanName("startCdmsSqlInsert");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsService() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsService.class);
		controller.setBeanName("startCdmsService");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsAdmin() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsAdmin.class);
		controller.setBeanName("startCdmsAdmin");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsReadFile() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsReadFile.class);
		controller.setBeanName("startCdmsReadFile");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController writeFile() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.WriteFile.class);
		controller.setBeanName("writeFile");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController loadLicenceFile() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.LoadLicenceFile.class);
		controller.setBeanName("loadLicenceFile");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startJasperReport() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startJasperReport.class);
		controller.setBeanName("startJasperReport");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController getReportList() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.GetReportList.class);
		controller.setBeanName("getReportList");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController fileUploadServlet() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.FileUploadServlet.class);
		controller.setBeanName("fileUploadServlet");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public ServletWrappingController startCdmsLdapConnect() throws Exception {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(CdmsApi.servlets.startCdmsLdapConnect.class);
		controller.setBeanName("startCdmsLdapConnect");
		controller.afterPropertiesSet();
		return controller;
	}
	
	@Bean
	public SimpleUrlHandlerMapping legacyServletsMapping() {
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		Properties urlProperties = new Properties();
		urlProperties.put("/startCdmsMail", "startCdmsMailController");
		urlProperties.put("/ResetLogger", "resetLogger");
		urlProperties.put("/startCdmsMailService", "startCdmsMailService");
		urlProperties.put("/getStructure", "getStructure");
		urlProperties.put("/startCdmsWriteSocket", "startCdmsWriteSocket");
		urlProperties.put("/startCdmsSql", "startCdmsSql");
		urlProperties.put("/startCdmsSql2", "startCdmsSql2");
		urlProperties.put("/startCdmsSqlInfo", "startCdmsSqlInfo");
		urlProperties.put("/startCdmsSqlInsert", "startCdmsSqlInsert");
		urlProperties.put("/startCdmsService", "startCdmsService");
		urlProperties.put("/startCdmsAdmin", "startCdmsAdmin");
		urlProperties.put("/startCdmsReadFile", "startCdmsReadFile");
		urlProperties.put("/WriteFile", "writeFile");
		urlProperties.put("/LoadLicenceFile", "loadLicenceFile");
		urlProperties.put("/startJasperReport", "startJasperReport");
		urlProperties.put("/GetReportList", "getReportList");
		urlProperties.put("/FileUploadServlet", "fileUploadServlet");
		urlProperties.put("/startCdmsLdapConnect", "startCdmsLdapConnect");
		mapping.setMappings(urlProperties);
		mapping.setOrder(Integer.MAX_VALUE - 2);

		return mapping;
	}
}
