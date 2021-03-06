package com.autocontrol.coc.cocautomanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.autocontrol.coc.cocautomanagement.service.LongRunningService;
import com.autocontrol.coc.cocautomanagement.service.SystemManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //获取root权限
        String apkRoot = "chmod 777 " + getPackageCodePath();
        SystemManager.RootCommand(apkRoot);
        //启动请示开战检测
        Intent serintent = new Intent(this, LongRunningService.class);
        startService(serintent);



    }


    @Override
    protected void onResume() {
        super.onResume();


//        File externalStorageDirectory = Environment.getExternalStorageDirectory();
//        System.out.println("内存卡路径" + externalStorageDirectory.getAbsolutePath());
//        String path1 = externalStorageDirectory.getAbsolutePath() + "/cocl.png";
//        String path2 = externalStorageDirectory.getAbsolutePath() + "/cocl-ai.png";

//        ImagePHash p = new ImagePHash();
//        String image1;
//        String image2;
//        try {
//            image1 = p.getHash(path1);
//            image2 = p.getHash(path2);
//            System.out.println("得分为 " + p.distance(image1, image2));
//        } catch (Exception e) {
//            Log.e("搓搓粗偶", e.toString());
//            e.printStackTrace();
//        }
//        long s = System.currentTimeMillis();
//
//        SearchImage searchImage = new SearchImage();
//        ArrayList<ResultXYBean> resultXYBeans = searchImage.searchImage(path1, path2);
//        System.out.println("得到的结果" + GsonUtil.GsonString(resultXYBeans));
//        long e = System.currentTimeMillis();
//        System.out.println("时间测试=" + (e - s));

        boolean canShow = LiveWindowManager.createSmallWindow(this);
//        if (canShow) {
//            moveTaskToBack(true);
//        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
