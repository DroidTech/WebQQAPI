package net.droidtech.consoleqq;

import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.httputils.PostParameter;
import net.droidtech.httputils.Response;
import net.droidtech.utils.URL;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Receiver {
	
	private ReceiverThread receiverThread=null;
	private Credential credential=null;
	private MessageReceivedListener listener=null;
	
	//构造一个接收器
	public Receiver(Credential credential,MessageReceivedListener listener){
		this.credential=credential;
		this.listener=listener;
	}
	
	//启动接收器
	public void startReceiveService(){
		if(receiverThread==null){
		receiverThread=new ReceiverThread(credential,listener);	
		receiverThread.start();
		}
	}
	
	public boolean isRunning(){
		return !(receiverThread==null);
	}
	
	//停止接收器
	public void stopReceiveService(){
		if(receiverThread!=null){
		receiverThread.interrupt();
		receiverThread=null;
		}
	}
	
	private class ReceiverThread extends Thread{
		
		//消息监听器
		private MessageReceivedListener listener=null;
		
		//凭据对象
		private Credential credential=null;
		
		public ReceiverThread(Credential credential,MessageReceivedListener listener){
			this.listener=listener;
			this.credential=credential;
		}
		
		@Override
		public void run(){
			//凭据和监听器不能为空，否则构造此接收器没有意义
			if(listener==null||credential==null){
				return;
			}
			HttpUtils utils=new HttpUtils();
			//设置读取超时，因为poll时如果没有任何消息会处于阻塞状态，直到最后返回一个无消息的提示或是返回消息才会结束连接
			utils.setReadTimeout(50000);
			//构造一个参数，用于获取消息
			JSONObject requestBody=new JSONObject();
			requestBody.put("ptwebqq",credential.getPtWebQQ());
			//固定值
			requestBody.put("clientid",53999199);
			requestBody.put("psessionid",credential.getPsessionID());
			//固定结构
			requestBody.put("key","");
			
			while(true){
				Response response=null;
				try{
				//响应对象	
				response=utils.post(URL.URL_RECEIVE_MESSAGE,new PostParameter[]{new PostParameter("r",requestBody.toString())},new HttpHeader[]{URL.URL_MESSAGE_REFERER,credential.getCookie()});
				//把返回结果解析为json对象
				JSONObject result=JSONObject.fromObject(JSONObject.fromObject(response.getContent("UTF-8"))).getJSONArray("result").getJSONObject(0);
				//获取消息的标识，例如来自哪个用户
				JSONObject value=result.getJSONObject("value");
				//获取消息的主体
				JSONArray content=value.getJSONArray("content");
				//获取消息的类型
				int messageType=value.getInt("msg_type");
				//消息内容
				StringBuffer buffer=new StringBuffer();
				//第一个元素是字体数据，可以忽略，接下来的数据就是真正的消息内容，必须要循环取出才能获得完整的消息，否则例如@类消息会无法接收到@哪个用户
				for(int i=1;i<content.size();i++){
					buffer.append(content.get(i));
				}
				//如果是来自好友的消息，则调用这个回调，此方法有缺陷，无法过滤自己发送的消息，也就是说会接收到自己发送的消息，一些特殊用法下可能引起无限循环
				if(messageType==Message.MESSAGE_TYPE_FRIEND_MESSAGE){
					Message message=new Message(buffer.toString(),value.getLong("from_uin"));
					listener.onFriendMessage(message);
				}
				//如果是来自群的消息，则调用这个回调，此方法有缺陷，无法过滤自己发送的消息，也就是说会接收到自己发送的消息，一些特殊用法下可能引起无限循环
				if(messageType==Message.MESSAGE_TYPE_GROUP_MESSAGE){
					Message message=new Message(buffer.toString(),value.getLong("from_uin"),value.getLong("send_uin"));
					listener.onGroupMessage(message);
				}
				//如果是来自讨论组的消息，则调用这个回调，此方法有缺陷，无法过滤自己发送的消息，也就是说会接收到自己发送的消息，一些特殊用法下可能引起无限循环
				if(messageType==Message.MESSAGE_TYPE_DISCUSS_MESSAGE){
					Message message=new Message(buffer.toString(),value.getLong("did"),value.getLong("send_uin"));
					listener.onDiscussMessage(message);
				}
				}catch(Exception e){
					if(e.getMessage().contains("SocketTimeoutException")){
						continue;
					}
					listener.onError(e,response);
					receiverThread=null;
					break;
				}
			}
			
		}
	}

}
