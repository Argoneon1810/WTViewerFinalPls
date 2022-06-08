package ark.noah.wtviewerfinalpls;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import ark.noah.wtviewerfinalpls.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements EntryPointGetter.EntryPointParser.Callback {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private boolean bVisible;

    private BackPressEvent backPressEvent;

    SharedPreferences sharedPreferences;

    public interface BackPressEvent {
        boolean onBackPressedExtra();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_key), MODE_PRIVATE);
        String entryUrl = sharedPreferences.getString(getString(R.string.shared_pref_entry_key), EntryPointGetter.EntryPointParser.defaultEntryRoot);

        EntryPointGetter.EntryPointParser.validateLink(entryUrl, this);

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
                R.id.nav_main, R.id.fragmentSettings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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
        if(binding == null) return;

        binding.appBarMain.fabLink.setVisibility(View.VISIBLE);
        binding.appBarMain.fabList.setVisibility(View.VISIBLE);
        binding.appBarMain.fabWeb .setVisibility(View.VISIBLE);

        binding.appBarMain.btnFablabelLink.setVisibility(View.VISIBLE);
        binding.appBarMain.btnFablabelList.setVisibility(View.VISIBLE);
        binding.appBarMain.btnFablabelWeb .setVisibility(View.VISIBLE);
    }
    private void showAllFABs() {
        if(binding == null) return;

        binding.appBarMain.fabOptions.setVisibility(View.VISIBLE);
        showMiniFABs();
    }

    private void hideMiniFABs() {
        if(binding == null) return;

        binding.appBarMain.fabLink.setVisibility(View.GONE);
        binding.appBarMain.fabList.setVisibility(View.GONE);
        binding.appBarMain.fabWeb .setVisibility(View.GONE);

        binding.appBarMain.btnFablabelLink.setVisibility(View.GONE);
        binding.appBarMain.btnFablabelList.setVisibility(View.GONE);
        binding.appBarMain.btnFablabelWeb .setVisibility(View.GONE);
    }
    public void hideAllFABs() {
        if(binding == null) return;

        binding.appBarMain.fabOptions.setVisibility(View.GONE);
        hideMiniFABs();
    }
    //endregion

    //region onclicks
    private void onClickFABWeb(View view) {
        hideAllFABs();
        Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.webFragment);
    }

    private void onClickFABList(View view) {
        hideAllFABs();
        Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.listFragment);
    }

    private void onClickFABLink(View view) {
        hideAllFABs();
        Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.linkFragment);
    }
    //endregion

    public void assignBackPressEvent(BackPressEvent backPressEvent) {
        this.backPressEvent = backPressEvent;
    }

    public void resumedFromMainFragment() { resetFABsToInitialState(); }
    public void resumedFromOtherFragment() { hideAllFABs(); }

    @Override
    public void onValidLinkSet() {
        EntryPointGetter.requestEntryPointLink(null);
    }
}