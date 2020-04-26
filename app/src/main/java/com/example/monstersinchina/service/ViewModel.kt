package com.example.monstersinchina.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*

class ViewModel(private val context: Context) {
    fun getHome() = GlobalScope.launch(Dispatchers.Main) {
        SpiderApi.getHomeAsync().awaitAndHandle {
            Log.d("getHome", it.toString())
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }?.let {
            homeLiveData.postValue(it)
        }
        loadingLiveData.postValue(false)
    }

    fun getHome(page: Int) = GlobalScope.launch(Dispatchers.Main) {
        SpiderApi.getHomeAsync(page).awaitAndHandle {
            Log.d("getHome", it.toString())
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }?.let {
            homeLiveData.postValue(it)
        }
        loadingLiveData.postValue(false)
    }

    fun getDetail(url: String) = GlobalScope.launch(Dispatchers.Main) {
        SpiderApi.getDetailAsync(url).awaitAndHandle {
            Log.d("getDetail", it.toString())
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }?.let {
            detailLiveData.postValue(it)
        }
    }


    private suspend fun <T> Deferred<T>.awaitAndHandle(handler: suspend (Throwable) -> Unit = {}): T? =
        try {
            await()
        } catch (t: Throwable) {
            handler(t)
            null
        }
}