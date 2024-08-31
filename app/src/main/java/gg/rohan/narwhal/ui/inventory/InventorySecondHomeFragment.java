package gg.rohan.narwhal.ui.inventory;

import androidx.fragment.app.Fragment;

public class InventorySecondHomeFragment extends Fragment {
//
//    private int id;
//    private String machineName;
//    InventorySecondHomeAdapter inventorySecondHomeAdapter;
//    RecyclerView recyclerView;
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Retrieve id from arguments
//        if (getArguments() != null) {
//            id = getArguments().getInt("id");
//            machineName = getArguments().getString("machineName");
//        }
//    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_inventory_second_home, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // Retrieve tab title passed from InventoryFragment
//        ConstraintLayout inventorytypeConstraintLayout = view.findViewById(R.id.inventorytypeConstraintLayout);
//        inventorytypeConstraintLayout.setOnClickListener(v -> showBottomSheet());
//
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setItemViewCacheSize(20);
//        recyclerView.setNestedScrollingEnabled(false);
//        inventorySecondHomeAdapter = new InventorySecondHomeAdapter(getContext(), new ArrayList<>(),machineName, id);
//        recyclerView.setAdapter(inventorySecondHomeAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        Call<List<MachineBox>> machinebox = RetrofitClient.getInventoryAdapter().getMachineBoxes(id,0);
//        machinebox.enqueue(new Callback<List<MachineBox>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<MachineBox>> listCall, @NonNull Response<List<MachineBox>> response) {
//                if (response.isSuccessful()) {
//                    inventorySecondHomeAdapter.addAll(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<MachineBox>> call, @NonNull Throwable t) {
//                t.printStackTrace();
//            }
//        });
//
//    }
//
//    private void openInventoryProductFragment() {
//        InventoryProductFragment inventoryProductFragment = new InventoryProductFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("tabTitle", String.valueOf(id));
//        inventoryProductFragment.setArguments(bundle);
//        getParentFragmentManager().beginTransaction().replace(R.id.child_fragment_container, inventoryProductFragment).addToBackStack(null).commit();
//    }
//
//    private void showBottomSheet(){
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
//        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_bottom_sheet_layout, null);
//        bottomSheetDialog.setContentView(bottomSheetView);
//
//        RecyclerView checkboxRecyclerView = bottomSheetView.findViewById(R.id.checkbox_recycler_view);
//        List<String> checkboxOptions = Arrays.asList("Option 1", "Option 2", "Option 3", "Option 4"); //  list of options
//        CheckboxAdapter checkboxAdapter = new CheckboxAdapter(checkboxOptions);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
//        checkboxRecyclerView.setLayoutManager(layoutManager);
//        int spanCount = 3; // Number of columns
//        int screenWidth = getResources().getDisplayMetrics().widthPixels;
//        int itemWidth = screenWidth / spanCount;
//        checkboxAdapter.setItemWidth(itemWidth);
//        checkboxRecyclerView.setAdapter(checkboxAdapter);
//
//
//        RecyclerView radioButtonRecyclerView = bottomSheetView.findViewById(R.id.room_names_recycler_view);
//        List<String> radioButtonOptions = Arrays.asList("Option A", "Option B", "Option C"); //  list of options
//        RadioButtonAdapter radioButtonAdapter = new RadioButtonAdapter(radioButtonOptions);
//        radioButtonRecyclerView.setAdapter(radioButtonAdapter);
//        radioButtonRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//
//
//
//        bottomSheetDialog.show();
//    }
//
//

}