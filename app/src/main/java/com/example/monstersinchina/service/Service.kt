package com.example.monstersinchina.service

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.custom.async

object SpiderApi {

    private const val baseUrl = "http://www.cbaigui.com/"
    private val client = OkHttpClient()

    fun getHomeAsync(): Deferred<List<Home>> =
        GlobalScope.async(IO + QuietCoroutineExceptionHandler) {
            client.newCall(
                Request.Builder()
                    .url(baseUrl)
                    .get()
                    .build()
            ).execute().body()?.string().orEmpty().parseHome()
        }

    fun getHomeAsync(page: Int): Deferred<List<Home>> =
        GlobalScope.async(IO + QuietCoroutineExceptionHandler) {
            client.newCall(
                Request.Builder()
                    .url("$baseUrl?paged=$page")
                    .get()
                    .build()
            ).execute().body()?.string().orEmpty().parseHome()
        }

    fun getDetailAsync(url: String): Deferred<Detail> =
        GlobalScope.async(IO + QuietCoroutineExceptionHandler) {
            client.newCall(
                Request.Builder()
                    .url(url)
                    .get()
                    .build()
            ).execute().body()?.string().orEmpty().parseDetail()
        }

}

val homeLiveData = MutableLiveData<List<Home>>()
val detailLiveData = MutableLiveData<Detail>()
val loadingLiveData = MutableLiveData<Boolean>()//loading flag

data class Home(
    val name: String,
    val image: String,
    val description: String,
    val date: String,
    val url: String,
    val finalPage: Int
)

data class Detail(
    val detail: String
)

inline fun <T> LiveData<T>.bindNonNull(
    lifecycleOwner: LifecycleOwner,
    crossinline block: (T) -> Unit
) =
    observe(lifecycleOwner, Observer { it?.let(block) })

private val QuietCoroutineExceptionHandler =
    CoroutineExceptionHandler { _, t -> t.printStackTrace() }
