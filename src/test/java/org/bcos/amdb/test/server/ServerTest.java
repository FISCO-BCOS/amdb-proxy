package org.bcos.amdb.test.server;

import org.junit.jupiter.api.Test;

import org.bcos.amdb.server.Main;

public class ServerTest {
	@SuppressWarnings("static-access")
	@Test
	void test() {
		Main main = new Main();
		
		String [] args = null;
		main.main(args);
		
		new MockService().setThrowEx(true);
		main.main(args);
	}
}
