package gg.rohan.narwhal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.inventory.MachineParts;

public class AddPartAdapter extends RecyclerView.Adapter<AddPartAdapter.ViewHolder> {
    private List<MachineParts> packets;
    private Context context;
    private int locationId;
    private int boxId;
    private String machineName;
    private int floor;
    private String room;
    private int rack;
    private int shelf;
    private OnItemClickListener listener;
    public AddPartAdapter(List<MachineParts> packets, Context context, int locationId, int boxId, String machineName, int floor, String room, int rack, int shelf,OnItemClickListener listener) {
        this.packets = packets;
        this.context = context;
        this.locationId = locationId;
        this.boxId = boxId;
        this.machineName = machineName;
        this.floor = floor;
        this.room = room;
        this.rack = rack;
        this.shelf = shelf;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_add_part_first_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MachineParts packet = packets.get(position);
        holder.partName.setText(packet.getPartInfo().getName());
        holder.partCondition.setText(packet.getType());
        holder.productId.setText(String.valueOf(packet.getPartInfo().getPartNo()));
        holder.quantity.setText(String.valueOf(packet.getQuantity()));
        holder.linearLayout.setOnClickListener(v -> listener.onItemClick(packet,position));
        if (packets.get(position).getType().equals("new") || packets.get(position).getType().equals("New") || packets.get(position).getType().equals("NEW")) {
            holder.ProductIdTagLayout.setCardBackgroundColor(context.getResources().getColor(R.color.pms_product_new_tag_bg));
            holder.ProductIdTagLayout.setStrokeColor(context.getResources().getColor(R.color.pms_product_new_tag_bg_stroke_color));
            holder.partCondition.setTextColor(context.getResources().getColor(R.color.pms_product_new_tag_text));
        } else {
            holder.ProductIdTagLayout.setCardBackgroundColor(context.getResources().getColor(R.color.pms_product_reconditioned_tag));
            holder.ProductIdTagLayout.setStrokeColor(context.getResources().getColor(R.color.pms_product_new_tag_bg_stroke_color));
            holder.partCondition.setTextColor(context.getResources().getColor(R.color.pms_product_reconditioned_tag_text));
        }
    }
    public interface OnItemClickListener {
        void onItemClick(MachineParts packet, int position);

        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }
    @Override
    public int getItemCount() {
        return packets.size();
    }

    public void addItem(MachineParts packet) {
        this.packets.add(0, packet); // Insert at the start of the list
        notifyItemInserted(0);
    }


    public void updateItem(int position, MachineParts packet) {
        packets.set(position, packet);
        notifyItemChanged(position);
    }
    public void addAll(List<MachineParts> packetList) {
        this.packets.clear();
        this.packets.addAll(packetList);
        notifyDataSetChanged();
    }

    public void filterList(List<MachineParts> filteredList) {
        this.packets.clear();
        this.packets.addAll(filteredList);
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView partName, partCondition, quantity, productId;
        LinearLayout linearLayout;
        MaterialCardView ProductIdTagLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            partName = itemView.findViewById(R.id.tv_product_name);
            partCondition = itemView.findViewById(R.id.tv_product_id_tag_text);
            productId = itemView.findViewById(R.id.tv_product_id);
            quantity = itemView.findViewById(R.id.tv_product_qty_value);
            linearLayout = itemView.findViewById(R.id.ll_inventory_add_part_first_items);
            ProductIdTagLayout = itemView.findViewById(R.id.mv_product_id_tag);
        }
    }
}
