package burp;

import burp.IBurpExtenderCallbacks;
import burp.IHttpRequestResponse;
import burp.IMenuItemHandler;

public class BurpCommander implements IMenuItemHandler {

	public BurpCommander(IBurpExtenderCallbacks arg0) {
		callback = arg0;
		
		callback.registerMenuItem("Send to Commander", this);
		
		ui = new BurpCommanderUI(this);
	}

	@Override
	public void menuItemClicked(String arg0, IHttpRequestResponse[] arg1) {
		ui.newRequest(arg1[0]);
		
	}
	
	public String sendRequest(String host, int port, boolean isSSL, String req) {
		
		try {
			Request request = new Request(host, port, req.getBytes());
	
			return new String(callback.makeHttpRequest(request.getHost(), request.getPort(), isSSL, request.getRequest()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
	}
	
	
	private IBurpExtenderCallbacks callback;
	private BurpCommanderUI ui;
}
