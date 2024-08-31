package gg.rohan.narwhal.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import gg.rohan.narwhal.ui.checkin.NewSpareFragment;
import gg.rohan.narwhal.ui.checkin.SpareFragment;

public class CheckInViewPagerAdapter extends FragmentStateAdapter {
    private List<String> tabTitles;

    public CheckInViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<String> tabTitles) {
        super(fragmentActivity);
        this.tabTitles = tabTitles;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment for the corresponding tab position
        NewSpareFragment newSpareFragment = new NewSpareFragment();
        Bundle args = new Bundle();
        args.putString("tabTitle", tabTitles.get(position));
        newSpareFragment.setArguments(args);
        switch (position) {
            case 0:
                return new SpareFragment();
            case 1:
            case 2:
                return newSpareFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        // Return the total number of tabs
        return tabTitles.size();
    }
}
