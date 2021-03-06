﻿package net.droidtech.launcher;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import net.droidtech.consoleqq.Credential;
import net.droidtech.consoleqq.QrcodeLogin;
import net.droidtech.plugin.PluginManager;
import net.droidtech.consoleqq.QrcodeVerifyListener;
import net.droidtech.httputils.HttpHeader;
import net.droidtech.io.DroidFile;
import net.droidtech.plugin.DroidPlugin;

public class Launcher {
	
	private static Credential credential=null;
	private static final DroidFile credentialFile=new DroidFile("."+File.separator+"Monospace.qcre");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(credentialFile.exists()){
			try {
				ObjectInputStream in=new ObjectInputStream(credentialFile.getInputStream());
				Object object=in.readObject();
				in.close();
				if(!(object instanceof Credential)){
					throw new ClassNotFoundException("Bad Credential file!");
				}
				credential=(Credential)object;
				System.out.println("凭据已载入!");
				if(!credential.isValid()){
					System.out.println("很抱歉，凭据失效了，请重新登录!");
					login();
				}else{
					init();
				}
			}catch(IOException e){
			e.printStackTrace();
		    }catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("损坏的凭据文件!");
			}
		}
		if(credentialFile.exists()==false){
			System.out.println("找不到凭据文件，请先登录!");
			login();
		}
	}
	
	private static void login(){
		DroidFile file=new DroidFile("."+File.separator+"login.png");
		String cookie=QrcodeLogin.getLoginCookie();
		byte[] qrdata=QrcodeLogin.getQRCodeImage();
		file.createNewFile();
		DataOutputStream out=file.getOutputStream();
		try {
			out.write(qrdata);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		QrcodeLogin.waitForVerify(cookie,new QrcodeVerifyListener(){

			@Override
			public void onVerified(String url, HttpHeader headers) {
				// TODO Auto-generated method stub
				credential=QrcodeLogin.getCredential(url,headers);
				credentialFile.createNewFile();
				credential.save(credentialFile);
				init();
			}

			@Override
			public void onFailed() {
				// TODO Auto-generated method stub
				System.out.println("登录失败!");
				return;
			}
			
		});
	}
	private static void init(){
		System.out.println("登录成功!");
		PluginManager pm=new PluginManager(credential);
		DroidFile conf=new DroidFile("."+File.separator+"pluginsConf");
		if(!conf.exists()){
			conf.mkdirs();
		}
		pm.setConfigDirectory(conf);
		ArrayList<DroidPlugin> pluginList=new ArrayList<DroidPlugin>();
		for(int i=0;i<pluginList.size();i++){
		System.out.println("正在注册插件"+pluginList.get(i).getName()+"，版本"+pluginList.get(i).getVersion());
		pm.regist(pluginList.get(i));
	    }
		pm.start();
	}

}
