package gg.rohan.narwhal.ui.checkin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.LocationInfo;
import gg.rohan.narwhal.newmodel.PacketInfo;
import gg.rohan.narwhal.newmodel.QuantisedSparePartInfo;
import gg.rohan.narwhal.rfid.ReaderStaticWrapper;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.CaseUtils;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import gg.rohan.narwhal.util.newapi.PacketService;
import io.reactivex.Observable;

public class SpareFragment extends Fragment implements SearchManager.SearchListener {

    private Pair<UUID, UUID> autoInventoryPair;
    private Pair<UUID, UUID> triggerHook;
    private UUID inventoryReader;
    public List<String> tagsScanned = new ArrayList<>();
    private final SpareAdapter adapter = new SpareAdapter(new ArrayList<>());
    private final List<PacketInfo> cachedList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReaderStaticWrapper.clearCurrentTasks();
        ReaderStaticWrapper.reInstallReader(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        removeReader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ReaderStaticWrapper.clearCurrentTasks();
        ReaderStaticWrapper.reInstallReader(getContext());
        return inflater.inflate(R.layout.fragment_spare, container, false);
    }

    private void setupReader() {
        TextView textView = this.getView().findViewById(R.id.tv_trigger_msg);
        autoInventoryPair = ReaderStaticWrapper.autoPerformInventory();
        triggerHook = ReaderStaticWrapper.addTriggerHook(state -> {
            if (!state) {
                adapter.clearItems();
                adapter.notifyDataSetChanged();
                adapter.addItems(cachedList);
                adapter.notifyDataSetChanged();
            }
            textView.setText(state ? "Scanning..." : "Press the trigger to start scanning!");
        });
        inventoryReader = ReaderStaticWrapper.addInventoryHook(inventoryMessage -> {
            String rfid = inventoryMessage.getEpc();
            if (!tagsScanned.contains(rfid)) {
                tagsScanned.add(rfid);
                NewRetrofitClient.getPacketServiceAdapter().getPacketInfo(rfid).subscribe(cachedList::add, Throwable::printStackTrace);
            }
        });
    }

    private void removeReader() {
        ReaderStaticWrapper.stopInventory();
        ReaderStaticWrapper.removeMessageHook(autoInventoryPair.first);
        ReaderStaticWrapper.removeMessageHook(autoInventoryPair.second);
        ReaderStaticWrapper.removeMessageHook(triggerHook.first);
        ReaderStaticWrapper.removeMessageHook(triggerHook.second);
        ReaderStaticWrapper.removeMessageHook(inventoryReader);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupReader();

        ExtendedFloatingActionButton confirmFabBtn = view.findViewById(R.id.fab_confirm);

        confirmFabBtn.setOnClickListener(v -> refreshItems(adapter));
    }

    private void openPopupWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.updated_popup, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // Handle click on the "Close" button in the dialog
        Button closeButton = dialogView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(v -> {
            alertDialog.dismiss(); // Dismiss the dialog
        });
    }

    private void showBottomSheet(CheckInPacketInfo checkInPacketInfo) {
        removeReader();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_dialog, null);
        ProgressBar progressBar = bottomSheetView.findViewById(R.id.progress_bar);
        AppCompatButton saveButton = bottomSheetView.findViewById(R.id.save_button);
        TextView checkInText = bottomSheetView.findViewById(R.id.text_view5);
        checkInText.setText("Check-In Qty");

        LocationInfo locationInfo = checkInPacketInfo.getLocationInfo();
        TextView floorValue = bottomSheetView.findViewById(R.id.text_floor_value);
        floorValue.setText(locationInfo.getFloorName());
        TextView roomView = bottomSheetView.findViewById(R.id.text_room_value);
        roomView.setText(locationInfo.getRoomName());
        TextView rackView = bottomSheetView.findViewById(R.id.text_rack_value);
        rackView.setText(String.valueOf(locationInfo.getRack()));
        TextView shelfView = bottomSheetView.findViewById(R.id.text_shelf_value);
        shelfView.setText(String.valueOf(locationInfo.getShelf()));

        EditText checkInQtyInput = bottomSheetView.findViewById(R.id.edit_text2);
        if (checkInPacketInfo.getCheckInQuantity() > 0) {
            checkInQtyInput.setText(String.valueOf(checkInPacketInfo.getCheckInQuantity()));
        }

        saveButton.setOnClickListener(v -> {
            String checkInString = checkInQtyInput.getText().toString();
            if (checkInString.isEmpty()) {
                bottomSheetDialog.dismiss();
                return;
            }
            int checkInQuantity = Integer.parseInt(checkInString);
            if (checkInPacketInfo.getCheckInQuantity() == checkInQuantity) {
                bottomSheetDialog.dismiss();
                return;
            }
            checkInPacketInfo.setCheckInQuantity(checkInQuantity);
            adapter.notifyItemChanged(adapter.jobProducts.indexOf(checkInPacketInfo));
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.scanned_qty_layout).setVisibility(View.GONE);

        Pair<UUID, UUID> pair = ReaderStaticWrapper.autoPerformLocating(checkInPacketInfo.getRfid());
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

    private void refreshItems(SpareAdapter spareAdapter) {

        Map<Integer, Integer> toUpdate = new HashMap<>();

        spareAdapter.jobProducts.stream()
                .filter(product -> product.getCheckInQuantity() > 0)
                .forEach(product -> toUpdate.put(product.getTagId(), product.getCheckInQuantity() + product.getQuantity()));

        PacketService packetServiceAdapter = NewRetrofitClient.getPacketServiceAdapter();
        List<Observable<Void>> calls = toUpdate.entrySet().stream()
                .map((entry) -> packetServiceAdapter.updatePacketQuantityFromId(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        
        Observable.merge(calls).subscribe((unused) -> {}, Throwable::printStackTrace, () -> {
            spareAdapter.clearItems();
            cachedList.clear();
            packetServiceAdapter.getPacketInfo(tagsScanned).subscribe(cachedList::addAll, Throwable::printStackTrace, () -> {
                getActivity().runOnUiThread(() -> {
                    adapter.addItems(cachedList);
                    adapter.notifyDataSetChanged();
                    openPopupWindow();
                });
            });
        });
    }

    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    public void performSearch(String query) {
        List<CheckInPacketInfo> filteredList = new ArrayList<>();
        if (!query.isEmpty()) {
            for (CheckInPacketInfo product : adapter.jobProducts) {
                QuantisedSparePartInfo partInfo = product.getPartInfo();
                if (partInfo.getName().toLowerCase().contains(query.toLowerCase()) || partInfo.getPartNo().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        } else {
            filteredList.addAll(adapter.jobProducts);
        }
        adapter.filterList(filteredList);
    }

    public class SpareAdapter extends RecyclerView.Adapter<SpareAdapter.SpareViewHolder> {

        private final List<CheckInPacketInfo> jobProducts;

        public SpareAdapter(final List<CheckInPacketInfo> jobProducts) {
            this.jobProducts = jobProducts;
        }

        @NonNull
        @Override
        public SpareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.spare_home_items, parent, false);
            return new SpareViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SpareViewHolder holder, int position) {
            CheckInPacketInfo checkInPacketInfo = jobProducts.get(position);
            QuantisedSparePartInfo partInfo = checkInPacketInfo.getPartInfo();
            holder.productName.setText(partInfo.getName());
            holder.machinery.setText(partInfo.getMachineModel().getName());
            holder.manufacturer.setText(partInfo.getMachineModel().getMaker());
            holder.tag.setText(CaseUtils.normal(checkInPacketInfo.getType().name()));
            holder.productId.setText(partInfo.getPartNo());
            holder.scannedValue.setText(String.valueOf(checkInPacketInfo.getQuantity()));
            holder.checkInValue.setText(String.valueOf(checkInPacketInfo.getCheckInQuantity()));
            holder.itemView.setOnClickListener(v -> showBottomSheet(checkInPacketInfo));
            holder.packetView.setVisibility(checkInPacketInfo.getCheckInQuantity() > 0 ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return jobProducts.size();
        }

        public void clearItems() {
            jobProducts.clear();
        }

        public void addItems(List<PacketInfo> items) {
            items.forEach(item -> this.jobProducts.add(new CheckInPacketInfo(item)));
        }

        public void addItem(PacketInfo product) {
            this.jobProducts.add(new CheckInPacketInfo(product));
        }

        public void filterList(List<CheckInPacketInfo> searchResults) {
            jobProducts.clear();
            jobProducts.addAll(searchResults);
            notifyDataSetChanged();
        }

        public class SpareViewHolder extends RecyclerView.ViewHolder {

            private final TextView productName;
            private final TextView machinery;
            private final TextView manufacturer;
            private final TextView tag;
            private final TextView productId;
            private final TextView scannedValue;
            private final TextView checkInValue;
            private final View packetView;

            public SpareViewHolder(@NonNull View itemView) {
                super(itemView);
                productName = itemView.findViewById(R.id.tv_product_name);
                machinery = itemView.findViewById(R.id.tv_machinery);
                manufacturer = itemView.findViewById(R.id.tv_manufacturer);
                tag = itemView.findViewById(R.id.tv_product_id_tag_text);
                productId = itemView.findViewById(R.id.tv_product_id);
                TextView robText = itemView.findViewById(R.id.tv_product_rob);
                TextView robValue = itemView.findViewById(R.id.tv_product_rob_value);
                scannedValue = itemView.findViewById(R.id.tv_product_scanned_qty_value);
                checkInValue = itemView.findViewById(R.id.tv_product_checkout_qty_value);
                robValue.setVisibility(View.GONE);
                robText.setVisibility(View.GONE);
                ((TextView) itemView.findViewById(R.id.tv_product_checkout_qty)).setText("Checkin Qty:");
                packetView = itemView.findViewById(R.id.triggerConstraintLayout);
            }
        }

    }
    
    private static class CheckInPacketInfo extends PacketInfo {
        private int checkInQuantity;

        private CheckInPacketInfo(PacketInfo packetInfo) {
            super(packetInfo);
        }

        public int getCheckInQuantity() {
            return checkInQuantity;
        }

        public void setCheckInQuantity(int checkInQuantity) {
            this.checkInQuantity = checkInQuantity;
        }
    }

}