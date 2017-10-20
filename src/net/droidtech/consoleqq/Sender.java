package net.droidtech.consoleqq;

import java.net.URLEncoder;

import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.httputils.PostParameter;
import net.droidtech.httputils.Response;
import net.droidtech.utils.URL;
import net.sf.json.JSONObject;

public class Sender {
	
	private Credential credential=null;
	private static final HttpUtils utils=new HttpUtils();
	
	public Sender(Credential credential){
		//设置超时，有时会一直阻塞，影响速度，所以需要及时断开
		utils.setConnectTimeout(1500);
		utils.setReadTimeout(1500);
		this.credential=credential;
	}
	
	public boolean sendToFriend(long target,String msg){
		try{
				//消息主体不允许为空，否则替换为字符串null
				if(msg==null){
					msg="null";
				}
				//把目标用户的账号，消息主体，以及pesesionid合成为一个完整表单
				PostParameter r_toUser=new PostParameter("r","%7B%22to%22%3A"+target+"%2C%22content%22%3A%22%5B%5C%22"+URLEncoder.encode(msg.replace("\"","\\\\\\\""),"UTF-8")+"%5C%22%2C%5B%5C%22font%5C%22%2C%7B%5C%22name%5C%22%3A%5C%22%E5%AE%8B%E4%BD%93%5C%22%2C%5C%22size%5C%22%3A10%2C%5C%22style%5C%22%3A%5B0%2C0%2C0%5D%2C%5C%22color%5C%22%3A%5C%22000000%5C%22%7D%5D%5D%22%2C%22face%22%3A0%2C%22clientid%22%3A53999199%2C%22msg_id%22%3A1084768%2C%22psessionid%22%3A%22"+credential.getPsessionID()+"%22%7D");
				//禁用url编码
				r_toUser.setEncodingEnabled(false);
				//带上cookies和referer发送获取消息的post包，并检查返回码是否为0，如果是0则返回true，否则表示失败
				Response result=utils.post(URL.URL_SEND_MESSAGE,new PostParameter[]{r_toUser},new HttpHeader[]{URL.URL_MESSAGE_REFERER,credential.getCookie()});
				return JSONObject.fromObject(result.getContent("UTF-8")).getInt("retcode")==0;
			}catch(Exception e){
				//异常时总是返回失败
				return false;
			}
	}
	
	public boolean sendToGroup(long target,String msg){
		try{
			if(msg==null){
				msg="null";
			}
			//其他内容和发送到好友一样，只是group_uin为群id
		    PostParameter r_toGroup=new PostParameter("r","%7B%22group_uin%22%3A"+target+"%2C%22content%22%3A%22%5B%5C%22"+URLEncoder.encode(msg.replace("\"","\\\\\\\""),"UTF-8")+"%5C%22%2C%5B%5C%22font%5C%22%2C%7B%5C%22name%5C%22%3A%5C%22%E5%AE%8B%E4%BD%93%5C%22%2C%5C%22size%5C%22%3A10%2C%5C%22style%5C%22%3A%5B0%2C0%2C0%5D%2C%5C%22color%5C%22%3A%5C%22000000%5C%22%7D%5D%5D%22%2C%22face%22%3A0%2C%22clientid%22%3A53999199%2C%22msg_id%22%3A2030003%2C%22psessionid%22%3A%22"+credential.getPsessionID()+"%22%7D");
		    r_toGroup.setEncodingEnabled(false);
		    Response result=utils.post(URL.URL_SEND_GROUP_MESSAGE,new PostParameter[]{r_toGroup},new HttpHeader[]{URL.URL_MESSAGE_REFERER,credential.getCookie()});
		    return JSONObject.fromObject(result.getContent("UTF-8")).getInt("retcode")==0;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean sendToDiscuss(long target,String msg){
		try{
			if(msg==null){
				msg="null";
			}
			//其他内容和发送到好友一样，只是did为讨论组的id
			PostParameter r_toDiscuss=new PostParameter("r","%7B%22did%22%3A"+target+"%2C%22content%22%3A%22%5B%5C%22"+URLEncoder.encode(msg.replace("\"","\\\\\\\""),"UTF-8")+"%5C%22%2C%5B%5C%22font%5C%22%2C%7B%5C%22name%5C%22%3A%5C%22%E5%AE%8B%E4%BD%93%5C%22%2C%5C%22size%5C%22%3A10%2C%5C%22style%5C%22%3A%5B0%2C0%2C0%5D%2C%5C%22color%5C%22%3A%5C%22000000%5C%22%7D%5D%5D%22%2C%22face%22%3A0%2C%22clientid%22%3A53999199%2C%22msg_id%22%3A1084768%2C%22psessionid%22%3A%22"+credential.getPsessionID()+"%22%7D");
			r_toDiscuss.setEncodingEnabled(false);
			Response result=utils.post(URL.URL_SEND_DISCUSS_MESSAGE,new PostParameter[]{r_toDiscuss},new HttpHeader[]{URL.URL_MESSAGE_REFERER,credential.getCookie()});
			return JSONObject.fromObject(result.getContent("UTF-8")).getInt("retcode")==0;
		}catch(Exception e){
			return false;
		}
	}

}
