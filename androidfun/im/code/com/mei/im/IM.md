# 老文档，已废弃，不要往下看了。看新文档： im代码.md(im_274模块下)
## 初始化
   初始化代码定义在IMHelper中的init方法中，会在application中被调用。初始化IM的SDK，设置刷新监听等等

##  核心业务类
   *  com.mei.im.ui.fragment.IMConversationFragment  会话列表
   *  com.mei.im.ui.IMChatBaseActivity 聊天基类
   *  com.mei.im.ui.IMChatSingleActivity 单聊
   *  com.mei.im.ui.IMChatGroupActivity 群聊

## 自定义消息步骤
   * com.mei.im.holder.IMMessageHelper  所有消息的简单工厂类，用类生产各种消息的ViewHolder。
     每定义一种自定义消息就要新增一个消息类型，以及生产对应的ViewHolder.
   * 自定义发送消息的ViewHolder需要继承IMCourseSendHolder类，重写refreshCustomChild方法，实现自定义消息的数据填充
      与UI刷新，同理接收消息ViewHolder继承IMCustomReceiveHolder，重写refreshCustomChild方法

## 自定义消息自定义字段
   * 客户端与服务端协商好，统一定义在有道云协作/开发组
   * 云协作文档链接：
   https://note.youdao.com/share/?token=B476D64CEF3D41E3B052391DF8CDD658&gid=6837947
   备注：这是云协作，坏男孩项目中的。因为两个app已经完全同类话了。


