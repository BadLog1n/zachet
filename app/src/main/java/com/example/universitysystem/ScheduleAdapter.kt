package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.ListOfChatsItemBinding
import com.example.universitysystem.databinding.ScheduleStrItemBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ScheduleAdapter: RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder> () {
    var scheduleRecordsList = ArrayList<ScheduleRecord>()
    class ScheduleHolder (item: View):RecyclerView.ViewHolder (item){

        private val binding = ScheduleStrItemBinding.bind(item)
        @SuppressLint("SimpleDateFormat")
        fun bind(scheduleRecord: ScheduleRecord) = with(binding){
            timeTv.text = scheduleRecord.time
            val s_n_t = scheduleRecord.subject+scheduleRecord.typeOfClass
            subjectAndTypeTv.text = s_n_t
            val r_n_f = scheduleRecord.numberOfRoom+scheduleRecord.teachersFIO
            roomNFioTv.text = r_n_f
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