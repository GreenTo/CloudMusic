package com.example.cloudmusic.domain;

import java.io.Serializable;

public class UserData implements Serializable {
    //等级
    public int level;
    //听歌总数
    public int listenSongs;
    //用户认证
    public UserPoint userPoint;
    //个人简介
    public Profile profile;

    public class UserPoint {
        //uid
        public long userId;
        //版本
        public int version;
    }

    public class Profile {
        //性别 0:保密 1:男性 2:女性
        public int gender;
        //用户昵称
        public String nickname;
        //头像
        public String avatarUrl;
        //背景图
        public String backgroundUrl;
        //生日 unix timestamp
        public long birthday;
        //省id
        public int province;
        //城市id
        public int city;
        ////详细描述
        //public String detailDescription;
        //用户签名
        public String signature;
    }
    //创建时间
    public long createTime;
    //创建天数
    public int createDays;

    @Override
    public String toString() {
        return "UserData{" +
                "level=" + level +
                ", listenSongs=" + listenSongs +
                ", userPoint=" + userPoint +
                ", profile=" + profile +
                ", createTime=" + createTime +
                ", createDays=" + createDays +
                '}';
    }
}
