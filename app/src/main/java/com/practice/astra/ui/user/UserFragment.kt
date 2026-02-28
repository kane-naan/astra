package com.practice.astra.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentUserBinding
import com.practice.astra.ui.base.BaseTicketListFragment
import com.practice.astra.ui.ticketDetail.ReviewAdapter


// 定数の定義
const val ARG_INITIAL_TAB = "initial_tab_index"
const val TAB_INDEX_FOLLOWING = 0
const val TAB_INDEX_FOLLOWER = 1

class UserFragment : BaseTicketListFragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    // ViewModelの取得
    private val viewModel: UserViewModel by viewModels()

    // アダプターをクラスプロパティとして定義
    private val reviewAdapter = ReviewAdapter()

    // BaseTicketListFragmentから継承した機能
    override fun onTicketClicked(ticket: TicketData) {
        val action = UserFragmentDirections.actionUserToTicketDetailTab(ticket.id)
        findNavController().navigate(action)
    }

    override fun onBookmarkClicked(ticket: TicketData) {
        viewModel.toggleBookmark(ticket.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerViewの初期設定を一度だけ行う
        setupRecyclerView()

        // データの監視を開始
        setupObservers()

        // クリックリスナーの設定
        setupClickListeners()

        arguments?.getString("userId")?.let { userId ->
            viewModel.setTargetUserId(userId)
        }

        // データ取得の開始
        viewModel.loadUserData()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewWriting.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
            setHasFixedSize(false) // 中身の量に合わせて高さを変える
        }

        binding.recyclerViewRecommended.apply {
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
        }
    }

    private fun setupObservers() {
        viewModel.isMyProfile.observe(viewLifecycleOwner) { isMyProfile ->
            binding.buttonFollow.visibility = if (isMyProfile) View.GONE else View.VISIBLE
        }

        // プロフィールデータの監視
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.apply {
                textName.text = user.name
                textMessage.text = user.message
                textFollowing.text = "フォロー中 ${user.following.size}"
                textFollower.text = "フォロワー ${user.follower.size}"
            }
            viewModel.myReviews.observe(viewLifecycleOwner) { reviews ->
                android.util.Log.d("DEBUG", "UserFragment - 取得した口コミ数: ${reviews.size}")
                reviewAdapter.submitList(reviews)
                binding.recyclerViewWriting.requestLayout()
            }
        }

        // おすすめチケット一覧の同期
        observeAndSync(viewModel.recommendedTickets, binding.recyclerViewRecommended)

        // 自分の口コミ一覧の監視
        viewModel.myReviews.observe(viewLifecycleOwner) { reviews ->
            // データの件数をログに出力して確認
            android.util.Log.d("DEBUG", "UserFragment - 取得した口コミ数: ${reviews.size}")

            // アダプターにデータを渡す
            reviewAdapter.submitList(reviews)
        }
    }

    // フォロー・フォロワー一覧への遷移
    private fun setupClickListeners() {
        binding.textFollowing.setOnClickListener {
            navigateToFollowList(TAB_INDEX_FOLLOWING)
        }
        binding.textFollower.setOnClickListener {
            navigateToFollowList(TAB_INDEX_FOLLOWER)
        }
    }

    private fun navigateToFollowList(initialTab: Int) {
        val bundle = Bundle().apply {
            putInt(ARG_INITIAL_TAB, initialTab)
        }
        findNavController().navigate(R.id.followListFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}