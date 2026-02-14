package com.example.footplaystats.ui.stats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.footplaystats.databinding.ItemCategoryBinding;
import com.example.footplaystats.databinding.ItemSubstatBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryItem> categories;

    public CategoryAdapter(List<CategoryItem> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategoryBinding binding;

        public CategoryViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CategoryItem category) {

            binding.textCategoryName.setText(category.getName());
            binding.textCategoryAverage.setText(String.valueOf(category.getAverage()));

            binding.containerSubStats.removeAllViews();

            for (SubStatItem subStat : category.getSubStats()) {

                ItemSubstatBinding subBinding = ItemSubstatBinding.inflate(
                        LayoutInflater.from(binding.getRoot().getContext()),
                        binding.containerSubStats,
                        false
                );

                subBinding.textSubStatName.setText(subStat.getName());
                subBinding.textSubStatValue.setText(String.valueOf(subStat.getValue()));

                binding.containerSubStats.addView(subBinding.getRoot());
            }

            binding.containerSubStats.setVisibility(
                    category.isExpanded() ? View.VISIBLE : View.GONE
            );

            binding.headerCategory.setOnClickListener(v -> {
                category.setExpanded(!category.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}