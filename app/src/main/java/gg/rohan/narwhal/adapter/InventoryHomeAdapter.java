package gg.rohan.narwhal.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.MachineInfo;
import gg.rohan.narwhal.ui.inventory.InventoryNewFloorFragment;

public class InventoryHomeAdapter extends RecyclerView.Adapter<InventoryHomeAdapter.InventoryHomeViewHolder> {

    private final Context context;

    private final List<MachineInfo> filteredList;
    int floor;

    public InventoryHomeAdapter(Context context, List<MachineInfo> filteredList, int floor) {
        this.context = context;
        this.filteredList = filteredList;
        this.floor = floor;
    }

    @NonNull
    @Override
    public InventoryHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_home_items, parent, false);
        return new InventoryHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryHomeAdapter.InventoryHomeViewHolder holder, int position) {
        MachineInfo machine = filteredList.get(position);
        holder.ProductNameText.setText(machine.getName());
        holder.linearLayout.setOnClickListener(v -> {
            openInventoryNewFloorFragment(machine.getModel(), machine.getName(), floor);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    //    public void filterList(List<Machine> filteredList) {
//        this.filteredList.clear();
//        this.filteredList.addAll(filteredList);
//        notifyDataSetChanged();
//    }
    public void filterList(List<MachineInfo> filteredList) {
        this.filteredList.clear();
        this.filteredList.addAll(filteredList);
        notifyDataSetChanged();
    }

    public static class InventoryHomeViewHolder extends RecyclerView.ViewHolder {
        TextView ProductNameText;
        LinearLayout linearLayout;

        public InventoryHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            ProductNameText = itemView.findViewById(R.id.tv_inventory_product_name);
            linearLayout = itemView.findViewById(R.id.ll_inv_home_item);

        }
    }

    public void addAll(List<MachineInfo> addedList) {
        int startPosition = filteredList.size();
        filteredList.addAll(addedList);
        notifyItemRangeInserted(startPosition, addedList.size());
    }

    private void openInventoryNewFloorFragment(String model, String machineName, int floor) {
//        Log.d("openInventoryNewFloorFragment:", "id: " + model + " machineName: " + machineName + " floor: " + floor);
        if (context instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) context;
            InventoryNewFloorFragment inventoryNewFloorFragment = new InventoryNewFloorFragment();
            Bundle bundle = new Bundle();
            bundle.putString("model", model);
            bundle.putString("machineName", machineName);
            bundle.putInt("floor", floor);
            inventoryNewFloorFragment.setArguments(bundle);
            fragmentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.child_fragment_container, inventoryNewFloorFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
