package net.droidtech.consoleqq;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.droidtech.httputils.HttpHeader;
import net.droidtech.httputils.HttpUtils;
import net.droidtech.httputils.PostParameter;
import net.droidtech.httputils.Response;
import net.droidtech.io.DroidFile;
import net.droidtech.utils.URL;
import net.sf.json.JSONObject;

public class Credential implements java.io.Serializable{
	
	private static final long serialVersionUID = 851278709963178295L;
	private long uin=0;
	private String vfwebqq=null;
	private String psessionid=null;
	private String hash=null;
	private int retcode=0;
	private String ptwebqq=null;
	private HttpHeader cookie=null;
	
	public Credential(long uin,String vfwebqq,String psessionid,String ptwebqq,HttpHeader cookie){
		this.uin=uin;
		this.vfwebqq=vfwebqq;
		this.psessionid=psessionid;
		this.ptwebqq=ptwebqq;
		this.hash=hash(uin,ptwebqq);
		this.cookie=cookie;
	}
	
	//生成hash值，这个在获取好友和自己的信息时需要使用
	private String hash(long x, String K) {
		if(K==null){
			return null;
		}
        int[] N = new int[4];
        for (int T = 0; T < K.length(); T++) {
            N[T % 4] ^= K.charAt(T);
        }
        String[] U = {"EC", "OK"};
        long[] V = new long[4];
        V[0] = x >> 24 & 255 ^ U[0].charAt(0);
        V[1] = x >> 16 & 255 ^ U[0].charAt(1);
        V[2] = x >> 8 & 255 ^ U[1].charAt(0);
        V[3] = x & 255 ^ U[1].charAt(1);

        long[] U1 = new long[8];

        for (int T = 0; T < 8; T++) {
            U1[T] = T % 2 == 0 ? N[T >> 1] : V[T >> 1];
        }

        String[] N1 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String V1 = "";
        for (long aU1 : U1) {
            V1 += N1[(int) ((aU1 >> 4) & 15)];
            V1 += N1[(int) (aU1 & 15)];
        }
        return V1;
    }
	
	//从js拿来的
	private String hash33(String arg){
		long t=0;
		for(int e=0,n=arg.length();e<n;++e){
			t+=(t<<5)+arg.charAt(e);
		}
			return Long.toUnsignedString(2147483647&t);
	}
	
	public void setRetcode(int retcode){
		this.retcode=retcode;
	}
	
	public int getRetcode(){
		return retcode;
	}
	
	public String getPsessionID(){
		return psessionid;
	}
	
	public HttpHeader getCookie(){
		return cookie;
	}
	
	public String getVfWebQQ(){
		return vfwebqq;
	}
	
	//使用这些数据进行登录验证这个凭据是否还有效
	public boolean isValid(){
		JSONObject r=new JSONObject();
		r.put("ptwebqq",ptwebqq);
		r.put("clientid",53999199);
		r.put("psessionid","");
		r.put("status","online");
		Response loginResult=new HttpUtils().post(URL.URL_LOGIN,new PostParameter[]{new PostParameter("r",r.toString())},new HttpHeader[]{cookie});
		JSONObject result=JSONObject.fromObject(JSONObject.fromObject(loginResult.getContent("UTF-8")));
		if(result.getInt("retcode")==0){
			return true;
		}else{
			return false;
		}
	}
	
	//保存凭据，载入这个凭据对象需要使用ObjectInputStream
	public boolean save(File file){
		DroidFile target=new DroidFile(file.getAbsolutePath());
		target.createNewFile();
		try {
			ObjectOutputStream out=new ObjectOutputStream(target.getOutputStream());
			out.writeObject(this);
			out.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	} 
	
	//销毁凭据，凭据一旦销毁则必须退出程序
	public void destroy(){
		String value=cookie.getValue();
		String[] cookies=value.split("; ");
		String ptcz=null;
		for(int i=0;i<cookies.length;i++){
			String temp=cookies[i];
			if(temp.contains("ptcz")){
				ptcz=temp.substring(temp.indexOf("=")+1);
				break;
			}
		}
		HttpUtils utils=new HttpUtils();
		utils.get(URL.URL_LOGOUT.replace("[var]",hash33(ptcz)),new HttpHeader[]{cookie});
		//https://ptlogin2.qq.com/logout?pt4_token=&pt4_hkey=0&pt4_ptcz=1573999124&deep_logout=1
		
	}
	
	public String getPtWebQQ(){
		return ptwebqq;
	}
	
	public String getHash(){
		return hash;
	}
	
	public long getUin(){
		return uin;
	}

}
