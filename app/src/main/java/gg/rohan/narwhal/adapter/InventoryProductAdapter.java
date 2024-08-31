package gg.rohan.narwhal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Map;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.inventory.MachineParts;

public class InventoryProductAdapter extends RecyclerView.Adapter<InventoryProductAdapter.InventoryProductViewHolder> {

    private final Context context;
    private final Map<String, List<MachineParts>> groupedItems;

    public InventoryProductAdapter(Context context, Map<String, List<MachineParts>> groupedItems) {
        this.context = context;
        this.groupedItems = groupedItems;
    }
    public void filterList(Map<String, List<MachineParts>> filteredItems) {
        groupedItems.clear();
        groupedItems.putAll(filteredItems);
        notifyDataSetChanged();
    }


    public void addAll(Map<String, List<MachineParts>> items) {
        groupedItems.clear();
        groupedItems.putAll(items);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public InventoryProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_product_item, parent, false);
        return new InventoryProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryProductViewHolder holder, int position) {
        List<MachineParts> items = getItemsAtPosition(position);
        if (items != null && !items.isEmpty()) {
            MachineParts firstItem = items.get(0);
            holder.ProductNameText.setText(firstItem.getPartInfo().getName());
            holder.ProductIdText.setText(firstItem.getPartInfo().getPartNo());
            holder.ProductROBValueText.setText(String.valueOf(firstItem.getQuantity()));

            InventoryProductPacketAdapter packetAdapter = new InventoryProductPacketAdapter(items);
            holder.productItems.setAdapter(packetAdapter);
            holder.productItems.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    @Override
    public int getItemCount() {
        return groupedItems.size();
    }

    private List<MachineParts> getItemsAtPosition(int position) {
        int index = 0;
        for (Map.Entry<String, List<MachineParts>> entry : groupedItems.entrySet()) {
            if (index == position) {
                return entry.getValue();
            }
            index++;
        }
        return null;
    }

    public static class InventoryProductViewHolder extends RecyclerView.ViewHolder {
        TextView ProductNameText, ProductIdText, ProductROBValueText;
        RecyclerView productItems;

        public InventoryProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ProductNameText = itemView.findViewById(R.id.tv_pms_product_name);
            ProductIdText = itemView.findViewById(R.id.tv_pms_product_id);
            ProductROBValueText = itemView.findViewById(R.id.tv_pms_product_rob_value);
            productItems = itemView.findViewById(R.id.product_items);
        }
    }

    private class InventoryProductPacketAdapter extends RecyclerView.Adapter<InventoryProductPacketAdapter.InventoryProductPacketViewHolder> {
        private final List<MachineParts> packets;

        public InventoryProductPacketAdapter(List<MachineParts> packets) {
            this.packets = packets;
        }

        @NonNull
        @Override
        public InventoryProductPacketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_job_product_option_button_item, parent, false);
            return new InventoryProductPacketViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull InventoryProductPacketViewHolder holder, int position) {
            MachineParts packet = packets.get(position);
            holder.serialNumber.setText(String.valueOf(packet.getId()));
            holder.robValue.setText(String.valueOf(packet.getPartInfo().getRob()));
            holder.scannedQty.setText(String.valueOf(packet.getQuantity()));
            String packetType = (packets.get(position).getType()).toString();
            if (packetType != null && !packetType.isEmpty()) {
                packetType = packetType.substring(0, 1).toUpperCase() + packetType.substring(1);
            }
            holder.ProductIdTagText.setText(packetType);
            if (packetType.equals("new") || packetType.equals("New") || packetType.equals("NEW")) {
                holder.ProductIdTagLayout.setCardBackgroundColor(context.getResources().getColor(R.color.pms_product_new_tag_bg));
                holder.ProductIdTagLayout.setStrokeColor(context.getResources().getColor(R.color.pms_product_new_tag_bg_stroke_color));
                holder.ProductIdTagText.setTextColor(context.getResources().getColor(R.color.pms_product_new_tag_text));
            } else {
                // check if the packet is reconditioned then only show recon. tag
                if (packetType.equals("reconditioned") || packetType.equals("Reconditioned") || packetType.equals("RECONDITIONED")) {
                    holder.ProductIdTagText.setText("RECON.");
                }
                holder.ProductIdTagLayout.setCardBackgroundColor(context.getResources().getColor(R.color.pms_product_reconditioned_tag));
                holder.ProductIdTagLayout.setStrokeColor(context.getResources().getColor(R.color.pms_product_new_tag_bg_stroke_color));
//                holder.ProductIdTagText.setTextColor(context.getResources().getColor(R.color.pms_product_reconditioned_tag_text));
            }
        }


        @Override
        public int getItemCount() {
            return packets.size();
        }

        public class InventoryProductPacketViewHolder extends RecyclerView.ViewHolder {
            TextView serialNumber, robValue, scannedQty;
            private final TextView ProductIdTagText;
            private final MaterialCardView ProductIdTagLayout;

            public InventoryProductPacketViewHolder(@NonNull View itemView) {
                super(itemView);
                serialNumber = itemView.findViewById(R.id.tv_inv_product_option_sn);
                robValue = itemView.findViewById(R.id.tv_inv_product_option_id_value);
                scannedQty = itemView.findViewById(R.id.tv_inv_product_option_scanned_qty_value);
                ProductIdTagText = itemView.findViewById(R.id.tv_inv_product_tag_text);
                ProductIdTagLayout = itemView.findViewById(R.id.tv_inv_product_tag);
            }
        }
    }
}
