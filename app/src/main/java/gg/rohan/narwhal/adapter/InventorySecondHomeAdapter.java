package gg.rohan.narwhal.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.inventory.MachineBox;
import gg.rohan.narwhal.ui.inventory.InventoryProductFragment;

public class InventorySecondHomeAdapter extends RecyclerView.Adapter<InventorySecondHomeAdapter.InventorySecondHomeViewHolder>  {

    private final Context context;
    public interface OnItemUpdateListener {
        void onUpdate(MachineBox machineBox, String action);
    }
    private final List<MachineBox> list;

    private String machineName;

    private String model;
    private OnItemUpdateListener updateListener;
    public InventorySecondHomeAdapter(Context context, List<MachineBox> list, String machineName, String model, OnItemUpdateListener listener) {
        this.context = context;
        this.list = list;
        this.machineName = machineName;
        this.model = model;
        this.updateListener = listener;
    }

    @NonNull
    @Override
    public InventorySecondHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_floor_items, parent, false);
        return new InventorySecondHomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventorySecondHomeAdapter.InventorySecondHomeViewHolder holder, int position) {

        holder.mFloorValue.setText(list.get(position).getFloor() != null ? String.valueOf(list.get(position).getFloor()) : "NA");
        holder.mAreaValue.setText(list.get(position).getRoomName() != null ? String.valueOf(list.get(position).getRoomName()) : "NA");
        holder.mRackValue.setText(list.get(position).getRack() != null ? String.valueOf(list.get(position).getRack()) : "N/A");
        holder.mShelfValue.setText(list.get(position).getShelf() != null ? String.valueOf(list.get(position).getShelf()) : "N/A");
        holder.mFloorLayout.setOnClickListener(v -> {
            // check if any value is null
            int floor = list.get(position).getFloor() == null ? 0 : list.get(position).getFloor();
            int rack = list.get(position).getRack() == null ? 0 : list.get(position).getRack();
            int shelf = list.get(position).getShelf() == null ? 0 : list.get(position).getShelf();
            int roomId = list.get(position).getRoom() == null ? 0 : list.get(position).getRoom();
            String room = list.get(position).getRoomName() == null ? "" : list.get(position).getRoomName();
            int boxId = list.get(position).getId();

            openInventoryProductFragment(model,machineName, list.get(position).getId(),boxId, floor, room, rack, shelf, roomId);
        });
        holder.mFloorLayout.setOnLongClickListener(v -> {
            if (updateListener != null) {
                updateListener.onUpdate(list.get(position),"update");
            }
            return true; // Consume the long click event
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class InventorySecondHomeViewHolder extends RecyclerView.ViewHolder {
        TextView mFloorValue, mAreaValue, mRackValue, mShelfValue;
        MaterialCardView mFloorLayout;


        public InventorySecondHomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mFloorValue = itemView.findViewById(R.id.tv_floor_value);
            mAreaValue = itemView.findViewById(R.id.tv_area_value);
            mRackValue = itemView.findViewById(R.id.tv_inv_rack_value);
            mShelfValue = itemView.findViewById(R.id.tv_shelf_value);
            mFloorLayout = itemView.findViewById(R.id.inv_floor_item_card_view);
        }

    }
    public void addAll(List<MachineBox> addedList) {
        list.clear();
        int startPosition = list.size();
        list.addAll(addedList);
        notifyItemRangeInserted(startPosition, addedList.size());
    }

    public void add(MachineBox machineBox) {
        list.add(machineBox);
        notifyItemInserted(list.size() - 1);
    }

    public void clear() {
        list.clear();
    }


    public void filterList(List<MachineBox> filteredList) {
        this.list.clear();
        this.list.addAll(filteredList);
        notifyDataSetChanged();
    }

    private void openInventoryProductFragment(String model ,String machineName, int locationId,int boxId, Integer floor, String room, Integer rack, Integer shelf, Integer roomId) {
        if (context instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) context;
            InventoryProductFragment inventoryProductFragment = new InventoryProductFragment();
            Bundle bundle = new Bundle();
            bundle.putString("model", model);
            bundle.putString("machineName", machineName);
            bundle.putInt("locationId", locationId);
            bundle.putInt("boxId", boxId);
            bundle.putInt("floor", floor);
            bundle.putString("room", room);
            bundle.putInt("roomId", roomId);
            bundle.putInt("rack", rack);
            bundle.putInt("shelf", shelf);

            inventoryProductFragment.setArguments(bundle);
            fragmentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.child_fragment_container, inventoryProductFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
