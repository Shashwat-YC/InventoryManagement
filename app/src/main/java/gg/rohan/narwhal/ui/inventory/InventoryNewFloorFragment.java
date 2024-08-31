package gg.rohan.narwhal.ui.inventory;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gg.rohan.narwhal.adapter.InventorySecondHomeAdapter;
import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.inventory.MachineBox;
import gg.rohan.narwhal.newmodel.InventoryRoom;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryNewFloorFragment extends Fragment implements SearchManager.SearchListener, InventorySecondHomeAdapter.OnItemUpdateListener {


    ExtendedFloatingActionButton mAddFab;
    RecyclerView recyclerView;
    private String model;
    private String machineName;
    private List<MachineBox> machineBoxList;
    private InventorySecondHomeAdapter inventorySecondHomeAdapter;
    private int selectedAreaOptionId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve id from arguments
        if (getArguments() != null) {
            model = getArguments().getString("model");
            machineName = getArguments().getString("machineName");
        }

        machineBoxList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory_new_floor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddFab = view.findViewById(R.id.fab_add);
        mAddFab.setOnClickListener(v -> showBottomSheet(null, "add"));
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setNestedScrollingEnabled(false);
        inventorySecondHomeAdapter = new InventorySecondHomeAdapter(getContext(), new ArrayList<>(), machineName, model, this);
        recyclerView.setAdapter(inventorySecondHomeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        getAllAvailableLocations();
    }

    private void getAllAvailableLocations() {
        Call<List<MachineBox>> machinebox = NewRetrofitClient.getInventoryServiceAdapter().getMachineLocations(model);
        machinebox.enqueue(new Callback<List<MachineBox>>() {
            @Override
            public void onResponse(@NonNull Call<List<MachineBox>> listCall, @NonNull Response<List<MachineBox>> response) {
                if (response.isSuccessful()) {
                    machineBoxList.clear();
                    inventorySecondHomeAdapter.addAll(response.body());
                    machineBoxList.addAll(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineBox>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });

    }

    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    public void performSearch(String query) {
        List<MachineBox> machinebox = new ArrayList<>();
        if (!query.isEmpty()) {

            for (MachineBox machineBox : machineBoxList) {
                if (machineBox.getRoomName().contains(query)) {
                    machinebox.add(machineBox);
                }
            }

        } else {
            machinebox.addAll(machineBoxList);
        }
        inventorySecondHomeAdapter.filterList(machinebox);
    }

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    private void showBottomSheet(MachineBox machineBox, String action) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.add_part_location_bottom_sheet_layout, null);
        AppCompatButton saveButton = bottomSheetView.findViewById(R.id.btn_save_location);
        EditText floorEditText = bottomSheetView.findViewById(R.id.et_floor_value);
        EditText areaEditText = bottomSheetView.findViewById(R.id.et_area_value);
        EditText rackEditText = bottomSheetView.findViewById(R.id.et_rack_value);
        EditText shelfEditText = bottomSheetView.findViewById(R.id.et_shelf_value);
        TextView title = bottomSheetView.findViewById(R.id.filters);
        TextView reqInfo = bottomSheetView.findViewById(R.id.req_info);

        areaEditText.setFocusable(false);
        areaEditText.setClickable(false);
        int predefinedFloor = floorEditText.getText().toString().isEmpty() ? 0 : Integer.parseInt(floorEditText.getText().toString());
        areaEditText.setOnClickListener(v -> fetchAreaOptions(floorEditText, areaEditText, predefinedFloor));
        if (action.equals("update")) {
            if (machineBox != null) {
                floorEditText.setText(String.valueOf(machineBox.getFloor()));
                areaEditText.setText(machineBox.getRoomName());
                rackEditText.setText(String.valueOf(machineBox.getRack()));
                shelfEditText.setText(String.valueOf(machineBox.getShelf()));
                selectedAreaOptionId = machineBox.getRoom();
            }
            saveButton.setText("Update Location");
            title.setText("Edit Location");
            reqInfo.setText("Click to edit the specific area");
        }
        saveButton.setOnClickListener(v -> {
            if (selectedAreaOptionId == -1) {
                Toast.makeText(requireContext(), "Please select an area", Toast.LENGTH_SHORT).show();
                return;
            }

            String rack = rackEditText.getText().toString();
            String shelf = shelfEditText.getText().toString();
            Integer rackInt = rack.isEmpty() ? null : Integer.parseInt(rack);
            Integer shelfInt = shelf.isEmpty() ? null : Integer.parseInt(shelf);

            int newFloor = Integer.parseInt(floorEditText.getText().toString());
            if (action.equals("update")) {
                Map<String, Object> requestBody = new HashMap<>();
                assert machineBox != null;
                requestBody.put("id", machineBox.getId());
                requestBody.put("floor", newFloor);
                requestBody.put("room", selectedAreaOptionId);
                requestBody.put("rack", rack);
                requestBody.put("shelf", shelf);

                Call<Void> updateBoxCall = NewRetrofitClient.getInventoryServiceAdapter().updateLinkedLocation(
                        model,
                        machineBox.getId(),
                        selectedAreaOptionId,
                        rackInt,
                        shelfInt
                );
                updateBoxCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            machineBox.setFloor(newFloor);
                            machineBox.setRoom(selectedAreaOptionId);
                            machineBox.setRack(rackInt);
                            machineBox.setShelf(shelfInt);
                            machineBox.setRoomName(areaEditText.getText().toString());
                           // update the box in the list
                            inventorySecondHomeAdapter.notifyDataSetChanged();
                            Toast.makeText(requireContext(), "Box updated successfully", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update box", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), "Failed to update box", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });

            } else if (action.equals("add")) {
                Call<Integer> addBoxCall = NewRetrofitClient.getInventoryServiceAdapter().addLinkedLocation(model, selectedAreaOptionId, rackInt, shelfInt);
                addBoxCall.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                        if (response.isSuccessful()) {
                            // add new box to the list
                            MachineBox machineBox = new MachineBox();
                            machineBox.setRoom(selectedAreaOptionId);
                            machineBox.setFloor(newFloor);
                            machineBox.setRack(rackInt);
                            machineBox.setShelf(shelfInt);
                            machineBox.setRoomName(areaEditText.getText().toString());
                            machineBox.setId(response.body());
                            machineBoxList.add(machineBox);
                            inventorySecondHomeAdapter.add(machineBox);
                            Toast.makeText(requireContext(), "Box added successfully", Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.dismiss();
                        } else {
                            Toast.makeText(requireContext(), "Failed to add box", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), "Failed to add box", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void fetchAreaOptions(EditText floorEt, EditText areaEditText, int predefinedFloor) {
        if (predefinedFloor != 0) {
            floorEt.setText(String.valueOf(predefinedFloor));
        }
        String floorValue = floorEt.getText().toString();
        // check if floor is empty
        if (floorValue.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a floor number", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<List<InventoryRoom>> fetchAreaOptionsCall = NewRetrofitClient.getInventoryServiceAdapter().getRooms(Integer.parseInt(floorValue));
        fetchAreaOptionsCall.enqueue(new Callback<List<InventoryRoom>>() {
            @Override
            public void onResponse(@NonNull Call<List<InventoryRoom>> call, @NonNull Response<List<InventoryRoom>> response) {
                if (response.isSuccessful()) {
                    List<InventoryRoom> areaOptions = response.body();
                    if (areaOptions != null && !areaOptions.isEmpty()) {
                        // Initialize a PopupMenu
                        PopupMenu popupMenu = new PopupMenu(requireContext(), areaEditText);

                        // Set to keep track of unique IDs or names
                        Set<Integer> uniqueIds = new HashSet<>();
                        Set<String> uniqueNames = new HashSet<>();

                        // Add options to the PopupMenu while filtering repetitive items
                        for (InventoryRoom roomoption : areaOptions) {
                            if (!uniqueIds.contains(roomoption.getId()) && !uniqueNames.contains(roomoption.getName())) {
                                popupMenu.getMenu().add(0, roomoption.getId(), 0, roomoption.getName());
                                uniqueIds.add(roomoption.getId());
                                uniqueNames.add(roomoption.getName());
                            }
                        }

                        // Set item click listener
                        popupMenu.setOnMenuItemClickListener(item -> {
                            selectedAreaOptionId = item.getItemId(); // Store the selected option ID
                            areaEditText.setText(item.getTitle());
                            return true;
                        });
                        // Show the PopupMenu
                        popupMenu.show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch area options", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<InventoryRoom>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Failed to fetch area options", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onUpdate(MachineBox machineBox, String action) {
        showBottomSheet(machineBox, action);
    }

}