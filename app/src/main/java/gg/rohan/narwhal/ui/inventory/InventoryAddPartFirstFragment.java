package gg.rohan.narwhal.ui.inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import gg.rohan.narwhal.adapter.AddPartAdapter;
import gg.rohan.narwhal.adapter.AddSparePartSearchAdapter;
import gg.rohan.narwhal.adapter.NewCheckboxAdapter;
import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.SparePartInfo;
import gg.rohan.narwhal.newmodel.inventory.MachineParts;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryAddPartFirstFragment extends Fragment implements AddPartAdapter.OnItemClickListener, SearchManager.SearchListener {

    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private AddPartAdapter addPartAdapter;
    private int locationId, boxId, floor, rack, shelf, roomId;
    private int selectedAreaOptionId = -1;
    //    List<PacketsFromBox.Packet> packetsFromBoxsList;
    List<MachineParts> packetsFromBoxsList;
    private Map<String, String> productMap = new HashMap<>();
    private String machineName, room, model;
    RecyclerView recyclerView;
    final String[] selectedPartNo = {""};
    final String[] selectedProductCode = {""};
    final String[] partName = {""};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve id from arguments
        if (getArguments() != null) {
            locationId = getArguments().getInt("locationId");
            boxId = getArguments().getInt("boxId");
            model = getArguments().getString("model");
            machineName = getArguments().getString("machineName");
            floor = getArguments().getInt("floor");
            room = getArguments().getString("room");
            rack = getArguments().getInt("rack");
            shelf = getArguments().getInt("shelf");
            roomId = getArguments().getInt("roomId");
        }
        packetsFromBoxsList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory_add_part_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        TextView checkChildPage = view.findViewById(R.id.no_data_found);
//        checkChildPage.setText("Add Spare Part");

        TextView mMachineName = view.findViewById(R.id.tv_item_heading);
        mMachineName.setText(machineName);

        TextView mMachineSubName = view.findViewById(R.id.tv_item_sub_heading);
        mMachineSubName.setText("N/A");

        ExtendedFloatingActionButton mAddFab = view.findViewById(R.id.fab_add);
        mAddFab.setOnClickListener(v -> openAddSparePartBottomSheet("add", null, 0));

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setNestedScrollingEnabled(false);

        addPartAdapter = new AddPartAdapter(new ArrayList<>(), requireContext(), locationId, boxId, machineName, floor, room, rack, shelf, this);
        recyclerView.setAdapter(addPartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fetchPacketsFromBox();

    }

    @Override
    public void onItemClick(MachineParts packet, int position) {
        openAddSparePartBottomSheet("update", packet, position);
    }

    private void fetchPacketsFromBox() {
        Call<List<MachineParts>> packetsFromBoxes = NewRetrofitClient.getInventoryServiceAdapter().getLocationPacketsByLocation(String.valueOf(locationId), model);
        packetsFromBoxes.enqueue(new Callback<List<MachineParts>>() {
            @Override
            public void onResponse(@NonNull Call<List<MachineParts>> call, @NonNull Response<List<MachineParts>> response) {
                if (response.isSuccessful()) {

                    List<MachineParts> packets = response.body();
                    if (packets != null && !packets.isEmpty()) {
                        addPartAdapter.addAll(packets);
                    } else {
                        Toast.makeText(requireContext(), "No packets found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch packets", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineParts>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private int generateEightDigitRandomNumber() {
        return (int) (Math.random() * 100000000);
    }


    private void callAddPacketApi(String type, String partNo, int quantity, String selectedType, int locationId, int boxId, String partName, int itemPosition, String productCode, int oldTagId, int newTagId) {

        if (type.equals("add")) {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", newTagId);
            requestBody.put("partCode", productCode);
            requestBody.put("quantity", quantity);
            requestBody.put("type", selectedType);
            requestBody.put("locationId", locationId);

            Call<Void> addPacketCall = NewRetrofitClient.getInventoryServiceAdapter().assignPacket(requestBody);
            addPacketCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Add the item to the adapter
                        MachineParts packet = new MachineParts();
                        MachineParts.PartInfo partInfo = new MachineParts.PartInfo();
                        partInfo.setName(partName);
                        partInfo.setPartNo(partNo);
                        packet.setPartInfo(partInfo);
                        packet.setQuantity(quantity);
                        packet.setType(selectedType);
                        packet.setId(newTagId);

                        addPartAdapter.addItem(packet);

                        Toast.makeText(requireContext(), "Spare Part Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to add Spare Part", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Failed to add Spare Part", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        } else {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", oldTagId);
            requestBody.put("quantity", quantity);
            requestBody.put("type", selectedType);
            requestBody.put("locationId", locationId);
            if (oldTagId != newTagId) requestBody.put("newId", newTagId);

            Call<Void> updatePacketCall = NewRetrofitClient.getInventoryServiceAdapter().updatePacket(requestBody);
            updatePacketCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Spare Part Updated", Toast.LENGTH_SHORT).show();
                        // Update the item in the adapter
                        MachineParts packet = new MachineParts();
                        MachineParts.PartInfo partInfo = new MachineParts.PartInfo();
                        partInfo.setName(partName);
                        partInfo.setPartNo(partNo);
                        packet.setPartInfo(partInfo);
                        packet.setQuantity(quantity);
                        packet.setType(selectedType);
                        addPartAdapter.updateItem(itemPosition, packet);
                    } else {
                        Toast.makeText(requireContext(), "Failed to update Spare Part", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(requireContext(), "Failed to update Spare Part", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        }
    }


    @SuppressLint("SetTextI18n")
    private void openAddSparePartBottomSheet(String type, MachineParts packet, int itemPosition) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.add_spare_part_bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        final int[] newRoomId = new int[1];
        autoCompleteTextView = bottomSheetDialog.findViewById(R.id.actv_spare_part);
        EditText tagEditText = bottomSheetView.findViewById(R.id.et_tag_id);
        EditText quantityEditText = bottomSheetView.findViewById(R.id.et_quantity);
        EditText boxNameEditText = bottomSheetView.findViewById(R.id.et_box_name);
        EditText floorEditText = bottomSheetView.findViewById(R.id.tv_floor_value);
        EditText areaEditText = bottomSheetView.findViewById(R.id.tv_area_value);
        EditText rackEditText = bottomSheetView.findViewById(R.id.tv_rack_value);
        EditText shelfEditText = bottomSheetView.findViewById(R.id.tv_shelf_value);
        AppCompatButton addSparePartButton = bottomSheetView.findViewById(R.id.btn_add_part);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) AppCompatButton cancelButton = bottomSheetView.findViewById(R.id.btn_clear);
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        List<String> selectedOptions = new ArrayList<>();
        areaEditText.setFocusable(false);
        areaEditText.setClickable(false);
        // Initialize the adapter with an empty list
//        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_expandable_list_item_2, new ArrayList<>());
//        autoCompleteTextView.setAdapter(adapter);

        // Set threshold for minimum number of characters before suggestions appear
//        autoCompleteTextView.setThreshold(1);

        // Set listener for item click event
        autoCompleteTextView.setOnClickListener(v -> showProductSuggestions());

        RecyclerView checkboxRecyclerView = bottomSheetView.findViewById(R.id.checkbox_recycler_view);
        List<String> checkboxOptions = Arrays.asList("RECONDITIONED","NEW","OLD"); //  list of options
        NewCheckboxAdapter checkboxAdapter = new NewCheckboxAdapter(checkboxOptions); // Use NewCheckboxAdapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        checkboxRecyclerView.setLayoutManager(layoutManager);
        int spanCount = 3; // Number of columns
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int itemWidth = screenWidth / spanCount;
        checkboxAdapter.setItemWidth(itemWidth);
        checkboxRecyclerView.setAdapter(checkboxAdapter);
        checkboxAdapter.setOnCheckedChangeListener((position, isChecked) -> {
            if (isChecked) {
                selectedOptions.clear(); // Clear the list when a new option is selected
                selectedOptions.add(checkboxOptions.get(position));
            }
        });
        int oldTagId;
        if (type.equals("update")) {
            oldTagId = packet.getId();
            autoCompleteTextView.setText(packet.getPartInfo().getName());
            tagEditText.setText(String.valueOf(packet.getId()));
            quantityEditText.setText(String.valueOf(packet.getQuantity()));
            boxNameEditText.setText(packet.getType());
            floorEditText.setText(String.valueOf(floor));
            areaEditText.setText(String.valueOf(room));
            rackEditText.setText(String.valueOf(rack));
            shelfEditText.setText(String.valueOf(shelf));
            selectedPartNo[0] = packet.getPartInfo().getPartNo();
            selectedAreaOptionId = roomId;
            if (packet.getId() == null){
                Toast.makeText(requireContext(), "Refresh Page", Toast.LENGTH_SHORT).show();
                return;
            }
//
            // Set the selected option
            if (packet.getType().equals("Reconditioned") || packet.getType().equals("reconditioned") || packet.getType().equals("RECONDITIONED")) {
                checkboxAdapter.setChecked(0, true);
                selectedOptions.add("RECONDITIONED");

            } else if (packet.getType().equals("New") || packet.getType().equals("new") || packet.getType().equals("NEW")) {
                checkboxAdapter.setChecked(1, true);
                selectedOptions.add("NEW");
            } else if (packet.getType().equals("Old") || packet.getType().equals("old") || packet.getType().equals("OLD")) {
                checkboxAdapter.setChecked(2, true);
                selectedOptions.add("OLD");
            }
            addSparePartButton.setText("Update Part");

        } else {
            oldTagId = -1;
        }

        addSparePartButton.setOnClickListener(v -> {
            String tagId = tagEditText.getText().toString();
            String quantity = quantityEditText.getText().toString();
            String boxName = boxNameEditText.getText().toString();
            String floor = floorEditText.getText().toString();
            String area = areaEditText.getText().toString();
            String rack = rackEditText.getText().toString();
            String shelf = shelfEditText.getText().toString();
            int tagIdInt = Integer.parseInt(tagId);
            if (tagId.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (selectedOptions.isEmpty()) {
                    Toast.makeText(requireContext(), "Please select a condition", Toast.LENGTH_SHORT).show();
                    return;
                }
                String selectedType = selectedOptions.get(0).toString();
                int qty = Integer.parseInt(quantity);
                String partNo = selectedPartNo[0];
//                String partName = autoCompleteTextView.getText().toString();
                String productCode = selectedProductCode[0];
//                Log.d("a","openAddSparePartBottomSheet: "+productId);
//                Toast.makeText(requireContext(), "openAddSparePartBottomSheet: "+String.valueOf(productId), Toast.LENGTH_SHORT).show();
                if (type.equals("add")) {
                    callAddPacketApi("add", partNo, qty, selectedType, locationId, boxId, partName[0], 0, productCode, oldTagId, tagIdInt);
                } else if (type.equals("update")) {
                    callAddPacketApi("update", partNo, qty, selectedType, locationId, boxId, partName[0], itemPosition, productCode, oldTagId, tagIdInt);
                }
                bottomSheetDialog.dismiss();
            }
        });

//        fetchProductSuggestions(); // Fetch product suggestions after initializing the adapter

        bottomSheetDialog.show();

    }

    private AlertDialog alertDialog; // Declare alertDialog as a class member

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showProductSuggestions() {
        // Initialize and set the adapter with suggestions
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.search_pop_up, null);
        builder.setView(dialogView);

        EditText searchEditText = dialogView.findViewById(R.id.searchInput);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view);
        ShapeableImageView searchIcon = dialogView.findViewById(R.id.search_img);
        ConstraintLayout closeButton = dialogView.findViewById(R.id.close_btn_layout);

        AddSparePartSearchAdapter addSparePartSearchAdapter = new AddSparePartSearchAdapter(requireContext(), new ArrayList<>(), product -> {
            autoCompleteTextView.setText(product.getName());
            selectedProductCode[0] = product.getCode();
            selectedPartNo[0] = product.getPartNo();
            partName[0] = product.getName();
            dialogView.findViewById(R.id.searchInput).clearFocus();
            alertDialog.dismiss(); // Dismiss the dialog after an item is selected
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(addSparePartSearchAdapter);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchQuery = searchEditText.getText().toString();
                if (!searchQuery.isEmpty()) {
                    // Filter the list based on the search query
                    addSparePartSearchAdapter.getFilter().filter(searchQuery);
                } else {
                    addSparePartSearchAdapter.clearFilter();
                }
                return true;
            }
            return false;
        });

        closeButton.setOnClickListener(v -> alertDialog.dismiss());

        Call<List<SparePartInfo>> fetchProductsCall = NewRetrofitClient.getInventoryServiceAdapter().getMachineParts(model);
        fetchProductsCall.enqueue(new Callback<List<SparePartInfo>>() {
            @Override
            public void onResponse(@NonNull Call<List<SparePartInfo>> call, @NonNull Response<List<SparePartInfo>> response) {
                if (response.isSuccessful()) {
                    List<SparePartInfo> productList = response.body();
                    if (productList != null && !productList.isEmpty()) {
                        addSparePartSearchAdapter.updateData(productList);
                    } else {
                        Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SparePartInfo>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
        searchIcon.setOnClickListener(v -> {
            String searchQuery = searchEditText.getText().toString();
            if (!searchQuery.isEmpty()) {
                // Filter the list based on the search query
                addSparePartSearchAdapter.getFilter().filter(searchQuery);
            } else {
                addSparePartSearchAdapter.clearFilter();
            }
        });

        alertDialog = builder.create(); // Initialize alertDialog
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

//    private void fetchProductSuggestions() {
//        Call<List<Product>> fetchProductsCall = RetrofitClient.getInventoryAdapter().fetchProducts(machineId);
//        fetchProductsCall.enqueue(new Callback<List<Product>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
//                if (response.isSuccessful()) {
//                    List<Product> productList = response.body();
//                    if (productList != null && !productList.isEmpty()) {
//                        // Clear the product map
//                        productMap.clear();
//                        // Extract product names for suggestions and map them to their IDs
//                        List<String> productSuggestions = new ArrayList<>();
//                        for (Product product : productList) {
//                            if (product.getMaterialDesc() != null) {
//                                String productName = product.getMaterialDesc();
//                                String productId = String.valueOf(product.getId());
//                                productSuggestions.add(productName); // Add product name to suggestions list
//                                partName[0] = productName;
//                                productMap.put(productName, productId); // Map product name to ID
//                            }
//                        }
//                        // Initialize and set the adapter with suggestions
//                        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, productSuggestions);
//                        autoCompleteTextView.setAdapter(adapter);
//                    }
//                } else {
//                    Toast.makeText(requireContext(), "Failed to fetch product suggestions", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
//                Toast.makeText(requireContext(), "Failed to fetch product suggestions", Toast.LENGTH_SHORT).show();
//                t.printStackTrace();
//            }
//        });
//    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = (String) parent.getItemAtPosition(position);
        autoCompleteTextView.setText(selectedItem);
        // Retrieve the selected product ID from the map
        String productId = productMap.get(selectedItem);
        Log.d("a", "onItemClick: " + productId);
        if (productId != null) {
            selectedPartNo[0] = productId; // set selected product ID
        }
    }

    private void openStockFragment() {
        StockReconcilationFragment stockFragment = new StockReconcilationFragment();
        getParentFragmentManager().beginTransaction().replace(R.id.child_fragment_container, stockFragment).addToBackStack(null).commit();
    }

    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    public void performSearch(String query) {
        List<MachineParts> filteredList = new ArrayList<>();
        if (!query.isEmpty()) {
            for (MachineParts packet : packetsFromBoxsList) {
                if (packet.getPartInfo().getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(packet);
                }
            }
        } else {
            // If the search query is empty, show all items
            filteredList.addAll(packetsFromBoxsList);
        }
        addPartAdapter.filterList(filteredList);
    }


}