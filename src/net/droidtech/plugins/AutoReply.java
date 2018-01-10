package net.droidtech.plugins;

import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.droidtech.accounts.Account;
import net.droidtech.accounts.User;
import net.droidtech.consoleqq.Credential;
import net.droidtech.consoleqq.Message;
import net.droidtech.consoleqq.Sender;
import net.droidtech.httputils.Response;
import net.droidtech.io.DroidFile;
import net.droidtech.plugin.DroidPlugin;

public class AutoReply implements DroidPlugin {
	
	private String[] keywords=null;
	private String[][] replydata=null;
	private Sender sender=null;
	private User my=null;
	private static final String[] replace=new String[]{"[uname]","[message]"};
	private Credential credential=null;
	private long selfUin=0;

	@Override
	public void init(Credential credential,DroidFile configureDir) {
		// TODO Auto-generated method stub
		loadData(configureDir);
		this.credential=credential;
		sender=new Sender(this.credential);
		my=new Account(this.credential).getSelfInfo();
		selfUin=my.getUID();
		System.out.println("当前用户:"+my.getUID()+",昵称为:"+my.getNickName()+",个性签名为:"+my.getPersonal());
	}
	
	private void loadData(DroidFile configureDir){
		DroidFile dataFile=configureDir.getChildFile(DroidFile.separator+"replyData.xml");
		if(dataFile!=null){
			System.out.println("找到了数据文件,正在载入...");
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document=builder.parse(dataFile);
				Element root=document.getDocumentElement();
				NodeList keywordNodes=root.getElementsByTagName("keyword");
				keywords=new String[keywordNodes.getLength()];
				replydata=new String[keywordNodes.getLength()][];
				for(int i=0;i<keywordNodes.getLength();i++){
					Node keyword=keywordNodes.item(i);
					keywords[i]=keyword.getAttributes().getNamedItem("name").getNodeValue();
					NodeList replyDataNodes=keyword.getChildNodes();
					ArrayList<String> replys=new ArrayList<String>();
					for(int i3=0;i3<replyDataNodes.getLength();i3++){
						if(replyDataNodes.item(i3).getNodeType()==Node.TEXT_NODE){
							continue;
						}
						replys.add(replyDataNodes.item(i3).getAttributes().getNamedItem("data").getNodeValue());
					}
					replydata[i]=replys.toArray(new String[0]);
				}
				System.out.println("数据载入完成.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		}else{
			return;
		}
	}

	@Override
	public void onGroupMessage(Message msg) {
		// TODO Auto-generated method stub
		if(msg.getSourceUserID()==selfUin){
			return;
		}
		if(keywords!=null){
			sender.sendToGroup(msg.getSourceGroupID(),getReplyMessage(msg.getContent()));
		}
	}

	@Override
	public void onFreindMessage(Message msg) {
		// TODO Auto-generated method stub
		if(keywords!=null){
		sender.sendToFriend(msg.getSourceUserID(),getReplyMessage(msg.getContent()));
		}
	}
	
	private String getReplyMessage(String arg){
			for(int i=0;i<keywords.length;i++){
				if(arg.contains(keywords[i])){
					String[] replys=replydata[i];
				    return replys[(int)(Math.random()*replys.length-1+1)].replace(replace[0],my.getNickName()).replace(replace[1],arg);
				}
			}
		return "";
	}

	@Override
	public void onDiscussMessage(Message msg) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onError(Exception e,Response response) {
		// TODO Auto-generated method stub
		e.printStackTrace();
	}

	@Override
	public void onRemoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "自动回复";
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
