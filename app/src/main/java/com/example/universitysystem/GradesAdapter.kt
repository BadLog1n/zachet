package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.SubjectGradesItemBinding

class GradesAdapter:RecyclerView.Adapter<GradesAdapter.GradesHolder>() {
    var gradesList = ArrayList<SubjectGrades>()
    class GradesHolder(item:View):RecyclerView.ViewHolder (item){

        private val binding = SubjectGradesItemBinding.bind(item)
        fun bind(subjectGrades: SubjectGrades) = with(binding){
            tv1Subject.text = subjectGrades.subject
            grAll.text = subjectGrades.allGradesCount.toString()
            expandBtn.setImageResource(R.drawable.ic_arrow_down)
            tv2Subject.text = subjectGrades.subject
            collapseBtn.setImageResource(R.drawable.ic_arrow_up)
            typeOfSubject.text = subjectGrades.typeOfSubject
            segment1.text = subjectGrades.segments[0]
            segment2.text = subjectGrades.segments[1]
            segment3.text = subjectGrades.segments[2]
            segment4.text = subjectGrades.segments[3]
            seg1Visit.text = subjectGrades.grades[0].toString()
            seg1Academ.text = subjectGrades.grades[1].toString()
            seg2Visit.text = subjectGrades.grades[2].toString()
            seg2Academ.text = subjectGrades.grades[3].toString()
            seg3Visit.text = subjectGrades.grades[4].toString()
            seg3Academ.text = subjectGrades.grades[5].toString()
            seg4Visit.text = subjectGrades.grades[6].toString()
            seg4Academ.text = subjectGrades.grades[7].toString()
            val test = "Баллы за ${subjectGrades.typeOfSubject}:"
            grForTestingTv.text = test
            grForTestingCount.text = subjectGrades.grades[8].toString()
            additionalGrTv.text = "Дополнительные баллы:"
            additionalGrCount.text = subjectGrades.grades[9].toString()
            premiumGrTv.text = "Премиальные баллы:"
            premiumGrCount.text = subjectGrades.grades[10].toString()
            userChatId1Tv.text = subjectGrades.userChatId
            teacherTv.text = subjectGrades.FIO1
            teacher2Tv.text = subjectGrades.FIO2
            userChatId2Tv.text = subjectGrades.userChatId2
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradesHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_grades_item,parent,false)
        view.findViewById<ImageButton>(R.id.expandBtn).setOnClickListener {
            val expView = view.findViewById<LinearLayout>(R.id.expandedView)
            expView.visibility = View.VISIBLE
            val collView = view.findViewById<LinearLayout>(R.id.collapsedView)
            collView.visibility = View.GONE
        }

        view.findViewById<LinearLayout>(R.id.collapsedView).setOnClickListener {
            val expView = view.findViewById<LinearLayout>(R.id.expandedView)
            expView.visibility = View.VISIBLE
            val collView = view.findViewById<LinearLayout>(R.id.collapsedView)
            collView.visibility = View.GONE
        }


        view.findViewById<TextView>(R.id.tv2_subject).setOnClickListener {
            val expView = view.findViewById<LinearLayout>(R.id.expandedView)
            expView.visibility = View.GONE
            val collView = view.findViewById<LinearLayout>(R.id.collapsedView)
            collView.visibility = View.VISIBLE
        }

        view.findViewById<ImageButton>(R.id.collapseBtn).setOnClickListener {
            val expView = view.findViewById<LinearLayout>(R.id.expandedView)
            expView.visibility = View.GONE
            val collView = view.findViewById<LinearLayout>(R.id.collapsedView)
            collView.visibility = View.VISIBLE
        }
        val expView = view.findViewById<LinearLayout>(R.id.expandedView)
        expView.visibility = View.GONE

        view.findViewById<ImageButton>(R.id.spechBubbblesImg).setOnClickListener {

            val intent = Intent(parent.context,IndividualChatActivity::class.java)
            intent.putExtra("getUser",view.findViewById<TextView>(R.id.teacher_tv).text)
            parent.context.startActivity(intent)
        }
        view.findViewById<TextView>(R.id.connectWTeacher_tv).setOnClickListener {
            val intent = Intent(parent.context,IndividualChatActivity::class.java)
            /**Здесь мы значение переменной грубо говоря передаем каждой кнопке в разных окнах.
             * То есть у каждого окна свой обработчик нажатия засчет того что userChatId - переменная.
             * Если интересна логика - то метод вверху и этот метод вызывается для каждого элемента списка.
             * **/
            intent.putExtra("getUser",view.findViewById<TextView>(R.id.teacher_tv).text)
            parent.context.startActivity(intent)

        }
        return GradesHolder(view)
    }

    override fun onBindViewHolder(holder: GradesHolder, position: Int) {
        holder.bind(gradesList[position])
    }

    override fun getItemCount(): Int {
        return gradesList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addSubjectGrades(subjectGrades: SubjectGrades){
        gradesList.add(subjectGrades)
        notifyDataSetChanged()
    }

    fun clearRecords(){
        gradesList.removeAll(gradesList.toSet())
    }
}