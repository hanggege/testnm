package com.net;

import com.mei.orc.john.model.AccountLoginResult;
import com.mei.orc.john.model.Empty_data;
import com.mei.orc.john.model.MOBILE_send_mobile_code;
import com.net.model.ali.AliRpToken;
import com.net.model.bag.NewPeopleGiftBagInfo;
import com.net.model.broadcast.BROADCAST_CHANNEL_validate_code;
import com.net.model.broadcast.BROADCAST_info;
import com.net.model.broadcast.BroadCast_Room_Status;
import com.net.model.chat.ChatAuth;
import com.net.model.chat.ChatConfig;
import com.net.model.chat.CommandPhrase;
import com.net.model.chat.CommandPhraseModifyInfo;
import com.net.model.chat.ExclusiveApply;
import com.net.model.chat.ExclusiveHandle;
import com.net.model.chat.MessageUnread;
import com.net.model.chick.coupon.CouponListInfo;
import com.net.model.chick.course.AudioGet;
import com.net.model.chick.course.ToReceiveCourseInfo;
import com.net.model.chick.evaluation.EvaluationAfterBean;
import com.net.model.chick.evaluation.EvaluationBean;
import com.net.model.chick.evaluation.TagBean;
import com.net.model.chick.find.FindCourseTab;
import com.net.model.chick.find.FindTab;
import com.net.model.chick.friends.BlackListResult;
import com.net.model.chick.friends.ChickIsLivingResult;
import com.net.model.chick.friends.CourseListResult;
import com.net.model.chick.friends.DeleteFriendResult;
import com.net.model.chick.friends.GuardianRankingListResult;
import com.net.model.chick.friends.LikeFriendResult;
import com.net.model.chick.friends.LikeNexusListResult;
import com.net.model.chick.friends.MyFansListResult;
import com.net.model.chick.friends.MyFollowListBean;
import com.net.model.chick.friends.MyFollowListResult;
import com.net.model.chick.friends.ServiceListResult;
import com.net.model.chick.friends.UserHomePagerResult;
import com.net.model.chick.friends.WorkListResult;
import com.net.model.chick.global.TipsGet;
import com.net.model.chick.login.CheckSunResult;
import com.net.model.chick.login.ImgCodeTokenResult;
import com.net.model.chick.login.LoginWithOauthResult;
import com.net.model.chick.login.OauthConnectResult;
import com.net.model.chick.message.MessageBanner;
import com.net.model.chick.message.VisitorsMessage;
import com.net.model.chick.pay.OrderCreateResult;
import com.net.model.chick.pay.ProductListResult;
import com.net.model.chick.recommend.BatRoomStatusNewResult;
import com.net.model.chick.recommend.BatRoomStatusResult;
import com.net.model.chick.recommend.RecommendList;
import com.net.model.chick.report.ChatC2CPubInfo;
import com.net.model.chick.report.ChatC2CUseInfo;
import com.net.model.chick.report.ReasonBean;
import com.net.model.chick.report.ReportBean;
import com.net.model.chick.room.HandPickResult;
import com.net.model.chick.room.RoomList;
import com.net.model.chick.room.RoomQuestionsListResult;
import com.net.model.chick.tab.TabBar;
import com.net.model.chick.tim.SystemInviteResult;
import com.net.model.chick.tim.TimSendMessage;
import com.net.model.chick.tim.UserSig;
import com.net.model.chick.update.VersionCheckResult;
import com.net.model.chick.upload.CheckAvatarViolation;
import com.net.model.chick.upload.UploadToken;
import com.net.model.chick.user.ChickUserInfo;
import com.net.model.chick.user.HomeStatus;
import com.net.model.chick.user.NicknameBatchInfo;
import com.net.model.chick.user.NicknameResponse;
import com.net.model.chick.user.RangeOfAgeInfo;
import com.net.model.chick.user.UserAuthInfo;
import com.net.model.chick.video.LikeVideoList;
import com.net.model.chick.video.MentorShortVideo;
import com.net.model.chick.video.ShortVideoCompletionInfo;
import com.net.model.chick.video.ShortVideoInfo;
import com.net.model.chick.video.ShortVideoList;
import com.net.model.chick.video.ShortVideoTab;
import com.net.model.chick.workroom.WorkRoomInfo;
import com.net.model.chick.workroom.WorkRoomMemberListResponse;
import com.net.model.chick.workroom.WorkRoomServiceLivingRoomInfo;
import com.net.model.config.AppConfigLoad;
import com.net.model.config.UPLOAD_uptoken;
import com.net.model.gift.GiftListInfo;
import com.net.model.gift.SendGiftBalance;
import com.net.model.gift.UserLevelInfo;
import com.net.model.newhome.Broadcast_v2_home;
import com.net.model.newhome.List_cate;
import com.net.model.newhome.List_daily_elite;
import com.net.model.newhome.Tab_content_v4;
import com.net.model.radio.RadioAudioInfo;
import com.net.model.radio.RadioDetailInfo;
import com.net.model.radio.RadioFavoriteListInfo;
import com.net.model.radio.RadioShowLikeInfo;
import com.net.model.report.ShieldListInfo;
import com.net.model.room.AgoraStatus;
import com.net.model.room.CoinReceive;
import com.net.model.room.ContributionsInfo;
import com.net.model.room.DataStatistics;
import com.net.model.room.GenericEffectConfig;
import com.net.model.room.MatchInfo;
import com.net.model.room.PeopleCount;
import com.net.model.room.ProductCategory;
import com.net.model.room.QueueMy;
import com.net.model.room.QueueShow;
import com.net.model.room.QueueTop;
import com.net.model.room.RefreshRoomWeekRank;
import com.net.model.room.RoomApplyLaunch;
import com.net.model.room.RoomExit;
import com.net.model.room.RoomGetToken;
import com.net.model.room.RoomInfoResponse;
import com.net.model.room.RoomPopularity;
import com.net.model.room.RoomRedPacketResult;
import com.net.model.room.RoomStatus;
import com.net.model.room.RoomUserQueue;
import com.net.model.room.RoomUserRecent;
import com.net.model.room.RoomUserTypeEnum;
import com.net.model.room.RoomWeekRank;
import com.net.model.room.SpecialServicePageList;
import com.net.model.room.UpstreamTypeList;
import com.net.model.room.UpstreamTypeText;
import com.net.model.rose.MyRoseInfo;
import com.net.model.share.LivingShareResult;
import com.net.model.user.DataCard;
import com.net.model.user.FollowFriend;
import com.net.model.user.MyBalanceInfo;
import com.net.model.user.MyPageInfo;
import com.net.model.user.ShieldingInfo;
import com.net.model.user.UserBasicInfo;
import com.net.model.wechat.WeChat_Number_Response;
import com.net.model.works.DeleteWorksModel;
import com.net.network.works.AddUserResourcesListRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by zzw on 2019-12-06
 * Des:
 */
public interface ApiInterface {

    String basePath = "/v18/dove/";

    /**
     * 文件上传
     */
    @GET("/app/generic/upload/multi/token")
    Observable<UPLOAD_uptoken.Response> uploadToken();

    //举报 /report/add
    @GET("/report/add")
    Observable<Empty_data.Response> REPORT_add(
            @Query("feed_id") long feed_id,//动态ID
            @Query("user_id") int user_id,//被举报人id
            @Query("report_type") int report_type,//举报类型1,2,3
            @Query("login_user_id") int login_user_id,//举报者user_id
            @Query("feed_content_type") String feed_content_type,//默认是feed, 也可以是话题的评论：comment
            @Query("remark") String remark//补充说明
    );

    // 客户端在登录腾讯云IM前，获取登录所需的userSig等信息
    @GET("/server/tim/getSin")
    Observable<UserSig.Response> getTimSin(@Query("userId") int userId);


    // 上传头像获取token
    @GET("/app/generic/upload/token")
    Observable<UploadToken.Response> uploadToken(@Query("login_user_id") int login_user_id,
                                                 @Query("session_id") String session_id,
                                                 @Query("suffix") String suffix);

    //主页tab信息
    @GET(basePath + "generic/tabInfo")
    Observable<TabBar.Response> tabInfo(@Query("version") String version, @Query("specifications") String specifications);

    //获取品类
    @GET("/app/generic/product/category")
    Observable<ProductCategory.Response> productCategory();

    //获取品类
    @GET(basePath + "generic/productCategory")
    Observable<ProductCategory.Response> newProductCategory();

    @GET(basePath + "publisher/match/quick")
    Observable<MatchInfo.Response> quickMatch(@Query("proCateId") int proCateId);

    //客户端收到个推的消息之后上报：
    @GET("/app/getui/report_message_received_callback")
    Observable<Empty_data.Response> GetuiReport(
            @Query("refer_id") String refer_id,
            @Query("refer_type") String refer_type,
            @Query("getui_push_log_id") String getui_push_log_id);

    @GET("/app/generic/effect/config")
    Observable<GenericEffectConfig.Response> getGenericEffectConfig();

    //获取举报原因
    @GET("/app/generic/reason")
    Observable<ReasonBean.Response> reason();

    //获取年龄段
    @GET("app/generic/years")
    Observable<RangeOfAgeInfo.Response> getRangeOfAge();

    //检测更新
    @GET("/app/version/check")
    Observable<VersionCheckResult.Response> appVersionCheck();

    // 全局通知
    @GET(basePath + "global/tips/get")
    Observable<TipsGet.Response> TipsGet(@Query("nextStartKey") String nextStartKey, @Query("unable") boolean unable, @Query("count") int count,
                                         @Query("tabId") String tabId, @Query("showSystemInvite") boolean showSystemInvite);

    // 上报个推pcid
    @GET("/app/getui/uploadCid")
    Observable<Empty_data.Response> uploadCid();


    @GET("/app/config/load")
    Observable<AppConfigLoad.Response> AppConfigLoad(@Query("keys") String keys);

    @POST("/app/config/update")
    @FormUrlEncoded
    Observable<Empty_data.Response> AppConfigUpdate(@Field("key") String key, @Field("value") String value);


    @POST("/app/gio/upload")
    @FormUrlEncoded
    Observable<Empty_data.Response> GIOUpload(@Field("json") String json);

    //第三方登录第一步 检测是否登录
    @GET(basePath + "account/connect")
    Observable<OauthConnectResult.Response> oauthConnect(@Query("authorizationCode") String authorization_code,
                                                         @Query("state") String state,
                                                         @Query("bind") String bind
    );

    //手机号一键登录
    @GET(basePath + "account/useOwnNumberLogin")
    Observable<LoginWithOauthResult.Response> useOwnNumberLogin(@Query("token") String token);

    //查询基本信息
    @GET(basePath + "user/basicInfo")
    Observable<UserBasicInfo.Response> userBaseInfo(@Query("targetUserId") String targetId, @Query("publisherId") String publisherId);


    //查询im基本信息    主要四im私聊页面
    @GET(basePath + "user/basic/userImInfo")
    Observable<UserBasicInfo.Response> userImInfo(@Query("targetUserId") String targetUserId);

    //查询消息列表会话列表用户信息
    @GET(basePath + "/user/moreInfoForImPage")
    Observable<UserBasicInfo.Response> userMessageListInfo(@Query("targetUserId") String targetUserId);


    //查询所有信息
    @GET(basePath + "user/myInfo")
    Observable<ChickUserInfo.Response> myInfo(
            @Query("login_user_id") int login_user_id,
            @Query("session_id") String session_id
    );

    //微信解绑
    @GET(basePath + "account/wechat/unbind")
    Observable<Empty_data.Response> unbindWechat(@Query("state") String state);


    //我的页面接口
    @GET(basePath + "user/myPage")
    Observable<MyPageInfo.Response> myPage();

    //资料卡接口
    @GET(basePath + "user/dataCard")
    Observable<DataCard.Response> dataCard(@Query("roomId") String roomId,
                                           @Query("targetUserId") int userId);

    //不做过滤的资料卡接口
    @GET(basePath + "user/publisherCardForRank")
    Observable<DataCard.Response> publisherCardForRank(@Query("targetUserId") int userId);

    // 通过第三方登录后，直接注册，获得登录态及user_id
    @GET(basePath + "account/loginWithOauth")
    Observable<LoginWithOauthResult.Response> loginWithOauth(
            @Query("nickname") String nickname, // 用户昵称,必传
            @Query("avatar") String avatar, // 用户昵称,头像
            @Query("seqId") Long seqId // 访问/cooperate/connect_callback返回里面的seq_id，用于回传验证临时登录态
    );

    // 发送验证码
    @GET(basePath + "sms/sendMobileCode")
    Observable<MOBILE_send_mobile_code.Response> sendMobileCode(
            @Query("phoneNo") String phoneNo, // 手机号【用于新绑定用户】
            @Query("captcha") String captcha,// 用户填写的图片验证码
            @Query("nextCode") String nextCode
    );

    // 检测登录态
    @GET(basePath + "account/isLogin")
    Observable<Empty_data.Response> isLogin();

    // 发送手机认证赠送心币提示  im消息
    @GET(basePath + "user/donateRose")
    Observable<Empty_data.Response> donateRose();

    // 使用手机验证码来登录
    @POST(basePath + "account/useMobileCodeLogin")
    @FormUrlEncoded
    Observable<LoginWithOauthResult.Response> useMobileCodeLogin(
            @Field("phoneNo") String phoneNo, // 帐号，第一版只支持手机号
            @Field("mobileCode") String mobileCode // 手机验证码
    );

    // 特殊账号  使用密码登录
    @GET(basePath + "account/userAccountPasswordLogin")
    Observable<LoginWithOauthResult.Response> userAccountPasswordLogin(
            @Query("account") String account,
            @Query("password") String password,
            @Query("checksum") String checksum,
            @Query("machineCode") String machineCode
    );

    // 获取加密sum，用于判断是否是测试账号
    @GET(basePath + "account/getChecksum")
    Observable<CheckSunResult.Response> getChecksum(
            @Query("account") String account,
            @Query("machineCode") String machineCode
    );

    // 测试弹窗弹出后上报
    @POST(basePath + "test/evaluation/reportTipsShow")
    @FormUrlEncoded
    Observable<Empty_data.Response> reportTestDialog(
            @Field("login_user_id") String login_user_id,
            @Field("session_id") String session_id);

    // 获取图形验证码
    @GET(basePath + "sms/getToken")
    Observable<ImgCodeTokenResult.Response> getImgCodeToken(
            @Query("phoneNo") String phoneNo);

    // 完善资料  这里还是未登录状态  所以传入相关id
    @PUT(basePath + "user/edit")
    @FormUrlEncoded
    Observable<Empty_data.Response> complementUserInfo(
            @Field("gender") String gender,
            @Field("birthYear") String birthday,
            @Field("avatar") String avatar,
            @Field("nickname") String nickname,
            @Field("interestIds") String interestIds,
            @Field("login_user_id") String login_user_id,
            @Field("session_id") String session_id);


    // 修改资料  这里已经登录了，不需要传入相关id
    @PUT(basePath + "user/edit")
    @FormUrlEncoded
    Observable<Empty_data.Response> userEdit(
            @Field("gender") String gender,
            @Field("birthYear") String birthday,
            @Field("avatar") String avatar,
            @Field("nickname") String nickname,
            @Field("interestIds") String interestId
    );

    // 获取随机昵称
    @GET(basePath + "user/getRandomNickname")
    Observable<NicknameResponse.Response> getNickname();

    // 获取随机昵称(服务器也做修改)
    @GET(basePath + "user/changeUserNicknameByRandom")
    Observable<NicknameResponse.Response> getNickNameAndUpdate();

    // 获取一批随机昵称(用于注册最后一步,需要登录态)
    @GET(basePath + "user/getRandomNicknameBatch")
    Observable<NicknameBatchInfo.Response> getRandomNicknameBatch(@Query("login_user_id") int login_user_id, @Query("session_id") String session_id);

    //检查头像是否违规
    @POST(basePath + "user/checkAvatarViolation")
    @FormUrlEncoded
    Observable<CheckAvatarViolation.Response> CheckAvatarViolation(@Field("imageUrl") String imageUrl);

    @POST(basePath + "verify/ali/token/generate")
    @FormUrlEncoded
    Observable<AliRpToken.Response> Ali_rp_token(@Field("realName") String realName,
                                                 @Field("idCard") String idCard);

    @GET(basePath + "verify/ali/finish")
    Observable<Empty_data.Response> Ali_rp_finish_verify();

    @GET(basePath + "room/system/invite")
    Observable<SystemInviteResult.Response> invite(@Query("count") long count, @Query("name") String name);

    //仅供客户端测试使用，请自行拼接消息内容msgContent
    @GET(basePath + "app/tim/testSend")
    Observable<TimSendMessage.Response> TimSendMessage(
            @Query("fromUser") int fromUser,
            @Query("toUser") String toUser,
            @Query("groupId") String groupId,
            @Query("msgContent") String msgContent);

    //房间创建
    @POST(basePath + "room/created")
    @FormUrlEncoded
    Observable<Empty_data.Response> RoomCreate(
            @Query("roomId") String roomId,
            @Query("roomType") String roomType);

    // 获取agora的连线token
    @POST(basePath + "room/token/generate")
    @FormUrlEncoded
    Observable<RoomGetToken.Response> RoomGetToken(
            @Query("tokenType") String tokenType,
            @Query("roomId") String roomId);

    //获取正在相亲的好友列表
    @GET(basePath + "friend/blindUsers")
    Observable<MyFollowListBean.Response> blindUsers();

    //检测当前用户是否在相亲中
    @GET(basePath + "friend/blind")
    Observable<ChickIsLivingResult.Response> chickIsLiving(@Query("userId") int userId);

    //上传位置
    @GET(basePath + "user/uploadLocation")
    Observable<Empty_data.Response> uploadLocation(
            @Query("location") String location,
            @Query("locationCity") String locationCity,
            @Query("longitude") double longitude,
            @Query("latitude") double latitude
    );

    //房间信息
    @GET(basePath + "room/info")
    Observable<RoomInfoResponse.Response> RoomInfo(
            @Query("roomId") String roomId,
            @Query("title") String title,
            @Query("tag") String tag,
            @Query("from") String from);

    @POST(basePath + "room/start")
    @FormUrlEncoded
    Observable<RoomInfoResponse.Response> roomStart(
            @Field("title") String title,
            @Field("tagId") int tagId);

    //新的首页
    //获取正在相亲的好友列表
    @GET(basePath + "homepage/new")
    Observable<RoomList.Response> homepageNew(@Query("pageNo") int pageNo,
                                              @Query("pageSize") int pageSize,
                                              @Query("statusHeight") String statusHeight,
                                              @Query("webVersion") String version);

    //首页优化 --- 去掉短视频
    @GET(basePath + "homepage/index")
    Observable<RoomList.Response> homepageIndex(@Query("pageNo") int pageNo,
                                                @Query("pageSize") int pageSize,
                                                @Query("webVersion") String version);

    //提审版本文章
    @GET(basePath + "room/home")
    Observable<HandPickResult.Response> handPick();

    //查看他人主页信息
    @GET(basePath + "publisher/homepage/info")
    Observable<UserHomePagerResult.Response> homePage(
            @Query("targetUserId") int targetUserId//目标用户id
    );

    //获取主播作品
    @GET(basePath + "publisher/works/list")
    Observable<WorkListResult.Response> getProductList(
            @Query("targetUserId") int targetUserId,//目标用户id
            @Query("pageNo") int pageNo,
            @Query("workPageSize") int pageSize);

    //获取主播服务列表
    @GET(basePath + "special/service/list")
    Observable<ServiceListResult.Response> getServiceList(
            @Query("publisherId") int targetUserId,//目标用户id
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize);

    //获取主播课程列表
    @GET(basePath + "course/list")
    Observable<CourseListResult.Response> getCourseList(
            @Query("publisherId") int targetUserId,//目标用户id
            @Query("pageNo") int pageNo);

    /**
     * 领取课程
     */
    @GET(basePath + "user/free/receive/result/verified")
    Observable<ToReceiveCourseInfo.Response> toReceiveCourse(@Query("userId") int userId,
                                                             @Query("publisherId") int publisherId,
                                                             @Query("referType") String referType,
                                                             @Query("referId") String referId);

    //刷新相亲状态
    @GET(basePath + "user/roomStatus")
    Observable<HomeStatus.Response> roomStatus(
            @Query("targetUserId") int targetUserId//目标用户id
    );


    //批量刷新相亲状态
    @GET(basePath + "user/batchRoomStatus")
    Observable<BatRoomStatusResult.Response> batRoomStatus(
            @Query("targetUserId") String targetId//目标用户id
    );

    //批量刷新相亲状态
    @GET(basePath + "user/batchUserStatus")
    Observable<BatRoomStatusNewResult.Response> batchUserStatus(
            @Query("targetUserId") String targetId//目标用户id
    );

    //申请/邀请
    @POST(basePath + "room/apply/launch")
    @FormUrlEncoded
    Observable<RoomApplyLaunch.Response> roomApplyLaunch(
            @Field("userId") int userId,
            @Field("roomId") String roomId,
            @Field("type") String type,
            @Field("roomType") String roomType,
            @Field("from") String from,
            @Field("videoMode") int videoMode,
            @Field("couponNum") int couponNum
    );

    //处理申请/邀请
    @POST(basePath + "room/apply/handle")
    @FormUrlEncoded
    Observable<QueueMy.Response> ApplyHandle(
            @Field("userId") int userId,
            @Field("roomId") String roomId,
            @Field("type") String type,
            @Field("result") int result,
            @Field("videoMode") int videoMode);

    //处理抢单申请/邀请
    @POST(basePath + "exclusive/match/accept")
    @FormUrlEncoded
    Observable<ExclusiveHandle.Response> SnapUpHandle(
            @Field("userId") int userId,
            @Field("roomId") String roomId);

    // 当前在线用户
    @GET(basePath + "room/queue/show")
    Observable<QueueShow.Response> QueueShow(
            @Query("roomId") String roomId,
            @Query("type") int type,
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize);

    //主播端上麦队列指定tab列表数据
    @GET(basePath + "room/queue")
    Observable<RoomUserQueue.Response> roomUserQueue(@Query("roomId") String roomId, @Query("current") RoomUserTypeEnum type, @Query("pageNo") int pageNo);

    //退出直播房间
    @POST(basePath + "room/exit")
    @FormUrlEncoded
    Observable<RoomExit.Response> RoomExit(@Field("roomId") String roomId);
    //进入直播房间

    @POST(basePath + "room/enter")
    @FormUrlEncoded
    Observable<Empty_data.Response> RoomEnter(@Field("roomId") String roomId);


    //充值弹窗数据
    @GET(basePath + "deposit/product/list")
    Observable<ProductListResult.Response> productList(
            @Query("showAll") int showAll,
            @Query("showTask") int showTask,
            @Query("textType") int textType
    );

    //创建订单
    @POST(basePath + "deposit/order/create")
    @FormUrlEncoded
    Observable<OrderCreateResult.Response> orderCreate(@Field("productId") String productId,
                                                       @Field("fromType") String fromType,
                                                       @Field("payType") String payType,
                                                       @Field("userAgent") String userAgent,
                                                       @Field("fromId") String fromId);

    //支付完成
    @POST(basePath + "deposit/finish")
    @FormUrlEncoded
    Observable<Empty_data.Response> payFinish(@Field("orderSn") String orderSn);

    //我的心币(货币)
    @GET(basePath + "coin/my")
    Observable<MyRoseInfo.Response> MyRoseInfo();

    //举报嘉宾或者红娘
    @POST(basePath + "interact/report")
    Observable<Empty_data.Response> report(@Body ReportBean param);

    //评价嘉宾或者红娘
    @POST(basePath + "interact/evaluation")
    Observable<EvaluationAfterBean.Response> evaluation(@Body EvaluationBean param);

    //获取评论子标签
    @GET(basePath + "generic/tag")
    Observable<TagBean.Response> evaluationTags();

    //获取礼物列表信息
    @GET(basePath + "gift/list")
    Observable<GiftListInfo.Response> giftListInfo();

    //房间自动邀请
    @POST(basePath + "room/invite/auto")
    @FormUrlEncoded
    Observable<Empty_data.Response> InviteAuto(@Field("roomId") String roomId,
                                               @Field("gender") int gender,
                                               @Field("use") int use);

    //发送消息/发微信
    @POST(basePath + "room/send")
    @FormUrlEncoded
    Observable<Empty_data.Response> roomSend(@Field("type") String type,
                                             @Field("roomId") String roomId,
                                             @Field("to") int to,
                                             @Field("at") String at,
                                             @Field("count") int count,
                                             @Field("msg") String msg);

    //查询房间人数
    @GET(basePath + "room/queue/count")
    Observable<PeopleCount.Response> PeopleCount(@Query("roomId") String roomId);

    //查询排麦前三的用户
    @GET(basePath + "room/queue/top")
    Observable<QueueTop.Response> queueTop(@Query("roomId") String roomId);

    //查询申请信息
    @GET(basePath + "room/queue/my")
    Observable<QueueMy.Response> queueMy(@Query("roomId") String roomId);

    //获取上麦按钮文案的
    @GET(basePath + "room/user/upstream/type/text")
    Observable<UpstreamTypeText.Response> upstreamTypeText(@Query("roomId") String roomId);

    //获取上麦按钮文案的
    @GET(basePath + "room/user/upstream/type/list")
    Observable<UpstreamTypeList.Response> upstreamTypeList(@Query("roomId") String roomId);

    //添加好友
    @POST(basePath + "friend/invite")
    @FormUrlEncoded
    Observable<Empty_data.Response> InviteFriend(@Field("inviteUser") int inviteUser, @Field("source") int source);

    //更改好友关系
    @POST(basePath + "friend/change")
    @FormUrlEncoded
    Observable<Empty_data.Response> changeFriend(@Field("status") int status,
                                                 @Field("source") int source,
                                                 @Field("friendId") String friendId);

    // 推送微信
    @POST(basePath + "room/wechat/push")
    @FormUrlEncoded
    Observable<Empty_data.Response> PushWechat(
            @Field("roomId") String roomId,
            @Field("to") int userId);

    //专属房间的定时刷新接口
    @GET(basePath + "room/exclusive/status")
    Observable<RoomStatus.Response> RoomStatus(@Query("roomId") String roomId);

    //手机认证
    @GET(basePath + "user/authPhone")
    Observable<AccountLoginResult.Response> authPhone(@Query("phoneNo") String phoneNo,
                                                      @Query("mobileCode") String mobileCode,
                                                      @Query("nextCode") String nextCode);

    //删除好友
    @DELETE(basePath + "friend/delete")
    Observable<DeleteFriendResult.Response> deleteFriend(
            @Query("friendId") String friendId,
            @Query("source") int source
    );


    //我的余额
    @GET(basePath + "user/myBalance")
    Observable<MyBalanceInfo.Response> myBalance();

    //送礼
    @POST(basePath + "gift/send")
    @FormUrlEncoded
    Observable<SendGiftBalance.Response> giftSend(@Field("giftId") int giftId,
                                                  @Field("number") int number,
                                                  @Field("toUserId") long toUserId,
                                                  @Field("fromScene") String fromScene,
                                                  @Field("referId") String referId,
                                                  @Field("fromType") String fromType);

    //直播间分享
    @GET(basePath + "activity/invite/room/share")
    Observable<LivingShareResult.Response> livingShare(@Query("roomId") String roomId);


    @GET(basePath + "user/getAuthInfo")
    Observable<UserAuthInfo.Response> UserAuthinfo(@Query("userId") int userId);

    @POST(basePath + "agora/status")
    @FormUrlEncoded
    Observable<AgoraStatus.Response> AgoraStatus(@Field("roomId") String roomId, @Field("status") int status, @Field("roomType") String roomType,
                                                 @Field("time") String time);

    @POST(basePath + "agora/report")
    @FormUrlEncoded
    Observable<Empty_data.Response> AgoraReport(@Field("roomId") String roomId, @Field("userId") int userId, @Field("status") int status);

    @POST(basePath + "room/kick")
    @FormUrlEncoded
    Observable<Empty_data.Response> RoomKick(@Field("roomId") String roomId, @Field("targetUserId") int targetUserId);


    //获取推荐列表
    @GET(basePath + "getui/referral")
    Observable<RecommendList.Response> recommendList(@Query("nextTime") String pageNo
    );

    //收到心币数
    @GET(basePath + "room/coin/receive")
    Observable<CoinReceive.Response> coinReceive(@Query("publisherId") int publisherId);

    //房间实时人气
    @GET(basePath + "room/popularity")
    Observable<RoomPopularity.Response> roomPopularity(@Query("roomId") String roomId);

    //获取推荐列表
    @GET(basePath + "getui/referral")
    Observable<RecommendList.Response> recommendList(@Query("nextTime") String pageNo, @Query("isMatch") Boolean isMatch);

    //完善征友条件
    @POST(basePath + "user/editPersonals")
    @FormUrlEncoded
    Observable<Empty_data.Response> editPersonalsCondition(

            @Field("location") String location,
            @Field("ageBegin") String ageBegin,
            @Field("ageEnd") String ageEnd,
            @Field("heightBegin") String heightBegin,
            @Field("heightEnd") String heightEnd,
            @Field("lowestIncome") String lowestIncome,
            @Field("lowestEducation") String lowestEducation
    );

    //获取黑名单列表
    @GET(basePath + "black/list")
    Observable<BlackListResult.Response> getBlackList();

    //关注好友
    @POST(basePath + "follow")
    @FormUrlEncoded
    Observable<Empty_data.Response> followFriend(@Field("followId") int followId,
                                                 @Field("source") int source,
                                                 @Field("fromId") String fromId);

    @DELETE(basePath + "follow/delete")
    Observable<Empty_data.Response> followDelete(@Query("followId") int followId);

    //查询是否是关注关系
    @GET(basePath + "follow/exist")
    Observable<FollowFriend.Response> followExist(@Query("followId") int followId);

    //获取我关注的人列表
    @GET(basePath + "follow/my")
    Observable<MyFollowListResult.Response> getMyFollowList(@Query("pageNo") int pageNo,
                                                            @Query("pageSize") int pageSize);

    //获取我粉丝列表
    @GET(basePath + "follow/fans")
    Observable<MyFansListResult.Response> getMyFansList(@Query("pageNo") int pageNo,
                                                        @Query("pageSize") int pageSize);

    //获取守护榜列表
    @GET(basePath + "black/list")
    Observable<GuardianRankingListResult.Response> getGuardianRankingList();

    //获取喜欢人列表
    @GET(basePath + "ambiguous/list")
    Observable<LikeFriendResult.Response> getLikeFriendList(@Query("type") int type);

    //获取喜欢人和我喜欢的人列表
    @GET(basePath + "ambiguous/likeNexusList")
    Observable<LikeNexusListResult.Response> getLikeNexusList();


    //添加/解除黑名单
    @POST(basePath + "black/change")
    @FormUrlEncoded
    Observable<Empty_data.Response> changeBlack(@Field("passiveUser") int passiveUser,
                                                @Field("status") int status);

    //添加/解除喜欢
    @POST(basePath + "ambiguous/change")
    @FormUrlEncoded
    Observable<Empty_data.Response> changeLike(@Field("passiveUser") int passiveUser,
                                               @Field("status") int status);

    //更新绑定手机

    /**
     * phoneNo    手机号
     * mobileCode 验证码
     * fromPage   0 个人资料编辑绑定手机号，1 直播间绑定手机号，2  首页
     */
    @POST(basePath + "user/updatePhone")
    @FormUrlEncoded
    Observable<AccountLoginResult.Response> updatePhone(@Field("phoneNo") String phoneNo,
                                                        @Field("mobileCode") String mobileCode,
                                                        @Field("fromPage") int fromPage);

    @GET(basePath + "message/banner")
    Observable<MessageBanner.Response> MessageBanner();

    //上报观看时长
    @GET(basePath + "room/statistics/watch")
    Observable<Empty_data.Response> statisticsWatch(@Query("minutes") int minutes);

    //修改申请连线开关
    @POST(basePath + "room/switch/status")
    @FormUrlEncoded
    Observable<Empty_data.Response> switchStatus(@Field("roomId") String roomId,
                                                 @Field("isOpen") boolean isOpen);

    //修改视频播放模式
    @POST(basePath + "room/mode")
    @FormUrlEncoded
    Observable<Empty_data.Response> roomMode(@Field("roomId") String roomId,
                                             @Field("mode") int mode);

    //对连线用户禁言
    @POST(basePath + "room/user/forbidden")
    @FormUrlEncoded
    Observable<Empty_data.Response> userForbidden(@Field("roomId") String roomId,
                                                  @Field("targetUserId") int targetUserId,
                                                  @Field("forbidden") int forbidden,
                                                  @Field("type") String type,
                                                  @Field("fromUserId") int fromUserId,
                                                  @Field("reason") String reason);

    @GET(basePath + "room/user/stream/report")
    Observable<Empty_data.Response> userStreamReport(@Query("roomId") String roomId,
                                                     @Query("mode") int mode,
                                                     @Query("status") int status);

    //检查对方客户端是否支持
    @GET(basePath + "room/feature/check")
    Observable<Empty_data.Response> roomFeatureCheck(@Query("roomId") String roomId,
                                                     @Query("feature") String feature);

    @GET(basePath + "room/data/statistics")
    Observable<DataStatistics.Response> dataStatistics(@Query("roomId") String roomId,
                                                       @Query("broadcastId") String broadcastId);

    /****************************搬小鹿的接口************************************/
    //首页每日优选点击查看更多（AB版相同）
    @GET(basePath + "homepage/list_daily_elite")
    Observable<List_daily_elite.Response> List_daily_elite(
            @Query("start") String start,
            @Query("pro_cate_id") int pro_cate_id
    );

    //直播主页AB测替换接口
    @GET(basePath + "homepage/home_v3")
    Observable<Broadcast_v2_home.Response> Broadcast_v2_home(
            @Query("cate_id") int cate_id,
            @Query("pro_cate_id") int pro_cate_id
    );

    //获取首页测试版本信息，2.7版接口 b版
    @GET(basePath + "homepage/list")
    Observable<Tab_content_v4.Response> Front_Page_V6_List_b(
            @Query("pro_cate_id") int pro_cate_id,
            @Query("module_id") String module_id,
            @Query("user_agent") String user_agent
    );

    //直播预约上报
    @GET(basePath + "homepage/reservation_broadcast")
    Observable<Empty_data.Response> Reservation_Broadcast(
            @Query("broadcast_id") int broadcast_id,
            @Query("is_notify") int is_notify);

    //首页顶部点击分类详情（或在下面的各分类点查看更多（A版）
    @GET(basePath + "homepage/list_cate")
    Observable<List_cate.Response> List_cate(
            @Query("start") String start,
            @Query("cate_id") int cate_id
    );

    //通过导师ID获取微信号接口
    @GET(basePath + "homepage/get_weixin")
    Observable<WeChat_Number_Response.Response> get_WeChat_Number(
            @Query("mentor_user_id") int mentor_user_id,
            @Query("from_type") String from_type,
            @Query("from_id") String from_id);

    //通过导师ID获取微信号接口,之后“复制并打开微信”按钮点击后上报，服务端计时60s后发送固定模板消息
    @POST(basePath + "homepage/copy_weixin")
    Observable<Empty_data.Response> weChat_Had_Copy(
            @Query("mentor_id") int mentor_id,
            @Query("send_im") int send_im,
            @Query("send_im_mp") boolean send_im_mp
    );

    /****************************小鹿直播间接口************************************/

    //房间内操作
    @POST(basePath + "homepage/send_message")
    @FormUrlEncoded
    Observable<Empty_data.Response> BROADCAST_send_message(
            @Field("broadcast_id") Integer broadcast_id,//broadcast_id
            @Field("room_id") String room_id,//room_id
            @Field("type") String type,//发言内容:send_text, 提问内容:send_question,图片Url:send_photo,连线：begin_upstream,下麦:end_upstream
            @Field("content") String content,//内容
            @Field("photo_id") String photo_ids//图片id
    );

    //获取房间的详细信息
    @GET(basePath + "homepage/room_info")
    Observable<BROADCAST_info.Response> BROADCAST_info(
            @Query("broadcast_id") Integer broadcast_id,//broadcast_id
            @Query("room_id") String room_id
    );

    //客户端轮询直播间状态，用于检测直播状态改变	/broadcast/room_status
    @GET(basePath + "homepage/room_status")
    Observable<BroadCast_Room_Status.Response> BroadCast_Room_Status(
            @Query("room_id") String room_id);

    /**
     * 将客户端不安全的口令验证转移服务端
     * 同一个导师的口令相同，所以本地使用导师id作为key把验证过的口令保存下来，没取到本地就进行验证
     *
     * @param broadcastId 录音id
     * @param history     0:验证直播口令，1:验证录音口令
     * @param code        用户输入的验证码
     */
    @GET(basePath + "homepage/validate_code")
    Observable<BROADCAST_CHANNEL_validate_code.Response> BROADCAST_CHANNEL_validate_code(
            @Query("broadcast_id") int broadcastId,
            @Query("history") String history, //0:验证直播口令，1:验证录音口令
            @Query("code") String code
    );

    /**************************搬小鹿的接口************************************/
    //贡献榜单接口
    @GET(basePath + "room/rank")
    Observable<ContributionsInfo.Response> getContributionList(
            @Query("publisherId") String publisherId,
            @Query("isDaily") boolean isDaily,
            @Query("pageNo") int pageNo
    );

    // 知心周榜
    @GET(basePath + "room/publisher/received/rank")
    Observable<RoomWeekRank.Response> getLiveWeekRank(@Query("roomId") String roomId,
                                                      @Query("pageNo") int nextPageNo);

    //刷新榜单排名
    @GET(basePath + "room/publisher/received/rank/refresh")
    Observable<RefreshRoomWeekRank.Response> refreshWeekRank(@Query("roomId") String roomId);

    @GET(basePath + "activity/invite/login/first")
    Observable<Empty_data.Response> FirstLogin();

    //获取用户等级
    @GET(basePath + "coin/level/info")
    Observable<UserLevelInfo.Response> getUserLevel();

    @GET(basePath + "exclusive/chat/config")
    Observable<ChatConfig.Response> ChatConfig(@Query("userId") String userId,
                                               @Query("fromPage") String fromPage);// 上一个界面名称


    @GET(basePath + "exclusive/chat/auth")
    Observable<ChatAuth.Response> ChatAuth(@Query("userId") String userId);

    //添加常用语
    @FormUrlEncoded
    @POST(basePath + "exclusive/chat/phrase/add")
    Observable<CommandPhrase.Base> addCommandPhrase(@Field("content") String content);

    //更新常用语排序更新
    @FormUrlEncoded
    @POST(basePath + "exclusive/chat/phrase/modify")
    Observable<CommandPhraseModifyInfo.Base> updateCommandPhrase(@Field("list") String chatPhrases);

    //用户&主播发起私密连线   (2.5.0 添加快速咨询优化，添加两个参数：couponNum，和咨询方向proCateId)
    @FormUrlEncoded
    @POST(basePath + "exclusive/apply")
    Observable<ExclusiveApply.Response> exclusiveApply(@Field("targetUserId") int targetUserId,
                                                       @Field("justCheckBalance") int justCheckBalance,
                                                       @Field("ignorePreviousApply") int ignorePreviousApply,
                                                       @Field("forceApply") int forceApply,
                                                       @Field("specialServiceOrderId") String specialServiceOrderId,
                                                       @Field("from") String from,
                                                       @Field("videoMode") int videoMode,
                                                       @Field("couponNum") long couponNum,
                                                       @Field("categoryId") int categoryId);

    @FormUrlEncoded
    @POST(basePath + "exclusive/match/apply")
    Observable<ExclusiveApply.Response> exclusiveMatchApply(@Field("couponNum") long couponNum,
                                                            @Field("categoryId") int categoryId,
                                                            @Field("videoMode") int videoMode,
                                                            @Field("publisherId") int publisherId);

    //用户&主播处理私密连线请求（接听/拒绝）
    @FormUrlEncoded
    @POST(basePath + "exclusive/handle")
    Observable<ExclusiveHandle.Response> exclusiveHandle(@Field("result") int result,
                                                         @Field("userId") int userId);

    @FormUrlEncoded
    @POST(basePath + "exclusive/match/reject")
    Observable<Empty_data.Response> rejectMatch(@Field("userId") int userId);

    //用户&主播取消私密连线请求
    @POST(basePath + "exclusive/cancel")
    Observable<Empty_data.Response> exclusiveCancel();

    //用户&主播取消快速咨询请求
    @POST(basePath + "exclusive/match/cancel")
    Observable<Empty_data.Response> exclusiveMatchCancel();

    //快速咨询超时
    @FormUrlEncoded
    @POST(basePath + "exclusive/match/timeout")
    Observable<Empty_data.Response> exclusiveMatchTimeout(@Field("couponId") long couponId, @Field("categoryId") int categoryId);

    //获取未读消息
    @GET(basePath + "message/unread")
    Observable<MessageUnread.Response> messageUnread();

    //编辑擅长领域,简介，标签
    @POST(basePath + "publisher/info/edit")
    @FormUrlEncoded
    Observable<Empty_data.Response> editsTagsSkills(@Field("skills") String skills, @Field("introduction") String introduction, @Field("tags") String tags);

    /**
     * 添加用户资源
     */
    @POST(basePath + "publisher/works/add")
    Observable<Empty_data.Response> addResourcesList(@Body AddUserResourcesListRequest.ResourcesRequest body);

    /**
     * 用户作品置顶
     */
    @POST(basePath + "publisher/works/top")
    @FormUrlEncoded
    Observable<Empty_data.Response> worksTop(@Field("seqId") String seqId, @Field("isTop") int isTop);

    /**
     * 用户作品删除
     */
    @POST(basePath + "publisher/works/delete")
    @FormUrlEncoded
    Observable<DeleteWorksModel.Response> worksDelete(@Field("seqId") String seqId, @Field("workPageNo") int nextPageNo);

    /**
     * 封面图上传
     */
    @POST(basePath + "publisher/homepage/cover")
    @FormUrlEncoded
    Observable<Empty_data.Response> homepageCover(@Field("cover") String cover);

    /**
     * 知心达人信息
     */
    @GET(basePath + "publisher/infoAndSpecialServices")
    Observable<ChatC2CPubInfo.Response> publisherInfo(@Query("targetUserId") String targetUserId);

    //游客信息
    @GET(basePath + "publisher/user/infoAndSpecialServices")
    Observable<ChatC2CUseInfo.Response> userInfo(@Query("targetUserId") String targetUserId);

    /**
     * 用于获取用户端请求已购买的专属服务（普通用户请求，主播用户请求）
     *
     * @param userId      对应用户id
     * @param publisherId 对应主播用户id
     */
    @GET(basePath + "special/service/chat/page/list")
    Observable<SpecialServicePageList.Response> getBuyServiceList(@Query("userId") int userId,
                                                                  @Query("publisherId") int publisherId);

    /**
     * 用于获取用户端直播间内请求已购买的专属服务（普通用户请求）
     */
    @GET(basePath + "special/service/room/page/list")
    Observable<SpecialServicePageList.Response> specialServiceRoomList(@Query("userId") int userId,
                                                                       @Query("publisherId") int publisherId,
                                                                       @Query("roomId") String roomId);

    /**
     * 获取主播端获取访客列表
     */
    @GET(basePath + "publisher/visitor/list")
    Observable<VisitorsMessage.Response> getVisitorsList(@Query("pageNo") Integer pageNo
            , @Query("pageSize") Integer pageSize);


    /**
     * 修改直播间主题
     */
    @POST(basePath + "room/info/modify")
    @FormUrlEncoded
    Observable<Empty_data.Response> roomInfoModify(@Field("roomId") String roomId,
                                                   @Field("title") String title,
                                                   @Field("proCateId") int proCateId);

    /**
     * 拉黑
     */
    @POST(basePath + "user/block/add")
    @FormUrlEncoded
    Observable<ShieldingInfo.Response> blackAdd(@Field("userId") String userId, @Field("roomId") String roomId);


    /**
     * 解除拉黑名单
     */
    @POST(basePath + "user/block/delete")
    @FormUrlEncoded
    Observable<ShieldingInfo.Response> blackDelete(@Field("userId") String userId, @Field("roomId") String roomId);


    /**
     * 普通用户黑名单列表
     */
    @GET(basePath + "user/block/my")
    Observable<ShieldListInfo.Response> userBlacklist(@Query("roomId") String roomId);


    /**
     * 添加管理员
     */
    @POST(basePath + "room/user/keeper/add")
    @FormUrlEncoded
    Observable<Empty_data.Response> keeperAdd(@Field("roomId") String roomId,
                                              @Field("userId") String userId);

    /**
     * 移除管理员
     */
    @POST(basePath + "room/user/keeper/delete")
    @FormUrlEncoded
    Observable<Empty_data.Response> keeperDelete(@Field("roomId") String roomId,
                                                 @Field("userId") String userId);

    /**
     * 管理员名单
     */
    @GET(basePath + "room/user/keeper")
    Observable<ShieldListInfo.Response> keeperList(@Query("roomId") String roomId);


    /**
     * 推送消息上报
     */
    @POST(basePath + "report/messagePushReport")
    @FormUrlEncoded
    Observable<Empty_data.Response> messagePushReport(@Field("seqId") String seqId);

    /**
     * 富文本点击上报
     */
    @POST(basePath + "report/message")
    @FormUrlEncoded
    Observable<Empty_data.Response> messageClickReport(@Field("payload") String seqId, @Field("from") String from);

    @POST(basePath + "interact/evaluation/gift")
    @FormUrlEncoded
    Observable<Empty_data.Response> giftEvaluation(@Field("id") String id, @Field("giftid") String giftld);

    /**
     * 进入私聊页面未发送内容统计上报
     */
    @POST(basePath + "publisher/visit/im")
    @FormUrlEncoded
    Observable<Empty_data.Response> uploadC2cStatistics(@Field("publisherId") String publisherId);


    /**
     * 房间内最近用户
     */
    @GET(basePath + "room/user/recent")
    Observable<RoomUserRecent.Response> roomUserRecent(
            @Query("roomId") String roomId,
            @Query("topN") int topN,
            @Query("type") String type);

    /**
     * 分享成功后再次请求分享
     */
    @GET(basePath + "activity/test/share/finish")
    Observable<RoomExit.Response> againExitRoomGetShare();

    /**
     * 分享成功后再次请求分享
     */
    @POST(basePath + "activity/test/share/first")
    @FormUrlEncoded
    Observable<RoomExit.Response> againExitRoomFirst(@Field("roomId") String roomId);

    /**
     * 音频详情获取
     */
    @GET(basePath + "course/audio/get")
    Observable<AudioGet.Response> AudioGet(
            @Query("audioId") int audioId,
            @Query("courseId") int courseId,
            @Query("direction") int direction);

    /**
     * 上报音频播放进度
     */
    @POST(basePath + "course/audio/progress")
    @FormUrlEncoded
    Observable<Empty_data.Response> AudioProgress(@Field("courseId") int courseId,
                                                  @Field("audioId") int audioId,
                                                  @Field("progress") int progress);

    /**
     * 设置隐身
     */
    @POST(basePath + "user/room/invisible/set")
    @FormUrlEncoded
    Observable<Empty_data.Response> invisibleSet(@Field("roomInvisible") int roomInvisible);

    @GET(basePath + "short/video/detail")
    Observable<ShortVideoInfo.Response> ShortVideoInfo(@Query("seqId") String seqId
            , @Query("publisherId") String publisherId
            , @Query("tagId") String tagId
            , @Query("isMyLike") boolean isMyLike
            , @Query("isAllTag") boolean isAllTag);

    /**
     * 短视频推荐
     */
    @GET(basePath + "short/video/recommend/list")
    Observable<ShortVideoInfo.Response> getRecommendShortVideoList(@Query("nextStartKey") String nextStartKey);

    /**
     * 推荐短视频上报
     */
    @GET(basePath + "short/video/recommend/playing")
    Observable<Empty_data.Response> recommendPlaying(@Query("seqId") String seqId);

    @GET(basePath + "short/video/list")
    Observable<ShortVideoList.Response> ShortVideoList(@Query("tagId") int tagId, @Query("pageNo") int pageNo);

    @GET(basePath + "short/video/report")
    Observable<Empty_data.Response> ShortVideoReport(
            @Query("time") long time,
            @Query("duration") long duration,
            @Query("seqId") String seqId,
            @Query("fromType") String fromType);

    @GET(basePath + "short/video/view/send/check")
    Observable<ShortVideoCompletionInfo.Response> ShortVideoCompletion(
            @Query("seqId") String seqId,
            @Query("duration") long duration);

    /**
     * 返回点赞视频列表
     */
    @GET(basePath + "short/video/my/like")
    Observable<LikeVideoList.Response> shortLikeVideoList(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    /**
     * 获取工作室信息
     */
    @GET(basePath + "group/publisher/info")
    Observable<WorkRoomInfo.Response> getWorkRoomInfo();

    /**
     * 编辑工作室信息
     */
    @POST(basePath + "group/publisher/info/edit")
    @FormUrlEncoded
    Observable<Empty_data.Response> editWorkRoomInfo(@Field("avatar") String avatar, @Field("nickname") String nickName
            , @Field("tags") String tags
            , @Field("introduction") String introduction);

    /**
     * 发现页
     */
    // 获取发现页Tab接口
    @GET(basePath + "find/tabs")
    Observable<FindTab.Response> getFindPageTab();

    /**
     * 短视频tab
     */
    @GET(basePath + "short/video/tabs")
    Observable<ShortVideoTab.Response> getShortVideoTabData();

    // 获取课程页的Tab接口
    @GET(basePath + "find/partition")
    Observable<FindCourseTab.Response> getFindCourseTab(@Query("type") String type);


    /**
     * 闪验授权成功后调用
     * <p>
     * token    闪验返回的结果
     * fromPage 0 从编辑资料页调用的, 1 直播间,2 首页
     */
    @POST(basePath + "account/useOwnNumberBind")
    @FormUrlEncoded
    Observable<Empty_data.Response> userNumberBind(@Field("token") String token
            , @Field("fromPage") int fromPage);

    /**
     * 获取直播间红包状态
     */
    @GET(basePath + "room/redPacket/status")
    Observable<RoomRedPacketResult.Response> getRedPacketStatus(@Query("roomId") String roomId);

    /**
     * 直播间观看时长领取红包
     */
    @GET(basePath + "red/envelope/watch/finish")
    Observable<Empty_data.Response> openRedPacket(@Query("roomId") String roomId);

    /**
     * 获取工作室成员信息
     */
    @GET(basePath + "group/publisher/memberInfo/edit/list")
    Observable<WorkRoomMemberListResponse.Response> getWorkRoomMemberInfo();

    /**
     * 编辑工作室成员介绍信息
     */
    @GET(basePath + "group/publisher/memberInfo/edit")
    Observable<Empty_data.Response> editMemberInfo(@Query("targetUserId") int targetUserId
            , @Query("introduction") String introduction);

    /**
     * 发送咨询师简介
     */
    @POST(basePath + "exclusive/chat/group/msg")
    @FormUrlEncoded
    Observable<Empty_data.Response> sendAnalystsInfo(@Field("userId") int userId, @Field("targetUserId") int targetUserId);

    /**
     * 工作室主页点击询问获取直播间id
     */
    @GET(basePath + "group/publisher/member/roomId/ifLiving")
    Observable<WorkRoomServiceLivingRoomInfo.Response> getSpecialServiceRoomInfo(@Query("publisherId") int publisherId);

    /**
     * 点赞视频
     */
    @GET(basePath + "short/video/like")
    Observable<Empty_data.Response> likeVideo(@Query("seqId") int seqId, @Query("isLike") boolean isLike);

    /**
     * 获取知心人发布的视频
     */
    @GET(basePath + "short/video/detail/videos")
    Observable<MentorShortVideo.Response> getMentorVideos(@Query("userId") int userId,
                                                          @Query("pageNo") int pageNo,
                                                          @Query("excludeId") String excludeId);


    /*上报短视频数据start*/

    /**
     * 短视频关注上报
     */
    @GET(basePath + "short/video/log/follow")
    Observable<Empty_data.Response> logFollow(@Query("publisherWorksId") int publisherWorksId);

    /**
     * 短视频私聊上报
     */
    @GET(basePath + "short/video/log/chat")
    Observable<Empty_data.Response> logChat(@Query("publisherWorksId") int publisherWorksId);

    /*上报短视频数据end*/


    /**
     * 优惠券列表
     */
    @GET(basePath + "coupon/resource/list")
    Observable<CouponListInfo.Response> getCouponList(@Query("couponId") int couponId,
                                                      @Query("couponNum") String couponNum,
                                                      @Query("publisherId") int publisherId);

    /**
     * 电台详情
     */
    @GET(basePath + "radio/home")
    Observable<RadioDetailInfo.Response> getRadioDetailData(@Query("firstOpen") boolean firstOpen);


    /**
     * 请求播放当前频道音频
     */
    @GET(basePath + "radio/play")
    Observable<RadioAudioInfo.Response> getNextAudioInfo(@Query("channelId") int channelId,
                                                         @Query("audioId") int audioId,
                                                         @Query("direction") int direction);

    /**
     * 首页直播顶部tab配置
     */
    @GET(basePath + "homepage/tabs")
    Observable<TabBar.Response> getLivingRoomTab();

    /**
     * 电台中喜欢列表
     */
    @GET(basePath + "radio/listMyLike")
    Observable<RadioFavoriteListInfo.Response> getRadioFavoriteList();

    /**
     * 电台上报进度
     */
    @GET(basePath + "radio/report")
    Observable<Empty_data.Response> reportRadio(@Query("channelId") int channelId,
                                                @Query("audioId") int audioId,
                                                @Query("playTime") long playTime,
                                                @Query("completed") boolean completed);

    /**
     * 电台中喜欢
     */
    @GET(basePath + "radio/like")
    Observable<RadioShowLikeInfo.Response> likeRadio(@Query("audioId") int audioId, @Query("dislike") boolean dislike);

    /**
     * 定时关闭 上报
     */
    @POST(basePath + "radio/reportScheduling")
    @FormUrlEncoded
    Observable<Empty_data.Response> reportRadioSchedule(@Field("id") String id);

    /**
     * 获取剪切板内容上报
     */
    @POST(basePath + "track/upload")
    Observable<Empty_data.Response> ddlReportServer(@Query("content") String content, @Query("webviewUa") String webViewUa);

    /**
     * 勋章弹框上报
     */
    @POST(basePath + "user/honor/medal/popup/verified")
    @FormUrlEncoded
    Observable<Empty_data.Response> reportHonorDialogMsg(@Field("msgId") String msgId);

    /**
     * 新人礼包信息
     */
    @GET(basePath + "gift/bag/newcomer/bag")
    Observable<NewPeopleGiftBagInfo.Response> getNewPeopleGiftBag();

    /**
     * 新人礼包创建订单
     */
    @POST(basePath + "gift/bag/order/create")
    @FormUrlEncoded
    Observable<OrderCreateResult.Response> createNewPeopleGiftBagOrder(@Field("bagId") String productId,
                                                                       @Field("fromType") String fromType,
                                                                       @Field("payType") String payType,
                                                                       @Field("userAgent") String userAgent);

    //新人礼包支付完成
    @POST(basePath + "gift/bag/deposit/finish")
    @FormUrlEncoded
    Observable<Empty_data.Response> giftBagPayFinish(@Field("orderSn") String orderSn);

    @GET(basePath + "room/questions/list")
    Observable<RoomQuestionsListResult.Response> roomQuestionList();
}