# README

移动开发课作业，仅作自用

数据来源： [知妖(中国妖怪百集)](http://www.cbaigui.com)

已获得网站开发者授权

---

通过获取网页的 html 代码并用 Jsoup 解析得到数据。用 MVVM 简单分了个层，view 层是 UI 相关，service 层是数据处理和网络请求相关。还有 commons.rec 是复制过来的对 RecyclerView 相关处理的一层封装 DSL。

### view

包括初始的 HomeActivity 和 DetailActivity、其中 RecyclerView 分别用到的 Item 和双击事件的扩展函数。

- HomeActivity 用一个简单的 RelativeLayout 作为顶部类似 Toolbar，下方是简单的 RecyclerView。一页显示的就是网页上一页显示的内容，监听滑动到底部后加载下一页，顶部有下拉刷新功能和自己写了个写了个 DoubleClick 双击顶部 Toolbar 回到顶端。

```kotlin
class OnDoubleClickListener(val onDoubleClick: () -> Unit) : View.OnTouchListener {
    private var count = 0
    private var firstClick = 0L
    private var secondClick = 0L
    private val doubleClickTime = 1000L

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (MotionEvent.ACTION_DOWN == event!!.action) {
            count++
            if (count == 1) {
                firstClick = System.currentTimeMillis()
            } else if (count == 2) {
                secondClick = System.currentTimeMillis()
                if (secondClick - firstClick < doubleClickTime) {
                    v?.performClick()
                    onDoubleClick()
                }
                count = 0
                firstClick = 0L
                secondClick = 0L
            }
        }
        return true
    }
}
```

- DetailActivity 改动了一下 Toolbar 的内容，下方具体的内容也是用 RecyclerView 显示的，具体原因是 html 数据格式emmm解析出来个 List 所以方便点直接用 RecyclerView 展示了，缺点是图片的位置可能会因为滚动和网络显示有一些不好看，whatever

  数据绑定

```kotlin
detailLiveData.bindNonNull(this) {
    tb_detail.apply {
        tv_title.text = it.title
        tv_toolbar.text = it.date
    }

    val items = mutableListOf<Item>()
    it.detail.forEach { detail ->
        when (detail.type) {
            TYPE_STRONG -> items.detailStrongItem {
                textView.text = detail.content
            }

            TYPE_TEXT -> items.detailTextItem {
                textView.text = detail.content
            }

            TYPE_IMAGE -> items.detailImageItem {
                Glide.with(this@DetailActivity)
                    .load(detail.content)
                    .into(imageView)
            }
        }
    }
    itemManager.addAll(items)
}
```

### service

- Service 放了数据类和爬虫的 Api网络请求相关，使用 OkHttp 进行网络请求。使用 LiveData 存放数据，还有个 bindNonNull 的内联函数和 QuietCoroutineExceptionHandler 协程的一个 ExceptionHandler，就是个简单的封装（也是从别的大佬项目里拿来的

  部分代码

```kotlin
object SpiderApi {

    private const val baseUrl = "http://www.cbaigui.com/"
    private val client = OkHttpClient()

    fun getHomeAsync(page: Int): Deferred<HomePage> =
        GlobalScope.async(IO + QuietCoroutineExceptionHandler) {
            client.newCall(
                Request.Builder()
                    .url("$baseUrl?paged=$page")
                    .get()
                    .build()
            ).execute().body()?.string().orEmpty().parseHomePage()
        }
}

val homeLiveData = MutableLiveData<HomePage>()
val detailLiveData = MutableLiveData<DetailPage>()
val loadingLiveData = MutableLiveData<Boolean>()//loading flag

data class HomePage(
    val homeList: List<Home>,
    val finalPage: Int
)

data class Home(
    val name: String,
    val image: String,
    val description: String,
    val date: String,
    val url: String,
    val type: String
)

inline fun <T> LiveData<T>.bindNonNull(
    lifecycleOwner: LifecycleOwner,
    crossinline block: (T) -> Unit
) =
    observe(lifecycleOwner, Observer { it?.let(block) })

private val QuietCoroutineExceptionHandler =
    CoroutineExceptionHandler { _, t -> t.printStackTrace() }
```

- Manager 本来要封装一下网络请求，不过因为很简易就没有弄，只放了对 html 的解析方法，具体就是拿 Jsoup 一层一层解并返回成数据类，也是因为这个活比较麻烦所以只做了首页和具体内容的简易解析，网站本身还有很多内容

  部分代码

```kotlin
fun String.parseHomePage(): HomePage {
    val homeList = mutableListOf<Home>()
    val body = Jsoup.parse(this).body()
    val articles = body.getElementsByTag("article")
    articles.forEach {
        val day = it.getElementsByClass("post-date-day")[0].text()
        val month = it.getElementsByClass("post-date-month")[0].text()
        val year = it.getElementsByClass("post-date-year")[0].text()
        val date = year + "年" + month + day + "日"
        val image = it.getElementsByTag("img")
        var imageUrl = "http://www.cbaigui.com/wp-content/uploads/2019/10/中国妖怪百集loading-320x320.jpg"
        var type = TYPE_NO_PIC
        if (!image.isNullOrEmpty()) {
            imageUrl = image[0].attr("src")
            type = TYPE_WITH_PIC
        }
        val title = it.getElementsByClass("post-title")[0].getElementsByTag("a")[0]
        val name = title.text()
        val url = title.attr("href")
        val description = it.getElementsByClass("entry excerpt")[0].text()
        homeList.add(
            Home(
                name = name,
                image = imageUrl,
                description = description,
                date = date,
                url = url,
                type = type
            )
        )
    }
    val finalPage = Regex("(\\d+)").find(body.getElementsByClass("pages")[0].text())!!.value.toInt()
    Log.d("finalPage", finalPage.toString())
    return HomePage(
        homeList = homeList,
        finalPage = finalPage
    )
}
```

- ViewModel 其实就是个 Presenter（因为刷新状态需要 context emmm）具体就是使用协程处理网络请求，并传给 LiveData。awaitAndHandle 也是简单封装，也是拿来的

  LiveData 使用观察者模式和 Activity 绑定，数据刷新比较灵活。
  
  部分代码

```kotlin
class ViewModel(private val context: Context) {

    fun getHome(page: Int) = GlobalScope.launch(Dispatchers.Main) {
        SpiderApi.getHomeAsync(page).awaitAndHandle {
            Log.d("getHome", it.toString())
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }?.let {
            homeLiveData.postValue(it)
        }
        loadingLiveData.postValue(false)
    }

    private suspend fun <T> Deferred<T>.awaitAndHandle(handler: suspend (Throwable) -> Unit = {}): T? =
        try {
            await()
        } catch (t: Throwable) {
            handler(t)
            null
        }
}
```

### commons

是复制过来的对 RecyclerView 相关处理的一层封装 DSL，框架作者的文章 

- [构建 RecyclerViewDSL](https://www.kotliner.cn/2018/06/recyclerviewdsl/)
- [DSL in Action](https://www.kotliner.cn/2018/04/dsl-in-action/)