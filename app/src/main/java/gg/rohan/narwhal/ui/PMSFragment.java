package gg.rohan.narwhal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import gg.rohan.narwhal.adapter.ViewPagerAdapter;
import gg.rohan.narwhal.R;
import gg.rohan.narwhal.ui.inventory.DynamicInventoryTabFragment;
import gg.rohan.narwhal.ui.pms.DynamicPMSTabFragment;

public class PMSFragment extends Fragment {

    private List<String> tabTitles = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public PMSFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tab_layout);

        // Fetch tab titles from API or set it from a list/array
        fetchTabTitlesFromAPI(); // Implement this method to fetch titles from API

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
        // Implement this method to fetch tab titles from API
        // Replace with API fetching data
        tabTitles.clear();
        tabTitles.add("Sailing");
        tabTitles.add("Port");
        tabTitles.add("Dock");
    }

    private void replaceFragment(int position) {
        Fragment fragment = new DynamicPMSTabFragment();
        Bundle args = new Bundle();
        args.putString("tabTitle", tabTitles.get(position));
        fragment.setArguments(args);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.pms_child_fragment_container, fragment)
                .commit();
    }

}

