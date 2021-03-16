package com.mei.orc.common;

import com.mei.orc.Cxt;

/**
 * Created by guoyufeng on 6/5/15.
 */
public abstract class CommonConstant {

    public static final int FEMALE = 0;
    public static final int MALE = 1;
    public static final int CAMERA = 2;

    public static final String DataFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";//包含时分秒
    public static final String DataFormatYmd = "yyyy-MM-dd";//不包含时分秒
    public static final String DataFormatYMD = "yyyy年MM月dd日";//不包含时分秒


    public static class Action {

        private static String packageName() {
            return Cxt.get().getPackageName();
        }

        public static String LOGIN() {
            return packageName() + ".ACTION_LOGIN";
        }

        public static String LOGOUT() {
            return packageName() + ".ACTION_LOGOUT";
        }

        public static final String LOGIN_LOGOUT_EXTRA_USER_ID = "userId";

        public static String SESSION_EXPIRED() {
            return packageName() + ".SESSION_EXPIRED";
        }

    }


    public interface Key {
        String USER_AGENT = "User-Agent";
        String LOGIN_USER_ID = "login_user_id";
        String SESSION_ID = "session_id";
        String PHONE_SN = "phone_sn";
        String DEVICE_ID = "device_id";
        String LOGIN_USER_NAME = "login_user_name";
        String LOCAL_USER_NAME = "local_user_name";
        String VERSION = "version";
        String COOKIE = "cookie";
        String APP_CHANNEL = "app_channel";
        String PUSH_CILENT_ID = "pcid";
        String RECOMMEND_INFO = "recommend_info";
        String CATE_ID_LIST = "cate_id_list";//坏男孩保留字段
        String GAID = "gaid";//google数据统计
    }

    public interface ErrCode {
        int SPICE_ERROR = 0x070001;
        int UPLOAD_FAILED = 0x070002;
        int IMAGE_LOAD_FAILED = 0x070003;
        int SAVE_FILE_FAILED = 0x070004;
        int IMAGE_TOO_BIG_FAILED = 0x070005;
    }

    public interface Config {
        // 账号被T
        int BASE_CODE_T_SESSION = 1103;
        // session过期
        int BASE_CODE_SESSION_PAST = 1100;
        int BASE_CODE_SESSION_EXPIREED = 1101;
        int BASE_CODE_NO_SESSION = 1102;
        int SESSION_ID_ERROR = 1147;
        //LOGIN_USER_ID不正确
        int LOGIN_USER_ID_ERROR = 1148;
        // 账号被封（登录后校验返回）
        int BASE_CODE_BAD_SESSION = 20202;

        // 登录的时候账号被封（dove使用）
        int LOGIN_BASE_CODE_BAD_SESSION = 20203;

        int FEED_DELETE_ERR_CODE = 30301;

        int FIRST_LOGIN_NEED_REGIST = 20201;//第三方登陆用户是第一次登录

        int NEED_PHONE_CODE = 10402;//需要输入图片验证码
    }

    public interface DataKey {
        String session_rtn_code = "session_rtn_code";
        String session_rtn_json = "session_rtn_json";
    }


    /**
     * 坏男孩IM 官方id
     */
    public interface IMOfficialMelkor {

        int SYS_USER_ID_XIAOHUAI_AS_INT = 99;
        String SYS_USER_ID_XIAOHUAI = "99";     // 小坏
        String SYS_USER_ID_REPLY = "98";        // 动态回复@
        String SYS_USER_ID_GROUPNOTIFY = "97";  // 群通知(透出)
        String SYS_USER_ID_GROUPMESSAGE = "96"; // 群消息
        String SYS_USER_ID_PAY_TIP = "93"; // 支付凭证
    }

    /**
     * 小鹿IM 官方id
     */
    public interface IMOfficialWood {

        /**
         * 小鹿-系统通知（通知普通用户的消息）
         */
        String ID_SYSTEM_NOTIFICATION = "80"; // 系统通知
        int ID_SYSTEM_NOTIFICATION_INT = 80; // 系统通知
        /**
         * 小鹿-小鹿官方
         */
        String ID_XIAOLU_OFFICIAL = "81"; // 小鹿官方
        /**
         * 小鹿-服务通知（通知导师/销售的消息）
         */
        String ID_SERVICE_NOTIFICATION = "82"; // 服务通知
        int ID_SERVICE_NOTIFICATION_INT = 82; // 服务通知
        /**
         * 小鹿-小鹿支付
         */
        String ID_XIAOLU_PAY = "83"; // 小鹿支付

        /**
         * 小鹿-@我的，1.8加入
         */
        int XIAOLU_AT_ME = 84;
        String XIAOLU_AT_ME_STR = "84";

        /**
         * 小鹿-回复我的，1.8加入
         */
        int XIAOLU_REPLY_ME = 85;
        String XIAOLU_REPLY_ME_STR = "85";

        /**
         * 小鹿-赞我的，1.8加入
         */
        int XIAOLU_LIKE_ME = 86;
        String XIAOLU_LIKE_ME_STR = "86";

        /**
         * 小鹿-新的粉丝，1.8加入
         */
        int XIAOLU_NEW_FOLLOWER = 87;
        String XIAOLU_NEW_FOLLOWER_STR = "87";

        /**
         * 小鹿-关注好友动态，1.8加入
         */
        int XIAOLU_FOLLOWED_FEED = 88;
        String XIAOLU_FOLLOWED_FEED_STR = "88";

        /**
         * 小鹿-小鹿社区管理员，1.8加入
         */
        int XIAOLU_FORUM_ADMIN = 89;
        String XIAOLU_FORUM_ADMIN_STR = "89";

        String customer_service_user_id = "800";//客服
    }
}


