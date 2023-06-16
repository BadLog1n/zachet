package com.oneseed.zachet.ui.grades.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.oneseed.zachet.activities.IndividualChatActivity
import com.oneseed.zachet.R
import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.databinding.SubjectGradesItemBinding

class GradesAdapter(
    private val onChatClick: (String) -> Unit
) : RecyclerView.Adapter<GradesAdapter.GradesHolder>() {
    private var gradesList = ArrayList<SubjectGrades>() //!!!!
    private lateinit var database: DatabaseReference

    class GradesHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val binding = SubjectGradesItemBinding.bind(item)
        fun bind(subjectGrades: SubjectGrades) = with(binding) {
            tv1Subject.text = subjectGrades.subject
            grAll.text = subjectGrades.allGradesCount.toString()
            grAll3.text = subjectGrades.allGradesCount.toString()
            expandBtn.setImageResource(R.drawable.ic_arrow_down)
            tv2Subject.text = subjectGrades.subject
            collapseBtn.setImageResource(R.drawable.ic_arrow_up)
            typeOfSubject.text = subjectGrades.typeOfSubject
            segment1.text = subjectGrades.segments[0]
            segment2.text = subjectGrades.segments[1]
            segment3.text = subjectGrades.segments[2]
            segment4.text = subjectGrades.segments[3]
            seg1Visit.text = subjectGrades.grades[0]
            seg1Academ.text = subjectGrades.grades[1]
            seg2Visit.text = subjectGrades.grades[2]
            seg2Academ.text = subjectGrades.grades[3]
            seg3Visit.text = subjectGrades.grades[4]
            seg3Academ.text = subjectGrades.grades[5]
            seg4Visit.text = subjectGrades.grades[6]
            seg4Academ.text = subjectGrades.grades[7]
            val test = "Баллы за ${subjectGrades.typeOfSubject.lowercase()}:"
            grForTestingTv.text = test
            grForTestingCount.text = subjectGrades.grades[8]
            additionalGrTv.text = "Дополнительные баллы:"
            additionalGrCount.text = subjectGrades.grades[9]
            premiumGrTv.text = "Премиальные баллы:"
            premiumGrCount.text = subjectGrades.grades[10]
            userChatId1Tv.text = subjectGrades.userChatId
            teacherTv.text = subjectGrades.FIO
            expandedView.visibility = View.GONE
            collapsedView.visibility = View.VISIBLE
            subjectChange.visibility = View.GONE

            if (subjectGrades.subjectIsChange) {
                subjectChange.visibility = View.VISIBLE
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradesHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.subject_grades_item, parent, false)
        val expView = view.findViewById<LinearLayout>(R.id.expandedView)
        val collView = view.findViewById<LinearLayout>(R.id.collapsedView)
        //временно
        val userChatId1Tv = view.findViewById<TextView>(R.id.userChatId1_tv)
        expView.visibility = View.GONE
        collView.visibility = View.VISIBLE

        view.findViewById<LinearLayout>(R.id.collapsedView).setOnClickListener {
            expView.visibility = View.VISIBLE
            collView.visibility = View.GONE
        }
        view.findViewById<ImageButton>(R.id.expandBtn).setOnClickListener {
            expView.visibility = View.VISIBLE
            collView.visibility = View.GONE
        }
        view.findViewById<ImageButton>(R.id.collapseBtn).setOnClickListener {
            expView.visibility = View.GONE
            collView.visibility = View.VISIBLE
        }

        view.findViewById<LinearLayout>(R.id.linearLayoutOpenUp).setOnClickListener {
            expView.visibility = View.GONE
            collView.visibility = View.VISIBLE
        }

        expView.visibility = View.GONE

        view.findViewById<LinearLayout>(R.id.connectWTeacherLayout).setOnClickListener {
            onChatClick(userChatId1Tv.text.toString())
        }
        return GradesHolder(view)
    }

    override fun onBindViewHolder(holder: GradesHolder, position: Int) {
        holder.bind(gradesList[position])
    }

    override fun getItemCount(): Int {
        return gradesList.size
    }

    fun addSubjectGrades(subjectGrades: SubjectGrades) {
        gradesList.add(subjectGrades)
    }

    fun replaceAllGrades(_gradesList: ArrayList<SubjectGrades>) {
        clearRecords()
        gradesList = _gradesList

    }

    private fun clearRecords() {
        notifyItemRangeRemoved(0, gradesList.size)
        gradesList.removeAll(gradesList.toSet())
        gradesList.clear()
    }
}