package net.droidtech.plugin;

import net.droidtech.consoleqq.Credential;
import net.droidtech.consoleqq.Message;
import net.droidtech.httputils.Response;
import net.droidtech.io.DroidFile;

public interface DroidPlugin{
	
	public void init(Credential credential,DroidFile configureDir);
	
	public void onGroupMessage(Message msg);

	public void onFreindMessage(Message msg);

	public void onDiscussMessage(Message msg);
	
	public void onError(Exception e,Response response);
	
	public void onRemoved();
	
	public String getName();
	
	public String getVersion();
	
	public byte[] getIcon();

}
