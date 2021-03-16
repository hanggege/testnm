
我们项目中只有c2c和群组会话两种消息类型，官方账号消息属于c2c。

### 会话对象

项目中使用的会话对象为Conversation，Conversation类类似于Message类一样
是TIMConversation的一个封装


### 获取最近会话列表

1. sdk获取
```
TIMManagerExt.getInstance().conversationList
```
2. 项目进行了封装ConversationProvider.kt,我们一般用这个
```
val conversationProvider: ConversationProvider by lazy { ConversationProvider(this) }
conversationProvider.loadConversation()
```
当我们实例化的时候添加ConversationView回调，那么当需要刷新的
时候会自动回调获取回话列表，然后回调initListView,具体查看ConversationProvider实现。



### 会话未读消息数量获取
TencentKit.kt里面已经进行了封装，直接调用getAllMessageUnreadNum即可
```
/**
 * 获取除免打扰外的全部未读消息
 */
fun getAllMessageUnreadNum(back: (Int) -> Unit) {
    if (imIsLogin()) {
        val conversationList = TIMManager.getInstance().conversationList
        requestGroupIgnoreNotify { groupList ->
            val unreadNum =
                    conversationList.filter { !groupList.contains(it.peer) && it.type != TIMConversationType.System }
                            .sumBy { it.unreadMessageNum.toInt() }
            back(unreadNum)
        }
    } else back(0)
}

```
项目中封装了未读消息更改的刷新回调
```
/**
 * im刷新消息未读数
 */
fun checkIMRefresh(back: () -> Unit): IMAllEventManager =
        object : IMAllEventManager() {
            /**
             * 数据刷新通知回调（如未读计数，会话列表等）
             */
            override fun onRefresh() {
                super.onRefresh()
                back()
            }

            override fun onUserSigExpired() {
                super.onUserSigExpired()
                back()
            }

            override fun onForceOffline() {
                super.onForceOffline()
                back()
            }
        }
```
在需要的地方加入检测，然后在回调里面做对应的逻辑即可，可以查看IMBaseActivity未读消息刷新实现方式。


### 标记已读消息
Conversation对象已经进行了封装
```
conversation.readAllMessage()
```

### 会话删除
1. 被动删除，主要是群方面，比如被提之类的，主要逻辑在ConversationProvider，这里面监听了群组的一些事件，当条件满足触发remove事件
```
override fun onGroupTipsEvent(elem: TIMGroupTipsElem?) {
        when (elem?.tipsType) {
            TIMGroupTipsType.Quit, TIMGroupTipsType.Kick, TIMGroupTipsType.DelGroup -> converView?.removeConversation(
                    elem.groupId
            )
            else -> {
            }
        }
    }
```
2. 主动删除，删除之前需要标记一下已读
```
conversation.readAllMessage()
TIMManager.getInstance().deleteConversationAndLocalMsgs(conversation.type, conversation.peer)
```


### 获取会话方的资料
1. c2c对话  
根据需要会话资料的id集合请求我们服务器，然后做缓存，如果下次再取的时候直接从缓存取，
如果缓存里没有就筛选出需要重新请求的id集合重新请求添加缓存，支持强制刷新。  
主要的缓存实现逻辑:UserInfoCache.kt


2. 群组会话  
和c2c一样。  
主要的缓存实现逻辑: GroupInfoCache.kt








