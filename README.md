# ZhuLing
竹翎，一个Material风格的APP

突然想整合一下所学的知识，理一个框架出来，顺带熟悉一下谷歌的Material Design。工作这几年，很遗憾，所在公司都只看重iOS开发，安卓的要按照IOS的设计稿来开发，完全没有安卓的风格，不免扼腕叹息。百忙之中，抽出时间，本来想理一个框架出来，看来还有许多待完善的地方，后面会继续完善。先看效果图吧：[APK下载点这里](http://download.csdn.net/detail/ithouse/9803616) 

![](https://raw.githubusercontent.com/ITAnt/ZhuLing/master/image/demo.gif)  


项目的风格为谷歌的Material风格，其中主要运用到了MVP模式和rxandroid框架，都是简单集成，时间仓促，还没来得及很好封装。主要是Material开发还不是很火，起码就目前市面上看来，比较少产品完全依照Material风格来设计的，我知道的，有BiliBili、SegmentFault等少数几个，大多数产品只是蜻蜓点水般稍微运用了一些Material的元素。我想，产品毕竟注重功能和稳定。因为Material开发的过程中，确实有很多坑，比如，NestedScrollView在与AppBarLayout一起滑动的时候，就会莫名出现卡顿，为了解决这个问题，我苦苦搜寻3天，没能找到办法，只好用了一个开源库[smooth-app-bar-layout](https://github.com/henrytao-me/smooth-app-bar-layout)来解决，这个开源库确实还不错，但与其他开源框架或谷歌原生控件结合使用的时候，又会有这样那样的问题出现，比如，使用RecyclerView时要设置一个header才能正常显示，[参考这里](http://www.jianshu.com/p/079fc98dd739)。

adapter主要放了主界面ViewPager的adapter

api本来是想放置Retrofit相关的接口的，结果运行了个demo没有成功，就不了了之了，现在才做了一个网络请求，直接用的okhttp和rxandroid结合，并没有封装成很好的工具框架，大家可以先尝试，我有时间就完成封装。

Event主要放置EventBus的一些事件，都是简单的事件，比如通知其他界面刷新UI等。EventBus是个好工具，用起来非常方便，但我不会滥用（目前还没使用，gradle里也注释掉了，要用时再打开）。

listener目前还没有写什么，里面放了一个类，本来是用来监听RecyclerView滚动，以实时控制AppBarLayout滚动的，但是这个仅适用于单个Activity的情况，由于主界面有一个ViewPager包含多个Fragment，如果再用这个监听的话，那情况复杂得很，而且切换不同Fragment时AppBarLayout会闪动，我就直接用了谷歌原生的Material元素来控制了。虽然在包含NestedScrollView的Fragment，向上滚动的时候，部分机型会有卡顿的情况，比如华为P9，其他手机如一加、nexus5暂时没发现这种情况，这其实是谷歌的一个bug，体验了一下Bilibili的APP，发现这个bug同样存在，暂时还没有很好的替代方案，当然smooth-app-bar-layout可以解决这个bug，但是集成起来不太容易，而且扩展性不太好，比如在smooth-app-bar-layout和smarttablayout之间，你只能选择一个，因为他们的初始化方法不同，因为我必须要用到[smarttablayout](https://github.com/ogaclejapan/SmartTabLayout)的漂亮切换效果，所以主界面直接采用了原生的Material控件。

ui是最主要的代码集中地，我为什么没有写fragment、activity等包？因为我建议以功能模块划分逻辑功能包，而不是根据文件类别划分，因为同一个界面的功能实现模块就是一个集合，没必要把它们强行分开来按文件类型归档，后面MVP的代码也是一个功能放在一个包里，这样找代码的时候，看到包名一下子就可以找到相关功能的所有代码了。

utils里面放置一些公共工具类，比如Toast、获取ToolBar高度等

widget里面放置一些开源控件，比如LoadMoreRecyclerView等

library项目主要放置了鸿洋大神的CommonAdapter

项目还用到了很多开源框架，有好几个功能点都是直接拿来，还没来得及更改，惭愧，感谢以下大神或开源项目：

[多彩资讯客户端](https://github.com/kaku2015/ColorfulNews)
```Java
代码家
鸿洋大神

/*网络请求*/
compile 'com.squareup.retrofit2:retrofit:2.2.0'
compile 'com.squareup.retrofit2:converter-gson:2.2.0'

/*tab切换*/
compile 'com.ogaclejapan.smarttablayout:library:1.6.1@aar'
compile 'com.github.bumptech.glide:glide:3.7.0'

/*右侧菜单*/
compile 'com.yalantis:contextmenu:1.0.7'

/*可以隐藏toolbar的RecyclerView，可以用，但不够流畅，用系统监听代替了*/
/* compile 'com.github.ksoichiro:android-observablescrollview:1.6.0'*/

/*EventBus*/
compile 'org.greenrobot:eventbus:3.0.0'

/*Material风格的下拉刷新*/
compile 'com.cjj.materialrefeshlayout:library:1.3.0'

compile 'me.henrytao:smooth-app-bar-layout:25.1.0.0'

/*compile 'com.github.bumptech.glide:glide:3.6.0'*/

/*圆形头像*/
compile 'de.hdodenhof:circleimageview:1.3.0'

/*列表滚动动画*/
compile 'jp.wasabeef:recyclerview-animators:2.2.6'
```
[ContextMenu](https://github.com/Yalantis/Context-Menu.Android)

不建议直接在代码上面修改哦，因为我的国际化没有做完善，导致打包会有错误提示，还有针对Android6.0的权限申请也还没完善。。。有时间一定补上，现在好累，要先睡觉啦。
