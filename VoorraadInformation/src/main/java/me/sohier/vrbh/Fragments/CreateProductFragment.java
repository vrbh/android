package me.sohier.vrbh.Fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;

import me.sohier.vrbh.R;
import me.sohier.vrbh.internal.APIClasses.Prd;


public class CreateProductFragment extends DialogFragment {


    private Response.Listener<String> listener;
    private Prd product;

    public static CreateProductFragment newInstance(Prd product, Response.Listener<String> rs) {
        if (product == null) {
            product = new Prd();
        }

        CreateProductFragment frag = new CreateProductFragment();
        Bundle args = new Bundle();
        args.putSerializable("product", product);

        frag.setArguments(args);
        frag.setListener(rs);
        return frag;
    }

    public void setListener(Response.Listener<String> rs) {
        listener = rs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.dialog_create_product, container, false);

        product = (Prd) getArguments().getSerializable("product");

        final EditText name = (EditText) v.findViewById(R.id.product_name);
        final EditText description = (EditText) v.findViewById(R.id.product_description);
        final EditText orderNumber = (EditText) v.findViewById(R.id.product_order_nr);
        final EditText ean = (EditText) v.findViewById(R.id.product_ean);
        final EditText stockUnit = (EditText) v.findViewById(R.id.product_stock_unit);
        final EditText orderUnit = (EditText) v.findViewById(R.id.product_order_unit);
        final EditText minStock = (EditText) v.findViewById(R.id.product_min_stock);
        final EditText maxStock = (EditText) v.findViewById(R.id.product_max_stock);

        if (product.id == 0) {
            getDialog().setTitle(getString(R.string.create_product));
        } else {
            getDialog().setTitle(getString(R.string.update_product));

            name.setText(product.name);
            description.setText(product.description);
            orderNumber.setText(product.orderNumber);
            ean.setText(product.ean);
            stockUnit.setText(product.stockUnit);
            orderUnit.setText(product.orderUnit);
            minStock.setText(product.minStock);
            maxStock.setText(product.maxStock);
        }


        // Watch for button clicks.
        Button ok = (Button) v.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v2) {
                product.name = name.getText().toString();
                product.description = description.getText().toString();
                product.orderNumber = orderNumber.getText().toString();
                try {
                    product.ean = Integer.parseInt(ean.getText().toString());
                } catch (NumberFormatException ignored) {
                }
                product.stockUnit = stockUnit.getText().toString();
                product.orderNumber = orderUnit.getText().toString();
                try {
                    product.minStock = Integer.parseInt(minStock.getText().toString());

                } catch (NumberFormatException ignored) {
                }
                try {
                    product.maxStock = Integer.parseInt(maxStock.getText().toString());
                } catch (NumberFormatException ignored) {
                }
                dismiss();
                //API.addProductToServer(product, listener);
            }
        });

        // Watch for button clicks.
        Button cancel = (Button) v.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v2) {
                dismiss();
            }
        });

        return v;
    }
}
