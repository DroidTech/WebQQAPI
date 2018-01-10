package net.droidtech.qzone;

import java.util.ArrayList;

import net.droidtech.consoleqq.Credential;
import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.httputils.Response;
import net.droidtech.httputils.UA;
import net.droidtech.utils.URL;

public class QZone {
	
	private Credential credential=null;
	
	public QZone(Credential credential){
	    this.credential=credential;	
	}
	
	public void test(){
		HttpUtils utils=new HttpUtils();
		System.out.println(credential.getCookie().getValue());
		Response response=utils.get(URL.URL_QZONE.replace("[var]",Long.toString(credential.getUin())),new HttpHeader[]{UA.USER_AGENT_CHROME});
		//Response response=utils.get("https://user.qzone.qq.com/proxy/domain/r.qzone.qq.com/cgi-bin/user/cgi_personal_card?uin=1816887190&fupdate=1&rd=1515360714914&g_tk=1363906073&qzonetoken=ea91a0e1ce4fb277d2b6dc85572b4170a0cf2ae65d270c26be36857b0dd16bbb2658c1dfb267596b66",new HttpHeader[]{UA.USER_AGENT_CHROME,credential.getCookie()});
		System.out.println(response.getContent());
		ArrayList<HttpHeader> headers=response.getHeaders();
		for(int i=0;i<headers.size();i++){
			System.out.println(headers.get(i).getValue());
		}
		
	}
	
}
