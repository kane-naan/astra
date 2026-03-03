package com.practice.astra.ui.ticketDetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.DialogAddReviewBinding
import com.practice.astra.databinding.FragmentTicketInformationBinding

class TicketInformationFragment : Fragment() {

    private var _binding: FragmentTicketInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TicketDetailViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // チケット詳細データの表示
        viewModel.selectedTicketDetails.observe(viewLifecycleOwner) { ticket ->
            binding.textContentDetail.text = ticket.description
            binding.textDateDetail.text = ticket.event_date
            binding.textHighlightDetail.text = ticket.point
            binding.textLocationDetail.text = ticket.place

            binding.includeTicketItem.apply {
                ticketTitle.text = ticket.title
                ticketActor.text = ticket.actor
                ticketPlace.text = ticket.place
                ticketPrice.text = ticket.price?.toString() ?: "0"
                ticketImage.setImageResource(R.drawable.ticket_image)

                // ブックマークのクリック
                ticketBookmark.setOnClickListener {
                    viewModel.toggleBookmark(ticket.id)
                }
            }
        }


        // X 共有
        binding.buttonShareX.setOnClickListener {
            viewModel.selectedTicketDetails.value?.let { ticket ->
                shareToX(ticket)
            }
        }

        // LINE 共有
        binding.buttonShareLine.setOnClickListener {
            viewModel.selectedTicketDetails.value?.let { ticket ->
                shareToLine(ticket)
            }
        }

        // 購入済み状態の監視
        viewModel.isPurchased.observe(viewLifecycleOwner) { purchased ->
            if (purchased) {
                binding.buttonPurchase.apply {
                    text = "購入済み"
                    isEnabled = false
                    backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
                }
            } else {
                binding.buttonPurchase.apply {
                    text = "購入"
                    isEnabled = true
                    backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blueGray)
                }
            }
        }

        // ブックマークアイコンの状態連動
        viewModel.isBookmarked.observe(viewLifecycleOwner) { isBookmarked ->
            val icon = if (isBookmarked) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24
            binding.includeTicketItem.ticketBookmark.setImageResource(icon)
        }

        // 価格表示のフォーマット
        viewModel.formattedPrice.observe(viewLifecycleOwner) { priceString ->
            binding.textPriceDetail.text = priceString
        }

        // 購入ボタンのクリック処理
        binding.buttonPurchase.setOnClickListener {
            viewModel.selectedTicketDetails.value?.let { ticket ->
                viewModel.purchaseTicket(ticket.id)
            }
        }

        // 購入結果の通知
        viewModel.purchaseSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "チケットを購入しました！", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "購入に失敗しました（在庫切れなど）", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabAddReview.setOnClickListener {
            showAddReviewDialog()
        }
    }

    // 共有用メッセージ作成ヘルパー
    private fun createShareMessage(ticket: TicketData): String {
        return """
            【Astra】おすすめの公演情報！
            
            公演名：${ticket.title}
            場所：${ticket.place}
            日付：${ticket.event_date}
            
            #Astra #アプリ紹介
        """.trimIndent()
    }

    // OS標準の共有
    private fun shareStandard(ticket: TicketData) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, createShareMessage(ticket))
        }
        startActivity(Intent.createChooser(intent, "チケット情報を共有"))
    }

    // X (旧Twitter) へ直接
    private fun shareToX(ticket: TicketData) {
        val tweetUrl = "https://twitter.com/intent/tweet?text=${Uri.encode(createShareMessage(ticket))}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl))
        startActivity(intent)
    }

    // LINE へ直接
    private fun shareToLine(ticket: TicketData) {
        val lineUrl = "https://line.me/R/msg/text/?${Uri.encode(createShareMessage(ticket))}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lineUrl))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAddReviewDialog() {
        val dialogBinding = DialogAddReviewBinding.inflate(layoutInflater)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.buttonSubmit.setOnClickListener {
            val rating = dialogBinding.editRating.rating
            val title = dialogBinding.editReviewTitle.text.toString()
            val comment = dialogBinding.editReviewComment.text.toString()

            if (title.isNotEmpty() && comment.isNotEmpty()) {
                viewModel.postReview(rating, title, comment)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "タイトルと本文を入力してください", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.buttonCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}