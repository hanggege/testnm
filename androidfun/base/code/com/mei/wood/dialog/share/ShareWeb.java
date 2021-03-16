package com.mei.wood.dialog.share;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/8/14.
 */

public class ShareWeb {

    public String title = "";
    public String content = "";
    public String imageUrl = "";
    public String shareUrl = "";
    public String regularUrl = "";
    public String shareType = "";//分享的类型：无/"default":之前普通分享；"only_image":分享单独的图片；
    public String miniProgramPath = "";//小程序

    public String shareReferId = "";//口令id
    public String shareReferType = "";//口令类型

    @ShowFlag
    public int shareClient;

}
