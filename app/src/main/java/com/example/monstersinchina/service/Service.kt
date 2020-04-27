package com.example.monstersinchina.service

import android.text.Html
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.OkHttpClient
import okhttp3.Request

object SpiderApi {

    private const val baseUrl = "http://www.cbaigui.com/"
    private val client = OkHttpClient()

    fun getHomeAsync(): Deferred<HomePage> =
        GlobalScope.async(IO + QuietCoroutineExceptionHandler) {
            client.newCall(
                Request.Builder()
                    .url(baseUrl)
                    .get()
                    .build()
            ).execute().body()?.string().orEmpty().parseHomePage()
        }

    fun getHomeAsync(page: Int): Deferred<HomePage> =
        GlobalScope.async(IO + QuietCoroutineExceptionHandler) {
            client.newCall(
                Request.Builder()
                    .url("$baseUrl?paged=$page")
                    .get()
                    .build()
            ).execute().body()?.string().orEmpty().parseHomePage()
        }

    fun getDetailAsync(url: String): Deferred<DetailPage> =
        GlobalScope.async(IO + QuietCoroutineExceptionHandler) {
            client.newCall(
                Request.Builder()
                    .url(url)
                    .get()
                    .build()
            ).execute().body()?.string().orEmpty().parseDetail()
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

data class DetailPage(
    val date: String,
    val title: String,
    val detail: List<Detail>
)

data class Detail(
    val type: String,
    val content: String
)

inline fun <T> LiveData<T>.bindNonNull(
    lifecycleOwner: LifecycleOwner,
    crossinline block: (T) -> Unit
) =
    observe(lifecycleOwner, Observer { it?.let(block) })

private val QuietCoroutineExceptionHandler =
    CoroutineExceptionHandler { _, t -> t.printStackTrace() }
