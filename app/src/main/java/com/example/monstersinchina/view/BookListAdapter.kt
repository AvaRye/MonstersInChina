package com.example.monstersinchina.view

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.monstersinchina.R
import com.example.monstersinchina.service.Book
import com.example.monstersinchina.service.homeLiveData
import kotlinx.android.synthetic.main.item_book.view.*

class BookListAdapter(private val context: Context) : BaseAdapter() {
    private val list = mutableListOf<Book>()

    fun getList(newList: List<Book>) {
        list.clear()
        list.addAll(newList)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = inflate(context, R.layout.item_book, null)
        val line = view.v_book
        val bookName = view.tv_book_name
        val letter = view.tv_book_letter

        if (list[position].url.isNullOrEmpty()) {
            line.visibility = View.GONE
            bookName.visibility = View.GONE
            letter.visibility = View.VISIBLE
            letter.text = list[position].text
        } else {
            view.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                    .putExtra("url", list[position].url)
                context.startActivity(intent)
            }
            line.visibility = View.VISIBLE
            bookName.visibility = View.VISIBLE
            letter.visibility = View.GONE
            bookName.text = list[position].text
        }

        var holder = ViewHolder(view, bookName, letter)

        if (convertView == null) {
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    inner class ViewHolder(
        view: View,
        val bookName: TextView,
        val letter: TextView
    )

}