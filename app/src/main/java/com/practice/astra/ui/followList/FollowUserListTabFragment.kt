package com.practice.astra.ui.followList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practice.astra.databinding.FragmentFollowUserListTabBinding
import com.practice.astra.ui.base.BaseTabFragment
import com.practice.astra.ui.user.ARG_INITIAL_TAB

class FollowUserListTabFragment : BaseTabFragment() {
    private var _binding: FragmentFollowUserListTabBinding? = null
    private val binding get() = _binding!!

    override val tabTitles: List<String> = listOf("フォロー中", "フォロワー")
    override val bindingRoot: View get() = binding.root
    override fun getTabLayout() = binding.followTabLayout
    override fun getViewPager() = binding.pager

    override fun createTabFragment(position: Int): Fragment {
        return when(position){
            0 -> FollowingListFragment()
            1 -> FollowerListFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowUserListTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        val initialTabIndex = arguments?.getInt(ARG_INITIAL_TAB, 0) ?: 0
        if (initialTabIndex != 0) {
            getViewPager().setCurrentItem(initialTabIndex, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}