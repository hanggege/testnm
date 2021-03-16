# 用于IM、发帖等的布局文件util，用于加载表情等面板的处理
## 方法有以下：
```
     attach(panelLayout: View, focusView: View?, switchClickListener: SwitchClickListener? = null, vararg subPanelAndTriggers: SubPanelAndTrigger)
```
1. panelLayout：包裹表情等布局的外部控件，有以下四个选择
        KPSwitchPanelLinearLayout，KPSwitchPanelRelativeLayout，KPSwitchPanelLayoutHandler，KPSwitchPanelFrameLayout
   focusView：当前的存在焦点的view
   switchClickListener：监听是否切换到了面板
   subPanelAndTriggers：可变参数，将可点击切换面板与键盘的按钮与面板中布局绑定
```
     attach(panelLayout: View, switchPanelKeyboardBtn: View?, focusView: View?, switchClickListener: SwitchClickListener? = null)
```
2. panelLayout：包裹表情等布局的外部控件
   switchPanelKeyboardBtn: 切换面板与键盘的按钮
   focusView：当前的存在焦点的view
   switchClickListener：监听是否切换到了面板
```
   showPanel(panelLayout: View)
```
3. 切换到面板
```
   showKeyboard(panelLayout: View, focusView: View?)
```
4. 切换到键盘
```
   switchPanelAndKeyboard(panelLayout: View, focusView: View?): Boolean
```
5. 键盘与面板间切换，返回值是是否切换到了面板
```
   hidePanelAndKeyboard(panelLayout: View)
```
6. 隐藏面板和键盘