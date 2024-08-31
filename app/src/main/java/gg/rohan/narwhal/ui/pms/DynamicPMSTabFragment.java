package gg.rohan.narwhal.ui.pms;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.JobInfo;
import gg.rohan.narwhal.newmodel.JobMachineInfo;
import gg.rohan.narwhal.newmodel.MachineInfo;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.util.DateFormatter;
import gg.rohan.narwhal.util.EnumUtils;
import gg.rohan.narwhal.util.CaseUtils;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;

public class DynamicPMSTabFragment extends Fragment  implements  SearchManager.SearchListener{

    PowerMenu statusPowerMenu;
    PowerMenu dateRangePowerMenu;
    private String tabTitle;
    private JobInfo.Place jobPlace;
    private JobInfo.Status status = JobInfo.Status.PLANNING;
    private JobInfo.DueWithin dueWithin = JobInfo.DueWithin.ALL;
    List<JobMachineInfo> jobsList = new ArrayList<>();
    PMSJobAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve title from arguments
        if (getArguments() != null) {
            tabTitle = getArguments().getString("tabTitle");
            jobPlace = EnumUtils.fromString(JobInfo.Place.class, tabTitle);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dynamic_pms_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout statusDropdownLayout = view.findViewById(R.id.status_linear_layout);
        LinearLayout dateRangeDropdownLayout = view.findViewById(R.id.date_range_linear_layout);
        TextView dateRangeTextView = view.findViewById(R.id.date_range_txt);
        dateRangeTextView.setText(CaseUtils.normal(dueWithin.name()));
        TextView statusTextView = view.findViewById(R.id.status_value);
        statusTextView.setText(CaseUtils.normal(status.name()));

        // Build PowerMenu for status dropdown
        PMenuClickListener statusPowerMenuItemClickListener = new PMenuClickListener();
        statusPowerMenu = buildPowerMenu(Arrays.stream(JobInfo.Status.values()).map(status -> CaseUtils.normal(status.name().replace("_", " "))).collect(Collectors.toList()), statusPowerMenuItemClickListener);
        statusPowerMenuItemClickListener.setItems(statusPowerMenu, statusTextView, (index, item) -> status = EnumUtils.fromString(JobInfo.Status.class, item.title.toString().replace(" ", "_")));
        statusDropdownLayout.setOnClickListener(v -> statusPowerMenu.showAsDropDown(v));

        // Build PowerMenu for date range dropdown
        PMenuClickListener dateRangePowerMenuItemClickListener = new PMenuClickListener();
        dateRangePowerMenu = buildPowerMenu(Arrays.stream(JobInfo.DueWithin.values()).map(dueWithin -> CaseUtils.normal(dueWithin.name())).collect(Collectors.toList()), dateRangePowerMenuItemClickListener);
        dateRangePowerMenuItemClickListener.setItems(dateRangePowerMenu, dateRangeTextView, (index, item) -> dueWithin = EnumUtils.fromString(JobInfo.DueWithin.class, item.title.toString()));
        dateRangeDropdownLayout.setOnClickListener(v -> dateRangePowerMenu.showAsDropDown(v));


        RecyclerView recyclerView = view.findViewById(R.id.pms_jobs_recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new PMSJobAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshApi();
    }

    private PowerMenu buildPowerMenu(Collection<String> items, PMenuClickListener clickListener) {
        List<PowerMenuItem> powerMenuItems = new ArrayList<>();
        for (String item : items) {
            powerMenuItems.add(new PowerMenuItem(item, false)); // Create PowerMenuItem objects
        }
        return new PowerMenu.Builder(requireContext())
                .addItemList(powerMenuItems)
                .setAnimation(MenuAnimation.DROP_DOWN)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setDivider(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.drop_down_divider)))
                .setDividerHeight(1)
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.dark))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(ContextCompat.getColor(requireContext(), R.color.skyblue))
                .setMenuColor(Color.WHITE)
                .setOnMenuItemClickListener(clickListener)
                .setLifecycleOwner(this)
                .setFocusable(false)
                .setShowBackground(true)
                .build();
    }

    private void refreshApi() {
        NewRetrofitClient.getJobsServiceAdapter().getAllJobs(status, dueWithin, jobPlace).subscribe(jobs -> {
            adapter.clear();
            adapter.addAll(jobs);
            jobsList.addAll(jobs);
            getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    @Override
    public void onSearch(String query) {
        performSearch(query);
    }

    public void performSearch(String query) {
        List<JobMachineInfo> filteredList = new ArrayList<>();
        if (!query.isEmpty()) {
            for (JobMachineInfo job : jobsList) {
                if (job.getMachineInfo().getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(job);
                }
            }
        } else {
            filteredList.addAll(jobsList);
        }
        adapter.filterList(filteredList);
    }

    private class PMenuClickListener implements OnMenuItemClickListener<PowerMenuItem> {

        private PowerMenu powerMenu;
        private TextView textView;
        private BiConsumer<Integer, PowerMenuItem> additionalConsumer;

        public void setItems(PowerMenu powerMenu, TextView textView, BiConsumer<Integer, PowerMenuItem> additionalConsumer) {
            this.powerMenu = powerMenu;
            this.textView = textView;
            this.additionalConsumer = additionalConsumer;
        }

        @Override
        public void onItemClick(int position, PowerMenuItem item) {
            // Show toast with selected item title
            Toast.makeText(requireContext(), item.title, Toast.LENGTH_SHORT).show();

            // Change selected item
            powerMenu.setSelectedPosition(position);

            // Dismiss the PowerMenu
            powerMenu.dismiss();
            textView.setText(item.title);
            additionalConsumer.accept(position, item);
            refreshApi();
        }
    }

    public class PMSJobAdapter extends RecyclerView.Adapter<PMSJobAdapter.PMSJobViewHolder> {

        private final List<JobMachineInfo> jobMachines;
        private final Fragment fragment;

        public PMSJobAdapter(final List<JobMachineInfo> jobMachines, Fragment fragment) {
            super();
            this.jobMachines = jobMachines;
            this.fragment = fragment;
        }

        @NonNull
        @Override
        public PMSJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.pms_home_items, parent, false);
            return new PMSJobViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull PMSJobViewHolder holder, int position) {
            JobMachineInfo jobMachine = jobMachines.get(position);
            JobInfo jobInfo = jobMachine.getJobInfo();
            MachineInfo machineInfo = jobMachine.getMachineInfo();
            // Set values
            holder.machineId.setText(String.valueOf(machineInfo.getNo()));
            holder.jobName.setText(machineInfo.getName());
            holder.jobTag.setText(CaseUtils.upper(jobInfo.getKind().name()));
            holder.pic.setText(CaseUtils.upper(jobInfo.getPic().name()));
            holder.date.setText(DateFormatter.formatDate(jobInfo.getNextSchedule()));
            holder.description.setText(jobInfo.getDescription());
            holder.itemView.setOnClickListener(v -> {
                Fragment fragmentToSpawn;
                switch (status) {
                    case PLANNING:
                        fragmentToSpawn = new PMSPlanningFragment();
                        break;
                    case IN_PROGRESS:
                        fragmentToSpawn = new PMSInProgressFragment();
                        break;
                    case COMPLETED:
                        fragmentToSpawn = new PMSCompletedFragment();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: ");
                }
                Bundle args = new Bundle();
                args.putString("jobPmsCode", jobInfo.getCode());
                args.putInt("maintenanceId", jobInfo.getMaintenanceRecordId());
                args.putString("machine_name", machineInfo.getName());
                args.putString("description", jobInfo.getDescription());

                fragmentToSpawn.setArguments(args);
                fragment.getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragmentToSpawn)
                        .addToBackStack(null)  // Optional for backstack
                        .commit();
            });
        }

        @Override
        public int getItemCount() {
            return jobMachines.size();
        }

        public void clear() {
            this.jobMachines.clear();
        }

        public void addAll(List<JobMachineInfo> jobMachines) {
            if (jobMachines == null) return;
            this.jobMachines.addAll(jobMachines);
        }

        public void filterList(List<JobMachineInfo> filteredList) {
            this.jobMachines.clear();
            this.jobMachines.addAll(filteredList);
            notifyDataSetChanged();
        }

        public class PMSJobViewHolder extends RecyclerView.ViewHolder {
            private final TextView jobName;
            private final TextView jobTag;
            private final TextView pic;
            private final TextView date;
            private final TextView description;
            private final TextView machineId;
            private final View clickHandler;


            public PMSJobViewHolder(@NonNull View itemView) {
                super(itemView);
                jobName = itemView.findViewById(R.id.tv_pms_job_name);
                jobTag = itemView.findViewById(R.id.tv_pms_item_tag);
                pic = itemView.findViewById(R.id.tv_pms_item_pic_value);
                date = itemView.findViewById(R.id.tv_pms_item_date);
                description = itemView.findViewById(R.id.tv_pms_item_description);
                clickHandler = itemView.findViewById(R.id.ll_pms_home_item);
                machineId = itemView.findViewById(R.id.tv_machine_uid_value);
            }
        }


    }

}
