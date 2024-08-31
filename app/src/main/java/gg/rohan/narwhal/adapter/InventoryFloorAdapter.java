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
import gg.rohan.narwhal.ui.inventory.InventoryProductFragment;

public class InventoryFloorAdapter extends RecyclerView.Adapter<InventoryFloorAdapter.InventoryFloorViewHolder> {

    private List<Void> floorList;
    private Context context;
    private int machineId;
    private String machineName;

    public InventoryFloorAdapter(List<Void> floorList, Context context, int machineId, String machineName) {
        this.floorList = floorList;
        this.context = context;
        this.machineId = machineId;
        this.machineName = machineName;
    }

    @NonNull
    @Override
    public InventoryFloorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_floor_items, parent, false);
        return new InventoryFloorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryFloorViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return floorList.size();
    }

    public class InventoryFloorViewHolder extends RecyclerView.ViewHolder {
        TextView mFloorValue, mAreaValue, mRackValue, mShelfValue;
        MaterialCardView mFloorLayout;

        public InventoryFloorViewHolder(@NonNull View itemView) {
            super(itemView);
            mFloorValue = itemView.findViewById(R.id.tv_floor_value);
            mAreaValue = itemView.findViewById(R.id.tv_area_value);
            mRackValue = itemView.findViewById(R.id.tv_inv_rack_value);
            mShelfValue = itemView.findViewById(R.id.tv_shelf_value);
            mFloorLayout = itemView.findViewById(R.id.inv_floor_item_card_view);
        }
    }
    private void openInventoryProductFragment(int machineId, String machineName, int boxId) {
        if (context instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) context;
            InventoryProductFragment inventoryProductFragment = new InventoryProductFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("machineId", machineId);
            bundle.putString("machineName", machineName);
            bundle.putInt("boxId", boxId);
            inventoryProductFragment.setArguments(bundle);
            fragmentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.child_fragment_container, inventoryProductFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


}
