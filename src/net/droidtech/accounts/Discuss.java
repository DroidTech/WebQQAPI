package net.droidtech.accounts;

import java.util.ArrayList;

import net.droidtech.consoleqq.Credential;
import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.utils.URL;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Discuss {
	private long discussID=0;
	private String name=null;
	private Credential credential=null;
	private final HttpUtils utils=new HttpUtils();
	
	//构造一个讨论组对象，这个不需要由用户构造，一般是Account对象的getDiscussesList方法返回这种类型的对象
	public Discuss(long discussID,Credential credential){
		this.discussID=discussID;
		this.credential=credential;
	}
	
	//将这个id认为是一个讨论组id
	public Discuss(long discussID){
		this.discussID=discussID;
	}
	
	//设置名称，不需要由用户设置，除非有特殊需要
	public void setName(String name){
		this.name=name;
	}
	
	//获取讨论组成员，由第二个构造方法产生的讨论组对象不具备这个功能
	public ArrayList<User> getDiscussUsers(){
		ArrayList<User> users=new ArrayList<User>();
		//用这个讨论组的did，以及vfwebqq合成一个完整的URL，并且将结果解析为json对象
		String url=URL.URL_GET_DISCUSS_INFO.replace("[var]",Long.toString(discussID)).replace("[var1]",credential.getVfWebQQ()).replace("[var2]",credential.getPsessionID());
		String info=utils.get(url,new HttpHeader[]{URL.URL_MESSAGE_REFERER,credential.getCookie()}).getContent("UTF-8");
		JSONObject result=JSONObject.fromObject(info).getJSONObject("result");
		//获取用户信息
		JSONArray usersInfo=result.getJSONArray("mem_info");
		//获取在线状态
		JSONArray usersStatus=result.getJSONArray("mem_status");
		//设置昵称，状态以及uin
		int tempIndex=0;
		for(int i=0;i<usersInfo.size();i++){
			JSONObject tempUserInfo=JSONObject.fromObject(usersInfo.get(i));
			User tempUser=new User(tempUserInfo.getLong("uin"));
			tempUser.setNickName(tempUserInfo.getString("nick"));
			if(tempIndex<usersStatus.size()&&tempUser.getUID()==JSONObject.fromObject(usersStatus.get(tempIndex)).getLong("uin")){
				tempUser.setOnlineStatus(true);
				tempIndex++;
		}
			users.add(tempUser);
		}
		return users;
		
	}
	
	public long getDiscussID(){
		return discussID;
	}
	
	public String getName(){
		return name;
	}
}
