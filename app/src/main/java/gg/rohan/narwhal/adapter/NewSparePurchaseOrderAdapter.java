package gg.rohan.narwhal.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.model.order.PurchaseOrder;
import gg.rohan.narwhal.ui.checkin.NewSpareDetailFragment;

public class NewSparePurchaseOrderAdapter extends RecyclerView.Adapter<NewSparePurchaseOrderAdapter.NewSparePurchaseOrderHolder> {

    private final List<PurchaseOrder> purchaseOrders;
    private final String tabTitle;
    private final Fragment fragment;

    public NewSparePurchaseOrderAdapter(List<PurchaseOrder> purchaseOrders, String tabTitle, Fragment fragment) {
        this.purchaseOrders = purchaseOrders;
        this.tabTitle = tabTitle;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public NewSparePurchaseOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.new_spare_home_items, parent, false);
        return new NewSparePurchaseOrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewSparePurchaseOrderHolder holder, int position) {
        PurchaseOrder purchaseOrder = this.purchaseOrders.get(position);
        holder.purchaseOrderValue.setText(purchaseOrder.getPurchaseOrderNo());
        holder.tag.setText(purchaseOrder.getPurchaseOrderStatus().name());
        holder.manufacturer.setText(purchaseOrder.getSupplier());
        holder.itemView.setOnClickListener(v -> {
            NewSpareDetailFragment newSpareDetailFragment = new NewSpareDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tabTitle", tabTitle);
            bundle.putString("purchaseOrderNo", purchaseOrder.getPurchaseOrderNo());
            bundle.putString("manufacturer", purchaseOrder.getSupplier());
            newSpareDetailFragment.setArguments(bundle);
            fragment.getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, newSpareDetailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return purchaseOrders.size();
    }

    public static class NewSparePurchaseOrderHolder extends RecyclerView.ViewHolder {

        private final TextView purchaseOrderValue;
        private final TextView tag;
        private final TextView manufacturer;

        public NewSparePurchaseOrderHolder(@NonNull View itemView) {
            super(itemView);
            purchaseOrderValue = itemView.findViewById(R.id.po_no_value);
            tag = itemView.findViewById(R.id.tv_product_id_tag_text);
            manufacturer = itemView.findViewById(R.id.tv_product_manufacturer);
        }

    }

}
