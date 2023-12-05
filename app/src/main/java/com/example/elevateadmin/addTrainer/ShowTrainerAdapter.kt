package com.example.elevateadmin.addTrainer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.elevateadmin.databinding.ItemRowAddtrainerBinding

class ShowTrainerAdapter(private var trainerDetails:List<TrainerDetails>)
    :RecyclerView.Adapter<ShowTrainerAdapter.ShowTrainerViewHolder>(){


    inner class ShowTrainerViewHolder(val binding:ItemRowAddtrainerBinding)
        :RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowTrainerViewHolder {
        return ShowTrainerViewHolder(ItemRowAddtrainerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return trainerDetails.size
    }

    override fun onBindViewHolder(holder: ShowTrainerViewHolder, position: Int) {
        val trainerDetailItem = trainerDetails[position]

        holder.binding.trainerTitle.text = trainerDetailItem.trainerName
        holder.binding.trainerDescription.text = trainerDetailItem.trainerDescription

        Glide.with(holder.itemView.context)
            .load(trainerDetailItem.imageUriOfTheTrainer)
            .into(holder.binding.profileImage)
    }

    fun updateData(newList: List<TrainerDetails>) {
        trainerDetails = newList
        notifyDataSetChanged()
    }
}