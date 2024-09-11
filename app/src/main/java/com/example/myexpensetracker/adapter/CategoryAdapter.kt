package com.example.myexpensetracker.adapter
import com.example.myexpensetracker.QuizActivity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myexpensetracker.databinding.CategoryItemsBinding
import com.example.myexpensetracker.models.CategoryModelClass

class CategoryAdapter(
    private var categoryList: ArrayList<CategoryModelClass>,
    private var requireActivity: FragmentActivity
) : RecyclerView.Adapter<CategoryAdapter.MyCategoryViewHolder>() {
    class MyCategoryViewHolder(var binding: CategoryItemsBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategoryViewHolder {
        return MyCategoryViewHolder(
            CategoryItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = categoryList.size
    override fun onBindViewHolder(holder: MyCategoryViewHolder, position: Int) {
        val data = categoryList[position]

        holder.binding.apply{
            categoryImg.setImageResource(data.categoryImage)
           NameOfSubj.text = data.categoryText

           CategorySubj.setOnClickListener {
                var intent = Intent(requireActivity, QuizActivity::class.java)
               intent.apply {
                 putExtra("QuizImg", data.categoryImage)
                   putExtra("QuestionType", data.categoryText)
                   requireActivity.startActivity(intent)
               }
            }
        }
    }
}



