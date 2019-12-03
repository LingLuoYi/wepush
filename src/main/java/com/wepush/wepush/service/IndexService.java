package com.wepush.wepush.service;

import com.google.gson.Gson;
import com.wepush.wepush.config.Config;
import com.wepush.wepush.entity.User;
import com.wepush.wepush.entity.UserWx;
import com.wepush.wepush.utils.LoginUserUtils;
import com.wepush.wepush.utils.RocksUtils;
import me.xuxiaoxiao.chatapi.wechat.WeChatApi;
import me.xuxiaoxiao.chatapi.wechat.WeChatContacts;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXContact;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXGroup;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXUser;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXNotify;
import me.xuxiaoxiao.chatapi.wechat.protocol.*;
import me.xuxiaoxiao.xtools.common.XTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class IndexService {

    @Autowired
    private WeChatApi weChatApi;

    @Autowired
    private WeChatContacts weChatContacts;

    //登录验证码
    public String qrCode(HttpServletRequest httpServletRequest) {
        if (XTools.strEmpty(weChatApi.sid)) {
            String uuid = RocksUtils.get(httpServletRequest.getSession().getId());
            if (uuid != null && !"".equals(uuid)){
                return weChatApi.qrCode + uuid;
            }else {
                uuid = weChatApi.jslogin();
                RocksUtils.set(httpServletRequest.getSession().getId(),uuid);
                return weChatApi.qrCode + uuid;
            }
        } else {
            return null;
        }
    }

    public String isWxLogin(HttpServletRequest request) {//检查是否登录
        HttpSession httpSession = request.getSession();
        if (RocksUtils.get(httpSession.getId())!=null){
            weChatApi.setUuid(RocksUtils.get(httpSession.getId()));
        }
        RspLogin rspLogin = weChatApi.login();
        switch (rspLogin.code) {
            case 200:
                weChatApi.webwxnewloginpage(rspLogin.redirectUri);//用户登录，获取用户相关信息
                if ("-1".equals(weChatApi.sid)) {
                    RocksUtils.remove(httpSession.getId());
                    return "为了你的帐号安全，此微信号不能登录网页微信。";
                } else {
                    //初始化微信
                    initial();
                    RocksUtils.remove(httpSession.getId());
                    return "已授权登录";
                }
            case 201:
                return rspLogin.userAvatar;
            case 408:
                return "等待授权登录";
            default:
                return "登录超时,请重新获取二维码";
        }
    }

    //获取全部好友信息
    public Map<String, WXUser> friends(){
        return weChatContacts.getFriends();
    }

    //获取单个好友信息
    public WXUser friends(String id){
        return weChatContacts.getFriend(id);
    }

    //获取群信息
    public Map<String, WXGroup> getGroup(){
        return weChatContacts.getGroups();
    }

    //获取群详细信息
    public WXGroup getGroup(String id){
        return weChatContacts.getGroup(id);
    }

    public WXUser me(){
        return weChatContacts.getMe();
    }

    /**
     *
     * @param type 推送别名
     * @param id 推送人或群
     * @return
     */
    public String setPush(String type,String id){
        if (id == null || "".equals(id) || type == null || "".equals(type))
            return "参数不能为空";
        RocksUtils.set(Config.PUSH +type,id);
        return "成功";
    }

    /**
     * 查询所有的推送列表
     * @return
     */
    public List<Map<String, WXContact>> allPush(){
        List<String> types = RocksUtils.getLike(Config.PUSH);
        if (types == null)
            return null;
        List<Map<String, WXContact>> lianxiren = new ArrayList<>();
        for (String type:types) {
            String id = RocksUtils.get(type);
            Map<String,WXContact> map = new HashMap<>();
            if (id.startsWith("@@")){
                map.put(type,getGroup(id));
            }else {
                map.put(type,friends(id));
            }
            lianxiren.add(map);
        }
        return lianxiren;
    }

    @Nullable
    private void initial() {
        try {
            //获取自身信息
            RspInit rspInit = weChatApi.webwxinit();
            weChatContacts.setMe(weChatApi.host, rspInit.User);

            //获取并保存最近联系人
//            loadContacts(rspInit.ChatSet, true);
            if (!XTools.strEmpty(rspInit.ChatSet)) {
                LinkedList<ReqBatchGetContact.Contact> contacts = new LinkedList<>();
                for (String userName : rspInit.ChatSet.split(",")) {
                    if (!XTools.strEmpty(userName)) {
                        contacts.add(new ReqBatchGetContact.Contact(userName, ""));
                    }
                }
                contacts.removeIf(contact -> !contact.UserName.startsWith("@@") && weChatContacts.getContact(contact.UserName) != null);
                if (contacts.size() > 50) {
                    LinkedList<ReqBatchGetContact.Contact> temp = new LinkedList<>();
                    for (ReqBatchGetContact.Contact contact : contacts) {
                        temp.add(contact);
                        if (temp.size() >= 50) {
                            RspBatchGetContact rspBatchGetContact = weChatApi.webwxbatchgetcontact(contacts);
                            for (RspInit.User user : rspBatchGetContact.ContactList) {
                                weChatContacts.putContact(weChatApi.host, user);
                            }
                            temp.clear();
                        }
                    }
                    contacts = temp;
                }
                if (contacts.size() > 0) {
                    RspBatchGetContact rspBatchGetContact = weChatApi.webwxbatchgetcontact(contacts);
                    for (RspInit.User user : rspBatchGetContact.ContactList) {
                        weChatContacts.putContact(weChatApi.host, user);
                    }
                }
            }

            //发送初始化状态信息
            weChatApi.webwxstatusnotify(weChatContacts.getMe().id, WXNotify.NOTIFY_INITED);

            //获取好友、保存的群聊、公众号列表。
            //这里获取的群没有群成员，不过也不能用fetchContact方法暴力获取群成员，因为这样数据量会很大
            RspGetContact rspGetContact = weChatApi.webwxgetcontact();
            for (RspInit.User user : rspGetContact.MemberList) {
                weChatContacts.putContact(weChatApi.host, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
