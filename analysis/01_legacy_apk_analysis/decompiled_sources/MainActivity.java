package com.p001yd.electricecollector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.p000ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.p001yd.electricecollector.common.AppConfig;
import com.p001yd.electricecollector.entities.Users;
import com.p001yd.electricecollector.network.DialogCallback;
import java.util.ArrayList;

/* loaded from: classes6.dex */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    WifiP2pManager.Channel channel;
    private ImageView logo;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private RecyclerView mRecyclerDrawer;
    private Toolbar mToolbar;
    WifiP2pManager manager;
    private BottomNavigationView navigation;
    TextView responseText;
    TabLayout tabs;
    private TextView tvAppName;
    ViewPager viewPager;
    private final IntentFilter intentFilter = new IntentFilter();
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private boolean isPERMISSION_GRANTED = false;
    DialogCallback dialogCallback = new DialogCallback() { // from class: com.yd.electricecollector.MainActivity.1
        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onCancel() {
        }

        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onOk(Object obj) {
            new PreferencesManager(MainActivity.this).setValue(LoginActivity.IS_LOGOUT, true);
            MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), (Class<?>) LoginActivity.class));
            MainActivity.this.finish();
        }

        @Override // com.p001yd.electricecollector.network.DialogCallback
        public void onOk(Object obj, Object obj2) {
        }
    };
    boolean isPermitted = false;

    private void alertView() {
        new AlertDialog.Builder(this, 2132017700).setTitle("Permission Denied").setInverseBackgroundForced(true).setMessage("Without those permission the app is unable to save your profile. App needs to save profile image in your external storage and also need to get profile image from camera or external storage.Are you sure you want to deny this permission?").setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.MainActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).setPositiveButton("RE-TRY", new DialogInterface.OnClickListener() { // from class: com.yd.electricecollector.MainActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                MainActivity.this.checkRunTimePermission();
            }
        }).show();
    }

    private boolean checkAndRequestPermissions() {
        ArrayList arrayList;
        ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION");
        int checkSelfPermission2 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION");
        int checkSelfPermission3 = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS");
        int checkSelfPermission4 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int checkSelfPermission5 = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH");
        int checkSelfPermission6 = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_ADMIN");
        int checkSelfPermission7 = ContextCompat.checkSelfPermission(this, "android.permission.SEND_SMS");
        int checkSelfPermission8 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION");
        int checkSelfPermission9 = ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN");
        ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT");
        ContextCompat.checkSelfPermission(this, "android.permission.MANAGE_EXTERNAL_STORAGE");
        ArrayList arrayList2 = new ArrayList();
        if (checkSelfPermission4 != 0) {
            arrayList = arrayList2;
            arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
        } else {
            arrayList = arrayList2;
        }
        if (checkSelfPermission != 0) {
            arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }
        if (checkSelfPermission5 != 0) {
            arrayList.add("android.permission.BLUETOOTH");
        }
        if (checkSelfPermission6 != 0) {
            arrayList.add("android.permission.BLUETOOTH_ADMIN");
        }
        if (checkSelfPermission7 != 0) {
            arrayList.add("android.permission.SEND_SMS");
        }
        if (checkSelfPermission8 != 0) {
            arrayList.add("android.permission.ACCESS_COARSE_LOCATION");
        }
        if (checkSelfPermission9 != 0) {
            arrayList.add("android.permission.BLUETOOTH_SCAN");
        }
        if (checkSelfPermission9 != 0) {
            arrayList.add("android.permission.BLUETOOTH_CONNECT");
        }
        if (checkSelfPermission2 != 0) {
            arrayList.add("android.permission.ACCESS_FINE_LOCATION");
        }
        if (checkSelfPermission3 != 0) {
            arrayList.add("android.permission.READ_CONTACTS");
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(new String[arrayList.size()]), 1);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkRunTimePermission() {
        requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.READ_CONTACTS", "android.permission.READ_EXTERNAL_STORAGE"}, 11111);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        String companyLogo;
        super.onActivityResult(i, i2, intent);
        if (i != 8 || (companyLogo = TAPreferences.getCompanyLogo(this)) == null) {
            return;
        }
        this.logo.setImageURI(Uri.parse(companyLogo));
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        DialogHelper.msgDialogConfirm("هل تريد انهاءالتطبيق?", this, new DialogCallback() { // from class: com.yd.electricecollector.MainActivity.4
            @Override // com.p001yd.electricecollector.network.DialogCallback
            public void onCancel() {
            }

            @Override // com.p001yd.electricecollector.network.DialogCallback
            public void onOk(Object obj) {
                System.exit(0);
            }

            @Override // com.p001yd.electricecollector.network.DialogCallback
            public void onOk(Object obj, Object obj2) {
                System.exit(0);
            }
        }).create().show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1018R.layout.activity_main);
        this.viewPager = (ViewPager) findViewById(C1018R.id.viewpager);
        this.tabs = (TabLayout) findViewById(C1018R.id.tablayout);
        this.viewPager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        this.tabs.setupWithViewPager(this.viewPager);
        checkAndRequestPermissions();
        Toolbar toolbar = (Toolbar) findViewById(C1018R.id.toolbar);
        setSupportActionBar(toolbar);
        Users user = AppConfig.getInstance().getUser();
        if (user != null) {
            setTitle("المستخدم/ " + user.getname());
        }
        this.mDrawerLayout = (DrawerLayout) findViewById(C1018R.id.drawer_layout);
        new ActionBarDrawerToggle(this, this.mDrawerLayout, toolbar, C1018R.string.navigation_drawer_open, C1018R.string.navigation_drawer_close).syncState();
        NavigationView navigationView = (NavigationView) findViewById(C1018R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(C1018R.id.textViewHeader);
        TextView textView2 = (TextView) headerView.findViewById(C1018R.id.textViewHeaderNote);
        this.logo = (ImageView) headerView.findViewById(C1018R.id.imageView);
        String companyLogo = TAPreferences.getCompanyLogo(this);
        if (companyLogo != null) {
            this.logo.setImageURI(Uri.parse(companyLogo));
        }
        textView.setText(TAPreferences.getCompanyName(this));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.MainActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
            }
        });
        if (TAPreferences.getAnswerKey(this) != null) {
            textView2.setText("المالك : " + TAPreferences.getLicenseOwner(this));
        }
        this.logo.setOnClickListener(new View.OnClickListener() { // from class: com.yd.electricecollector.MainActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MainActivity.this.startActivityForResult(new Intent(MainActivity.this, (Class<?>) LogoActivity.class), 8);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        checkRunTimePermission();
        if (new PreferencesManager(this).getValue("isResetPass", false)) {
            return;
        }
        startActivityForResult(new Intent(this, (Class<?>) ChangePasswordActivity.class), 10);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1018R.menu.main, menu);
        return true;
    }

    @Override // com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == C1018R.id.nav_home) {
            startActivity(new Intent(this, (Class<?>) AboutActivity.class));
        } else if (itemId == C1018R.id.nav_gallery) {
            startActivity(new Intent(this, (Class<?>) Preferences.class));
        } else if (itemId == C1018R.id.nav_resetpass) {
            startActivity(new Intent(this, (Class<?>) ChangePasswordActivity.class));
        } else if (itemId == C1018R.id.nav_slideshow) {
            DialogHelper.msgDialogConfirm("هل تريد تسجيل خروج مستخدم?", this, this.dialogCallback).create().show();
        } else if (itemId == C1018R.id.nav_tools) {
            System.exit(0);
            ((DrawerLayout) findViewById(C1018R.id.drawer_layout)).closeDrawer(GravityCompat.START);
            return true;
        }
        ((DrawerLayout) findViewById(C1018R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == C1018R.id.action_logout) {
            DialogHelper.msgDialogConfirm("هل تريد تسجيل الخروج من جلسة العمل?", this, this.dialogCallback).create().show();
        } else if (menuItem.getItemId() == C1018R.id.action_settings) {
            startActivity(new Intent(this, (Class<?>) Preferences.class));
        }
        if (menuItem.getItemId() != C1018R.id.nav_home) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.END);
            return true;
        }
        this.mDrawerLayout.openDrawer(GravityCompat.END);
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 11111) {
            for (int i2 = 0; i2 < iArr.length; i2++) {
                String str = strArr[i2];
                this.isPermitted = iArr[i2] == 0;
                if (iArr[i2] == -1 && shouldShowRequestPermissionRationale(str) && 1 != 0) {
                    alertView();
                }
            }
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity
    public boolean onSupportNavigateUp() {
        return true;
    }
}
