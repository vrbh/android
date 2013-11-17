package me.sohier.vrbh.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import me.sohier.vrbh.R;
import me.sohier.vrbh.internal.AlertDialogInterface;


public class NoProductsYetFragment extends android.support.v4.app.DialogFragment {


    private AlertDialogInterface adi;

    public static NoProductsYetFragment newInstance(int title, AlertDialogInterface adi) {
        NoProductsYetFragment frag = new NoProductsYetFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);

        frag.setInterface(adi);

        frag.setArguments(args);
        return frag;
    }

    private void setInterface(AlertDialogInterface adi) {
        this.adi = adi;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                //.setIcon(R.drawable.alert_dialog_icon)
                .setTitle(title)
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                adi.doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                adi.doNegativeClick();
                            }
                        }
                )
                .create();
    }
}
