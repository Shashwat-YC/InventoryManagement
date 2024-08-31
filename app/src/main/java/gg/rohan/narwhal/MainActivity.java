package gg.rohan.narwhal;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;
import java.util.Objects;

import gg.rohan.narwhal.databinding.ActivityMainBinding;
import gg.rohan.narwhal.rfid.ReaderStaticWrapper;
import gg.rohan.narwhal.rfid.result.ReaderConnectResult;
import gg.rohan.narwhal.rfid.result.ReaderOpenResult;
import gg.rohan.narwhal.rfid.result.ReaderWakeupResult;
import gg.rohan.narwhal.ui.CheckInFragment;
import gg.rohan.narwhal.ui.CheckOutFragment;
import gg.rohan.narwhal.ui.HamburgerMenu;
import gg.rohan.narwhal.ui.InventoryFragment;
import gg.rohan.narwhal.ui.PMSFragment;
import gg.rohan.narwhal.ui.SearchManager;
import gg.rohan.narwhal.ui.checkin.NewSpareDetailFragment;
import gg.rohan.narwhal.ui.checkin.SpareFragment;
import gg.rohan.narwhal.ui.inventory.DynamicInventoryTabFragment;
import gg.rohan.narwhal.ui.inventory.InventoryAddPartFirstFragment;
import gg.rohan.narwhal.ui.inventory.InventoryNewFloorFragment;
import gg.rohan.narwhal.ui.inventory.InventoryProductFragment;
import gg.rohan.narwhal.ui.inventory.StockReconcilationFragment;
import gg.rohan.narwhal.ui.pms.DynamicPMSTabFragment;
import gg.rohan.narwhal.ui.pms.PMSPlanningFragment;
import gg.rohan.narwhal.ui.pms.PMSCompletedFragment;
import gg.rohan.narwhal.ui.pms.PMSInProgressFragment;
import gg.rohan.narwhal.util.Storage;
import gg.rohan.narwhal.util.api.RetrofitClient;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchManager.SearchListener {
    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigationView;
    private SearchManager searchManager;
    EditText searchInput;
    private HamburgerMenu hamburgerMenu;
    private boolean screensSetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set screen orientation to portrait
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);
        searchInput = findViewById(R.id.searchInput);
        Storage.setupDefaults(this);
        RetrofitClient.setup(this);
        NewRetrofitClient.setup(this);
        this.setupReader();
        // Set the initial fragment

        searchManager = new SearchManager();
        searchManager.setSearchListener(this);

        hamburgerMenu = new HamburgerMenu(this);
        hamburgerMenu.setupDrawer();
        checkForIp();
    }

    public void checkForIp() {
        if (invalidIp()) {
            hamburgerMenu.openIpEditor();
        } else {
            setupUiScreens();
        }
    }

    public void setupUiScreens() {
        if (screensSetup) {
            return;
        }
        screensSetup = true;
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                new PMSFragment()).commit();
        // Register back stack listener
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString().trim();
                searchManager.triggerSearch(query);
                return true;
            }
            return false;
        });
    }

    private boolean invalidIp() {
        Response<Void> resp = NewRetrofitClient.syncTestConnection();
        if (resp == null || !resp.isSuccessful()) {
            Log.d("MainActivity", "Connection failed");
            return true;
        }
        return false;
    }

    @Override
    public void onSearch(String query) {
        Fragment fragment = getCurrentFragment();
        String fragmentName = fragment.getClass().getSimpleName();
        Log.d("MainActivity", "Current Fragment: " + fragmentName);
        // if fragment is InventoryFragment, then search in DynamicInventoryTabFragment
        if (fragmentName.equals("InventoryFragment")) {
            Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.child_fragment_container);
            // Check if the fragment is an instance of ..
            if (fragment2 instanceof DynamicInventoryTabFragment) {
                ((DynamicInventoryTabFragment) fragment2).performSearch(query);
            }
            if (fragment2 instanceof InventoryNewFloorFragment) {
                ((InventoryNewFloorFragment) fragment2).performSearch(query);
            }
            if (fragment2 instanceof InventoryProductFragment) {
                ((InventoryProductFragment) fragment2).performSearch(query);
            }
            if (fragment2 instanceof StockReconcilationFragment) {
                ((StockReconcilationFragment) fragment2).performSearch(query);
            }
            if (fragment2 instanceof InventoryAddPartFirstFragment) {
                ((InventoryAddPartFirstFragment) fragment2).performSearch(query);
            }
        } else if (fragmentName.equals("CheckInFragment") || fragmentName.equals("SpareFragment") || fragmentName.equals("NewSpareFragment")) {
            Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.check_in_child_fragment_container);
            if (fragment2 instanceof SpareFragment) {
                ((SpareFragment) fragment2).performSearch(query);
            }
//                if (fragment2 instanceof NewSpareFragment) {
//                    ((NewSpareFragment) fragment2).performSearch(query);
//                }
            Fragment fragment4 = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment4 instanceof NewSpareDetailFragment) {
                ((NewSpareDetailFragment) fragment2).performSearch(query);
            }
        } else if (fragmentName.equals("CheckOutFragment")) {
            Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            if (fragment2 instanceof CheckOutFragment) {
                ((CheckOutFragment) fragment2).performSearch(query);
            }
        } else if (fragmentName.equals("PMSFragment") || fragmentName.equals("DynamicPMSTabFragment") || fragmentName.equals("NewPMSProductFragment") || fragmentName.equals("PMSCompletedFragment") || fragmentName.equals("PMSInProgressFragment")) {
//            Log.d("MainActivity", "Performing search in DynamicPMSTabFragment");
            if (fragmentName.equals("PMSFragment")) {
                Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.pms_child_fragment_container);
                if (fragment2 instanceof DynamicPMSTabFragment) {
                    ((DynamicPMSTabFragment) fragment2).performSearch(query);
                }
            }
            Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//            if (fragment2 instanceof DynamicPMSTabFragment) {
////                Log.d("MainActivity", "Performing search in DynamicPMSTabFragment");
//                ((DynamicPMSTabFragment) fragment2).performSearch(query);
//            }
            if (fragment2 instanceof PMSPlanningFragment) {
                ((PMSPlanningFragment) fragment2).performSearch(query);
            }
            if (fragment2 instanceof PMSCompletedFragment) {
                ((PMSCompletedFragment) fragment2).performSearch(query);
            }
            if (fragment2 instanceof PMSInProgressFragment) {
                ((PMSInProgressFragment) fragment2).performSearch(query);
            }
        } else {
            Log.d("MainActivity", "No active fragment found");
        }
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();

        if (!fragments.isEmpty()) {
            for (Fragment fragment : fragments) {
                if (fragment.isVisible()) {
                    return fragment;
                }
            }
        }

        return null;
    }

    private void setupReader() {
        ReaderStaticWrapper.initializeHandler();
        openReaderConnection();
        ReaderStaticWrapper.clearCurrentTasks();
    }

    public void openReaderConnection() {
        ReaderStaticWrapper.reInstallReader(this);
        ReaderOpenResult openResult = ReaderStaticWrapper.openReaderConnection();
        if (openResult == ReaderOpenResult.FAIL) {
            // Handle failure
            return;
        }
        ReaderWakeupResult wakeupResult = ReaderStaticWrapper.wakeupReader();
        ReaderConnectResult connectResult = ReaderStaticWrapper.connectReader();
        if (connectResult != ReaderConnectResult.SUCCESS) {
            // Handle failure
            return;
        }
    }

    private BottomNavigationView.OnItemSelectedListener navListener =
            new BottomNavigationView.OnItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.pms) {
                        selectedFragment = new PMSFragment();
                    } else if (item.getItemId() == R.id.inventory) {
                        selectedFragment = new InventoryFragment();
                    } else if (item.getItemId() == R.id.check_in) {
                        selectedFragment = new CheckInFragment();
                    } else if (item.getItemId() == R.id.check_out) {
                        selectedFragment = new CheckOutFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment,
                                selectedFragment).commit();
                        return true;
                    }
                    return false;
                }
            };

}