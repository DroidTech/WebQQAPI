package net.droidtech.accounts;

import java.util.ArrayList;
import net.droidtech.consoleqq.Credential;
import net.droidtech.consoleqq.MessageReceivedListener;
import net.droidtech.consoleqq.Receiver;
import net.droidtech.consoleqq.Sender;
import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpRequestException;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.httputils.OnRequestFinishedListener;
import net.droidtech.httputils.PostParameter;
import net.droidtech.httputils.Response;
import net.droidtech.utils.URL;
import net.droidtech.utils.UserLogo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Account {
	private Credential credential=null;
	private final HttpUtils utils=new HttpUtils();
	private UserLogo logoUtil=null;
	
	//使用凭据构造一个Account对象
	public Account(Credential credential){
		this.credential=credential;
		this.logoUtil=new UserLogo(credential);
	}
	
	//获取自己的信息
	public final User getSelfInfo(){
		JSONObject data=JSONObject.fromObject(utils.get(URL.URL_GET_SELF_INFO,new HttpHeader[]{URL.URL_REFERER,credential.getCookie()}).getContent("UTF-8"));
		JSONObject info=data.getJSONObject("result");
		//获取生日
		JSONObject birthday=info.getJSONObject("birthday");
		//构造一个用户
		User user=new User(info.getLong("account"));
		//设置昵称为结果内的nick值
		user.setNickName(info.getString("nick"));
		//设置性别为结果内的gender值，有male，female，unknown三种属性
		user.setGender(info.getString("gender"));
		//设置生日
		user.setBirthday(birthday.getInt("year")+"-"+birthday.getInt("month")+"-"+birthday.getInt("day"));
		//设置vip级别
		user.setVipLevel(info.getInt("vip_info"));
		//获取自己的签名
		user.setPersonal(info.getString("lnick"));
		//设置自己的头像
		user.setLogo(logoUtil.getUserLogoByUin(info.getLong("account")));
		return user;
	}
	
	public Sender getSender(){
		//获得此Account对象的Sender
		return new Sender(credential);
	}
	
	public Receiver getReceiver(MessageReceivedListener listener){
		//获得此Account对象的Receiver
		return new Receiver(credential,listener);
	}
	
	//获取讨论组列表
	public ArrayList<Discuss> getDiscussList(){
		ArrayList<Discuss> discussesList=new ArrayList<Discuss>();
		//用psessionid合成一个完整的URL并且访问它，并将结果解析为json
		JSONObject result=JSONObject.fromObject(utils.get(URL.URL_GET_DISCUSS_LIST.replace("[var]",credential.getPsessionID()).replace("[var1]",credential.getVfWebQQ()),new HttpHeader[]{URL.URL_REFERER,credential.getCookie()}).getContent("UTF-8")).getJSONObject("result");
		//获取讨论组列表
		JSONArray discussesListInfo=result.getJSONArray("dnamelist");
		//构造讨论组列表
		for(int i=0;i<discussesListInfo.size();i++){
			JSONObject tempDiscussInfo=JSONObject.fromObject(discussesListInfo.get(i));
			//取出讨论组id，构造一个讨论组，credential用于获取成员列表
			Discuss tempDiscuss=new Discuss(tempDiscussInfo.getLong("did"),credential);
			//设置讨论组的名称
			tempDiscuss.setName(tempDiscussInfo.getString("name"));
			//添加到list内
			discussesList.add(tempDiscuss);
		}
		return discussesList;
	}
	
	//获取群列表
	public ArrayList<Group> getGroupList(){
		ArrayList<Group> groupsList=new ArrayList<Group>();
		JSONObject r=new JSONObject();
		r.put("vfwebqq",credential.getVfWebQQ());
		r.put("hash",credential.getHash());
		//构造一个请求表单，用于获取群列表
		
		//访问获取群信息的链接，并将结果解析为json
		JSONObject result=JSONObject.fromObject(utils.post(URL.URL_GET_GROUP_LIST,new PostParameter[]{new PostParameter("r",r.toString())},new HttpHeader[]{URL.URL_REFERER,credential.getCookie()}).getContent("UTF-8")).getJSONObject("result");
		//从结果取出群列表
		JSONArray groups=result.getJSONArray("gnamelist");
		//构造群对象
		for(int i=0;i<groups.size();i++){
			JSONObject tempInfo=JSONObject.fromObject(groups.get(i));
			//构造一个群对象，gid为群id，gcode用于获取成员，credential用于获取成员列表
			Group tempGroup=new Group(tempInfo.getLong("gid"),tempInfo.getLong("code"),credential);
			//设置群的名称
			tempGroup.setName(tempInfo.getString("name"));
			groupsList.add(tempGroup);
		}
		return groupsList;
	}
	
	//获取分组
	public ArrayList<String> getCategories(){
		ArrayList<String> categories=new ArrayList<String>();
		JSONObject r=new JSONObject();
		//构造一个请求表单
		r.put("vfwebqq",credential.getVfWebQQ());
		//这里用到了hash
		r.put("hash", credential.getHash());
		//访问获取好友列表的链接，取出分组信息
		JSONArray result=JSONObject.fromObject(JSONObject.fromObject(utils.post(URL.URL_GET_FRIEND_LIST,new PostParameter[]{new PostParameter("r",r.toString())},new HttpHeader[]{URL.URL_REFERER,credential.getCookie()}).getContent("UTF-8")).getJSONObject("result")).getJSONArray("categories");
		//取出分组的名称
		for(int i=0;i<result.size();i++){
			categories.add(JSONObject.fromObject(result.get(i)).getString("name"));
		}
		return categories;
	}
	
	//重载的方法，默认不带头像
	public ArrayList<User> getFriendList(){
		return getFriendList(false);
	}
	
	//获取好友列表，withHeadImg为true时会带上头像
	public ArrayList<User> getFriendList(final boolean withHeadImg){
		final ArrayList<User> friendsList=new ArrayList<User>();
		JSONObject r=new JSONObject();
		//构造一个请求表单，也用到了hash
		r.put("vfwebqq",credential.getVfWebQQ());
		r.put("hash", credential.getHash());
		HttpUtils utils=new HttpUtils();
		//访问获取好友列表的链接，带上cookies和referer
		JSONObject result=JSONObject.fromObject(JSONObject.fromObject(utils.post(URL.URL_GET_FRIEND_LIST,new PostParameter[]{new PostParameter("r",r.toString())},new HttpHeader[]{URL.URL_REFERER,credential.getCookie()}).getContent("UTF-8")).getJSONObject("result"));
		//取出friends，这里面包含了分组索引信息
		JSONArray friends=result.getJSONArray("friends");
		//分组列表，包含了分组的索引号，与friends里的分组索引是对应的
		JSONArray categories=result.getJSONArray("categories");
		//获取备注
		JSONArray marknames=result.getJSONArray("marknames");
		//获取在线好友
		JSONArray onlineUsersInfo=JSONObject.fromObject(utils.get(URL.URL_GET_ONLINE_USERS.replace("[var]",credential.getVfWebQQ()).replace("[var1]",credential.getPsessionID()),new HttpHeader[]{URL.URL_MESSAGE_REFERER,credential.getCookie()}).getContent("UTF-8")).getJSONArray("result");
		//使用多线程加速抓取好友的信息，头像
		for(int i=0;i<friends.size();i++){
			long tempUin=JSONObject.fromObject(friends.get(i)).getLong("uin");
			utils.getInThread(URL.URL_GET_FRIEND_INFO.replace("[var]",Long.toString(tempUin)).replace("[var1]",credential.getVfWebQQ()).replace("[var2]",credential.getPsessionID()),new HttpHeader[]{URL.URL_REFERER,credential.getCookie()},new OnRequestFinishedListener(){

				@Override
				public void onError(HttpRequestException arg0) {
					// TODO Auto-generated method stub
					arg0.printStackTrace();
				}

				@Override
				public void onFinished(Response arg0) {
					// TODO Auto-generated method stub
					//请求完成时利用返回的数据构造一个User对象，与解析个人信息的过程一样
					JSONObject tempUserInfo=JSONObject.fromObject(arg0.getContent("UTF-8")).getJSONObject("result");
					JSONObject birthday=tempUserInfo.getJSONObject("birthday");
					User tempUser=new User(tempUserInfo.getLong("uin"));
					tempUser.setNickName(tempUserInfo.getString("nick"));
					tempUser.setGender(tempUserInfo.getString("gender"));
					tempUser.setBirthday(tempUserInfo.getJSONObject("birthday").getInt("year")+"-"+birthday.getInt("month")+"-"+birthday.getInt("day"));
					if(withHeadImg){
					tempUser.setLogo(logoUtil.getUserLogoByUin(tempUserInfo.getLong("uin")));
					}
					tempUser.setVipLevel(tempUserInfo.getInt("vip_info"));
					tempUser.setPersonal(tempUserInfo.getString("personal"));
					friendsList.add(tempUser);
				}});
			
		}
		
		//等待线程完成工作，否则将导致返回一个不完整的列表
		while(true){
			try {
				//避免死锁
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//好友列表和friends的尺寸一样，表示获取完成，可以中断阻塞
			if(friendsList.size()==friends.size()){
				break;
			}
		}
		//由于是多线程，这里需要重新排序，排序的顺序和friends保持一致
		ArrayList<User> sortedList=new ArrayList<User>();
		for(int i=0;i<friends.size();i++){
			long tempUin=JSONObject.fromObject(friends.get(i)).getLong("uin");
			for(int i3=0;i3<friendsList.size();i3++){
				if(friendsList.get(i3).getUID()==tempUin){
					sortedList.add(friendsList.get(i3));
					break;
				}
			}
		}
		//将好友列表清除，并设置为重新排序过的列表
		friendsList.clear();
		friendsList.addAll(sortedList);
		//设置备注，uin以及所在的分组名称
		int tempIndex=0;
		int tempIndex2=0;
			for(int i=0;i<friends.size();i++){
				long tempUID=friendsList.get(i).getUID();
				if(tempIndex<marknames.size()&&tempUID==JSONObject.fromObject(marknames.get(tempIndex)).getLong("uin")){
				    friendsList.get(i).setMarkName(JSONObject.fromObject(marknames.get(tempIndex)).getString("markname"));
				    tempIndex++;
				}
			    if(tempIndex2<onlineUsersInfo.size()&&tempUID==JSONObject.fromObject(onlineUsersInfo.get(tempIndex2)).getLong("uin")){
			        friendsList.get(i).setOnlineStatus(true);
			        tempIndex2++;
			    }
			    for(int i3=0;i3<categories.size();i3++){
					if(JSONObject.fromObject(friends.get(i)).getInt("categories")==JSONObject.fromObject(categories.get(i3)).getInt("index")){
						friendsList.get(i).setCategory(JSONObject.fromObject(categories.get(i3)).getString("name"));
					}
				}
			}
		return friendsList;
	}

}
