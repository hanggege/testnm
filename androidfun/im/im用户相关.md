### 用户登录
1. 登录具体实现类为im_v4模块里面的IMLoginProvider，子类IMLoginBusiness为我们项目中的扩展实现。
2. 登录之前我们需要保证我们当前项目用户已登录，然后在登录im。在IMLoginBusiness.kt里面已经写好了逻辑。
只需要调用loginAppIM函数即可，然后会触发相关的回调。
3. 更快速简单的方式，直接使用IMLoginBusiness.kt里面的下面这个函数,这里是对第二个的封装。
```
/**
 * 快速登录IM
 */
fun loginIM(success: () -> Unit = {}, failure: ((Int, String) -> Unit)? = null) {
    IMLoginBusiness(object : IMLoginView {
        override fun loginSuccess() {
            success()
        }

        override fun loginFailure(code: Int, msg: String) {
            if (failure != null) failure(code, msg)
            else UIToast.toast(Cxt.get(), "登录失败：$code $msg")
        }

        override fun knackOut() {
        }

        override fun onConnected() {
        }

        override fun onDisconnected() {
        }

    }).loginAppIM()
}

```
4. 使用IMInitKit.kt里面quickLoginIM函数，一般使用这个，这个是3的外部访问封装。
5. 检测im是否登录，使用TencentKit.kt里面的imIsLogin()函数判断即可。


#### 登录被踢
当其他机子登录的时候进行踢出，直接调用IMInitKit.kt里面checkIMForceOffline函数进行监听即可。
如果不是在FragmentActivity和Fragment里面使用的话，需要自己手动维护事件的注册和解绑，
调用registered和unregistered进行管理,不然事件将无法触发调用。


#### 黑名单相关
黑名单相关调用封装在TencentKit.kt，直接调用相关函数即可。
```
//是否在黑名单内
isImUserBlacked()
//加入黑名单
addImUserBlacked()
//移除黑名单
removeImUserBlacked()
```


#### 注销
TencentKit.kt里面进行了封装imLogout()函数
#### 登录状态改变
查看文档: im事件回调.md

#### 查询聊天用户资料
查看文档: im会话相关.md