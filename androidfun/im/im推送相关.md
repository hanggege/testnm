im推送比较简单，因为我们项目中没有做进程kill之后的推送，所以没有接入小米，华为等推送。
在我们项目只做了简单的当进程还没有被系统kill，或者当前app不在前台展示的时候的推送通知。


调用操作类:IMPushNotifyKit.kt ，里面简单封装了如下的操作:

```
//删除指定id的通知
resetNotifyById（）
//删除所有的消息通知
resetNotifyAll()
//展示通知
showNotification（）
```

im推送的初始化，在imInit.kt里面封装了registerIMEvent，这个函数里面进行了腾讯sdk的离线推送注册：
```
if (SessionWrapper.isMainProcess(any)) {
    TIMManager.getInstance().setOfflinePushListener {
        showNotification(it.title, it.content, it.conversationId, it.conversationType)
    }
}
```
registerIMEvent函数在初始化im的时候调用。


#### 指定页面或者指定聊天id进行忽略推送
1. 当我们在聊天页面这种情况的时候，我们就需要忽略通知推送新消息，这个时候我们就需要忽略这个聊天id。  
2. 当我们在主页的时候就不需要推送，需要全部忽略。  

在项目中某个Activity实现IMNotifyLevelInterface
```
interface IMNotifyLevelInterface {

    /**
     * 全都不通知
     */
    fun isAllIgnore(): Boolean

    /**
     * 不通知指定ID
     */
    fun IgnoreByID(): String
}
```
然后重写赋值即可。这样的话就动态配置了不发通知的id或者全部不通知。   
这里的实现原理主要是application注册了一个activity的回调进行全局监听，所以这个接口只能在activity实现。具体查看TencentProvider.kt 

#### 推送通知实现原理
这个实现原理稍微有点绕，当我们使用离线推送配置监听的时候，也就是初始化的时候调用registerIMEvent的时候，
里面调用了resetNotifyById("")函数，这个时候实例化了一个IMPushNotify对象，这个类实现了IMAllEventManager，当初始化的时候
就会把本身注册到总的事件管理里面进行管理回调，当收到新消息的时候就会触发回调，然后进行消息通知的操作判断等。具体查看IMPushNotifyExt.kt




