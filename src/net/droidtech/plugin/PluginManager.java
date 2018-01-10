package net.droidtech.plugin;

import java.util.ArrayList;

import net.droidtech.consoleqq.Credential;
import net.droidtech.consoleqq.Message;
import net.droidtech.consoleqq.Receiver;
import net.droidtech.httputils.Response;
import net.droidtech.io.DroidFile;
import net.droidtech.plugin.DroidPlugin;
import net.droidtech.consoleqq.MessageReceivedListener;

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
	
	public void start(){
		if(receiver==null){
		receiver=new Receiver(credential, new MessageReceivedListener(){

			@Override
			public void onFriendMessage(final Message msg) {
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
			public void onGroupMessage(final Message msg) {
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
			public void onDiscussMessage(final Message msg) {
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
			public void onError(final Exception e, final Response response) {
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
		receiver.start();
		}
	}
	
	public void stop(){
		if(receiver!=null){
		receiver.stop();
		receiver=null;
		}
	}

}
