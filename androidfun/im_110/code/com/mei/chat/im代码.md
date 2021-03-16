#### 主要代码结构

##### 展示类


IMBaseActivity: 聊天页面的公用抽离,封装了新消息的接收，发送，展示，消息点击长按，刷新，功能切换等。  
- IMC2CActivity: 个人对个人聊天页面的实现,官方账号也属于c2c  。
- IMGroupActivity: 群组聊天页面的实现 。

IMBaseConversationFragment:会话列表的公用抽离，封装会话列表的获取，展示，移除等事件处理。
- MeTabFragment:项目中的会话列表和登录业务结合的展示页。

ImMenuFragment: 更多菜单展示片段的抽离，对应的菜单item的对象抽离为BaseAction的实现。

Action: action包下的类，比如ImgAction，VideoAction等，对应的每一个菜单实现。

ImEmojiFragment: 表情展示片段的抽离。  

ChatGroupListActivity: 群列表页面，包含数据有加入和管理的群。

ChatGroupInfoActivity: 群信息展示页面。

ChatGroupNoticeActivity: 群通知消息展示和操作页面，比如邀请通知，申请通知等等。

IMChatCreateReplyActivity: 添加新的快速回复页面。

IMChatQuickReplyActivity: 快捷回复选择。

ChatInviteGroupActivity: 邀请成员入群，选择邀请的群页面。

IMChatSettingActivity: c2c对话的对方用户设置页面。

IMGroupMemberActivity: group对话的设置页面。

IMRemoveGroupActivity: 在群里踢出某个成员的时候，需要一个踢出的理由，然后才能踢出，这个就是那个选择理由提交踢出的页面。

ImPlayerManager:语音播放管理类，在这里主要用于播放语音。 

ImRecordManager:语音录制管理类，在这里主要用于按住说话。 

ImVideoInputDialogV2:视频录制。

IMVideoActivity:视频播放。

IMImagePreviewActivity:图片预览。

IMSelectConverActivity: 选择会话，一般用于分享之类的选择。


##### 功能封装类

IMAllEventManager: im的所用到的事件的总集，详见im的事件回调.md。

ChatMessageProvider : 主要用于消息的增删查，更新等操作。
- IMChatMessageBusiness: ChatMessageProvider在项目中的扩展实现，主要是扩展了自定义消息。

ConversationProvider : 主要用于会话的查询，移除，刷新等操作。

IMLoginProvider: 主要用于im登录登出等相关操作。
- IMLoginBusiness:IMLoginProvider在项目中的扩展实现。

TencentKit: 封装了im的常用操作和检测。

TencentProvider: 初始化，忽略推送id的改变以及标识当前是否是在前台展示。

GroupNotifyCache: 群消息免打扰缓存管理。

UserInfoCache: 聊天用户信息缓存管理。

GroupInfoCache: 聊天群组信息缓存管理。

IMPushNotify: 聊天消息推送管理。


#####  对外开放访问类
im_v4:主要是一些im基础操作  
IMAllEventManagerKit.kt: im事件管理类，用于添加移除绑定生命周期，手动回调一些事件  
TencentGroupKit.kt: im群组的一些操作，比如设置/检测/获取消息免打扰等  
TencentKit.kt: im的一些基础操作，退出登录，检测是否登录，获取登录用户id，还有黑名单的增删查操作  

im: 涉及到一些和义务耦合的操作  
IMInitKit.kt: im初始化，快速登录，消息刷新，登录被踢事件检测等  
IMPushNotifyKit.kt: im推送的一些操作  


im_274:主要是页面相关  
IMKit.kt: 主要是页面的一些入口操作


#### 聊天页面结构  
不管是c2c还group聊天，页面的结构都是一样的,整体分为五个结构  
1. 头部  
IMC2CActivity和IMGroupActivity单独实现，展示用户/群组信息等。  
2. 聊天消息列表展示  
IMBaseActivity里面实现，通过IMChatMessageBusiness实现消息相关的获取和事件监听相关逻辑。
3. 文字/语音输入，发送，功能切换  
主要实现发送文字，发送语音，以及点击表情按钮，更多功能按钮不同状态切换，展示不同页面的逻辑。
4. 表情  
通过读取本地预制好的表情包，然后展示选择。
5. 更多  
主要是菜单栏的选择，不同的条件展示的菜单不一样。具体查看IMC2CActivity和IMGroupActivity的功能添加。








