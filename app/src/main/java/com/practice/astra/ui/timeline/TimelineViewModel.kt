package com.practice.astra.ui.timeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.astra.R
import com.practice.astra.data.TicketData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TimelineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _timelineTickets = MutableLiveData<List<TicketData>>()
    val timelineTickets: LiveData<List<TicketData>> = _timelineTickets

    fun loadTimeline() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("tickets").get().await()

                val tickets = snapshot.documents.mapNotNull{ doc ->
                    doc.toObject(TicketData::class.java)?.copy(id = doc.id)
                }
                _timelineTickets.value = tickets
            }catch(e: Exception){
                Log.e("TimelineViewModel", "Firestore読み込みエラー", e)
            }
        }
    }
}