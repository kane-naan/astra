package com.practice.astra.ui.user

import android.os.Bundle
import android.util.Log
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

    // アダプターの初期化（口コミクリックといいねクリックの動作を定義）
    private val reviewAdapter = ReviewAdapter(
        onReviewClick = { reviewData ->
            val action = UserFragmentDirections.actionUserToTicketDetailTab(reviewData.ticketId)
            findNavController().navigate(action)
        },
        onLikeClick = { reviewData ->
            // ViewModelの関数を呼び出す
            viewModel.toggleReviewLike(reviewData)
        }
    )

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

        // RecyclerViewの設定
        setupRecyclerView()

        // データの監視（LiveData）
        setupObservers()

        // クリックリスナーの設定
        setupClickListeners()

        // 遷移元からのユーザーID取得
        arguments?.getString("userId")?.let { userId ->
            viewModel.setTargetUserId(userId)
        }

        // データ取得開始
        viewModel.loadUserData()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewWriting.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
        }

        binding.recyclerViewRecommended.apply {
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
        }
    }

    private fun setupObservers() {
        // プロフィール編集ボタン等の表示切り替え
        viewModel.isMyProfile.observe(viewLifecycleOwner) { isMyProfile ->
            binding.buttonFollow.visibility = if (isMyProfile) View.GONE else View.VISIBLE
        }

        // ユーザー情報の反映
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.apply {
                textName.text = user.name
                textMessage.text = user.message
                textFollowing.text = "フォロー中 ${user.following.size}"
                textFollower.text = "フォロワー ${user.follower.size}"
            }
        }

        // おすすめチケット一覧の同期（BaseTicketListFragmentの機能を利用）
        observeAndSync(viewModel.recommendedTickets, binding.recyclerViewRecommended)

        // 口コミ一覧の監視（ここ1箇所でリスト更新を管理）
        viewModel.myReviews.observe(viewLifecycleOwner) { reviews ->
            Log.d("DEBUG", "UserFragment - 取得した口コミ数: ${reviews.size}")
            reviewAdapter.submitList(reviews)
            // リスト更新後にレイアウトを再計算
            binding.recyclerViewWriting.post {
                binding.recyclerViewWriting.requestLayout()
            }
        }
    }

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