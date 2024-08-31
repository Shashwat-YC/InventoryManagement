package gg.rohan.narwhal.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import gg.rohan.narwhal.ui.inventory.DynamicInventoryTabFragment;

public class InventoryViewPagerAdapter extends FragmentStateAdapter {
    private List<String> tabTitles;

    public InventoryViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> tabTitles) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment for the corresponding tab position
        DynamicInventoryTabFragment dynamicInventoryTabFragment = new DynamicInventoryTabFragment();
        Bundle args = new Bundle();
        args.putString("tabTitle", tabTitles.get(position)); // Get title based on position
        dynamicInventoryTabFragment.setArguments(args);
        return dynamicInventoryTabFragment;
    }

    @Override
    public int getItemCount() {
        // Return the total number of tabs
        return tabTitles.size();
    }
}
