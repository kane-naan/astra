package com.practice.astra.ui.base

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


/**
 * タブ抽象規定クラス
 * タブを表示する際にこのFragmentを継承する
 * onViewCreateにてsetupTabsを呼びだすこと
 * */
abstract class BaseTabFragment: Fragment() {
    protected abstract val tabTitles: List<String>
    protected abstract val bindingRoot: View
    protected abstract fun getTabLayout(): TabLayout
    protected abstract fun getViewPager():  ViewPager2
    protected abstract fun createTabFragment(position: Int): Fragment

    protected fun setupTabs(){
        val viewPager = getViewPager()
        val tabLayout = getTabLayout()

        val pagerAdapter = BaseFragmentPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private inner class BaseFragmentPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment){
        override fun getItemCount(): Int = tabTitles.size
        override fun createFragment(position: Int): Fragment {
            return createTabFragment(position)
        }
    }
}