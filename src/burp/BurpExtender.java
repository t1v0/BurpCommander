package burp;


public class BurpExtender implements IBurpExtender {

	public BurpExtender() {
	}
	
	@Override
	public void applicationClosing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newScanIssue(IScanIssue arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processHttpMessage(String arg0, boolean arg1,
			IHttpRequestResponse arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] processProxyMessage(int arg0, boolean arg1, String arg2,
			int arg3, boolean arg4, String arg5, String arg6, String arg7,
			String arg8, String arg9, byte[] arg10, int[] arg11) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerExtenderCallbacks(IBurpExtenderCallbacks arg0) {
		commander = new BurpCommander(arg0);
		
	}

	@Override
	public void setCommandLineArgs(String[] arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private BurpCommander commander;

}
