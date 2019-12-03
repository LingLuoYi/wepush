package com.wepush.wepush.service;

import com.google.gson.Gson;
import com.wepush.wepush.config.Config;
import com.wepush.wepush.entity.JsonModel;
import com.wepush.wepush.utils.JsonUtils;
import com.wepush.wepush.utils.RocksUtils;
import me.xuxiaoxiao.chatapi.wechat.WeChatApi;
import me.xuxiaoxiao.chatapi.wechat.WeChatContacts;
import me.xuxiaoxiao.chatapi.wechat.entity.contact.WXContact;
import me.xuxiaoxiao.chatapi.wechat.entity.message.WXText;
import me.xuxiaoxiao.chatapi.wechat.protocol.ReqSendMsg;
import me.xuxiaoxiao.chatapi.wechat.protocol.RspSendMsg;
import me.xuxiaoxiao.chatapi.wechat.protocol.RspSync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WePushService {

    @Autowired
    private WeChatApi weChatApi;

    @Autowired
    private WeChatContacts weChatContacts;

    @Autowired
    private Gson gson;

    //单条消息推送
    public JsonModel pushText(String type, String text){
        String wxContactStr = RocksUtils.get(Config.PUSH+type);
        if (wxContactStr == null || "".equals(wxContactStr)){
            return JsonUtils.error("该方式没有配置接收人");
        }
        WXContact wxContact = gson.fromJson(wxContactStr,WXContact.class);
        RspSendMsg rspSendMsg = weChatApi.webwxsendmsg(new ReqSendMsg.Msg(RspSync.AddMsg.TYPE_TEXT, null, 0, text, null, weChatContacts.getMe().id, wxContact.id));
        WXText wxText = new WXText();
        wxText.id = Long.parseLong(rspSendMsg.MsgID);
        wxText.idLocal = Long.parseLong(rspSendMsg.LocalID);
        wxText.timestamp = System.currentTimeMillis();
        wxText.fromGroup = null;
        wxText.fromUser = weChatContacts.getMe();
        wxText.toContact = wxContact;
        wxText.content = text;
        return JsonUtils.success(wxText);
    }
}
