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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.LocationInfo;
import gg.rohan.narwhal.newmodel.PacketInfo;
import gg.rohan.narwhal.newmodel.SparePartForMaintenanceInfo;
import gg.rohan.narwhal.rfid.ReaderStaticWrapper;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.CaseUtils;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import io.reactivex.Observable;

public class PMSInProgressFragment extends Fragment implements SearchManager.SearchListener {

    private final JobProductAdapter jobProductAdapter = new JobProductAdapter(new ArrayList<>());
    private boolean noPacketsNeeded = false;


    private void setupReader() {
        getView().findViewById(R.id.triggerConstraintLayout).setVisibility(View.GONE);
    }

    private void removeReader() {
    }

    private void setupTitleFromArgs() {
        TextView machineName = getView().findViewById(R.id.tv_pms_item_heading);
        machineName.setText(getArguments().getString("machine_name"));

        TextView description = getView().findViewById(R.id.tv_pms_item_sub_heading);
        description.setText(getArguments().getString("description"));
    }

    private void loadProductsWithPacketFromApi() {
        NewRetrofitClient.getJobsServiceAdapter().getJobSparePartsForMaintenance(getArguments().getInt("maintenanceId")).subscribe(sparePartInfoList -> {
            this.jobProductAdapter.addAll(sparePartInfoList);
            if (sparePartInfoList.isEmpty()) {
                noPacketsNeeded = true;
                getActivity().runOnUiThread(this::checkStartEnable);
                return;
            }
            getActivity().runOnUiThread(() -> this.jobProductAdapter.notifyItemRangeInserted(0, this.jobProductAdapter.getItemCount()));
            sparePartInfoList.forEach(sparePartForMaintenanceInfo -> {
                NewRetrofitClient.getInventoryServiceAdapter().getMaintenanceSparePartWithPackets(getArguments().getInt("maintenanceId"), sparePartForMaintenanceInfo.getCode()).subscribe(sparePartWithPacketInfo -> {
                    SparePartForJobWithPacketInfo jobProduct = jobProductAdapter.jobProducts.stream().filter(product -> product.getCode().equals(sparePartForMaintenanceInfo.getCode())).findFirst()
                            .orElse(null);
                    if (jobProduct == null) return;
                    jobProduct.addPackets(sparePartWithPacketInfo.getPackets());
                    getActivity().runOnUiThread(() -> jobProductAdapter.notifyItemChanged(jobProductAdapter.jobProducts.indexOf(jobProduct)));
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
        recyclerView.setAdapter(jobProductAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadProductsWithPacketFromApi();

        setupReader();

        checkStartEnable();
        ExtendedFloatingActionButton fabProgress = getView().findViewById(R.id.fab_progress);
        fabProgress.setText("Complete");

        Supplier<Map<Integer, Integer>> packetQuantitiesSupplier = () -> {
            Map<Integer, Integer> packetQuantities = new HashMap<>();
            for (SparePartForJobWithPacketInfo sparePart : jobProductAdapter.jobProducts) {
                for (SparePartForJobWithPacketInfo.UsablePacketInfo packet : sparePart.getPackets()) {
                    packetQuantities.put(packet.getTagId(), packet.getInUseQuantity());
                }
            }
            return packetQuantities;
        };


        fabProgress.setOnClickListener(v -> {
                    System.out.println("Clicked");
            NewRetrofitClient.getJobsServiceAdapter().completeJob(
                    getArguments().getInt("maintenanceId"),
                    packetQuantitiesSupplier.get()
            ).subscribe(aVoid -> getActivity().runOnUiThread(this::openPopupWindow), Throwable::printStackTrace);
                }
        );
    }

    public void checkStartEnable() {
        ExtendedFloatingActionButton fabProgress = getView().findViewById(R.id.fab_progress);
        boolean enabled = noPacketsNeeded || (!jobProductAdapter.jobProducts.isEmpty() && jobProductAdapter.jobProducts.stream().allMatch(SparePartForJobWithPacketInfo::isQuantitiesSet));
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

        EditText inUseQuantity = bottomSheetView.findViewById(R.id.edit_text3);
        if (packet.getInUseQuantity() != null) {
            inUseQuantity.setText(String.valueOf(packet.getInUseQuantity()));
        }

        EditText checkOutQtyText = bottomSheetView.findViewById(R.id.edit_text2);
        if (jobProduct.getQuantityNeededUpdatedOrElse(jobProduct.getQuantityNeeded()) > 0) {
            checkOutQtyText.setText(String.valueOf(jobProduct.getQuantityNeededUpdatedOrElse(jobProduct.getQuantityNeeded())));
        }

        EditText scannedQtyText = bottomSheetView.findViewById(R.id.edit_text);
        scannedQtyText.setText(String.valueOf(packet.getUpdateQuantity()));

        bottomSheetView.findViewById(R.id.inuse_qty_layout).setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(v -> {
            String checkOutString = checkOutQtyText.getText().toString();
            String inUseString = inUseQuantity.getText().toString();
            String scannedQtyString = scannedQtyText.getText().toString();
            int checkOutQuantity;
            int inUseQuantityInt;
            int scannedQuantityInt;
            if (!checkOutString.isEmpty()) checkOutQuantity = Integer.parseInt(checkOutString);
            else {
                checkOutQuantity = 0;
            }
            if (!inUseString.isEmpty()) inUseQuantityInt = Integer.parseInt(inUseString);
            else {
                inUseQuantityInt = 0;
            }
            if (!scannedQtyString.isEmpty()) scannedQuantityInt = Integer.parseInt(scannedQtyString);
            else {
                scannedQuantityInt = 0;
            }

            boolean checkoutUpdated = jobProduct.getQuantityNeededUpdatedOrElse(jobProduct.getQuantityNeeded()) != checkOutQuantity;
            boolean inUseUpdated = packet.getInUseQuantityOr(0) != inUseQuantityInt;
            boolean scannedUpdated = packet.getUpdateQuantity() != Integer.parseInt(scannedQtyText.getText().toString());

            if (!checkoutUpdated && !inUseUpdated && !scannedUpdated) {
                bottomSheetDialog.dismiss();
                return;
            }

            if (checkOutQuantity < 0 || inUseQuantityInt < 0 || scannedQuantityInt < 0) {
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
                        NewRetrofitClient.getPacketServiceAdapter().updatePacketForInUseQuantityFromId(
                                packet.getTagId(),
                                scannedQuantityInt
                        )
                );
            }

            Observable.merge(requests).subscribe((unused) -> {}, Throwable::printStackTrace, () -> {
                jobProduct.setQuantityNeededUpdated(checkOutQuantity);
                packet.setInUseQuantity(inUseQuantityInt);
                packet.setUpdateQuantity(scannedQuantityInt);
                getActivity().runOnUiThread(() -> jobProductAdapter.notifyItemChanged(jobProductAdapter.jobProducts.indexOf(jobProduct)));
                bottomSheetDialog.dismiss();
            });

        });

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
            for (SparePartForJobWithPacketInfo product : jobProductAdapter.jobProducts) {
                if (product.getName().toLowerCase().contains(query.toLowerCase()) || product.getPartNo().toLowerCase().contains(query.toLowerCase())) {
                    searchResults.add(product);
                }
            }
        } else {
            searchResults.addAll(jobProductAdapter.jobProducts);
        }
        jobProductAdapter.filterList(searchResults);
    }


    private class JobProductAdapter extends RecyclerView.Adapter<JobProductAdapter.ProductHolder> {

        private final List<SparePartForJobWithPacketInfo> jobProducts;

        public JobProductAdapter(final List<SparePartForJobWithPacketInfo> jobProducts) {
            this.jobProducts = jobProducts;
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
            SparePartForJobWithPacketInfo sparePartInfo = jobProducts.get(position);
            List<SparePartForJobWithPacketInfo.UsablePacketInfo> packets = sparePartInfo.getPackets();
            holder.productName.setText(sparePartInfo.getName());
            holder.productId.setText(sparePartInfo.getPartNo());
            holder.rob.setText(String.valueOf(sparePartInfo.getRob()));
            holder.workingAndReplace.setText(String.valueOf(sparePartInfo.getQuantityNeeded()));
            holder.checkoutQuantity.setText(String.valueOf(sparePartInfo.getQuantityNeededUpdatedOrElse(sparePartInfo.getQuantityNeeded())));

            int scannedQuantity = packets.stream().map(SparePartForJobWithPacketInfo.UsablePacketInfo::getQuantity).reduce(0, Integer::sum);

            holder.scannedQuantity.setText(String.valueOf(scannedQuantity));

            boolean quantitiesSet =
                    sparePartInfo.packets.stream().allMatch(packet -> packet.getInUseQuantity() != null) &&
                    sparePartInfo.packets.stream().map(SparePartForJobWithPacketInfo.UsablePacketInfo::getInUseQuantity).reduce(0, Integer::sum) >= sparePartInfo.getQuantityNeededUpdatedOrElse(sparePartInfo.getQuantityNeeded());
            sparePartInfo.setQuantitiesSet(quantitiesSet);
            if (quantitiesSet) {
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
            return this.jobProducts.size();
        }

        private void addAll(List<SparePartForMaintenanceInfo> products) {
            products.forEach(this::add);
        }

        private void add(SparePartForMaintenanceInfo product) {
            this.jobProducts.add(new SparePartForJobWithPacketInfo(product));
        }

        public void filterList(List<SparePartForJobWithPacketInfo> filteredList) {
            jobProducts.clear();
            jobProducts.addAll(filteredList);
            notifyDataSetChanged();
        }
        private class ProductHolder extends RecyclerView.ViewHolder {

            private final View productStatusDot;
            private final TextView productName;
            private final TextView productId;
            private final TextView rob;
            private final TextView workingAndReplace;
            private final TextView scannedQuantity;
            private final TextView checkoutQuantity;
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
                holder.inUseQuantity.setText(String.valueOf(packet.getInUseQuantityOr(0)));
                int color = 0x00FFFFFF;
                int strokeColor = 0xFF000000;
                int stroke = 1;
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
                private final TextView inUseQuantity;
                private final TextView tag;
                private final MaterialCardView layer;

                public PacketHolder(@NonNull View itemView) {
                    super(itemView);
                    this.packetId = itemView.findViewById(R.id.tv_pms_product_option_id_value);
                    TextView scannedLabel = itemView.findViewById(R.id.tv_pms_product_option_scanned_qty);
                    scannedLabel.setText("S. Qty: ");
                    this.packetQuantity = itemView.findViewById(R.id.tv_pms_product_option_scanned_qty_value);
                    this.inUseQuantity = itemView.findViewById(R.id.tv_pms_product_option_inuse_qty_value);
                    itemView.findViewById(R.id.tv_pms_product_option_inuse_qty).setVisibility(View.VISIBLE);
                    this.inUseQuantity.setVisibility(View.VISIBLE);
                    this.tag = itemView.findViewById(R.id.tv_pms_product_tag_text);
                    this.layer = itemView.findViewById(R.id.pms_packet_container_id);
                }
            }

        }

    }

    private static class SparePartForJobWithPacketInfo extends SparePartForMaintenanceInfo {

        private final List<UsablePacketInfo> packets;
        private boolean quantitiesSet = false;

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

        public void addPackets(Collection<PacketInfo> packets) {
            packets.forEach(this::addPacket);
        }

        public boolean isQuantitiesSet() {
            return this.quantitiesSet;
        }

        public void setQuantitiesSet(boolean quantitiesSet) {
            this.quantitiesSet = quantitiesSet;
        }

        private static class UsablePacketInfo extends PacketInfo {

            private Integer inUseQuantity;
            private Integer updateQuantity;

            public UsablePacketInfo(PacketInfo packetInfo) {
                super(packetInfo);
            }

            public Integer getInUseQuantity() {
                return this.inUseQuantity;
            }

            public int getInUseQuantityOr(int defaultValue) {
                return this.inUseQuantity != null ? this.inUseQuantity : defaultValue;
            }

            public void setInUseQuantity(Integer inUseQuantity) {
                this.inUseQuantity = inUseQuantity;
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
