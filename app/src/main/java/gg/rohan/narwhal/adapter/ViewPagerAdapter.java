package gg.rohan.narwhal.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import gg.rohan.narwhal.ui.pms.DynamicPMSTabFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private List<String> tabTitles;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> tabTitles) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment for the corresponding tab position
        DynamicPMSTabFragment dynamicPMSTabFragment = new DynamicPMSTabFragment();
        Bundle args = new Bundle();
//        Log.e("createFragment: ", String.valueOf(position));
        args.putString("tabTitle", tabTitles.get(position)); // Get title based on position
        dynamicPMSTabFragment.setArguments(args);
        return dynamicPMSTabFragment;
    }

    @Override
    public int getItemCount() {
        // Return the total number of tabs
        return tabTitles.size();
    }
}
