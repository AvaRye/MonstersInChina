package com.example.monstersinchina.view

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class BottomPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private var fragments = mutableListOf<Fragment>()
    private var titles = mutableListOf<String>()
    private var current = Fragment()

    fun add(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence = titles[position]

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        current = `object` as Fragment
        super.setPrimaryItem(container, position, `object`)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

}