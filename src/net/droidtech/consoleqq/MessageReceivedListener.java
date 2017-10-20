package net.droidtech.consoleqq;

import net.droidtech.httputils.Response;

public interface MessageReceivedListener {

	public void onFriendMessage(Message msg);
	
	public void onGroupMessage(Message msg);
	
	public void onDiscussMessage(Message msg);
	
	public void onError(Exception e,Response response);
}
