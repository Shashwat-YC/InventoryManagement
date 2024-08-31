package gg.rohan.narwhal.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import gg.rohan.narwhal.R;
import gg.rohan.narwhal.ui.checkin.NewSpareFragment;
import gg.rohan.narwhal.ui.checkin.SpareFragment;


public class CheckInFragment extends Fragment {

    private List<String> tabTitles = new ArrayList<>();

    public CheckInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        // Fetch tab titles from API or set it from a list/array
        fetchTabTitlesFromAPI(); // Implement this method to fetch titles from API

        // Add tabs to the TabLayout
        for (String title : tabTitles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.black));
        tabLayout.setTabRippleColor(null);
        tabLayout.setUnboundedRipple(false);
        tabLayout.setTabIndicatorFullWidth(true);
        tabLayout.setTabTextColors(getResources().getColor(R.color.dark), getResources().getColor(R.color.black));
        tabLayout.setBackgroundColor(getResources().getColor(R.color.white));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
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
        tabTitles.clear();
        tabTitles.add("Spares");
        tabTitles.add("New Spares");
        tabTitles.add("DryDock");
    }

    private void replaceFragment(int position) {
        switch (position) {
            case 0:
                navigateToSpareFragment(position);
                break;
            case 1:
            case 2:
                navigateToNewSpareFragment(position);
                break;
            default:
                break;
        }

    }
    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.check_in_child_fragment_container, fragment)
                .commit();
    }

    // Example usage
    private void navigateToSpareFragment(int position) {
        Bundle args = new Bundle();
        args.putString("tabTitle", tabTitles.get(position));
        SpareFragment spareFragment = new SpareFragment();
        spareFragment.setArguments(args);
        replaceFragment(spareFragment);
    }

    private void navigateToNewSpareFragment(int position) {
        Bundle args = new Bundle();
        args.putString("tabTitle", tabTitles.get(position));
        NewSpareFragment newSpareFragment = new NewSpareFragment();
        newSpareFragment.setArguments(args);
        replaceFragment(newSpareFragment);
    }

}