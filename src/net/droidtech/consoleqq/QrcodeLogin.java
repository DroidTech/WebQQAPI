package net.droidtech.consoleqq;

import java.util.ArrayList;
import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.httputils.PostParameter;
import net.droidtech.httputils.Response;
import net.droidtech.utils.URL;
import net.sf.json.JSONObject;

public class QrcodeLogin {
	
	private static final HttpUtils util=new HttpUtils();
	
	private static byte[] qrcodeData=null;
	
	private QrcodeLogin(){
		
	}
	
	//获取二维码的数据
	public static byte[] getQRCodeImage(){
		return qrcodeData;
	}
	
	public static String getLoginCookie(){
		//获取二维码
		Response response=util.get(URL.URL_GET_QRCODE);
		//把二维码的数据赋值给qrcodeData
		qrcodeData=response.getBytes();
		ArrayList<HttpHeader> headers=response.getHeaders();
		String cookie=null;
		//取出必要的Cookie，在下一次验证扫描结果是必须的
		for(int i=0;i<headers.size();i++){
			if(headers.get(i).getHeader()==null){
				continue;
			}
			if(headers.get(i).getHeader().equals("Set-Cookie")){
				cookie=headers.get(i).getValue();
				break;
			}
		}
		return cookie;
	}
	
	//等待二维码扫描
	public static Thread waitForVerify(String cookie,QrcodeVerifyListener listener){
		Thread observeThread=new ObserveThread(cookie,listener);
		observeThread.start();
		return observeThread;
	}
	
	//生成ptqrToken的方法
	private static int genPtqrToken(String s) {
	        int e = 0, n = s.length();
	        for (int i = 0; n > i; ++i)
	            e += (e << 5) + s.charAt(i);
	        return 2147483647 & e;
	}
	
	//获取凭据的方法
	public static Credential getCredential(String url,HttpHeader cookies){
		HttpUtils util=new HttpUtils();
		//必须设置不跟随重定向，否则无法取得cookies
		util.setGetFollowRedirect(false);
		//访问302跳转的url
		Response response=util.get(url);
		StringBuffer cookie=new StringBuffer();
		//取出cookies
		ArrayList<HttpHeader> headers=response.getHeaders();
		String[] cookieArray=cookies.getValue().split("; ");
		//获取验证成功后的cookies
		String ptwebqq=null;
		for(int i=0;i<cookieArray.length;i++){
			if(cookieArray[i].contains("ptwebqq")){
			ptwebqq=cookieArray[i].substring(cookieArray[i].indexOf("=")+1,cookieArray[i].indexOf(";"));
			}
			String tempCookie=cookieArray[i];
			if(tempCookie.substring(tempCookie.indexOf("=")+1,tempCookie.indexOf(";")).equals("")){
            continue;				
			}
			cookie.append(tempCookie.substring(0,tempCookie.indexOf(";")));
			cookie.append("; ");
		}
		//获取跳转URL的cookies
		for(int i=0;i<headers.size();i++){
			if(headers.get(i).getHeader()==null){
				continue;
			}
			if(headers.get(i).getHeader().equals("Set-Cookie")){
			String temp=headers.get(i).getValue().substring(0,headers.get(i).getValue().indexOf(";")+1);
			//避免重复
			if(cookie.indexOf(temp)!=-1){
				continue;
			}
			//判断每一项cookie的值是否为空，如果等于号后面直接就是分号结尾，那就是空值，需要过滤
			if(!temp.substring(temp.indexOf("=")+1,temp.length()).equals(";")){
			cookie.append(temp+" ");
			}
			}
		}
		//生成一个登录表单
		JSONObject r=new JSONObject();
		r.put("ptwebqq",ptwebqq);
		r.put("clientid",53999199);
		r.put("psessionid","");
		r.put("status","online");
		//访问登录链接，需要带上前面的cookies访问
		Response loginResult=util.post(URL.URL_LOGIN,new PostParameter[]{new PostParameter("r",r.toString())},new HttpHeader[]{new HttpHeader("Cookie",cookie.toString())});
		JSONObject result=JSONObject.fromObject(JSONObject.fromObject(loginResult.getContent("UTF-8")));
		JSONObject data=JSONObject.fromObject(result.get("result"));
		//获取自己的uin
		long uin=data.getLong("uin");
		//获取psessionid，这个在收发消息需要用到
		String psessionid=data.getString("psessionid");
		//获取vfwebqq，必须带上Referer和前面的cookies
		String vfwebqq_temp=util.get(URL.URL_GET_VFWEBQQ,new HttpHeader[]{URL.URL_REFERER,new HttpHeader("Cookie",cookie.toString())}).getContent("UTF-8");
		//将结果解析为JSON对象
		JSONObject result_jsonObj=JSONObject.fromObject(vfwebqq_temp);
		//构造一个凭据对象，将获取到的数据全部传入
		Credential credential=new Credential(uin,result_jsonObj.getJSONObject("result").getString("vfwebqq"),psessionid,ptwebqq,new HttpHeader("Cookie",cookie.toString()));
		//返回码，这个可有可无，无所谓
		credential.setRetcode(result.getInt("retcode"));
		return credential;
	}
	
	private static class ObserveThread extends Thread{
		
		//验证结果监听器
		private QrcodeVerifyListener listener=null;
		private String cookie=null;
		public ObserveThread(String cookie,QrcodeVerifyListener listener){
			this.listener=listener;
			this.cookie=cookie;
		}
		@Override
		public void run(){
	    //这个cookie里只有qrsig，所以只需要截取它用于生成ptqrlogin就行了
		String ptqrlogin=Integer.toString(genPtqrToken(cookie.substring(cookie.indexOf("=")+1,cookie.indexOf(";"))));
		//验证链接
		String verifyURL=URL.URL_VERIFY_QRCODE.replace("[var]",ptqrlogin);
		while(true){
			//获取服务器的响应，这一步需要带上之前的cookie
			Response response=util.get(verifyURL,new HttpHeader[]{new HttpHeader("Cookie",cookie)});
			String result=response.getContent("UTF-8");
			//分为两种情况，一种是返回"登录成功"，一种是返回"二维码已失效"
			if(result.contains("成功")){
				//取出Cookies，这些cookies在获取登录所需的cookies是必须的
				ArrayList<HttpHeader> headers=response.getHeaders();
				StringBuffer cookies=new StringBuffer();
				for(int i=0;i<headers.size();i++){
					if(headers.get(i).getHeader()==null){
						continue;
					}
					if(headers.get(i).getHeader().equals("Set-Cookie")){
					cookies.append(headers.get(i).getValue()+" ");
					}
				}
				//将服务器返回结果分割
				String[] message=result.split(",");
				for(int i=0;i<message.length;i++){
					//因为这个返回结果中带有一个302链接，访问它能得到必要的cookies，所以需要截取它
					if(message[i].contains("http")){
						String url=message[i];
						//将链接和验证成功产生的Cookies传给回调方法，回调方法里最好将这两个数据传给getCredential方法获取凭据
						listener.onVerified(url.substring(1,url.length()-1),new HttpHeader("Cookie",cookies.toString()));
						this.interrupt();
						break;
					}
				}
				this.interrupt();
				break;
			}
			if(result.contains("已失效")){
				//验证失败
				listener.onFailed();
				this.interrupt();
				break;
			}
		}
		
		}
		
	}
}
