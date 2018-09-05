package org.bcos.amdb.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.bcos.channel.client.Service;

public class Main {
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	private static ApplicationContext context;
	
	public static void main(String[] args) {
		logger.debug("启动AMDB Server");
		
		context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
		Service service = context.getBean("DBChannelService", Service.class);
		
		try {
			service.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}