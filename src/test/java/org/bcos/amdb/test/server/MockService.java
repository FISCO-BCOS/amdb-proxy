package org.bcos.amdb.test.server;
import org.fisco.bcos.channel.client.Service;

public class MockService extends Service {
	private Boolean throwEx = false;
	
	public Boolean getThrowEx() {
        return throwEx;
    }

    public void setThrowEx(Boolean throwEx) {
        this.throwEx = throwEx;
    }

    public void run() throws Exception {
		if(throwEx) {
			throw new Exception("");
		}
	}
}
