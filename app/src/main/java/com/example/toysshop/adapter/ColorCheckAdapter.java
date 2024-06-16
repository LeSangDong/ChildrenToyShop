package com.example.toysshop.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toysshop.databinding.SpinnerColorItemBinding;
import com.example.toysshop.model.ColorItem;

import java.util.ArrayList;
import java.util.List;

public class ColorCheckAdapter extends RecyclerView.Adapter<ColorCheckAdapter.ColorViewHolder> {

    private Context context;
    private List<String> colorNames;
    private int[] colorValues;
    private List<Boolean> selectedColors;

    private List<ColorItem> listColor;

    public ColorCheckAdapter(Context context, List<String> colorNames, int[] colorValues, List<Boolean> selectedColors) {
        this.context = context;
        this.colorNames = colorNames;
        this.colorValues = colorValues;
        this.selectedColors = selectedColors;
        this.listColor = new ArrayList<>();
    }

    @NonNull
    @Override
    public ColorCheckAdapter.ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       SpinnerColorItemBinding binding = SpinnerColorItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
       return  new ColorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorCheckAdapter.ColorViewHolder holder, int position) {
       // holder.binding.colorCheckbox.setText(colorNames.get(position));
        holder.binding.colorView.getBackground().setColorFilter(colorValues[position], PorterDuff.Mode.SRC_ATOP);
        holder.binding.colorCheckbox.setChecked(selectedColors.get(position));
        holder.binding.tvNameColor.setText(colorNames.get(position));


        holder.binding.colorCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Nếu màu đã được chọn, thêm vào danh sách màu nếu chưa tồn tại
                ColorItem colorItem = new ColorItem(colorNames.get(position), colorValues[position]);
                if (!listColor.contains(colorItem)) {
                    listColor.add(colorItem);
                }
            } else {
                // Nếu màu bị bỏ chọn, loại bỏ khỏi danh sách màu
                ColorItem colorItem = new ColorItem(colorNames.get(position), colorValues[position]);
                listColor.remove(colorItem);
            }
        });


    }

    @Override
    public int getItemCount() {
        return colorNames.size();
    }

    public class ColorViewHolder extends  RecyclerView.ViewHolder{
        SpinnerColorItemBinding binding;

        public ColorViewHolder(@NonNull SpinnerColorItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public List<ColorItem> getListColor() {
        return listColor;
    }
}

