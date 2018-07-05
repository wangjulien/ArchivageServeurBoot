package com.telino.avp.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.telino.avp.config.AppSpringConfig;
import com.telino.avp.config.WebConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { WebConfig.class, AppSpringConfig.class })
@WebAppConfiguration
public class TestArchivageApiController {
	
	@Autowired
    private WebApplicationContext wac;

	private MockMvc mockMvc;

//	@Autowired
//	private JournalEventService journalEventServiceMock;
//
//	@Autowired
//	private SwitchDataSourceService switchDataSourceServiceMock;
//
//	@Autowired
//	private ArchivageApiService archivageApisMock;
	
	@Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

	
//	@Test
//	public void contextLoads() {
//		assertNotNull(archivageApisMock);
//	}

	@Test
	public void doGetAndPost_Should_ReturnMapResult() throws Exception {
		Map<String, Object> result = new HashMap<>();

//		when(archivageApisMock.execApi(anyMap())).thenReturn(result);

//		mockMvc.perform(get("/ArchivageService?nomBase=AVP")).andExpect(status().isOk());
		
		mockMvc.perform(get("/startElastic")).andExpect(status().isOk());
		
//		verify(archivageApisMock).execApi(anyMap());
//		verifyNoMoreInteractions(archivageApisMock);
	}

}
