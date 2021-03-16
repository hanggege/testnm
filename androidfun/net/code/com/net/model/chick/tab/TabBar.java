package com.net.model.chick.tab;

import androidx.annotation.Nullable;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.mei.orc.util.json.JsonExtKt;

import java.util.ArrayList;
import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/7/27
 */
public class TabBar {

    public static class Response extends BaseResponse<TabBar> {

    }

    public List<TabItem> tabs = new ArrayList<>();
    public int show_privacy;
    public boolean local_log_enable;//是否打开声网日志记录
    public String convention;
    public String defaultTab;//默认选择
    public LoginConfig loginConfig;//
    public long timMsgPullSec;
    public boolean showUserLevel;
    public boolean canPushWhenAtForeground;//是否在前台也打开通知
    public int firstSystemInviteTime;
    public String abVer; //AB测试
    //是否处于审查状态，true 隐藏相应的数据
    public boolean review;
    // 是否开启自定义美颜
    public boolean showCustomBeauty;
    // 默认美颜配置
    public String customBeautyConfigKey = "defaultKey";
    @Nullable
    public List<SplitText> diamondEmoji;
    @Nullable
    public String hasBlacklistTips; //拉黑操作提示

    public boolean blockEnable; //是否显示拉黑操作
    @Nullable
    public String publisherBlacklistUser; //知心达人被拉黑提示

    public String startup;//启动图

    @Nullable
    public String groupBlacklistUser; //工作室成员已离线
    @Nullable
    public String userBlacklistPublisher; //普通用户被拉黑，知心达人端提示

    // 是否是游客模式登录
    public boolean guestEnable;
    // 完善资料具体几步，true为4步，false为3步
    public boolean setNameRequired;


    @Override
    public String toString() {
        return JsonExtKt.toJson(this);
    }

    public static class LoginConfig {
        public LoginType first = LoginType.login_type_wsp;
    }


}
