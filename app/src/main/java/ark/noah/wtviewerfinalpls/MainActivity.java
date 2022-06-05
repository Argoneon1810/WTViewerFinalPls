package ark.noah.wtviewerfinalpls;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import ark.noah.wtviewerfinalpls.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private boolean bVisible;

    private BackPressEvent backPressEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        EntryPointGetter.requestEntryPointLink(null);

        bVisible = false;
        binding.appBarMain.fabWeb.setOnClickListener(this::onClickFABWeb);
        binding.appBarMain.fabList.setOnClickListener(this::onClickFABList);
        binding.appBarMain.fabLink.setOnClickListener(this::onClickFABLink);
        binding.appBarMain.btnFablabelWeb.setOnClickListener(this::onClickFABWeb);
        binding.appBarMain.btnFablabelList.setOnClickListener(this::onClickFABList);
        binding.appBarMain.btnFablabelLink.setOnClickListener(this::onClickFABLink);
        binding.appBarMain.fabOptions.setOnClickListener(view -> {
            if(bVisible) hideMiniFABs();
            else showMiniFABs();
            bVisible = !bVisible;
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_main)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(backPressEvent!=null) {
            if(!backPressEvent.onBackPressedExtra()) {
                backPressEvent = null;
                return false;
            }
            backPressEvent = null;
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //region FAB onoff
    public void resetFABsToInitialState() {
        bVisible = false;
        showAllFABs();
        hideMiniFABs();
    }

    private void showMiniFABs() {
        binding.appBarMain.fabLink.setVisibility(View.VISIBLE);
        binding.appBarMain.fabList.setVisibility(View.VISIBLE);
        binding.appBarMain.fabWeb .setVisibility(View.VISIBLE);

        binding.appBarMain.btnFablabelLink.setVisibility(View.VISIBLE);
        binding.appBarMain.btnFablabelList.setVisibility(View.VISIBLE);
        binding.appBarMain.btnFablabelWeb .setVisibility(View.VISIBLE);
    }
    private void showAllFABs() {
        binding.appBarMain.fabOptions.setVisibility(View.VISIBLE);
        showMiniFABs();
    }

    private void hideMiniFABs() {
        binding.appBarMain.fabLink.setVisibility(View.GONE);
        binding.appBarMain.fabList.setVisibility(View.GONE);
        binding.appBarMain.fabWeb .setVisibility(View.GONE);

        binding.appBarMain.btnFablabelLink.setVisibility(View.GONE);
        binding.appBarMain.btnFablabelList.setVisibility(View.GONE);
        binding.appBarMain.btnFablabelWeb .setVisibility(View.GONE);
    }
    public void hideAllFABs() {
        binding.appBarMain.fabOptions.setVisibility(View.GONE);
        hideMiniFABs();
    }
    //endregion

    //region onclicks
    private void onClickFABWeb(View view) {
        hideAllFABs();
        //Log.i("", "this is web fab");
        Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.webFragment);
    }

    private void onClickFABList(View view) {
        hideAllFABs();
//        Log.i("", "this is list fab");
        Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.listFragment);
    }

    private void onClickFABLink(View view) {
        hideAllFABs();
//        Log.i("", "this is link fab");
        Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.linkFragment);
    }
    //endregion

    public interface BackPressEvent {
        boolean onBackPressedExtra();
    }

    public void assignBackPressEvent(BackPressEvent backPressEvent) {
        this.backPressEvent = backPressEvent;
    }

    public void resumedFromMainFragment() { resetFABsToInitialState(); }
    public void resumedFromOtherFragment() { hideAllFABs();}
}