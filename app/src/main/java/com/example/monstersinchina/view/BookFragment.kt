package com.example.monstersinchina.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.monstersinchina.R
import com.example.monstersinchina.service.Book
import com.example.monstersinchina.service.ViewModel
import com.example.monstersinchina.service.bindNonNull
import com.example.monstersinchina.service.bookLiveData
import kotlinx.android.synthetic.main.fragment_book.view.*

class BookFragment : Fragment() {

    private val viewModel = ViewModel()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = BookListAdapter(this.context!!)
        view.lv_frag_book.adapter = adapter
        viewModel.getBookList()

        bookLiveData.bindNonNull(this) {
            adapter.getList(it)
            adapter.notifyDataSetChanged()
            Log.d("books", it.toString())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BookFragment()
    }
}
