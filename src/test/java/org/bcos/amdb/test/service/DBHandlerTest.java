package org.bcos.amdb.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.bcos.amdb.service.DBHandler;
import org.bcos.amdb.service.DBService;
import org.bcos.channel.dto.ChannelPush;
import org.bcos.channel.dto.ChannelResponse;

public class DBHandlerTest {
	public class MockChannelPush extends ChannelPush {
		public void sendResponse(ChannelResponse response) {
			if(ex) {
				assertEquals(response.getErrorCode(), new Integer(-1));
			}
			else {
				assertEquals(response.getErrorCode(), new Integer(0));
				assertEquals(response.getContent(), "test result");
			}
		}
		
		public Boolean ex = false;
	}
	
	public class MockDBService extends DBService  {
		@Override
		public String process(String content) throws JsonProcessingException {
			assertEquals(content, "test message");
			
			if(throwEx) {
				throw new JsonProcessingException("test exception") {
					private static final long serialVersionUID = 1L;
				};
			}
			
			return "test result";
		}
		
		public Boolean throwEx = false;
	}
	
	MockDBService mockDBService = new MockDBService();
	DBHandler dbHandler = new DBHandler();
	
	@BeforeEach
	void initAll() {
		dbHandler.setDbService(mockDBService);
	}
	
	@Test
	void testOnPush() {
		assertEquals(dbHandler.getDbService(), mockDBService);
		
		MockChannelPush push = new MockChannelPush();
		push.setContent("test message");
		
		dbHandler.onPush(push);
		
		mockDBService.throwEx = true;
		
		push.ex = true;
		dbHandler.onPush(push);
	}
}
