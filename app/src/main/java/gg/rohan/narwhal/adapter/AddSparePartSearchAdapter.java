package gg.rohan.narwhal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.SparePartInfo;

public class AddSparePartSearchAdapter extends RecyclerView.Adapter<AddSparePartSearchAdapter.MyViewHolder> implements Filterable {

    private List<SparePartInfo> productList;
    private List<SparePartInfo> productListFiltered;
    private Context context;
    private AddSparePartSearchAdapterListener listener;

    public AddSparePartSearchAdapter(Context context, List<SparePartInfo> productList, AddSparePartSearchAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.productList = productList;
        this.productListFiltered = new ArrayList<>(productList); // Initialize filtered list with all items
    }

    public void updateData(List<SparePartInfo> productList) {
        this.productList = productList;
        this.productListFiltered = new ArrayList<>(productList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase().trim();
                List<SparePartInfo> filteredList = new ArrayList<>();

                if (charString.isEmpty()) {
                    // If the search query is empty, return the original list
                    filteredList.addAll(productList);
                } else {
                    // Filter by part number or material description
                    for (SparePartInfo product : productList) {
                        if (product.getPartNo().toLowerCase().contains(charString) ||
                                product.getName().toLowerCase().contains(charString)) {
                            filteredList.add(product);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                // Update the filtered list and notify adapter
                productListFiltered.clear();
                productListFiltered.addAll((List<SparePartInfo>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView partNo, materialDesc;

        public MyViewHolder(View view) {
            super(view);
            partNo = view.findViewById(R.id.tv_product_option_id_value);
            materialDesc = view.findViewById(R.id.tv_product_name_value);
            view.setOnClickListener(view1 -> {
                // send selected product in callback
                listener.onProductSelected(productListFiltered.get(getAdapterPosition()));
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_spare_part_search_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SparePartInfo product = productListFiltered.get(position);
        holder.partNo.setText(product.getPartNo());
        holder.materialDesc.setText(product.getName());
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }
    public void clearFilter() {
        productListFiltered.clear();
        productListFiltered.addAll(productList);
        notifyDataSetChanged();
    }

    public interface AddSparePartSearchAdapterListener {
        void onProductSelected(SparePartInfo product);
    }
}
