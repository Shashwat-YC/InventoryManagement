package gg.rohan.narwhal.ui.pms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.LocationInfo;
import gg.rohan.narwhal.newmodel.PacketInfo;
import gg.rohan.narwhal.newmodel.SparePartForMaintenanceInfo;
import gg.rohan.narwhal.rfid.ReaderStaticWrapper;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.CaseUtils;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import io.reactivex.Observable;

public class PMSPlanningFragment extends Fragment implements SearchManager.SearchListener {

    private Pair<UUID, UUID> autoInventoryHook;
    private Pair<UUID, UUID> triggerHook;
    private UUID inventoryListener;

    private final JobSparePartAdapter jobSparePartAdapter = new JobSparePartAdapter(new ArrayList<>());
    private final List<String> scannedTags = new ArrayList<>();
    private boolean noPartsNeeded = false;


    private void setupReader() {
        TextView triggerStatus = getView().findViewById(R.id.tv_trigger_msg);
        autoInventoryHook = ReaderStaticWrapper.autoPerformInventory();
        triggerHook = ReaderStaticWrapper.addTriggerHook(state -> triggerStatus.setText(state ? "Scanning..." : "Press the trigger to start scanning!"));
        inventoryListener = ReaderStaticWrapper.addInventoryHook(msg -> {
            String rfid = msg.getEpc();
            if (!scannedTags.contains(rfid)) {
                for (SparePartForJobWithPacketInfo jobProduct : jobSparePartAdapter.spareParts) {
                    for (SparePartForJobWithPacketInfo.UsablePacketInfo packet : jobProduct.getPackets()) {
                        if (packet.getRfid().equals(rfid) && packet.isSelected()) {
                            scannedTags.add(rfid);
                            packet.setScanned(true);
                            jobSparePartAdapter.notifyItemChanged(jobSparePartAdapter.spareParts.indexOf(jobProduct));
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

    private void setupTitleFromArgs() {
        TextView machineName = getView().findViewById(R.id.tv_pms_item_heading);
        machineName.setText(getArguments().getString("machine_name"));

        TextView description = getView().findViewById(R.id.tv_pms_item_sub_heading);
        description.setText(getArguments().getString("description"));
    }

    private void loadProductsWithPacketFromApi() {
        NewRetrofitClient.getJobsServiceAdapter().getJobSparePartsForMaintenance(getArguments().getInt("maintenanceId")).subscribe(sparePartInfoList -> {
            this.jobSparePartAdapter.addAll(sparePartInfoList);
            if (sparePartInfoList.isEmpty()) {
                noPartsNeeded = true;
                getActivity().runOnUiThread(this::checkStartEnable);
                return;
            }
            getActivity().runOnUiThread(() -> this.jobSparePartAdapter.notifyItemRangeInserted(0, this.jobSparePartAdapter.getItemCount()));
            sparePartInfoList.forEach(sparePartForJobInfo -> {
                NewRetrofitClient.getInventoryServiceAdapter().getSparePartWithPackets(sparePartForJobInfo.getCode()).subscribe(sparePartWithPacketInfo -> {
                    SparePartForJobWithPacketInfo jobProduct = jobSparePartAdapter.spareParts.stream().filter(product -> product.getCode().equals(sparePartForJobInfo.getCode())).findFirst()
                            .orElse(null);
                    if (jobProduct == null) return;
                    jobProduct.addPackets(sparePartWithPacketInfo.getPackets());
                    getActivity().runOnUiThread(() -> jobSparePartAdapter.notifyItemChanged(jobSparePartAdapter.spareParts.indexOf(jobProduct)));
                }, Throwable::printStackTrace);
            });
        }, Throwable::printStackTrace);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ReaderStaticWrapper.clearCurrentTasks();
        ReaderStaticWrapper.reInstallReader(inflater.getContext());
        return inflater.inflate(R.layout.fragment_p_m_s_item, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        removeReader();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTitleFromArgs();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setItemViewCacheSize(200);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(jobSparePartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadProductsWithPacketFromApi();

        setupReader();

        checkStartEnable();
        ExtendedFloatingActionButton fabProgress = getView().findViewById(R.id.fab_progress);

        Supplier<List<Integer>> usedPacketsSupplier = () -> jobSparePartAdapter.spareParts.stream().map(
                sparePartForJobWithPacketInfo -> sparePartForJobWithPacketInfo.packets.stream()
                        .filter(SparePartForJobWithPacketInfo.UsablePacketInfo::isSelected)
                        .map(SparePartForJobWithPacketInfo.UsablePacketInfo::getTagId)
                        .collect(Collectors.toList())
                ).flatMap(Collection::stream)
                .collect(Collectors.toList());

        fabProgress.setOnClickListener(v ->
                NewRetrofitClient.getJobsServiceAdapter()
                        .startJob(getArguments().getInt("maintenanceId"), usedPacketsSupplier.get())
                        .subscribe((unused) -> getActivity().runOnUiThread(this::openPopupWindow), Throwable::printStackTrace)
        );

    }

    public void checkStartEnable() {
        ExtendedFloatingActionButton fabProgress = getView().findViewById(R.id.fab_progress);
        boolean enabled = noPartsNeeded || (!jobSparePartAdapter.spareParts.isEmpty() && jobSparePartAdapter.spareParts.stream().allMatch(SparePartForJobWithPacketInfo::isFulfilled));
        fabProgress.setEnabled(enabled);
        if (enabled) {
            fabProgress.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.fab_progress_color)));
            fabProgress.setTextColor(Color.WHITE);
            fabProgress.setIconTint(ColorStateList.valueOf(Color.WHITE));
        } else {
            fabProgress.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.fab_background_color)));
            fabProgress.setTextColor(getResources().getColor(R.color.fab_initial_text_color));
            fabProgress.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.fab_initial_text_color)));
        }
    }

    private void showBottomSheet(SparePartForJobWithPacketInfo jobProduct, SparePartForJobWithPacketInfo.UsablePacketInfo packet) {
        removeReader();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_dialog, null);
        ProgressBar progressBar = bottomSheetView.findViewById(R.id.progress_bar);
        AppCompatButton saveButton = bottomSheetView.findViewById(R.id.save_button);

        LocationInfo locationInfo = packet.getLocationInfo();
        TextView floorValue = bottomSheetView.findViewById(R.id.text_floor_value);
        floorValue.setText(locationInfo.getFloorName());
        TextView roomView = bottomSheetView.findViewById(R.id.text_room_value);
        roomView.setText(locationInfo.getRoomName());
        TextView rackView = bottomSheetView.findViewById(R.id.text_rack_value);
        rackView.setText(String.valueOf(locationInfo.getRack()));
        TextView shelfView = bottomSheetView.findViewById(R.id.text_shelf_value);
        shelfView.setText(String.valueOf(locationInfo.getShelf()));

        EditText checkOutQtyText = bottomSheetView.findViewById(R.id.edit_text2);
        if (jobProduct.getQuantityNeededUpdatedOrElse(jobProduct.getQuantityNeeded()) > 0) {
            System.out.println(jobProduct.getQuantityNeededUpdated());
            checkOutQtyText.setText(String.valueOf(jobProduct.getQuantityNeededUpdatedOrElse(jobProduct.getQuantityNeeded())));
        }
        EditText scannedQtyText = bottomSheetView.findViewById(R.id.edit_text);
        if (packet.getUpdateQuantity() > 0) {
            scannedQtyText.setText(String.valueOf(packet.getUpdateQuantity()));
        }

        saveButton.setOnClickListener(v -> {
            String checkOutString = checkOutQtyText.getText().toString();
            int checkOutQuantity;
            if (!checkOutString.isEmpty()) checkOutQuantity = Integer.parseInt(checkOutString);
            else {
                checkOutQuantity = 0;
            }

            String scannedString = scannedQtyText.getText().toString();
            int scannedQuantity;
            if (!scannedString.isEmpty()) scannedQuantity = Integer.parseInt(scannedString);
            else {
                scannedQuantity = 0;
            }

            boolean checkoutUpdated = jobProduct.getQuantityNeededUpdatedOrElse(jobProduct.getQuantityNeeded()) != checkOutQuantity;
            boolean scannedUpdated = packet.getUpdateQuantity() != scannedQuantity;

            if (!checkoutUpdated && !scannedUpdated) {
                bottomSheetDialog.dismiss();
                return;
            }

            if (checkOutQuantity < 0) {
                return;
            }

            List<Observable<Void>> requests = new ArrayList<>();

            if (checkoutUpdated) {
                requests.add(
                        NewRetrofitClient.getJobsServiceAdapter().updateSparePartMaintenanceQuantity(
                                getArguments().getInt("maintenanceId"),
                                jobProduct.getCode(),
                                checkOutQuantity
                        )
                );
            }
            if (scannedUpdated) {
                requests.add(
                        NewRetrofitClient.getPacketServiceAdapter().updatePacketQuantityFromId(
                                packet.getTagId(),
                                scannedQuantity
                        )
                );
            }

            Observable.merge(requests).subscribe((aVoid) -> {}, Throwable::printStackTrace, () -> {
                bottomSheetDialog.dismiss();
                jobProduct.setQuantityNeededUpdated(checkOutQuantity);
                packet.setUpdateQuantity(scannedQuantity);
                getActivity().runOnUiThread(() -> jobSparePartAdapter.notifyItemChanged(jobSparePartAdapter.spareParts.indexOf(jobProduct)));
            });
        });

        Pair<UUID, UUID> pair = ReaderStaticWrapper.autoPerformLocating(packet.getRfid());
        UUID locatingHook = ReaderStaticWrapper.addLocatingHook(locateMessage -> {
            int strength = (int) locateMessage.getRssi();
            progressBar.setProgress(strength);
            if (strength > 90) {
                packet.setScanned(true);
                jobSparePartAdapter.notifyItemChanged(jobSparePartAdapter.spareParts.indexOf(jobProduct));
            }
        });
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

    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    public void performSearch(String query) {
        List<SparePartForJobWithPacketInfo> searchResults = new ArrayList<>();
        if (!query.isEmpty()) {
            for (SparePartForJobWithPacketInfo product : jobSparePartAdapter.spareParts) {
                if (product.getName().toLowerCase().contains(query.toLowerCase()) || product.getPartNo().toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(product);
                }
            }
        } else {
            searchResults.addAll(jobSparePartAdapter.spareParts);
        }
        jobSparePartAdapter.filterList(searchResults);
    }


    private class JobSparePartAdapter extends RecyclerView.Adapter<JobSparePartAdapter.ProductHolder> {

        private final List<SparePartForJobWithPacketInfo> spareParts;

        public JobSparePartAdapter(final List<SparePartForJobWithPacketInfo> spareParts) {
            this.spareParts = spareParts;
        }

        @NonNull
        @Override
        public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.pms_job_product_items, parent, false);
            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
            SparePartForJobWithPacketInfo sparePartInfo = spareParts.get(position);
            List<SparePartForJobWithPacketInfo.UsablePacketInfo> packets = sparePartInfo.getPackets();
            holder.productName.setText(sparePartInfo.getName());
            holder.productId.setText(sparePartInfo.getPartNo());
            holder.rob.setText(String.valueOf(
                    sparePartInfo.getPackets().stream()
                            .map(SparePartForJobWithPacketInfo.UsablePacketInfo::getUpdateQuantity)
                            .reduce(Integer::sum).orElse(0))
            );
            holder.workingAndReplace.setText(String.valueOf(sparePartInfo.getQuantityNeeded()));

            List<SparePartForJobWithPacketInfo.UsablePacketInfo> selectedScannedPackets = packets.stream()
                    .filter(SparePartForJobWithPacketInfo.UsablePacketInfo::isSelected)
                    .filter(SparePartForJobWithPacketInfo.UsablePacketInfo::isScanned)
                    .collect(Collectors.toList());
            int scannedQuantity = selectedScannedPackets.stream().map(SparePartForJobWithPacketInfo.UsablePacketInfo::getUpdateQuantity).reduce(0, Integer::sum);

            holder.scannedQuantity.setText(String.valueOf(scannedQuantity));
            holder.checkoutQuantity.setText(String.valueOf(sparePartInfo.getQuantityNeededUpdatedOrElse(sparePartInfo.getQuantityNeeded())));

            boolean fulfilled = scannedQuantity >= sparePartInfo.getQuantityNeededUpdatedOrElse(sparePartInfo.getQuantityNeeded());
            sparePartInfo.setFulfilled(fulfilled);
            if (fulfilled) {
                holder.productStatusDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.product_job_finished_status)));
            } else {
                holder.productStatusDot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), R.color.product_job_unfinished_status)));
            }
            checkStartEnable();

            holder.packetsView.setAdapter(new NewPMSPacketAdapter(packets, sparePartInfo));
            holder.packetsView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        }

        @Override
        public int getItemCount() {
            return this.spareParts.size();
        }

        private void addAll(List<SparePartForMaintenanceInfo> products) {
            products.forEach(this::add);
        }

        private void add(SparePartForMaintenanceInfo product) {
            this.spareParts.add(new SparePartForJobWithPacketInfo(product));
        }

        public void filterList(List<SparePartForJobWithPacketInfo> filteredList) {
            spareParts.clear();
            spareParts.addAll(filteredList);
            notifyDataSetChanged();
        }
        private class ProductHolder extends RecyclerView.ViewHolder {

            private final View productStatusDot;
            private final TextView productName;
            private final TextView productId;
            private final TextView rob;
            private final TextView workingAndReplace;
            private final TextView checkoutQuantity;
            private final TextView scannedQuantity;
            private final RecyclerView packetsView;

            public ProductHolder(@NonNull View itemView) {
                super(itemView);
                productStatusDot = itemView.findViewById(R.id.product_status_dot);
                productName = itemView.findViewById(R.id.tv_pms_product_name);
                productId = itemView.findViewById(R.id.tv_pms_product_id);
                rob = itemView.findViewById(R.id.tv_pms_product_rob_value);
                workingAndReplace = itemView.findViewById(R.id.tv_pms_product_working_and_replace_value);
                scannedQuantity = itemView.findViewById(R.id.tv_pms_product_scanned_qty_value);
                checkoutQuantity = itemView.findViewById(R.id.tv_pms_product_checkout_qty_value);
                packetsView = itemView.findViewById(R.id.rv_pms_product_items);
            }
        }

        private class NewPMSPacketAdapter extends RecyclerView.Adapter<NewPMSPacketAdapter.PacketHolder> {

            private final List<SparePartForJobWithPacketInfo.UsablePacketInfo> packets;
            private final SparePartForJobWithPacketInfo jobInfo;

            public NewPMSPacketAdapter(List<SparePartForJobWithPacketInfo.UsablePacketInfo> packets, SparePartForJobWithPacketInfo jobInfo) {
                this.packets = packets;
                this.jobInfo = jobInfo;
            }

            @NonNull
            @Override
            public PacketHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Context context = parent.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);

                View view = inflater.inflate(R.layout.pms_job_product_option_button_item, parent, false);
                return new PacketHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull PacketHolder holder, int position) {
                SparePartForJobWithPacketInfo.UsablePacketInfo packet = packets.get(position);
                holder.packetId.setText(String.valueOf(packet.getTagId()));
                holder.packetQuantity.setText(String.valueOf(packet.getUpdateQuantity()));
                holder.tag.setText(CaseUtils.normal(packet.getType().name()));
                holder.itemView.setOnClickListener(v -> showBottomSheet(jobInfo, packet));
                holder.itemView.setOnLongClickListener(v -> {
                    packet.setSelected(!packet.isSelected());
                    jobSparePartAdapter.notifyItemChanged(jobSparePartAdapter.spareParts.indexOf(jobInfo));
                    return true;
                });
                int color = packet.isScanned() && packet.isSelected() ? 0x9661ff61 : 0x00FFFFFF;
                int strokeColor = packet.isSelected() ? 0x9600ff00 : 0xFF000000;
                int stroke = packet.isSelected() ? 3 : 1;
                holder.layer.setCardBackgroundColor(color);
                holder.layer.setStrokeColor(strokeColor);
                holder.layer.setStrokeWidth(stroke);
            }

            @Override
            public int getItemCount() {
                return packets.size();
            }

            private class PacketHolder extends RecyclerView.ViewHolder {

                private final TextView packetId;
                private final TextView packetQuantity;
                private final TextView tag;
                private final MaterialCardView layer;

                public PacketHolder(@NonNull View itemView) {
                    super(itemView);
                    this.packetId = itemView.findViewById(R.id.tv_pms_product_option_id_value);
                    this.packetQuantity = itemView.findViewById(R.id.tv_pms_product_option_scanned_qty_value);
                    this.tag = itemView.findViewById(R.id.tv_pms_product_tag_text);
                    this.layer = itemView.findViewById(R.id.pms_packet_container_id);
                }
            }

        }

    }

    private static class SparePartForJobWithPacketInfo extends SparePartForMaintenanceInfo {

        private final List<UsablePacketInfo> packets;
        private boolean fulfilled = false;

        public SparePartForJobWithPacketInfo(SparePartForMaintenanceInfo sparePartInfo) {
            super(sparePartInfo);
            this.packets = new ArrayList<>();
        }

        public List<UsablePacketInfo> getPackets() {
            return Collections.unmodifiableList(packets);
        }

        public void addPacket(PacketInfo packet) {
            packets.add(new UsablePacketInfo(packet));
        }

        public void addPackets(List<PacketInfo> packets) {
            packets.forEach(this::addPacket);
            if (packets.size() == 1) {
                this.packets.get(0).setSelected(true);
            }
        }

        public boolean isFulfilled() {
            return this.fulfilled;
        }

        public void setFulfilled(boolean fulfilled) {
            this.fulfilled = fulfilled;
        }

        private static class UsablePacketInfo extends PacketInfo {

            private boolean selected = false;
            private boolean scanned = false;

            private Integer updateQuantity;

            public UsablePacketInfo(PacketInfo packetInfo) {
                super(packetInfo);
            }

            public boolean isSelected() {
                return selected;
            }

            public void setSelected(boolean selected) {
                this.selected = selected;
            }

            public boolean isScanned() {
                return scanned;
            }

            public void setScanned(boolean scanned) {
                this.scanned = scanned;
            }

            public Integer getUpdateQuantityNullable() {
                return updateQuantity;
            }

            public void setUpdateQuantity(Integer updateQuantity) {
                this.updateQuantity = updateQuantity;
            }

            public int getUpdateQuantity() {
                return this.updateQuantity != null ? this.updateQuantity : this.getQuantity();
            }

        }

    }

}
