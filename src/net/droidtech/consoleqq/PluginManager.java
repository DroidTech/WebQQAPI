package net.droidtech.consoleqq;

import java.util.ArrayList;
import net.droidtech.httputils.Response;
import net.droidtech.io.DroidFile;
import net.droidtech.plugin.DroidPlugin;

public class PluginManager{
	
	private Receiver receiver=null;
	private Credential credential=null;
	private DroidFile confDir=null;
	private ArrayList<DroidPlugin> plugins=new ArrayList<DroidPlugin>();
	
	public PluginManager(Credential credential){
		this.credential=credential;
	}
	
	public void setConfigDirectory(DroidFile file){
		this.confDir=file;
	}
	
	public synchronized void regist(DroidPlugin plugin){
		if(plugin==null){
			return;
		}
		plugins.add(plugin);
		Credential credential=new Credential(this.credential.getUin(),this.credential.getVfWebQQ(),this.credential.getPsessionID(),this.credential.getPtWebQQ(),this.credential.getCookie());
		credential.setRetcode(credential.getRetcode());
		plugin.init(credential,confDir);
	}
	
	public synchronized void unregist(int index){
		if(plugins.get(index)!=null){
			plugins.get(index).onRemoved();
			plugins.remove(index);
		}
	}
	
	public synchronized void unregistAll(){
		for(int i=0;i<plugins.size();i++){
			if(plugins.get(i)!=null){
				plugins.get(i).onRemoved();
			}
		}
		plugins.clear();
	}
	
	public synchronized void unregist(DroidPlugin plugin){
		if(!plugins.contains(plugin)||plugin==null){
			return;
		}
		plugin.onRemoved();
		plugins.remove(plugin);
	}
	
	public void enable(){
		if(receiver==null){
		receiver=new Receiver(credential, new MessageReceivedListener(){

			@Override
			public void onFriendMessage(Message msg) {
				// TODO Auto-generated method stub
				for(int i=0;i<plugins.size();i++){
					final DroidPlugin plugin=plugins.get(i);
					new Thread(){
						@Override
						public void run(){
						try{
					    plugin.onFreindMessage(msg);
						}catch(Exception e){
							plugin.onError(e,null);
						}
					}
					}.start();
				}
			}

			@Override
			public void onGroupMessage(Message msg) {
				// TODO Auto-generated method stub
				for(int i=0;i<plugins.size();i++){
					final DroidPlugin plugin=plugins.get(i);
					new Thread(){
						@Override
						public void run(){
						try{
					    plugin.onGroupMessage(msg);
						}catch(Exception e){
							plugin.onError(e,null);
						}
					}
					}.start();
				}
			}

			@Override
			public void onDiscussMessage(Message msg) {
				// TODO Auto-generated method stub
				for(int i=0;i<plugins.size();i++){
					final DroidPlugin plugin=plugins.get(i);
					new Thread(){
						@Override
						public void run(){
						try{
					    plugin.onDiscussMessage(msg);
						}catch(Exception e){
							plugin.onError(e,null);
						}
					}
					}.start();
				}
			}

			@Override
			public void onError(Exception e, Response response) {
				// TODO Auto-generated method stub
				for(int i=0;i<plugins.size();i++){
					final DroidPlugin plugin=plugins.get(i);
					Thread pthread=new Thread(){
						@Override
						public void run(){
					    plugin.onError(e, response);
						}
					};
					pthread.start();
				}
			}
			});
		receiver.startReceiveService();
		}
	}
	
	public void disable(){
		if(receiver!=null){
		receiver.stopReceiveService();
		receiver=null;
		}
	}

}
