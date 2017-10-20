package net.droidtech.consoleqq;

public class Message {
	
	//常量，表示消息类型
	public static final int MESSAGE_TYPE_FRIEND_MESSAGE=1;
	public static final int MESSAGE_TYPE_GROUP_MESSAGE=4;
	public static final int MESSAGE_TYPE_DISCUSS_MESSAGE=5;
	//消息主体
	private String message=null;
	//消息来源，不需要由用户构造
	private long sourceGroupID=0;
	private long sourceUserID=0;
	
	//构造一个来自普通用户的消息
	public Message(String content,long sourceUserID){
		this.message=content;
		this.sourceUserID=sourceUserID;
	}
	
	//构造一个来自讨论组和群的消息
	public Message(String content,long sourceGroupID,long sourceUserID){
		this.message=content;
		this.sourceGroupID=sourceGroupID;
		this.sourceUserID=sourceUserID;
	}
	
	//获取消息主体
	public String getContent(){
		return message;
	}
	
	
	//获取消息来自哪个群，返回一个群id
	public long getSourceGroupID(){
		return sourceGroupID;
	}
	
	//获取消息来自哪个用户，返回一个用户id
	public long getSourceUserID(){
		return sourceUserID;
	}
	
}
