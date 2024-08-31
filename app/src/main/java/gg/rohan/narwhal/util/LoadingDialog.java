package gg.rohan.narwhal.util;



import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.airbnb.lottie.LottieAnimationView;

import gg.rohan.narwhal.R;


public class LoadingDialog {
   private Activity activity;
    private AlertDialog dialog;
    private LottieAnimationView lottieAnimationView;

    public LoadingDialog(Activity myactivity){
        activity =myactivity;
    }
    public void startLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.TranslucentDialog);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_loader,null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }
    public void dismissDialog(){
        dialog.dismiss();
    }
}
