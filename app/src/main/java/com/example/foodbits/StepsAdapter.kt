package com.example.foodbits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodbits.databinding.ItemStepBinding

class StepsAdapter : RecyclerView.Adapter<StepsAdapter.StepViewHolder>() {

    private val steps = mutableListOf<String>()

    inner class StepViewHolder(private val binding: ItemStepBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(step: String) {
            binding.stepText.text = step
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(steps[position])
    }

    override fun getItemCount(): Int = steps.size

    fun addStep(step: String) {
        steps.add(step)
        notifyItemInserted(steps.size - 1)
    }

    fun getSteps(): List<String> = steps
}
