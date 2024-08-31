package gg.rohan.narwhal.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


import gg.rohan.narwhal.R;
import gg.rohan.narwhal.ui.inventory.DynamicInventoryTabFragment;

public class InventoryFragment extends Fragment  {

    private List<String> tabTitles = new ArrayList<>();

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        // Fetch tab titles from API or set it from a list/array
        fetchTabTitlesFromAPI();

        // Add tabs to the TabLayout
        for (String title : tabTitles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.black));
        tabLayout.setTabRippleColor(null);
//        tabLayout.setTabRippleColorResource(R.color.light_skyblue);
        tabLayout.setUnboundedRipple(false);
        tabLayout.setTabIndicatorFullWidth(true);
        tabLayout.setTabTextColors(getResources().getColor(R.color.dark), getResources().getColor(R.color.black));
        tabLayout.setBackgroundColor(getResources().getColor(R.color.white));
        // listener to handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Replace fragment based on selected tab
                replaceFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        replaceFragment(0);
    }

    private void fetchTabTitlesFromAPI() {
        // implementation for fetching tab titles
        tabTitles.clear();
        tabTitles.add("All");
        tabTitles.add("1st Floor");
        tabTitles.add("2nd Floor");
        tabTitles.add("3rd Floor");
    }

    private void replaceFragment(int position) {
        Fragment fragment = new DynamicInventoryTabFragment();
        Bundle args = new Bundle();
        args.putString("tabTitle", tabTitles.get(position));
        fragment.setArguments(args);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.child_fragment_container, fragment)
                .commit();
    }


}