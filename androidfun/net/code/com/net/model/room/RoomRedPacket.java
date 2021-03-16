package com.net.model.room;

import java.io.Serializable;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/7/27
 */
public class RoomRedPacket implements Serializable {
    @Nullable
    public String closeBackgroundImage;
    public long duration;
    public long needDuration;
    @Nullable
    public String openRedPacketButtonImage;
    @Nullable
    public String roomIcon;
    public String text;
    public boolean hasChance;
    //1 代表首次红包
    public int thisTime;

    public boolean needAnimation = false;
}
