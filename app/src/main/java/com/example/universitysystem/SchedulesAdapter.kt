package com.example.universitysystem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.ScheduleStrItemBinding
import kotlin.collections.ArrayList

class SchedulesAdapter: RecyclerView.Adapter<SchedulesAdapter.ScheduleHolder> () {
    var scheduleRecordsList = ArrayList<ScheduleRecord>()
    class ScheduleHolder (item: View):RecyclerView.ViewHolder (item){

        private val binding = ScheduleStrItemBinding.bind(item)
        fun bind(scheduleRecord: ScheduleRecord) = with(binding){
            timeTv.text = scheduleRecord.time
            subjectAndTypeTv.text = scheduleRecord.subject
            roomNFioTv.text = scheduleRecord.cabName
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule_str_item,parent,false)
        return SchedulesAdapter.ScheduleHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        holder.bind(scheduleRecordsList[position])
    }

    override fun getItemCount(): Int {
        return  scheduleRecordsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addScheduleRecord(scheduleRecord: ScheduleRecord){
        scheduleRecordsList.add(scheduleRecord)
        notifyDataSetChanged()
    }
    fun clearRecords(){
        scheduleRecordsList.removeAll(scheduleRecordsList.toSet())
    }

    fun removeObject(scheduleRecord: ScheduleRecord){
        scheduleRecordsList.remove(scheduleRecord)
    }
}