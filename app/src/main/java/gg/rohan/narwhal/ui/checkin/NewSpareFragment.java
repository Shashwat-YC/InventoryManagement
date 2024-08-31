package gg.rohan.narwhal.ui.checkin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gg.rohan.narwhal.adapter.NewSparePurchaseOrderAdapter;
import gg.rohan.narwhal.R;
import gg.rohan.narwhal.model.order.PurchaseOrder;
import gg.rohan.narwhal.model.order.PurchaseOrderStatus;
import gg.rohan.narwhal.ui.SearchManager;


public class NewSpareFragment extends Fragment implements  SearchManager.SearchListener{

    private String tabTitle;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve title from arguments
        if (getArguments() != null) {
            tabTitle = getArguments().getString("tabTitle");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_spare, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retrieve tab title passed from PMSFragment
//        Log.d("onViewCreated: ", "onViewCreated: "+tabTitle);
//        Toast.makeText(requireContext(), tabTitle, Toast.LENGTH_SHORT).show();

        TextView checkChildPage = view.findViewById(R.id.check_child_page);
        checkChildPage.setText("Open "+tabTitle+" Item");

        // Build PowerMenu for status dropdown

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setNestedScrollingEnabled(false);
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        purchaseOrders.add(new PurchaseOrder("PO-123", PurchaseOrderStatus.RECEIVED, "Supplier 1"));
        purchaseOrders.add(new PurchaseOrder("PO-124", PurchaseOrderStatus.NOT_RECEIVED, "Supplier 2"));
        purchaseOrders.add(new PurchaseOrder("PO-125", PurchaseOrderStatus.RECEIVED, "Supplier 3"));
        purchaseOrders.add(new PurchaseOrder("PO-126", PurchaseOrderStatus.NOT_RECEIVED, "Supplier 4"));
        purchaseOrders.add(new PurchaseOrder("PO-127", PurchaseOrderStatus.RECEIVED, "Supplier 5"));
        purchaseOrders.add(new PurchaseOrder("PO-128", PurchaseOrderStatus.NOT_RECEIVED, "Supplier 6"));
        NewSparePurchaseOrderAdapter newSparePurchaseOrderAdapter = new NewSparePurchaseOrderAdapter(purchaseOrders, tabTitle, this);
        recyclerView.setAdapter(newSparePurchaseOrderAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        checkChildPage.setOnClickListener(v -> {
            // open NewSpareDetailFragment on click passing tabTitle

        });


    }

    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    private void performSearch(String query) {
        // Perform search operation
    }
}