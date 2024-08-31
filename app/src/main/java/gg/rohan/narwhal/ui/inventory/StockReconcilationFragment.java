package gg.rohan.narwhal.ui.inventory;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.model.inventory.PacketsFromBox;
import gg.rohan.narwhal.newmodel.inventory.MachineParts;
import gg.rohan.narwhal.rfid.ReaderStaticWrapper;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StockReconcilationFragment extends Fragment implements SearchManager.SearchListener{

    FloatingActionButton mAddPartFab, mAddTaskFab;
    ExtendedFloatingActionButton mAddFab;

    boolean isAllFabsVisible = false;

    private Pair<UUID, UUID> autoInventoryHook;
    private Pair<UUID, UUID> triggerHook;
    private UUID inventoryListener;
    private final List<String> scannedTags = new ArrayList<>();
    private StockReconcilationAdapter stockReconcilationAdapter;
    List<PacketsFromBox> packetsFromBoxList = new ArrayList<>();
    private int boxId;
    private String machineModel;
    private int floor;
    private String room;
    private int rack;
    private int shelf;
    private int roomId;
    private String machineName;
    private int locationId;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            machineModel = args.getString("model");
            boxId = args.getInt("boxId");
            floor = args.getInt("floor");
            room = args.getString("room");
            rack = args.getInt("rack");
            shelf = args.getInt("shelf");
            roomId = args.getInt("roomId");
            machineName = args.getString("machineName");
            locationId = args.getInt("locationId");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ReaderStaticWrapper.clearCurrentTasks();
        ReaderStaticWrapper.reInstallReader(getContext());
        return inflater.inflate(R.layout.fragment_stock_reconcilation, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        removeReader();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupReader();

        TextView heading = view.findViewById(R.id.tv_pms_item_heading);
        heading.setText(machineName);
        view.findViewById(R.id.tv_pms_item_sub_heading).setVisibility(View.GONE);

        mAddFab = view.findViewById(R.id.add_fab);
        mAddPartFab = view.findViewById(R.id.add_part_fab);
        mAddTaskFab = view.findViewById(R.id.task_fab);
        mAddPartFab.setVisibility(View.GONE);
        mAddTaskFab.setVisibility(View.GONE);
        isAllFabsVisible = false;
        mAddFab.extend();
        mAddFab.setOnClickListener(v-> handleFabClick());
        mAddTaskFab.setOnClickListener(v -> Toast.makeText(requireContext(), "Task Fab Clicked", Toast.LENGTH_SHORT).show());
        mAddPartFab.setOnClickListener(v -> openInventoryAddPartFirstFragment());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        stockReconcilationAdapter = new StockReconcilationAdapter(new ArrayList<>());
        recyclerView.setAdapter(stockReconcilationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadProductsFromApi();
    }
    private void openInventoryAddPartFirstFragment() {
        InventoryAddPartFirstFragment inventoryAddPartFirstFragment = new InventoryAddPartFirstFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("boxId", boxId);
        bundle.putInt("locationId", locationId);
        bundle.putString("model", machineModel);
        bundle.putString("machineName", machineName);
        bundle.putInt("floor", floor);
        bundle.putString("room", room);
        bundle.putInt("rack", rack);
        bundle.putInt("shelf", shelf);
        bundle.putInt("roomId", roomId);

        inventoryAddPartFirstFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.child_fragment_container, inventoryAddPartFirstFragment).addToBackStack(null).commit();
    }
    private void handleFabClick() {
        if (!isAllFabsVisible) {
            mAddPartFab.setSupportImageTintList(ColorStateList.valueOf(Color.WHITE));
            mAddPartFab.show();
            mAddTaskFab.setSupportImageTintList(ColorStateList.valueOf(Color.WHITE));
            mAddTaskFab.show();
            mAddFab.shrink();
            mAddFab.setIconResource(R.drawable.cross_svgrepo_com);
            mAddFab.setIconTint(ColorStateList.valueOf(Color.WHITE));
            mAddFab.setBackgroundColor(getResources().getColor(R.color.fab_close_bg));
            isAllFabsVisible = true;
        } else {
            mAddPartFab.hide();
            mAddTaskFab.hide();
            mAddFab.extend();
            mAddFab.setIconResource(R.drawable.ic_bookmark_manager);
            mAddFab.setIconTint(ColorStateList.valueOf(Color.WHITE));
            mAddFab.setBackgroundColor(getResources().getColor(R.color.fab_progress_color));
            isAllFabsVisible = false;
        }
    }

    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    public void performSearch(String query) {
        List<PacketsFromBox> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(packetsFromBoxList);
        } else {
            for (PacketsFromBox PacketsFromBox : packetsFromBoxList) {
                if (PacketsFromBox.getProduct().getMaterialDesc().toLowerCase().contains(query.toLowerCase()) || PacketsFromBox.getProduct().getPartNo().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(PacketsFromBox);
                }
            }
        }
        stockReconcilationAdapter.filterList(filteredList);
    }

    private class StockReconcilationAdapter extends RecyclerView.Adapter<StockReconcilationAdapter.StockReconcilationViewHolder> {

        private final List<PacketsFromBox> packetsFromBoxes;
        private final List<InventoryProductPacketAdapter> packetAdapters = new ArrayList<>();

        public StockReconcilationAdapter(List<PacketsFromBox> packetsFromBoxes) {
            this.packetsFromBoxes = packetsFromBoxes;
            for (int i = 0; i < packetsFromBoxes.size(); i++) {
                packetAdapters.add(new InventoryProductPacketAdapter(packetsFromBoxes.get(i).getPackets(), packetsFromBoxes.get(i)));
            }
        }

        @NonNull
        @Override
        public StockReconcilationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StockReconcilationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_product_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StockReconcilationViewHolder holder, int position) {
            PacketsFromBox.Product product = packetsFromBoxes.get(position).getProduct();
            holder.productName.setText(product.getMaterialDesc());
            holder.productId.setText(product.getPartNo());
            holder.productItems.setAdapter(packetAdapters.get(position));
            holder.productItems.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        }

        @Override
        public int getItemCount() {
            return packetsFromBoxes.size();
        }

        public void addAll(List<PacketsFromBox> packetsFromBoxes) {
            this.packetsFromBoxes.addAll(packetsFromBoxes);
            for (int i = 0; i < packetsFromBoxes.size(); i++) {
                packetAdapters.add(new InventoryProductPacketAdapter(packetsFromBoxes.get(i).getPackets(), packetsFromBoxes.get(i)));
            }
        }

        public void filterList(List<PacketsFromBox> filteredList) {
            this.packetsFromBoxes.clear();
            this.packetsFromBoxes.addAll(filteredList);
            notifyDataSetChanged();
        }

        public void clear() {
            this.packetsFromBoxes.clear();
            this.packetAdapters.clear();
        }

        private class StockReconcilationViewHolder extends RecyclerView.ViewHolder {

            private final TextView productName;
            private final TextView productId;
            private final RecyclerView productItems;

            public StockReconcilationViewHolder(@NonNull View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.tv_pms_product_name);
                productId = itemView.findViewById(R.id.tv_pms_product_id);
                itemView.findViewById(R.id.mcv_pms_product_id_tag).setVisibility(View.GONE);
                itemView.findViewById(R.id.pms_product_info_layout).setVisibility(View.GONE);
                productItems = itemView.findViewById(R.id.product_items);

            }
        }

        private class InventoryProductPacketAdapter extends RecyclerView.Adapter<InventoryProductPacketAdapter.InventoryProductPacketViewHolder> {

            private final List<PacketsFromBox.Packet> packets = new ArrayList<>();
            private final PacketsFromBox packetsFromBox;

            public InventoryProductPacketAdapter(List<PacketsFromBox.Packet> packets, PacketsFromBox PacketsFromBox) {
                this.packets.addAll(packets);
                this.packetsFromBox = PacketsFromBox;
            }

            @NonNull
            @Override
            public InventoryProductPacketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new InventoryProductPacketViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_job_product_option_button_item, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull InventoryProductPacketViewHolder holder, int position) {
                PacketsFromBox.Packet packet = packets.get(position);
                holder.serialNumber.setText(String.valueOf(packet.getId()));
                holder.robValue.setText(String.valueOf(packetsFromBox.getRob()));
                System.out.println("Resetting the quantity");
                holder.scannedQty.setText(String.valueOf(packet.getQuantity()));
                holder.foundMissing.setText(packet.isFound() ? "Found" : "Missing");
                holder.itemView.setOnClickListener(v -> showBottomSheet(packets.get(position), packetsFromBox, this));
                int bgColor = packet.isFound() ? 0x6600FF00 : 0x66FF0000;
                int strokeColor = packet.isFound() ? 0xFF00FF00 : 0xFFFF0000;
                holder.foundTag.setCardBackgroundColor(bgColor);
                holder.foundTag.setStrokeColor(strokeColor);
                holder.foundTag.setStrokeWidth(2);
            }

            @Override
            public int getItemCount() {
                return packets.size();
            }

            private class InventoryProductPacketViewHolder extends RecyclerView.ViewHolder {

                private final TextView serialNumber;
                private final TextView robValue;
                private final TextView scannedQty;
                private final TextView foundMissing;
                private final MaterialCardView foundTag;

                public InventoryProductPacketViewHolder(@NonNull View itemView) {
                    super(itemView);
                    serialNumber = itemView.findViewById(R.id.tv_inv_product_option_sn);
                    robValue = itemView.findViewById(R.id.tv_inv_product_option_id_value);
                    scannedQty = itemView.findViewById(R.id.tv_inv_product_option_scanned_qty_value);
                    foundMissing = itemView.findViewById(R.id.tv_inv_product_tag_text);
                    foundTag = itemView.findViewById(R.id.tv_inv_product_tag);
                }
            }

        }

    }

    private void setupReader() {
        TextView triggerStatus = getView().findViewById(R.id.tv_trigger_msg);
        autoInventoryHook = ReaderStaticWrapper.autoPerformInventory();
        triggerHook = ReaderStaticWrapper.addTriggerHook(state -> triggerStatus.setText(state ? "Scanning..." : "Press the trigger to start scanning!"));
        inventoryListener = ReaderStaticWrapper.addInventoryHook(msg -> {
            String rfid = msg.getEpc();
            if (!scannedTags.contains(rfid)) {
                scannedTags.add(rfid);
                for (StockReconcilationAdapter.InventoryProductPacketAdapter packetAdapter : stockReconcilationAdapter.packetAdapters) {
                    for (int j = 0; j < packetAdapter.packets.size(); j++) {
                        if (packetAdapter.packets.get(j).getRfid().equals(rfid)) {
                            packetAdapter.packets.get(j).setFound(true);
                            packetAdapter.notifyItemChanged(j);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void removeReader() {
        ReaderStaticWrapper.removeMessageHook(autoInventoryHook.first);
        ReaderStaticWrapper.removeMessageHook(autoInventoryHook.second);
        ReaderStaticWrapper.removeMessageHook(triggerHook.first);
        ReaderStaticWrapper.removeMessageHook(triggerHook.second);
        ReaderStaticWrapper.removeMessageHook(inventoryListener);
    }

    private void showBottomSheet(PacketsFromBox.Packet packet, PacketsFromBox PacketsFromBox, StockReconcilationAdapter.InventoryProductPacketAdapter packetAdapter) {
        removeReader();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_dialog, null);
        ProgressBar progressBar = bottomSheetView.findViewById(R.id.progress_bar);
        AppCompatButton saveButton = bottomSheetView.findViewById(R.id.save_button);

        TextView floorValue = bottomSheetView.findViewById(R.id.text_floor_value);
        floorValue.setText(String.valueOf(floor));
        TextView roomView = bottomSheetView.findViewById(R.id.text_room_value);
        roomView.setText(room);
        TextView rackView = bottomSheetView.findViewById(R.id.text_rack_value);
        rackView.setText(String.valueOf(rack));

        TextView checkoutText = bottomSheetView.findViewById(R.id.text_view5);
        checkoutText.setText("Scanned Quantity: ");

        EditText checkOutQtyText = bottomSheetView.findViewById(R.id.edit_text2);
        if (packet.getUpdateQuantity() > 0) {
            checkOutQtyText.setText(String.valueOf(packet.getUpdateQuantity()));
        }

        saveButton.setOnClickListener(v -> {
            String checkOutString = checkOutQtyText.getText().toString();
            int checkOutQuantity = 0;
            if (!checkOutString.isEmpty()) checkOutQuantity = Integer.parseInt(checkOutString);
            if (packet.getUpdateQuantity() == checkOutQuantity) {
                bottomSheetDialog.dismiss();
                return;
            }

            NewRetrofitClient.getPacketServiceAdapter().updatePacketQuantityFromId(packet.getId(), checkOutQuantity).subscribe((unused) -> {
                getActivity().runOnUiThread(() ->  {
                    stockReconcilationAdapter.clear();
                    packetsFromBoxList.clear();
                    stockReconcilationAdapter.notifyDataSetChanged();
                    loadProductsFromApi();
                });
            }, Throwable::printStackTrace);

            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.scanned_qty_layout).setVisibility(View.GONE);

        Pair<UUID, UUID> pair = ReaderStaticWrapper.autoPerformLocating(packet.getRfid());
        UUID locatingHook = ReaderStaticWrapper.addLocatingHook(locateMessage -> progressBar.setProgress((int) locateMessage.getRssi()));
        bottomSheetDialog.setOnDismissListener(dialog -> {
            ReaderStaticWrapper.stopInventory();
            ReaderStaticWrapper.removeMessageHook(pair.first);
            ReaderStaticWrapper.removeMessageHook(pair.second);
            ReaderStaticWrapper.removeMessageHook(locatingHook);
            setupReader();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void loadProductsFromApi() {

        Call<List<MachineParts>> packetsByLocation = NewRetrofitClient.getInventoryServiceAdapter().getLocationPacketsByLocation(String.valueOf(this.locationId), this.machineModel);
        packetsByLocation.enqueue(new Callback<List<MachineParts>>() {
            @Override
            public void onResponse(Call<List<MachineParts>> _, Response<List<MachineParts>> response) {
                if (response.isSuccessful()) {
                    List<PacketsFromBox> processedPackets = new ArrayList<>();
                    for (MachineParts machineParts : response.body()) {
                        PacketsFromBox packetsFromBox = new PacketsFromBox();
                        PacketsFromBox.Product product = new PacketsFromBox.Product();
                        product.setId(machineParts.getPartInfo().getCode());
                        product.setPartNo(machineParts.getPartInfo().getPartNo());
                        product.setMaterialDesc(machineParts.getPartInfo().getName());
                        packetsFromBox.setRob(machineParts.getPartInfo().getRob());
                        packetsFromBox.setProduct(product);
                        PacketsFromBox.Packet packet = new PacketsFromBox.Packet();
                        packet.setRfid(machineParts.getRfid());
                        packet.setQuantity(machineParts.getQuantity());
                        packet.setFound(scannedTags.contains(packet.getRfid()));
                        packet.setId(machineParts.getId());
                        boolean matchFound = false;
                        for (PacketsFromBox processedPacket : processedPackets) {
                            if (processedPacket.getProduct().getId().equals(product.getId())) {
                                processedPacket.getPackets().add(packet);
                                matchFound = true;
                                break;
                            }
                        }
                        if (!matchFound) {
                            List<PacketsFromBox.Packet> packets = new ArrayList<>();
                            packets.add(packet);
                            packetsFromBox.setPackets(packets);
                            processedPackets.add(packetsFromBox);
                        }
                    }
                    stockReconcilationAdapter.addAll(processedPackets);
                    packetsFromBoxList.addAll(processedPackets);
                    stockReconcilationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<MachineParts>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}