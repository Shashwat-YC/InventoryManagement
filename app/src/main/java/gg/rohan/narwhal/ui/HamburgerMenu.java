package gg.rohan.narwhal.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import gg.rohan.narwhal.MainActivity;
import gg.rohan.narwhal.R;
import gg.rohan.narwhal.newmodel.MachineInfo;
import gg.rohan.narwhal.util.Storage;
import gg.rohan.narwhal.util.newapi.NewRetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HamburgerMenu {

    private MainActivity activity;

    public HamburgerMenu(MainActivity activity) {
        this.activity = activity;
    }

    private ProgressDialog progressDialog;

    public void openIpEditor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        LayoutInflater inflater = this.activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_ip_dialog, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        EditText ipEditText = dialogView.findViewById(R.id.et_ip_address);
        EditText portEditText = dialogView.findViewById(R.id.et_port_field);

        ipEditText.setText(Storage.getString(this.activity, "ip_address", ""));
        portEditText.setText(Storage.getString(this.activity, "port", ""));

        ImageView closeButton = dialogView.findViewById(R.id.close_btn_icon);
        AppCompatButton testConnBtn = dialogView.findViewById(R.id.test_con_btn);
        AppCompatButton saveButton = dialogView.findViewById(R.id.update_btn);
        testConnBtn.setOnClickListener(v -> {
            String ip = ipEditText.getText().toString();
            String port = portEditText.getText().toString();
            Storage.saveString(this.activity, "ip_address", ip);
            Storage.saveString(this.activity, "port", port);
            Toast.makeText(this.activity, "Testing Connection...", Toast.LENGTH_SHORT).show();
            NewRetrofitClient.rebuildRetrofit(this.activity);
            Response<Void> response = NewRetrofitClient.syncTestConnection();
            if (response != null && response.isSuccessful()) {
                Toast.makeText(this.activity, "Connection Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.activity, "Connection failed", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> alertDialog.dismiss());

        saveButton.setOnClickListener(v -> {
            String ip = ipEditText.getText().toString();
            String port = portEditText.getText().toString();
            Storage.saveString(this.activity, "ip_address", ip);
            Storage.saveString(this.activity, "port", port);
            NewRetrofitClient.rebuildRetrofit(this.activity);
            alertDialog.dismiss();
        });

        alertDialog.setOnDismissListener((v) -> this.activity.checkForIp());
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Testing Connection...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    public void setupDrawer() {
        SecondaryDrawerItem connectStatItem = new SecondaryDrawerItem()
                .withIdentifier(3)
                .withName("Not Connected")
                .withEnabled(false)
                .withSelectable(false);

        Drawer drawer = new DrawerBuilder()
                .withActivity(this.activity)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIdentifier(1)
                                .withName("Connect")
                                .withOnDrawerItemClickListener((view, position, item) -> {
                                    if (item.getIdentifier() != 1) return false;
                                    this.activity.openReaderConnection();
                                    return false;
                                })
                                .withSelectable(false),
                        new DividerDrawerItem()
                                .withSelectable(false),
                        new PrimaryDrawerItem()
                                .withIdentifier(2)
                                .withName("Change ip")
                                .withOnDrawerItemClickListener((view, position, item) -> {
                                    if (item.getIdentifier() != 2) return false;
                                    openIpEditor();
                                    return false;
                                })
                                .withSelectable(false),
                        new DividerDrawerItem()
                                .withSelectable(false),
                        connectStatItem
                )
                .withSelectedItem(-1)
                .build();

        this.activity.findViewById(R.id.imageView).setOnClickListener(v -> drawer.openDrawer());
    }

}
