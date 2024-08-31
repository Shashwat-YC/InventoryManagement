package gg.rohan.narwhal.ui;

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
import java.util.HashSet;
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

public class CheckOutTestFragment extends Fragment implements SearchManager.SearchListener {

    private Pair<UUID, UUID> autoInventoryPair;
    private Pair<UUID, UUID> triggerHook;
    private UUID inventoryReader;
    public List<String> tagsScanned = new ArrayList<>();
    private final CheckoutPacketInfoAdapter adapter = new CheckoutPacketInfoAdapter(new ArrayList<>());
    private final List<PacketInfo> cachedList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ReaderStaticWrapper.clearCurrentTasks();
        ReaderStaticWrapper.reInstallReader(getContext());
        return inflater.inflate(R.layout.fragment_test_check_out, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        removeReader();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ExtendedFloatingActionButton fabProgress = view.findViewById(R.id.fab_confirm);
        fabProgress.setOnClickListener(v -> refreshItems());

        setupReader();
    }

    private void setupReader() {
        TextView textView = this.getView().findViewById(R.id.tv_trigger_msg);
        autoInventoryPair = ReaderStaticWrapper.autoPerformInventory();
        triggerHook = ReaderStaticWrapper.addTriggerHook(state -> {
            if (!state) {
                adapter.clearItems();
                adapter.notifyDataSetChanged();
                adapter.addItems(this.cachedList);
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

    private void showBottomSheet(CheckoutPacketInfo CheckoutPacketInfo) {
        removeReader();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_dialog, null);
        ProgressBar progressBar = bottomSheetView.findViewById(R.id.progress_bar);
        AppCompatButton saveButton = bottomSheetView.findViewById(R.id.save_button);

        LocationInfo locationInfo = CheckoutPacketInfo.getLocationInfo();
        TextView floorValue = bottomSheetView.findViewById(R.id.text_floor_value);
        floorValue.setText(locationInfo.getFloorName());
        TextView roomView = bottomSheetView.findViewById(R.id.text_room_value);
        roomView.setText(locationInfo.getRoomName());
        TextView rackView = bottomSheetView.findViewById(R.id.text_rack_value);
        rackView.setText(String.valueOf(locationInfo.getRack()));
        TextView shelfView = bottomSheetView.findViewById(R.id.text_shelf_value);
        shelfView.setText(String.valueOf(locationInfo.getShelf()));

        EditText checkOutQtyText = bottomSheetView.findViewById(R.id.edit_text2);
        if (CheckoutPacketInfo.getCheckOutQuantity() > 0) {
            checkOutQtyText.setText(String.valueOf(CheckoutPacketInfo.getCheckOutQuantity()));
        }

        saveButton.setOnClickListener(v -> {
            String checkOutString = checkOutQtyText.getText().toString();
            int checkOutQuantity = 0;
            if (!checkOutString.isEmpty()) checkOutQuantity = Integer.parseInt(checkOutString);
            if (CheckoutPacketInfo.getCheckOutQuantity() == checkOutQuantity) {
                bottomSheetDialog.dismiss();
                return;
            }
            if (checkOutQuantity < 0 || checkOutQuantity > CheckoutPacketInfo.getQuantity()) {
                openErrorWindow();
                return;
            }
            CheckoutPacketInfo.setCheckOutQuantity(checkOutQuantity);
            adapter.notifyItemChanged(adapter.checkoutPacketInfoList.indexOf(CheckoutPacketInfo));
            bottomSheetDialog.dismiss();
        });

        bottomSheetView.findViewById(R.id.scanned_qty_layout).setVisibility(View.GONE);

        Pair<UUID, UUID> pair = ReaderStaticWrapper.autoPerformLocating(CheckoutPacketInfo.getRfid());
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

    private void openPopupWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.updated_popup, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Button closeButton = dialogView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void openErrorWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.error_popup, null);
        builder.setView(dialogView);

        TextView subtitle = dialogView.findViewById(R.id.text2);
        subtitle.setText("Invalid quantity");

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Button closeButton = dialogView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void refreshItems() {
        Map<Integer, Integer> toUpdate = new HashMap<>();

        adapter.checkoutPacketInfoList
                .stream()
                .filter(product -> product.getCheckOutQuantity() > 0)
                .forEach(tagId -> toUpdate.put(tagId.getTagId(), tagId.getQuantity() - tagId.getCheckOutQuantity()));

        PacketService packetServiceAdapter = NewRetrofitClient.getPacketServiceAdapter();
        List<Observable<Void>> calls = toUpdate.entrySet().stream()
                 .map(entry -> packetServiceAdapter.updatePacketQuantityFromId(entry.getKey(), entry.getValue()))
                 .collect(Collectors.toList());

        Observable.merge(calls).subscribe((unused) -> {}, Throwable::printStackTrace, () -> {
            adapter.clearItems();
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
        List<CheckoutPacketInfo> searchResults = new ArrayList<>();
        if (!query.isEmpty()) {
            for (CheckoutPacketInfo product : adapter.checkoutPacketInfoList) {
                QuantisedSparePartInfo partInfo = product.getPartInfo();
                if (partInfo.getName().toLowerCase().contains(query.toLowerCase()) || partInfo.getPartNo().toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(product);
                }
            }
        } else {
            searchResults.addAll(adapter.checkoutPacketInfoList);
        }
        adapter.filterItems(searchResults);
    }

    public class CheckoutPacketInfoAdapter extends RecyclerView.Adapter<CheckoutPacketInfoAdapter.NewSpareProductHolder> {

        private final List<CheckoutPacketInfo> checkoutPacketInfoList;

        private final HashSet<String> tagsScanned = new HashSet<>();

        public CheckoutPacketInfoAdapter(List<CheckoutPacketInfo> checkoutPacketInfoList) {
            this.checkoutPacketInfoList = checkoutPacketInfoList;
        }

        @NonNull
        @Override
        public NewSpareProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.spare_home_items, parent, false);
            return new NewSpareProductHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NewSpareProductHolder holder, int position) {
            CheckoutPacketInfo checkoutPacketInfo = checkoutPacketInfoList.get(position);
            QuantisedSparePartInfo partInfo = checkoutPacketInfo.getPartInfo();
            holder.productName.setText(partInfo.getName());
            holder.machinery.setText(partInfo.getMachineModel().getName());
            holder.manufacturer.setText(partInfo.getMachineModel().getMaker());
            holder.tag.setText(CaseUtils.normal(checkoutPacketInfo.getType().name()));
            holder.productId.setText(partInfo.getPartNo());
            holder.robValue.setText(String.valueOf(partInfo.getRob()));
            holder.scannedValue.setText(String.valueOf(checkoutPacketInfo.getQuantity()));
            holder.checkoutValue.setText(String.valueOf(checkoutPacketInfo.getCheckOutQuantity()));
            holder.itemView.setOnClickListener(v -> showBottomSheet(checkoutPacketInfo));
            holder.packetView.setVisibility(checkoutPacketInfo.getCheckOutQuantity() > 0 ? View.VISIBLE : View.GONE);
            TextView scannedQtyText = holder.itemView.findViewById(R.id.tv_product_scanned_qty);
            scannedQtyText.setText("S. Qty");
        }

        @Override
        public int getItemCount() {
            return checkoutPacketInfoList.size();
        }

        public void addItems(List<PacketInfo> jobProducts) {
            jobProducts.forEach(jobProduct -> this.checkoutPacketInfoList.add(new CheckoutPacketInfo(jobProduct)));
        }

        public void addItem(PacketInfo jobProduct) {
            this.checkoutPacketInfoList.add(new CheckoutPacketInfo(jobProduct));
        }

        public void filterItems(List<CheckoutPacketInfo> jobProducts) {
            this.checkoutPacketInfoList.clear();
            this.checkoutPacketInfoList.addAll(jobProducts);
            notifyDataSetChanged();
        }

        public void clearItems() {
            this.checkoutPacketInfoList.clear();
        }

        public class NewSpareProductHolder extends RecyclerView.ViewHolder {

            private final TextView productName;
            private final TextView machinery;
            private final TextView manufacturer;
            private final TextView tag;
            private final TextView productId;
            private final TextView robValue;
            private final TextView scannedValue;
            private final TextView checkoutValue;
            private final View packetView;

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
                packetView = itemView.findViewById(R.id.triggerConstraintLayout);
            }
        }

    }

    private static class CheckoutPacketInfo extends PacketInfo {
        private int checkOutQuantity;

        private CheckoutPacketInfo(PacketInfo packetInfo) {
            super(packetInfo);
        }

        public int getCheckOutQuantity() {
            return checkOutQuantity;
        }

        public void setCheckOutQuantity(int checkOutQuantity) {
            this.checkOutQuantity = checkOutQuantity;
        }
    }


}