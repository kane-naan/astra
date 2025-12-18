package com.practice.astra.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.practice.astra.databinding.FragmentPurchasedTicketsBinding
import com.practice.astra.ui.base.BaseTicketListFragment

class PurchasedTicketsFragment : BaseTicketListFragment() {
    private var _binding: FragmentPurchasedTicketsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TicketViewModel by viewModels()

    private var unusedAdapter: RecyclerAdapter? = null
    private var expiredAdapter: RecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPurchasedTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.purchasedTickets.observe(viewLifecycleOwner) { (unusedList, expiredList) ->
            // 未使用チケットリスト
            if (unusedAdapter == null) {
                unusedAdapter = setupRecyclerView(
                    binding.recyclerViewUnused,
                    unusedList,
                    { ticket -> commonHandleItemClick(ticket) }
                )
            } else {
                unusedAdapter?.updateData(unusedList)
            }

            // 使用済みチケットリスト
            if (expiredAdapter == null) {
                expiredAdapter = setupRecyclerView(
                    binding.recyclerViewExpired,
                    expiredList,
                    { ticket -> commonHandleItemClick(ticket) }
                )
            } else {
                expiredAdapter?.updateData(expiredList)
            }
        }
        viewModel.loadPurchasedTickets()
    }
}
