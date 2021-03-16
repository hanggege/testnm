### 群组资料
查看文档: im群相关.md

### 群组操作
1. 获取群成员/咨询师团队
通过我们服务器获取群组资料，里面members字段就是群成员,mentors是咨询师团队

2. 消息免打扰  
2.1 设置/取消消息免打扰，TencentGroupKit.kt里面进行了封装,使用setGroupIgnored()函数。  
2.2 获取消息免打扰群组，GroupNotifyCache.kt里面进行了封装requestGroupIgnoreNotify()
函数，和用户/群组资料获取实现类似，都是使用了本地缓存的方式,只不过这里是通过sdk获取的数据。

3. 踢出成员
调用我们服务器进行操作
```
val request = CHATGROUP_MANAGER_remove_Request(mUserId, mGroupId, CHATGROUP_MANAGER_remove_Request.KICKED_OUT, removeReason, removeSupplyReason)
apiSpiceMgr.executeKt(request,success = {
            if(it.isSuccess){
                 UIToast.toast(Cxt.getStr(R.string.remove_member_success))
                //...
            }else{
                var tips = it.errMsg
                if (tips.isNullOrEmpty()) {
                    tips = Cxt.getStr(R.string.remove_member_fail)
                }
                 UIToast.toast(tips)
            }
        },failure = {
             UIToast.toast(it?.currMessage)
        },finish = {
            showLoading(false)
        })
```

详看IMRemoveGroupActivity

4. 邀请成员
也是调用我们服务器进行操作
```
val request = CHATGROUP_list_group_Request(JohnUser.getSharedUser().userID, 1)
        apiSpiceMgr.executeKt(request, success = {
            if (it.isSuccess) {
                val groupList = it.data?.groups.orEmpty().filter { group -> group.isManager }
                mGroupList.clear()
                mGroupList.addAll(groupList)
                mAdapter.notifyDataSetChanged()
            } else {
                UIToast.toast(activity, it.errMsg)
            }
        }, failure = {
            UIToast.toast(activity, it?.currMessage)
        }, finish = {
            showLoading(false)
        })
```
具体查看ChatInviteGroupActivity

5. 群系统消息通知   
群系统消息通知其实也是一个与官方账号的c2c会话。业务类在ChatGroupNoticeActivity实现。  
5.1 邀请入群,被批准入群，入群申请被拒绝 只有学员可收到  
5.2 接受入群邀请 , 申请入群  只有导师可以收到  
5.3 退出群,被踢出群 两端都收的到  
相关状态码见 im模块里面 GroupSystemData.kt

6. 群处理(详见ChatGroupNoticeAdapter.kt)  
6.1 学员同意入群，调用CHATGROUP_agree_group_Request接口。  
6.2 群主/管理员同意学员入群，调用CHATGROUP_MANAGER_accept_apply_Request接口。  
6.3 群主/管理员拒绝学员入群，调用CHATGROUP_MANAGER_reject_apply_Request接口。


### 群组事件监听
查看文档: im事件回调.md


