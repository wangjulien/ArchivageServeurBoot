package com.telino.avp.service;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TSPValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.telino.avp.entity.auxil.LogEvent;
import com.telino.avp.exception.AvpExploitException;
import com.telino.avp.service.journal.TamponHorodatageService;
import com.telino.avp.utils.Sha;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitConfig(TamponHorodatageService.class)
public class TestTamponHorodatageService {
	
	@Autowired
	private TamponHorodatageService tamponHorodatageService;
	
	private LogEvent logEvent;
	
	@Before
	public void buildEntity() throws Exception {
		logEvent = new LogEvent();
		logEvent.setLogId(ConfigTestService.LOG_EVENT_ID);
		logEvent.setContenu("Test contenu");
		logEvent.setHash(Sha.encode(logEvent.getContenu(), "utf-8"));
		
		tamponHorodatageService.demanderTamponHorodatage(logEvent);
		
		logEvent.setTimestampTokenBytes(logEvent.getTimestampToken().getEncoded());
	}

	@Test
	public void get_logevent_by_id_and_init_tamponhorodatage() throws AvpExploitException, CMSException, TSPException, IOException {
		
		logEvent.setTimestampToken(null);
		tamponHorodatageService.initTamponHorodatage(logEvent);
		
		assertNotNull(logEvent.getTimestampToken());
	}
	
	@Test
	public void verify_tampon_horodatage() throws TSPValidationException, OperatorCreationException, TSPException, CMSException {
		tamponHorodatageService.verifyTamponHorodatage(logEvent);
	}

}
