package me.sohier.vrbh.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;

import me.sohier.vrbh.R;
import me.sohier.vrbh.internal.API;
import me.sohier.vrbh.internal.APIClasses.Organisation;
import me.sohier.vrbh.internal.AlertDialogInterface;


public class CreateOrgFragment extends DialogFragment {


    private Response.Listener<Organisation> listener;

    public static CreateOrgFragment newInstance(boolean close_on_cancel, Response.Listener<Organisation> rs) {
        CreateOrgFragment frag = new CreateOrgFragment();
        Bundle args = new Bundle();
        args.putBoolean("cancel", close_on_cancel);

        frag.setArguments(args);
        frag.setListener(rs);
        return frag;
    }

    public void setListener(Response.Listener<Organisation> rs)
    {
        listener = rs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_create_organisation, container, false);


        // Watch for button clicks.
        Button ok = (Button)v.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText text = (EditText)v.findViewById(R.id.org_name);

                API.registerOrg(text.getText().toString(), listener);
            }
        });

        // Watch for button clicks.
        Button cancel = (Button)v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (savedInstanceState.getBoolean("cancel"))
                {
                    getActivity().finish();
                }
                else
                {
                    dismiss();
                }
            }
        });

        return v;
    }
}
