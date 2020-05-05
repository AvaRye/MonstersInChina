package com.example.monstersinchina.view

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.monstersinchina.R
import com.example.monstersinchina.commons.Item
import com.example.monstersinchina.commons.ItemController
import kotlinx.android.synthetic.main.item_detail_image.view.*
import kotlinx.android.synthetic.main.item_detail_strong.view.*
import kotlinx.android.synthetic.main.item_detail_text.view.*
import org.jetbrains.anko.layoutInflater

class DetailStrongItem : Item {
    internal var builder: (ViewHolder.() -> Unit)? = null

    companion object Controller :
        ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_detail_strong, parent, false)
            val textView = view.tv_strong
            return ViewHolder(view, textView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as DetailStrongItem
            item.builder?.invoke(holder)
        }

        internal class ViewHolder(
            itemView: View,
            val textView: TextView
        ) : RecyclerView.ViewHolder(itemView)
    }

    override val controller: ItemController
        get() = DetailStrongItem
}

internal fun MutableList<Item>.detailStrongItem(builder: DetailStrongItem.Controller.ViewHolder.() -> Unit) =
    add(DetailStrongItem().apply { this.builder = builder })

class DetailTextItem : Item {
    internal var builder: (ViewHolder.() -> Unit)? = null

    companion object Controller :
        ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_detail_text, parent, false)
            val textView = view.tv_text
            return ViewHolder(view, textView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as DetailTextItem
            item.builder?.invoke(holder)
        }

        internal class ViewHolder(
            itemView: View,
            val textView: TextView
        ) : RecyclerView.ViewHolder(itemView)
    }

    override val controller: ItemController
        get() = DetailTextItem
}

internal fun MutableList<Item>.detailTextItem(builder: DetailTextItem.Controller.ViewHolder.() -> Unit) =
    add(DetailTextItem().apply { this.builder = builder })

class DetailImageItem : Item {
    internal var builder: (ViewHolder.() -> Unit)? = null

    companion object Controller :
        ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view =
                parent.context.layoutInflater.inflate(R.layout.item_detail_image, parent, false)
            val imageView = view.iv_detail
            return ViewHolder(view, imageView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as DetailImageItem
            item.builder?.invoke(holder)
        }

        internal class ViewHolder(
            itemView: View,
            val imageView: ImageView
        ) : RecyclerView.ViewHolder(itemView)
    }

    override val controller: ItemController
        get() = DetailImageItem
}

internal fun MutableList<Item>.detailImageItem(builder: DetailImageItem.Controller.ViewHolder.() -> Unit) =
    add(DetailImageItem().apply { this.builder = builder })