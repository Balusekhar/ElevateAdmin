package com.example.elevateadmin.addClass

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.elevateadmin.databinding.ItemRowShowclassBinding

class ShowClassAdapter(private var classDetails:List<ClassDetails>)
    :RecyclerView.Adapter<ShowClassAdapter.ShowClassViewHolder>(){

    inner class ShowClassViewHolder(val binding: ItemRowShowclassBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowClassViewHolder {
        return ShowClassViewHolder(ItemRowShowclassBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return classDetails.size
    }

    override fun onBindViewHolder(holder: ShowClassViewHolder, position: Int) {
        val classDetailItem = classDetails[position]
        holder.binding.textClassName.text = classDetailItem.className
        holder.binding.textClassDescription.text = classDetailItem.description
        holder.binding.textDateOfTheClass.text = classDetailItem.dateOfTheClass
        holder.binding.textCategory.text = classDetailItem.category

        //load the image
        Glide.with(holder.itemView.context)
            .load(classDetailItem.imageUri)
            .into(holder.binding.classImageView)
    }

    fun updateData(newList: List<ClassDetails>) {
        classDetails = newList
        notifyDataSetChanged()
    }
}