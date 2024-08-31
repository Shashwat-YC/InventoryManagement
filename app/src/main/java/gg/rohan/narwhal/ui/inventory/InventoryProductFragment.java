package gg.rohan.narwhal.ui.inventory;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gg.rohan.narwhal.adapter.InventoryProductAdapter;
import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.inventory.MachineParts;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryProductFragment extends Fragment implements SearchManager.SearchListener{
    FloatingActionButton mAddPartFab, mAddSearchFab;
    ExtendedFloatingActionButton mAddFab;
    private int  boxId, floor, rack, shelf, roomId,locationId;

    private String machineName, room,model;
    private List<MachineParts> packetsFromBoxList;
    Boolean isAllFabsVisible;
    InventoryProductAdapter inventoryProductAdapter;
    TextView mMachineName, mMachineSubName;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            model = getArguments().getString("model");
            boxId = getArguments().getInt("boxId");
            machineName = getArguments().getString("machineName");
            locationId = getArguments().getInt("locationId");
            floor = getArguments().getInt("floor");
            room = getArguments().getString("room");
            rack = getArguments().getInt("rack");
            shelf = getArguments().getInt("shelf");
            roomId = getArguments().getInt("roomId");
        }
        packetsFromBoxList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddFab = view.findViewById(R.id.add_fab);
        mAddPartFab = view.findViewById(R.id.add_part_fab);
        mAddSearchFab = view.findViewById(R.id.search_fab);

        mAddPartFab.setVisibility(View.GONE);
        mAddSearchFab.setVisibility(View.GONE);
        isAllFabsVisible = false;
        mAddFab.extend();
        mAddFab.setOnClickListener(v -> handleFabClick());
        mAddSearchFab.setOnClickListener(v -> openInventoryStockReconcilationFragment());
        mAddPartFab.setOnClickListener(v -> openInventoryAddPartFirstFragment());
        mMachineName = view.findViewById(R.id.tv_pms_item_heading);
        mMachineName.setText(machineName);
        mMachineSubName = view.findViewById(R.id.tv_pms_item_sub_heading);
        mMachineSubName.setText("N/A");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setNestedScrollingEnabled(false);

        // Initialize adapter with grouped items
        inventoryProductAdapter = new InventoryProductAdapter(getContext(), groupItemsByCode());
        recyclerView.setAdapter(inventoryProductAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Fetch items from API and update adapter
        fetchItemsAndUpdateAdapter();
    }

    // Method to fetch items from API and update adapter
    private void fetchItemsAndUpdateAdapter() {
        Call<List<MachineParts>> packetsFromBoxs = NewRetrofitClient.getInventoryServiceAdapter().getLocationPacketsByLocation(String.valueOf(locationId), model);
        packetsFromBoxs.enqueue(new Callback<List<MachineParts>>() {
            @Override
            public void onResponse(@NonNull Call<List<MachineParts>> listCall, @NonNull Response<List<MachineParts>> response) {
                if (response.isSuccessful()) {
                    packetsFromBoxList.clear();
                    packetsFromBoxList.addAll(response.body());
                    // Notify adapter about the data change on the UI thread
                    requireActivity().runOnUiThread(() -> inventoryProductAdapter.addAll(groupItemsByCode()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineParts>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // Method to group items by code
    private Map<String, List<MachineParts>> groupItemsByCode() {
        Map<String, List<MachineParts>> groupedItems = new HashMap<>();
        for (MachineParts item : packetsFromBoxList) {
            String code = item.getPartInfo().getCode();
            List<MachineParts> group = groupedItems.getOrDefault(code, new ArrayList<>());
            group.add(item);
            groupedItems.put(code, group);
        }
        return groupedItems;
    }

    private void openInventoryAddPartFirstFragment() {
        InventoryAddPartFirstFragment inventoryAddPartFirstFragment = new InventoryAddPartFirstFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("boxId", boxId);
        bundle.putInt("locationId", locationId);
        bundle.putString("model", model);
        bundle.putString("machineName", machineName);
        bundle.putInt("floor", floor);
        bundle.putString("room", room);
        bundle.putInt("rack", rack);
        bundle.putInt("shelf", shelf);
        bundle.putInt("roomId", roomId);
        inventoryAddPartFirstFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.child_fragment_container, inventoryAddPartFirstFragment).addToBackStack(null).commit();
    }

    private void openInventoryStockReconcilationFragment() {
        StockReconcilationFragment stockReconcilationFragment = new StockReconcilationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("floor", floor);
        bundle.putString("room", room);
        bundle.putInt("rack", rack);
        bundle.putInt("shelf", shelf);
        bundle.putInt("roomId", roomId);
        bundle.putInt("boxId", boxId);
        bundle.putInt("locationId", locationId);
        bundle.putString("model", model);
        bundle.putString("machineName", machineName);

        stockReconcilationFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.child_fragment_container, stockReconcilationFragment)
                .addToBackStack(null).commit();
    }

    private void handleFabClick() {
        if (!isAllFabsVisible) {
            mAddPartFab.setSupportImageTintList(ColorStateList.valueOf(Color.WHITE));
            mAddPartFab.show();
            mAddSearchFab.setSupportImageTintList(ColorStateList.valueOf(Color.WHITE));
            mAddSearchFab.show();
            mAddFab.shrink();
            mAddFab.setIconResource(R.drawable.cross_svgrepo_com);
            mAddFab.setIconTint(ColorStateList.valueOf(Color.WHITE));
            mAddFab.setBackgroundColor(getResources().getColor(R.color.fab_close_bg));
            isAllFabsVisible = true;
        } else {
            mAddPartFab.hide();
            mAddSearchFab.hide();
            mAddFab.extend();
            mAddFab.setIconResource(R.drawable.ic_bookmark_manager);
            mAddFab.setIconTint(ColorStateList.valueOf(Color.WHITE));
            mAddFab.setBackgroundColor(getResources().getColor(R.color.fab_progress_color));
            isAllFabsVisible = false;
        }
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showBottomSheet() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_dialog, null);
        ProgressBar progressBar = bottomSheetView.findViewById(R.id.progress_bar);
        AppCompatButton saveButton = bottomSheetView.findViewById(R.id.save_button);
        LinearLayout checkoutQtylayout = bottomSheetView.findViewById(R.id.checkout_qty_layout);
        checkoutQtylayout.setVisibility(View.GONE);
        LinearLayout locationLayout = bottomSheetView.findViewById(R.id.location_layout);
        locationLayout.setVisibility(View.GONE);
        LinearLayout floorRoomRackLayout = bottomSheetView.findViewById(R.id.floor_room_rack_lay);
        floorRoomRackLayout.setVisibility(View.GONE);
        TextView scannedTv = bottomSheetView.findViewById(R.id.scanned_qty_layout);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    public void performSearch(String query) {
        if (TextUtils.isEmpty(query)) {
            inventoryProductAdapter.addAll(groupItemsByCode());
            return;
        }
        Map<String, List<MachineParts>> groupedItems = groupItemsByCode();
        Map<String, List<MachineParts>> filteredItems = new HashMap<>();
        for (Map.Entry<String, List<MachineParts>> entry : groupedItems.entrySet()) {
            String code = entry.getKey();
            List<MachineParts> items = entry.getValue();
            List<MachineParts> filteredItemsList = new ArrayList<>();
            for (MachineParts item : items) {
                if (item.getPartInfo().getName().toLowerCase().contains(query.toLowerCase()) || item.getPartInfo().getPartNo().toLowerCase().contains(query.toLowerCase())) {
                    filteredItemsList.add(item);
                }
            }
            if (!filteredItemsList.isEmpty()) {
                filteredItems.put(code, filteredItemsList);
            }
        }
        inventoryProductAdapter.addAll(filteredItems);
    }

}