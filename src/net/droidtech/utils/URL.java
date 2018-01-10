package net.droidtech.utils;

import net.droidtech.httputils.HttpHeader;

public class URL {
	//URL的列表
	public static final String URL_GET_QRCODE="https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.7035131321700823";
	public static final String URL_VERIFY_QRCODE="https://ssl.ptlogin2.qq.com/ptqrlogin?ptqrtoken=[var]&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=https://w.qq.com/proxy.html?login2qq=1&webqq_type=10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-30179&mibao_css=m_webqq&t=undefined&g=1&js_type=0&js_ver=10224&login_sig=&pt_randsalt=0";
	public static final String URL_LOGIN="https://d1.web2.qq.com/channel/login2";
	public static final String URL_GET_VFWEBQQ="https://s.web2.qq.com/api/getvfwebqq?ptwebqq=[var]&clientid=53999199&psessionid=&t=1499517975271";
	public static final String URL_GET_USER_LOGO="https://face1.web.qq.com/cgi/svr/face/getface?cache=1&type=1&f=100&uin=[var]&t=1499526299&vfwebqq=[var1]";
    public static final String URL_GET_FRIEND_LIST="https://s.web2.qq.com/api/get_user_friends2";
    public static final String URL_GET_GROUP_LIST="https://s.web2.qq.com/api/get_group_name_list_mask2";
    public static final String URL_GET_ONLINE_USERS="https://d1.web2.qq.com/channel/get_online_buddies2?vfwebqq=[var]&clientid=53999199&psessionid=[var1]";
    public static final String URL_GET_SELF_INFO="https://s.web2.qq.com/api/get_self_info2?t=1499502657373";
    public static final String URL_GET_FRIEND_INFO="https://s.web2.qq.com/api/get_friend_info2?tuin=[var]&vfwebqq=[var1]&clientid=53999199&psessionid=[var2]";
    public static final String URL_GET_GROUP_INFO="https://s.web2.qq.com/api/get_group_info_ext2?gcode=[var]&vfwebqq=[var1]&t=1499622848638";
    public static final String URL_GET_DISCUSS_LIST="https://s.web2.qq.com/api/get_discus_list?clientid=53999199&psessionid=[var]&vfwebqq=[var1]&t=1499637043009";
    public static final String URL_GET_DISCUSS_INFO="https://d1.web2.qq.com/channel/get_discu_info?did=[var]&vfwebqq=[var1]&clientid=53999199&psessionid=[var2]";
    //没有实现获取最近的聊天列表，可以增加实现
    public static final String URL_GET_RECENT_LIST="https://d1.web2.qq.com/channel/get_recent_list2";
    public static final String URL_SEND_MESSAGE="https://d1.web2.qq.com/channel/send_buddy_msg2";
    public static final String URL_SEND_GROUP_MESSAGE="https://d1.web2.qq.com/channel/send_qun_msg2";
    public static final String URL_SEND_DISCUSS_MESSAGE="https://d1.web2.qq.com/channel/send_discu_msg2";
    public static final String URL_RECEIVE_MESSAGE="https://d1.web2.qq.com/channel/poll2";
    public static final HttpHeader URL_REFERER=new HttpHeader("Referer","https://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
    public static final HttpHeader URL_MESSAGE_REFERER=new HttpHeader("Referer","https://d1.web2.qq.com/cfproxy.html?v=20151105001&callback=1");
    public static final String URL_LOGOUT="https://ptlogin2.qq.com/logout?pt4_token=&pt4_hkey=0&pt4_ptcz=[var]&deep_logout=1";
    //QQ空间的实现
    public static final String URL_QZONE="https://user.qzone.qq.com/[var]/main";
}
