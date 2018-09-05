package org.bcos.amdb.test.server;

import org.bcos.channel.client.Service;

public class MockService extends Service {
	public static Boolean throwEx = false;
	
	public void run() throws Exception {
		if(throwEx) {
			throw new Exception("");
		}
	}
}
