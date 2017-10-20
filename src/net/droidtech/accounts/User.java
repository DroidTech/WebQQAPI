package net.droidtech.accounts;

import java.util.Calendar;

public class User {
	
	private String nickname=null;
	private long account=0;
	private String gender=null;
	private int vip_level=0;
	private byte[] logo=null;
	private String birthday=null;
	private String personal=null;
	private String markname=null;
	private boolean isOnline=false;
	private String categoryName=null;
	
	//将这个id认为是一个用户对象
	public User(long account){
		this.account=account;
	}
	
	//设置所在分组的名称，一般不需要由用户调用
	public void setCategory(String name){
		this.categoryName=name;
	}
	
	//获取所在分组的名称
	public String getCategoryName(){
		return categoryName;
	}
	
	//设置个性签名，一般不需要由用户调用
	public void setPersonal(String personal){
		this.personal=personal;
	}
	
	//获取个性签名
	public String getPersonal(){
		return personal;
	}
	
	//设置备注，一般不需要由用户调用
	public void setMarkName(String markname){
		this.markname=markname;
	}
	
	//获取备注名称
	public String getMarkName(){
		return markname;
	}
	
	//设置在线状态，一般不需要由用户调用
	public void setOnlineStatus(boolean status){
		this.isOnline=status;
	}
	
	//是否在线
	public boolean isOnline(){
		return isOnline;
	}
	
	//设置昵称，一般不需要由用户调用
	public void setNickName(String nickName){
		this.nickname=nickName;
	}
	
	//获取昵称
	public String getNickName(){
		return nickname;
	}
	
	//获取UID
	public long getUID(){
		return account;
	}
	
	//设置vip级别，一般不需要由用户调用
	public void setVipLevel(int level){
		this.vip_level=level;
	}
	
	//获取vip级别
	public int getVipLevel(){
		return vip_level;
	}
	
	//设置生日，一般不需要由用户调用
	public void setBirthday(String birthday){
		this.birthday=birthday;
	}
	
	//获取生日
	public String getBirthday(){
		return this.birthday;
	}
	
	//用生日和当前时间计算年龄，如果生日有误，则此值也有误
	public int getAge(){
		String[] parts=birthday.split("-");
		int year=Integer.parseInt(parts[0]);
		int month=Integer.parseInt(parts[1]);
		int day=Integer.parseInt(parts[2]);
		Calendar calendar=Calendar.getInstance();
		int currentYear=calendar.get(Calendar.YEAR);
		int currentMonth=calendar.get(Calendar.MONTH);
		int currentDay=calendar.get(Calendar.DATE);
		int age=(currentYear-year)-1;
		if(currentMonth>=month){
			if(currentDay>=day){
			age++;
			}
		}
		return age;
	}
	
	//设置性别，一般不需要由用户调用
	public void setGender(String gender){
		this.gender=gender;
	}
	
	//获取性别
	public String getGender(){
		return gender;
	}
	
	//设置头像数据，一般不需要由用户调用
	public void setLogo(byte[] logo){
		this.logo=logo;
	}
	
	//获取头像数据
	public byte[] getLogo(){
		return logo;
	}

}
