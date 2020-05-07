package com.example.monstersinchina.view

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.monstersinchina.R
import com.example.monstersinchina.commons.Item
import com.example.monstersinchina.commons.ItemController
import kotlinx.android.synthetic.main.item_book.view.*
import org.jetbrains.anko.layoutInflater

class BookItem : Item {
    internal var builder: (ViewHolder.() -> Unit)? = null

    companion object Controller :
        ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_book, parent, false)
            val name = view.tv_book_name
            val letter = view.tv_book_letter
            val line = view.v_book
            return ViewHolder(view, line, name, letter)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as BookItem
            item.builder?.invoke(holder)
        }

        internal class ViewHolder(
            itemView: View,
            val line: View,
            val name: TextView,
            val letter: TextView
        ) : RecyclerView.ViewHolder(itemView)
    }

    override val controller: ItemController
        get() = BookItem
}

internal fun MutableList<Item>.bookItem(builder: BookItem.Controller.ViewHolder.() -> Unit) =
    add(BookItem().apply { this.builder = builder })