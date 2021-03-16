# 编辑富文本文件
> 支持：图片、文字、表情等的富文本编辑器
## RichTextEditor
>本模块最主要的部分，富文本编辑器
>
>有时间了仍需将更多的基本颜色等的属性设置开放为代码可编辑

· internal修饰的变量和方法并不希望被外界使用
1. 开放的变量有：
>`delegate`（希望能多扩展些）
2. 开放的监听有：
>`imageDeleteListener`（删除图片，监听删除的图片本地地址值)
>
>`richTextWatcher`（监听文字的改变）
>
>`lastEditorWatcher`（监听文字编辑EditText的改变）
3. 开放的方法有：
>`buildEditList(type: FindType): ArrayList<RichInfo> `获取全部编辑的信息
>
>`getTextAll(): String`获取全部信息，图片是地址文字
>
>`setText(richTxt: String)`设置文本，图片是带有标签信息`<img src=" ">"`的文字，图片被删除则展示错误图片
>
>`insertImage(imagePath: String)`插入图片，传参图片的绝对路径
>
>`getLastEdit(): Emojicon?`获取最后一个文本编辑器
>
>`clearAllView()`清除所有信息，建议退出时和清零时使用
>
>`getImgList(): ArrayList<String>`获取全部的图片地址
>
>`setHint(hint: String)`设置提示信息
## RichTextUtil
>包含了一些图片处理的方法，可以拿出来使用
>
>
>支持的图片格式是`<img src=" ">"`
>
>`replaceLocalPath(richTxt: String, photoList: PhotoList)`替换本地地址到网络url
>`getLocationUrls(images: ArrayList<String>): ArrayList<String>`从众多图片地址中剔除网络地址
>
>其他方法符合使用规范均可开放使用