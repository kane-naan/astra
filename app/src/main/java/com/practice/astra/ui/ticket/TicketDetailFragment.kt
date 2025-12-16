package com.practice.astra.ui.ticket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practice.astra.databinding.FragmentTicketDetailBinding
import com.practice.astra.ui.base.BaseTabFragment

class TicketDetailFragment : BaseTabFragment() {

    private var _binding: FragmentTicketDetailBinding? = null
    private val binding get() = _binding!!

    // タブタイトルを定義
    override val tabTitles: List<String> = listOf("チケット情報", "団体情報")
    override val bindingRoot: View get() = bindingRoot
    override fun getTabLayout() = binding.ticketDetailTabLayout
    override fun getViewPager() = binding.pager

    // 切り替え先フラグメント
    override fun createTabFragment(position: Int): Fragment {
        return when(position){
            0 -> TicketInformationFragment()
            1 -> OrganizationInformationFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketDetailBinding.inflate(inflater, container, false)
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