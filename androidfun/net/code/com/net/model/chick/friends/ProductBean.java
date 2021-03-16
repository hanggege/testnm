package com.net.model.chick.friends;

import com.joker.im.custom.chick.SplitText;
import com.net.model.room.RoomInfo;

import java.util.List;

import androidx.annotation.Nullable;

public class ProductBean {

    public int worksType;//,0-图片,1-视频,2-个人主页封面 （刘彬说2.2.6 版本后视频类型改成4）
    public String url;//链接
    public String cover = ""; //  2.2.6 版本 新增字段 （刘彬说封面换成这个字段）
    public int isTop;//是否置顶
    public double progress = -1;//上传进度
    public List<String> fileUris;//视频地址
    public int seqId = 0;//作品ID

    public int index = 0;//当前列表的位置
    public int countAll = 0;
    @Nullable
    public RoomInfo livingRoomInfo;
    public boolean subscribe;
    public boolean like;
    public boolean isLiving;
    @Nullable
    public List<SplitText> tagItems;
    @Nullable
    public SplitText desc;
}