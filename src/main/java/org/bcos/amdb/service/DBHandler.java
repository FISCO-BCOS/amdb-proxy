package org.bcos.amdb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import org.bcos.channel.client.ChannelPushCallback;
import org.bcos.channel.dto.ChannelPush;
import org.bcos.channel.dto.ChannelResponse;

@Service
public class DBHandler extends ChannelPushCallback {
	private static Logger logger = LoggerFactory.getLogger(DBHandler.class);
	
	@Override
	public void onPush(ChannelPush push) {
		Integer resultCode = 0;
		
		String resultData = "";
		
		try {
			resultData = dbService.process(push.getContent());
		} catch (Exception e) {
			resultCode = -1;
			logger.error("Process request error", e);
		}
		
		//construct back to the package
		ChannelResponse response = new ChannelResponse();
		response.setMessageID(push.getMessageID());
		response.setErrorCode(resultCode);
		response.setErrorMessage("");
		response.setContent(resultData);
		
		logger.debug("Send response: {}", response.getContent());
		
		push.sendResponse(response);
	}

	@Autowired
	DBService dbService;
	
	public DBService getDbService() {
		return dbService;
	}

	public void setDbService(DBService dbService) {
		this.dbService = dbService;
	}

	Map<String, Map<String, Object> > cache = new HashMap<String, Map<String, Object> >();
}
