package com.example.universitysystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.ScheduleStrItemBinding
import kotlin.collections.ArrayList

class ScheduleAdapter: RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder> () {
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
        return ScheduleAdapter.ScheduleHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        holder.bind(scheduleRecordsList[position])
    }

    override fun getItemCount(): Int {
        return  scheduleRecordsList.size
    }
    fun clearRecords(){
        scheduleRecordsList.removeAll(scheduleRecordsList.toSet())
    }

    fun removeObject(scheduleRecord: ScheduleRecord){
        scheduleRecordsList.remove(scheduleRecord)
    }
}