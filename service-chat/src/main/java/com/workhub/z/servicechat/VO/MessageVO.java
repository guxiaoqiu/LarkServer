package com.workhub.z.servicechat.VO;

import javax.xml.crypto.Data;
import java.util.List;

public class MessageVO {
    //    消息的id
    private String id;
    //    发送者姓名
    private String username;
    //    发送者头像地址
    private String avatar;
    //    发送者id
    private String fromId;
    //    接受者id
    private String toId;
    //    被@成员的id
    private List<String> atId;
    //    消息密级，需要匹配当前聊天的密级（60 - 非密，70 - 秘密，80 - 机密，90 - 绝密）
    private Integer secretLevel;
    //    消息类型，根据类型判断消息显示方式（1 - 文本消息，2 - 文件消息，3 - 图片消息）
    private Integer type;
    //   消息的内容（根据消息类型，如果为文本消息，该值为文本内容；如果为图片或者文件，该值为文件或图片的名称和文件类型，文件id）
    private String content;
    //   消息发送时间
    private Data time;
    //    消息类型，1.群组消息，2.个人消息，3.系统消息，4.其他消息
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public List<String> getAtId() {
        return atId;
    }

    public void setAtId(List<String> atId) {
        this.atId = atId;
    }

    public Integer getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Data getTime() {
        return time;
    }

    public void setTime(Data time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MessageVO{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", atId=" + atId +
                ", secretLevel=" + secretLevel +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", code=" + code +
                '}';
    }
}
