package net.droidtech.consoleqq;

import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.httputils.PostParameter;
import net.droidtech.httputils.Response;
import net.droidtech.utils.URL;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Sender {
	
	private Credential credential=null;
	private final HttpUtils utils=new HttpUtils();
	private String TYPE_FRIEND_MESSAGE="to";
	private String TYPE_GROUP_MESSAGE="group_uin";
	private String TYPE_DISCUSS_MESSAGE="did";
	
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
				//把目标用户的id，消息主体，以及pesesionid合成为一个完整表单
				PostParameter r_toUser=new PostParameter("r",makeForm(this.TYPE_FRIEND_MESSAGE,msg,target,credential.getPsessionID()));
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
		    PostParameter r_toGroup=new PostParameter("r",makeForm(this.TYPE_GROUP_MESSAGE,msg,target,credential.getPsessionID()));
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
			PostParameter r_toDiscuss=new PostParameter("r",makeForm(this.TYPE_DISCUSS_MESSAGE,msg,target,credential.getPsessionID()));
			Response result=utils.post(URL.URL_SEND_DISCUSS_MESSAGE,new PostParameter[]{r_toDiscuss},new HttpHeader[]{URL.URL_MESSAGE_REFERER,credential.getCookie()});
			return JSONObject.fromObject(result.getContent("UTF-8")).getInt("retcode")==0;
		}catch(Exception e){
			return false;
		}
	}
	
	private String makeForm(String type,String message,long id,String psessionid){
		JSONObject r=new JSONObject();
		r.put(type,id);
		JSONArray content=new JSONArray();
		content.add(message);
		JSONArray font=new JSONArray();
		font.add("font");
		JSONObject fontType=new JSONObject();
		fontType.put("name","宋体");
		fontType.put("size",10);
		JSONArray fontStyle=new JSONArray();
		fontStyle.add(0);
		fontStyle.add(0);
		fontStyle.add(0);
		fontType.put("style",fontStyle);
		fontType.put("color","00FF00");
		font.add(fontType.toString());
		content.add(font.toString());
		r.put("content","\""+content.toString()+"\"");
		r.put("face",0);
		r.put("clientid", 53999199);
		r.put("msg_id",1024768);
		r.put("psessionid",psessionid);
		return r.toString();
	}

}
