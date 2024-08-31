package gg.rohan.narwhal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gg.rohan.narwhal.R;

public class NewCheckboxAdapter extends RecyclerView.Adapter<NewCheckboxAdapter.ViewHolder> {
    private List<String> options;
    private OnCheckedChangeListener listener;
    private int selectedItemPosition = -1; // Track the selected item position
    private int itemWidth;

    public NewCheckboxAdapter(List<String> options) {
        this.options = options;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_item, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = itemWidth;
        itemView.setLayoutParams(layoutParams);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.checkbox.setText(options.get(position));
        holder.checkbox.setChecked(position == selectedItemPosition);
        holder.itemView.setOnClickListener(view -> {
            if (selectedItemPosition != position) {
                // Unselect the previously selected item and select the new item
                int previouslySelectedItemPosition = selectedItemPosition;
                selectedItemPosition = position;
                notifyItemChanged(previouslySelectedItemPosition);
                notifyItemChanged(selectedItemPosition);
                if (listener != null) {
                    listener.onCheckedChanged(selectedItemPosition, true);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return options.size();
    }

    public void setChecked(int position, boolean isChecked) {
        if (isChecked) {
            selectedItemPosition = position;
        } else {
            selectedItemPosition = -1;
        }
        notifyDataSetChanged();
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(int position, boolean isChecked);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;

        ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.check_box);
        }
    }
}


