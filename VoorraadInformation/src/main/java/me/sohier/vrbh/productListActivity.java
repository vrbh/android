package me.sohier.vrbh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import me.sohier.vrbh.Fragments.productDetailFragment;
import me.sohier.vrbh.Fragments.productListFragment;
import me.sohier.vrbh.internal.API;
import me.sohier.vrbh.internal.APIClasses.Prd;
import me.sohier.vrbh.internal.APIClasses.User;


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
    private User user;
    private int org;

    private Menu optionsMenu;

    private boolean refreshing;

    private Handler refresh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("vrbh/productlistactivity/refresh", "Received a message to change refresh");
            if (optionsMenu != null) {
                final MenuItem refreshItem = optionsMenu
                        .findItem(R.id.action_refresh);
                if (refreshItem != null) {
                    if (refreshing) {
                        refreshItem.setActionView(R.layout.menu_progress);
                    } else {
                        refreshItem.setActionView(null);
                    }
                }
            }
            else
            {
                Log.d("vrbh/productlistactivity/refresh", "Optionsmenu is null");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        user = (User)getIntent().getSerializableExtra("user");
        org = getIntent().getIntExtra("org", 0);

        productListFragment lf =  ((productListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.product_list));

        Prd prd = new Prd();
        prd.name = "Testing...";

        API.addProduct(prd);

        if (findViewById(R.id.product_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.

            lf.setActivateOnItemClick(true);
        }
    }

    public void refresh(boolean rf)
    {
        Log.d("vrbh/productlist/changerefresh", "Changing refresh to " + rf);
        refreshing = rf;
        refresh.sendEmptyMessage(0);
    }

    /**
     * Callback method from {@link productListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Prd product) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable("product", product);
            productDetailFragment fragment = new productDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.product_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply main_menu the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, productDetailActivity.class);
            detailIntent.putExtra("product", product);
            detailIntent.putExtra("user", user);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;

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
        else if (id == R.id.action_refresh)
        {
            refresh(true);
return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
