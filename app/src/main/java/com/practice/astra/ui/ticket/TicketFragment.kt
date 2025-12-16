package com.practice.astra.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practice.astra.databinding.FragmentTicketBinding
import com.practice.astra.ui.base.BaseTabFragment

class TicketFragment() : BaseTabFragment() {

    private var _binding: FragmentTicketBinding? = null
    private val binding get() = _binding!!

    // タブのタイトルを定義
    override val tabTitles = listOf("購入済み", "ブックマーク")
    override val bindingRoot: View get() = bindingRoot
    override fun getTabLayout() = binding.myticketTabLayout
    override fun getViewPager() = binding.pager

    // 切り替え先フラグメント
    override fun createTabFragment(position: Int): Fragment {
        return when(position){
            0 -> PurchasedTicketsFragment()
            1 -> BookmarkedTicketsFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

