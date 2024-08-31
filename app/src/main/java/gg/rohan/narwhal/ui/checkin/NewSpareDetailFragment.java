package gg.rohan.narwhal.ui.checkin;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import gg.rohan.narwhal.adapter.NewSpareProductAdapter;
import gg.rohan.narwhal.R;
import gg.rohan.narwhal.model.product.JobProduct;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.api.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewSpareDetailFragment extends Fragment implements  SearchManager.SearchListener{

    private String tabTitle;
    NewSpareProductAdapter newSpareProductAdapter;
    List<JobProduct> jobProductList = new ArrayList<>();
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
        return inflater.inflate(R.layout.fragment_new_spare_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retrieve tab title passed from PMSFragment
//        Log.d("onViewCreated: ", "onViewCreated: "+tabTitle);
//        Toast.makeText(requireContext(), tabTitle, Toast.LENGTH_SHORT).show();

        TextView purchaseOrderNo = view.findViewById(R.id.tv_pms_item_heading);
        TextView manufacturer = view.findViewById(R.id.tv_pms_item_sub_heading);
        purchaseOrderNo.setText(getArguments().getString("purchaseOrderNo"));
        manufacturer.setText(getArguments().getString("manufacturer"));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setNestedScrollingEnabled(false);
        newSpareProductAdapter = new NewSpareProductAdapter(new ArrayList<>());
        recyclerView.setAdapter(newSpareProductAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RetrofitClient.getPmsAdapter().getProducts("0D1000.00005").enqueue(new Callback<List<JobProduct>>() {
            @Override
            public void onResponse(Call<List<JobProduct>> call, Response<List<JobProduct>> response) {
                newSpareProductAdapter.addItems(response.body());
            }

            @Override
            public void onFailure(Call<List<JobProduct>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        TextView checkChildPage = view.findViewById(R.id.check_child_page);
        checkChildPage.setText("Open "+tabTitle+" Item");

        ExtendedFloatingActionButton confirmFabBtn = view.findViewById(R.id.fab_confirm);

        confirmFabBtn.setOnClickListener(v -> {
            openPopupWindow(v);
        });

    }

    private void openPopupWindow(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.updated_popup, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // Handle click on the "Close" button in the dialog
        Button closeButton = dialogView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss(); // Dismiss the dialog
            }
        });
    }


    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    public void performSearch(String query) {
        // Perform search
        List<JobProduct> searchResults = new ArrayList<>();
        if (!query.isEmpty()) {
            for (JobProduct product : jobProductList) {
                if (product.getProduct().getMaterialDesc().toLowerCase().contains(query.toLowerCase()) || product.getProduct().getPartNo().toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(product);
                }
            }
        }else {
            searchResults.addAll(jobProductList);
        }
        newSpareProductAdapter.filterList(searchResults);
    }
}