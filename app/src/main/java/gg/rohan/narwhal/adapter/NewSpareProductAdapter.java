package gg.rohan.narwhal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.model.product.JobProduct;
import gg.rohan.narwhal.model.product.Product;

public class NewSpareProductAdapter extends RecyclerView.Adapter<NewSpareProductAdapter.NewSpareProductHolder> {

    private final List<JobProduct> jobProducts;

    public NewSpareProductAdapter(List<JobProduct> jobProducts) {
        this.jobProducts = jobProducts;
    }

    @NonNull
    @Override
    public NewSpareProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.spare_home_items, parent, false);
        return new NewSpareProductAdapter.NewSpareProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewSpareProductHolder holder, int position) {
        JobProduct jobProduct = jobProducts.get(position);
        Product product = jobProduct.getProduct();
        holder.productName.setText(product.getMaterialDesc());
        holder.machinery.setText("Machinery");
        holder.manufacturer.setText(product.getMakerDesc());
        holder.tag.setText("Reconditioned");
        holder.productId.setText(product.getPartNo());
        holder.robValue.setText(String.valueOf(jobProduct.getRob()));
        holder.scannedValue.setText(String.valueOf(jobProduct.getQuantityNeeded()));
        holder.checkoutValue.setText(String.valueOf(0));
    }

    @Override
    public int getItemCount() {
        return jobProducts.size();
    }

    public void addItems(List<JobProduct> jobProducts) {
        this.jobProducts.addAll(jobProducts);
        notifyDataSetChanged();
    }

    public void filterList(List<JobProduct> filteredList) {
        this.jobProducts.clear();
        this.jobProducts.addAll(filteredList);
        notifyDataSetChanged();
    }

    public static class NewSpareProductHolder extends RecyclerView.ViewHolder {

        private final TextView productName;
        private final TextView machinery;
        private final TextView manufacturer;
        private final TextView tag;
        private final TextView productId;
        private final TextView robValue;
        private final TextView scannedValue;
        private final TextView checkoutValue;

        public NewSpareProductHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.tv_product_name);
            machinery = itemView.findViewById(R.id.tv_machinery);
            manufacturer = itemView.findViewById(R.id.tv_manufacturer);
            tag = itemView.findViewById(R.id.tv_product_id_tag_text);
            productId = itemView.findViewById(R.id.tv_product_id);
            robValue = itemView.findViewById(R.id.tv_product_rob_value);
            scannedValue = itemView.findViewById(R.id.tv_product_scanned_qty_value);
            checkoutValue = itemView.findViewById(R.id.tv_product_checkout_qty_value);
        }
    }

}
