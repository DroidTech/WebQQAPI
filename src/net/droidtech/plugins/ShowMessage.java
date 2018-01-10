package net.droidtech.plugins;

import java.util.ArrayList;
import net.droidtech.accounts.Account;
import net.droidtech.accounts.Discuss;
import net.droidtech.accounts.Group;
import net.droidtech.accounts.User;
import net.droidtech.consoleqq.Credential;
import net.droidtech.consoleqq.Message;
import net.droidtech.httputils.Response;
import net.droidtech.io.DroidFile;
import net.droidtech.plugin.DroidPlugin;

public class ShowMessage implements DroidPlugin {
	
	private Account myAccount=null;
	
	private ArrayList<Group> groupList=null;
	
	private ArrayList<Discuss> discussList=null;
	
	private ArrayList<User> friendList=null; 
	
	private ArrayList<ArrayList<User>> groupMemberLists=null;
	
	private ArrayList<ArrayList<User>> discussMemberLists=null;

	@Override
	public void init(Credential credential, DroidFile configureDir) {
		// TODO Auto-generated method stub
		//创建自己的Account对象
		myAccount=new Account(credential);
		//获取群列表
		groupList=myAccount.getGroupList();
		//获取好友列表
		friendList=myAccount.getFriendList();
		//获取讨论组列表
		discussList=myAccount.getDiscussList();
		//获取所有群的所有成员
		groupMemberLists=new ArrayList<ArrayList<User>>();
		for(int i=0;i<groupList.size();i++){
			groupMemberLists.add(groupList.get(i).getGroupUsers());
		}
		//获取所有讨论组的所有成员
		discussMemberLists=new ArrayList<ArrayList<User>>();
		for(int i=0;i<discussList.size();i++){
		    discussMemberLists.add(discussList.get(i).getDiscussUsers());
		}
	}

	@Override
	public void onGroupMessage(Message msg) {
		// TODO Auto-generated method stub
		for(int i=0;i<groupList.size();i++){
			//找到对应的群对象
			if(groupList.get(i).getGroupID()==msg.getSourceGroupID()){
				//获取群成员列表
				ArrayList<User> tempUserList=groupMemberLists.get(i);
				for(int i3=0;i3<tempUserList.size();i3++){
					//找到指定群员
					if(tempUserList.get(i3).getUID()==msg.getSourceUserID()){
						//输出消息，如果设置了群名片就显示群名片，否则显示昵称
						System.out.println("来自群:"+groupList.get(i).getName()+",成员:"+(tempUserList.get(i3).getMarkName()==null?tempUserList.get(i3).getNickName():tempUserList.get(i3).getMarkName())+" 的消息:"+msg.getContent());
						return;
					}
				}
			}
		}
	}

	@Override
	public void onFreindMessage(Message msg) {
		// TODO Auto-generated method stub
		for(int i=0;i<friendList.size();i++){
			if(friendList.get(i).getUID()==msg.getSourceUserID()){
				//输出消息，有备注就显示备注，没有备注就显示昵称
				System.out.println("来自好友:"+(friendList.get(i).getMarkName()==null?friendList.get(i).getNickName():friendList.get(i).getMarkName())+" 的消息:"+msg.getContent());
				break;
			}
		}
	}

	@Override
	public void onDiscussMessage(Message msg) {
		// TODO Auto-generated method stub
		for(int i=0;i<discussList.size();i++){
			//找到对应的讨论组对象
			if(discussList.get(i).getDiscussID()==msg.getSourceGroupID()){
				//获取讨论组成员列表
				ArrayList<User> tempUserList=discussMemberLists.get(i);
				for(int i3=0;i3<tempUserList.size();i3++){
					//找到指定成员
					if(tempUserList.get(i3).getUID()==msg.getSourceUserID()){
						//输出消息
						System.out.println("来自讨论组:"+discussList.get(i).getName()+",成员:"+tempUserList.get(i3).getNickName()+" 的消息:"+msg.getContent());
						return;
					}
				}
			}
		}
	}

	@Override
	public void onError(Exception e, Response response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoved() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "消息显示插件";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "1.0.0";
	}

	@Override
	public byte[] getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
