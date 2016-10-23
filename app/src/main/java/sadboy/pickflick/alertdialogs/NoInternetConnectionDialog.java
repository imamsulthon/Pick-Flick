package sadboy.pickflick.alertdialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;


public class NoInternetConnectionDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Connectivity Problem")
                .setMessage("Please check your network connectivity and try again")

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                getActivity().finish();
                            }
                        })
                .setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent networkSetting = new Intent(
                                        Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(networkSetting);
                            }
                        });

        return builder.create();
    }

}