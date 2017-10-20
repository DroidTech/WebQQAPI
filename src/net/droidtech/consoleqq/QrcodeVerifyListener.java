package net.droidtech.consoleqq;

import net.droidtech.httputils.HttpHeader;

public interface QrcodeVerifyListener {

	public void onVerified(String url,HttpHeader headers);
	
	public void onFailed();
}
