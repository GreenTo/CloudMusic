package com.example.cloudmusic.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.bumptech.glide.Glide;
import com.coder.zzq.smartshow.toast.SmartToast;
import com.example.cloudmusic.R;
import com.example.cloudmusic.dialog.CardPickerDialog;
import com.example.cloudmusic.domain.UserData;
import com.example.cloudmusic.fragment.HomeFragment;
import com.example.cloudmusic.fragment.MusicFragment;
import com.example.cloudmusic.fragment.VideoFragment;
import com.example.cloudmusic.util.LogUtil;
import com.example.cloudmusic.util.OKhttpUtil;
import com.example.cloudmusic.util.ThemeHelper;
import com.example.cloudmusic.widget.CustomViewPager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends BaseActivity implements View.OnClickListener,CardPickerDialog.ClickListener {

    ActionBar actionBar;

    DrawerLayout drawerLayout;

    UserData userData;

    NavigationView navigationView;

    //Menu navMenu;
    //
    //MenuItem nightItem;
    //
    //MenuItem changeItem;
    //
    //MenuItem timerItem;
    //
    //MenuItem exitItem;

    CircleImageView headImageView;

    TextView nicknameText;

    TextView levelText;

    ImageView bgImageView;

    CustomViewPager customViewPager;

    List<ImageView> imageViewList = new ArrayList<>();

    ImageView homeImage,musicImage,videoImage;

    static final String TAG = "MainActivity";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);

        homeImage = findViewById(R.id.bar_home);
        musicImage = findViewById(R.id.bar_music);
        videoImage = findViewById(R.id.bar_video);

        setToolbar();
        setViewPager();
        initNavMenu();
        getUserDetail();
    }

    private void initNavMenu() {
        navigationView = findViewById(R.id.navigation_view);
        //navMenu = navigationView.getMenu();
        //nightItem = navMenu.getItem(R.id.nav_night);
        //changeItem = navMenu.getItem(R.id.nav_change);
        //timerItem = navMenu.getItem(R.id.nav_timer);
        //exitItem = navMenu.getItem(R.id.nav_exit);
        navigationView.setCheckedItem(R.id.nav_night);
        navigationView.setItemTextColor(null);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_change:
                        CardPickerDialog dialog = new CardPickerDialog();
                        dialog.setClickListener(MainActivity.this);
                        dialog.show(getSupportFragmentManager(),"theme");
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onConfirm(int currentTheme) {
        if (ThemeHelper.getTheme(MainActivity.this) != currentTheme) {
            ThemeHelper.setTheme(MainActivity.this, currentTheme);
            ThemeUtils.refreshUI(MainActivity.this, new ThemeUtils.ExtraRefreshable() {
                        @Override
                        public void refreshGlobal(Activity activity) {
                            //for global setting, just do once
                            if (Build.VERSION.SDK_INT >= 21) {
                                final MainActivity context = MainActivity.this;
                                ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(null, null, ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary));
                                setTaskDescription(taskDescription);
                                getWindow().setStatusBarColor(ThemeUtils.getColorById(context, R.color.theme_color_primary));
                            }
                        }

                        @Override
                        public void refreshSpecificView(View view) {
                        }
                    }
            );
        }
        //changeTheme();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        findViewById(R.id.drawer_layout).setFitsSystemWindows(false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        toolbar.setPadding(0,50,0,0);
    }

    private void setViewPager(){
        imageViewList.add(homeImage);
        imageViewList.add(musicImage);
        imageViewList.add(videoImage);

        customViewPager = findViewById(R.id.vp_content);
        final HomeFragment homeFragment = new HomeFragment();
        final MusicFragment musicFragment = new MusicFragment();
        final VideoFragment videoFragment = new VideoFragment();

        CustomViewPagerAdapter adapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(homeFragment);
        adapter.addFragment(musicFragment);
        adapter.addFragment(videoFragment);

        customViewPager.setAdapter(adapter);
        customViewPager.setCurrentItem(0);
        homeImage.setSelected(true);

        customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                for (int j = 0; j < imageViewList.size(); j++) {
                    if (i == j) {
                        imageViewList.get(j).setSelected(true);
                    } else {
                        imageViewList.get(j).setSelected(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    static class CustomViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public CustomViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }

    private void getUserDetail(){
        Intent intent = getIntent();
        long uid = intent.getLongExtra("uid", 0);
        OKhttpUtil.getUserDetail(uid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SmartToast.error("服务器发生错误，请稍后...");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                byte[] bytes = body.bytes();
                String responseData = new String(bytes);
                userData = new Gson().fromJson(responseData,UserData.class);
                if (userData != null) {
                    LogUtil.d(TAG,userData.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleUserData(userData);
                        }
                    });
                }
            }
        });
    }

    private void handleUserData(UserData userData) {
        bgImageView = findViewById(R.id.user_bg_Image);
        headImageView = findViewById(R.id.user_head_image);
        nicknameText = findViewById(R.id.nickname_text);
        levelText = findViewById(R.id.level_text);
        Glide.with(this).load(userData.profile.backgroundUrl).into(bgImageView);
        Glide.with(this).load(userData.profile.avatarUrl).into(headImageView);
        nicknameText.setText(userData.profile.nickname);
        levelText.setText("Lv." + userData.level);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_home:
                customViewPager.setCurrentItem(0);
                break;
            case R.id.bar_music:
                customViewPager.setCurrentItem(1);
                break;
            case R.id.bar_video:
                customViewPager.setCurrentItem(2);
                break;
        }
    }
}
