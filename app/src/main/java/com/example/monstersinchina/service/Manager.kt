package com.example.monstersinchina.service

import android.util.Log
import org.jsoup.Jsoup

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
        var type = "noPic"
        if (!image.isNullOrEmpty()) {
            imageUrl = image[0].attr("src")
            type = "withPic"
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

fun String.parseDetail(): Detail {
    val doc = Jsoup.parse(this)
    return Detail("")
}