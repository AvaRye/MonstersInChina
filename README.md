# README

移动开发课作业，仅作自用

数据来源： [知妖(中国妖怪百集)](http://www.cbaigui.com)

已获得网站开发者授权

---

通过获取网页的 html 代码并用 Jsoup 解析得到数据。用 MVVM 简单分了个层，view 层是 UI 相关，service 层是数据处理和网络请求相关。还有 commons.rec 是复制过来的对 RecyclerView 相关处理的一层封装 DSL。

### view

包括初始的 HomeActivity 和 DetailActivity、其中 RecyclerView 分别用到的 Item 和双击事件的扩展函数。

- HomeActivity 用一个简单的 RelativeLayout 作为顶部类似 Toolbar，下方是简单的 RecyclerView。一页显示的就是网页上一页显示的内容，监听滑动到底部后加载下一页，顶部有下拉刷新功能和自己写了个写了个 DoubleClick 双击顶部 Toolbar 回到顶端。

- DetailActivity 改动了一下 Toolbar 的内容，下方具体的内容也是用 RecyclerView 显示的，具体原因是 html 数据格式emmm解析出来个 List 所以方便点直接用 RecyclerView 展示了，缺点是图片的位置可能会因为滚动和网络显示有一些不好看，whatever

### service

- Service 放了数据类和爬虫的 Api网络请求相关，使用 OkHttp 进行网络请求。使用 LiveData 存放数据，还有个 bindNonNull 的内联函数和 QuietCoroutineExceptionHandler 协程的一个 ExceptionHandler，就是个简单的封装（也是从别的大佬项目里拿来的

- Manager 本来要封装一下网络请求，不过因为很简易就没有弄，只放了对 html 的解析方法，具体就是拿 Jsoup 一层一层解并返回成数据类，也是因为这个活比较麻烦所以只做了首页和具体内容的简易解析，网站本身还有很多内容

- ViewModel 其实就是个 Presenter（因为刷新状态需要 context emmm）具体就是使用协程处理网络请求，并传给 LiveData。awaitAndHandle 也是简单封装，也是拿来的

  LiveData 使用观察者模式和 Activity 绑定，数据刷新比较灵活。

### commons

是复制过来的对 RecyclerView 相关处理的一层封装 DSL，框架作者的文章 

- [构建 RecyclerViewDSL](https://www.kotliner.cn/2018/06/recyclerviewdsl/)
- [DSL in Action](https://www.kotliner.cn/2018/04/dsl-in-action/)