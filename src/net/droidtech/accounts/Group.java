package net.droidtech.accounts;

import java.util.ArrayList;
import net.droidtech.consoleqq.Credential;
import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.utils.URL;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Group {
	
	private long groupID=0;
	private String name=null;
	private long gcode=0;
	private Credential credential=null;
	private final HttpUtils utils=new HttpUtils();
	
	public Group(long groupID){
		this.groupID=groupID;
	}
	
	//构造一个群对象，这个方法一般不需要由用户调用
	public Group(long groupID,long gcode,Credential credential){
		this.groupID=groupID;
		this.credential=credential;
		this.gcode=gcode;
	}
	
	//获取公告
	public String getNotice(){
	    return JSONObject.fromObject(utils.get(URL.URL_GET_GROUP_INFO.replace("[var]",Long.toString(gcode)).replace("[var1]",credential.getVfWebQQ()),new HttpHeader[]{URL.URL_REFERER,credential.getCookie()}).getContent("UTF-8")).getJSONObject("result").getJSONObject("ginfo").getString("memo");
	}
	
	//设置名称，一般不需要由用户调用
	public void setName(String name){
		this.name=name;
	}
	
	//获取群成员
	public ArrayList<User> getGroupUsers(){
		ArrayList<User> users=new ArrayList<User>();
		//用群id，vfwebqq合成一个完整的URL并且带上cookies和referer访问，将结果解析为json对象
		JSONObject result=JSONObject.fromObject(utils.get(URL.URL_GET_GROUP_INFO.replace("[var]",Long.toString(gcode)).replace("[var1]",credential.getVfWebQQ()),new HttpHeader[]{URL.URL_REFERER,credential.getCookie()}).getContent("UTF-8")).getJSONObject("result");
		//获取成员列表
		JSONArray members=result.getJSONArray("minfo");
		//获取群名片
		JSONArray cards=new JSONArray();
		//如果没有人设置群名片则此处会引发异常
		try{
		cards=result.getJSONArray("cards");
		}catch(JSONException e){
			
		}
		//获取群成员的vip信息
		JSONArray vip_info=result.getJSONArray("vipinfo");
		//遍历成员列表，填充到成员list内
		int tempIndex=0;
		for(int i=0;i<members.size();i++){
			JSONObject tempMemberInfo=JSONObject.fromObject(members.get(i));
			User tempUser=new User(tempMemberInfo.getLong("uin"));
			tempUser.setNickName(tempMemberInfo.getString("nick"));
			tempUser.setGender(tempMemberInfo.getString("gender"));
			tempUser.setVipLevel(JSONObject.fromObject(vip_info.get(i)).getInt("vip_level"));
			if(tempIndex<cards.size()&&JSONObject.fromObject(cards.get(tempIndex)).getLong("muin")==tempUser.getUID()){
			tempUser.setMarkName(JSONObject.fromObject(cards.get(tempIndex)).getString("card"));
			tempIndex++;
			}
			users.add(tempUser);
		}
		return users;
		
	}
	
	//获取gcode,这不是群号
	public long getGroupCode(){
		return gcode;
	}
	
	//获取群id
	public long getGroupID(){
		return groupID;
	}
	
	//获取名称
	public String getName(){
		return name;
	}

}
