package net.droidtech.utils;

import net.droidtech.consoleqq.Credential;
import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpRequestException;
import net.droidtech.httputils.HttpUtils;

public class UserLogo {
	private Credential credential=null;
	public UserLogo(Credential credential){
		this.credential=credential;
	}
	
	//返回该uin的100x100头像，如果目标没有头像，则会引起阻塞，所以需要4秒后强制中断连接
	public byte[] getUserLogoByUin(long uin){
		try{
			HttpUtils utils=new HttpUtils();
			utils.setReadTimeout(4000);
		return utils.get(URL.URL_GET_USER_LOGO.replace("[var]",Long.toString(uin)).replace("[var1]",credential.getVfWebQQ()),new HttpHeader[]{credential.getCookie()}).getBytes();
		}catch(HttpRequestException e){
			return new byte[0];
		}
	}

}
