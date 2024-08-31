package gg.rohan.narwhal.ui.inventory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import gg.rohan.narwhal.adapter.InventoryHomeAdapter;
import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.MachineInfo;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DynamicInventoryTabFragment extends Fragment implements SearchManager.SearchListener {

    private String tabTitle;
    InventoryHomeAdapter inventoryHomeAdapter;
    RecyclerView recyclerView;
    int floor = -1;
    private List<MachineInfo> machineList;
//    private List<Machine> filteredMachineList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve title from arguments
        if (getArguments() != null) {
            tabTitle = getArguments().getString("tabTitle");
        }
        machineList = new ArrayList<>();
//        filteredMachineList = new ArrayList<>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dynamic_inventory_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Retrieve tab title passed from InventoryFragment
        TextView checkChildPage = view.findViewById(R.id.check_child_page);
        checkChildPage.setText("Open "+tabTitle+" Item");
        // on click open InventorySecondHomeFragment in nav_host_fragment
        // checkChildPage.setOnClickListener(v -> openInventorySecondHomeFragment());

        if (tabTitle.equals("All")) {
            floor = -1;
        } else if (tabTitle.equals("1st Floor")) {
            floor = 1;
        } else if (tabTitle.equals("2nd Floor")) {
            floor = 2;
        } else if (tabTitle.equals("3rd Floor")) {
            floor = 3;
        }
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setNestedScrollingEnabled(false);
        inventoryHomeAdapter = new InventoryHomeAdapter(getContext(), new ArrayList<>(), floor);
        recyclerView.setAdapter(inventoryHomeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadMachineData();
    }

    private void loadMachineData() {
//        tabTitles.add("1st Floor");
//        tabTitles.add("2nd Floor");
//        tabTitles.add("3rd Floor");

        Call<List<MachineInfo>> machines = NewRetrofitClient.getInventoryServiceAdapter().getMachines(floor);
        machines.enqueue(new Callback<List<MachineInfo>>() {
            @Override
            public void onResponse(@NonNull Call<List<MachineInfo>> call, @NonNull Response<List<MachineInfo>> response) {
                if (response.isSuccessful()) {
                    machineList.clear(); // Clear existing data
                    machineList.addAll(response.body());
                    inventoryHomeAdapter.addAll(response.body());
//                    filteredMachineList.clear();
//                    filteredMachineList.addAll(machineList);
//                    inventoryHomeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineInfo>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });


    }

    private void openInventorySecondHomeFragment() {

        InventoryNewFloorFragment inventoryNewFloorFragment = new InventoryNewFloorFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tabTitle", tabTitle);
        inventoryNewFloorFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.child_fragment_container, inventoryNewFloorFragment).addToBackStack(null).commit();
    }
    public void setSearchQuery(String query) {
//        if (isVisible()) {
//            onSearch(query);
//        }
    }

    public void performSearch(String query) {
//        Log.d("Mess", "Working");
//        Log.d("TAG", "onSearch: " + query);
        List<MachineInfo> filteredList = new ArrayList<>();
        if (!TextUtils.isEmpty(query)) {
            for (MachineInfo machine : machineList) {
//                Log.d("TAG", "performSearch: " + machine.getName().toLowerCase() + " " + query.toLowerCase());
                if (machine.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(machine);
                }
            }
        } else {
            // If the search query is empty, show all items
            filteredList.addAll(machineList);
        }
        inventoryHomeAdapter.filterList(filteredList);
    }

    @Override
    public void onSearch(String query) {
        Log.d("Mess","Working");
        Log.d("TAG", "onSearch: "+query);
        performSearch(query);
    }
}