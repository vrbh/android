package me.sohier.vrbh;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import me.sohier.vrbh.Fragments.productDetailFragment;
import me.sohier.vrbh.Fragments.productListFragment;
import me.sohier.vrbh.dummy.DummyContent;
import me.sohier.vrbh.internal.API;


/**
 * An activity representing a list of products. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link productDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link me.sohier.vrbh.Fragments.productListFragment} and the item details
 * (if present) is a {@link me.sohier.vrbh.Fragments.productDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link me.sohier.vrbh.Fragments.productListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class productListActivity extends FragmentActivity
        implements productListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        if (findViewById(R.id.product_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((productListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.product_list))
                    .setActivateOnItemClick(true);
        }


        DummyContent.addItem(new DummyContent.DummyItem("5", " paul test dit 2"));

        productListFragment lf = ((productListFragment) getSupportFragmentManager().findFragmentById(R.id.product_list));
        lf.adapter.notifyDataSetChanged();

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link productListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

        DummyContent.addItem(new DummyContent.DummyItem("6", " paul test dit 3"));
        productListFragment lf = ((productListFragment) getSupportFragmentManager().findFragmentById(R.id.product_list));
        lf.adapter.notifyDataSetChanged();
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(productDetailFragment.ARG_ITEM_ID, id);
            productDetailFragment fragment = new productDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.product_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply main_menu the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, productDetailActivity.class);
            detailIntent.putExtra(productDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);

            return true;
        } else if (id == R.id.action_logout) {
            API.getManager().deleteCredential("userId", null, null);
            // @TODO: Make me nicer.
            API.setCreds(null);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
