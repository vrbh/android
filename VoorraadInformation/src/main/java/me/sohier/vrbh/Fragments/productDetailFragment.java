package me.sohier.vrbh.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.sohier.vrbh.R;
import me.sohier.vrbh.internal.APIClasses.Prd;

/**
 * A fragment representing a single product detail screen.
 * This fragment is either contained in a {@link me.sohier.vrbh.productListActivity}
 * in two-pane mode (on tablets) or a {@link me.sohier.vrbh.productDetailActivity}
 * on handsets.
 */
public class productDetailFragment extends Fragment {
    /**
     * The dummy content this fragment is presenting.
     */
    private Prd mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public productDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("product")) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = (Prd)getArguments().getSerializable("product");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.product_detail)).setText(mItem.content);
        }

        return rootView;
    }
}
