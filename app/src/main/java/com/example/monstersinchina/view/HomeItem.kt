package com.example.monstersinchina.view

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.monstersinchina.R
import com.example.monstersinchina.commens.rec.Item
import com.example.monstersinchina.commens.rec.ItemController
import kotlinx.android.synthetic.main.item_home.view.*
import org.jetbrains.anko.layoutInflater

class HomeItem() : Item {
    internal var builder: (ViewHolder.() -> Unit)? = null

    companion object Controller : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val view = parent.context.layoutInflater.inflate(R.layout.item_home, parent, false)
            val image = view.iv_home_item
            val name = view.tv_home_item_name
            val description = view.tv_home_item_description
            val card = view.cv_home_item
            return ViewHolder(
                view,
                image,
                name,
                description,
                card
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as ViewHolder
            item as HomeItem
            item.builder?.invoke(holder)
        }

        internal class ViewHolder(
            itemView: View,
            val image: ImageView,
            val name: TextView,
            val description: TextView,
            val card: CardView
        ) : RecyclerView.ViewHolder(itemView)
    }

    override val controller: ItemController
        get() = HomeItem
}

internal fun MutableList<Item>.homeItem(builder: HomeItem.Controller.ViewHolder.() -> Unit) =
    add(HomeItem().apply { this.builder = builder })