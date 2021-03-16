### 1. 消息类型

所有的消息都需要集成im_v4模块里面Message类，实现相关的抽象。

目前存在的消息module:

#### 1.1 基本常用消息:
im_v4模块里面function里是一些基本的消息:
```
FileMessage: 文件消息
GroupTipsMessage: 群通知消息
ImageMessage: 图片消息
TextMessage: 文本消息
VoiceMessage: 视频消息
```

#### 1.2 自定义消息:
im模块code_model里主要是一些业务消息的拓展:
```
CustomMessage: 自定义消息
    - CustomInfo: 自定义消息的内容数据
    - CustomType: 消息类型标识
```

##### 1.2.1 CustomInfo
CustomInfo类是所有自定义消息体的包装，主要作用类似于网络请求的BaseResponse。结构如下:
```
type: 自定义消息类型
data: 不同消息的不同数据结构，必须继承CustomBaseData实现
```
目前业务中存在的自定义消息和data对照关系查看CustomType类


### 2. 消息发送与接收

#### 2.1 消息创建
直接实例化相关的消息类即可，比如文本消息直接 TextMessage()

#### 2.2 消息发送
2.2.1 发送消息  

2.2.1.1 根据当前会话id得到一个TIMConversation类，通过这个类实现消息的发送。
如果存在的消息可以直接通过 TimMessage.conversation获取到会话。

```kotlin
 /** 发送消息 **/
fun sendMessage(message: Message) {

    //1 根据会话类型和会话id获取
    val conversation =  TIMManager.getInstance().getConversation(type, id)
    //2 根据存在的消息获取
    //val conversation = message.timMessage.conversation
    conversation.sendMessage(message.timMessage, object : TIMValueCallBack<TIMMessage> {
        override fun onSuccess(msg: TIMMessage?) {
            
        }

        override fun onError(code: Int, msg: String?) {
            
        }
    })
}
```


2.2.1.2  一般使用ChatMessageProvider.kt,这个类封装了大量的基础操作，包括消息发送，消息接收以及消息列表等
已经有了实现类:IMChatMessageBusiness，对ChatMessageProvider的扩展，加了发送自定义消息等

比如:

```
val chatProvider: IMChatMessageBusiness by lazy { IMChatMessageBusiness(imChatId, chatType, this) }
chatProvider.sendText()
chatProvider.sendPicture()
chatProvider.sendVoice()
chatProvider.sendCustomMessage()
...
```



#### 2.3 新消息接收
使用ChatMessageProvider或者IMChatMessageBusiness接收到新消息就会触发相关回调，如果需要自己实现请查看文档:  im事件回调.md

#### 2.3 消息转换
2.3.1 TIMMessage->Message
实现类:IMMessageCreator.kt,使用TIMMessage类的拓展函数mapToMeiMessage()即可。

2.3.2 Message->TIMMessage
Message对象里面包含的TIMMessage(timMessageExt)。


### 3. 消息的增删查撤回等操作

3.1 原始sdk实现通过TIMConversation获取到TIMConversationExt类，然后调用相关的函数调用。  
3.2 我们项目中对此操作进行了封装，和上面发送消息一样，使用ChatMessageProvider.kt实现，扩展实现类
为IMChatMessageBusiness，使用IMChatMessageBusiness调用相关函数即可。




### 4. 未读消息和标记已读消息
查看文档: im会话相关.md







