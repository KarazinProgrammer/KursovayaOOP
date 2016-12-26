package com.example.root.navigationdrawertest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.navigationdrawertest.Core.ClientUDP;
import com.example.root.navigationdrawertest.Core.ServerUDP;
import com.example.root.navigationdrawertest.Core.Translator;
import com.example.root.navigationdrawertest.Core.User;
import com.example.root.navigationdrawertest.Core.UserSenderTimerTask;
import com.example.root.navigationdrawertest.Dialog.Dialog;
import com.example.root.navigationdrawertest.Dialog.DialogAdapter;
import com.example.root.navigationdrawertest.Dialog.DialogListFragment;
import com.example.root.navigationdrawertest.InitialActivity.InitialActivity;
import com.example.root.navigationdrawertest.OnlineList.OnlineListFragment;
import com.example.root.navigationdrawertest.data.ChatDbHelper;
import com.example.root.navigationdrawertest.data.DataContract;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private View headerLayout;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private User iAm;
    private ServerUDP serverUDP;
    private UserSenderTimerTask myInfoTimerTask;
    private Timer mainActivityTimer;
    private SharedPreferences sPref;
    private Handler handler;
    private ChatDbHelper dbHelper;
    private ClientUDP clientUDP;
    private DialogAdapter dialogAdapter;
    private ArrayList<Dialog> dialogs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerLayout = navigationView.getHeaderView(0);

        //<--START
        iAm = new User(null, "default", 1);
        try {
            iAm.macToByte(getMacAddress(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbHelper = new ChatDbHelper(this);
        try {
            clientUDP = new ClientUDP();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Translator translator = new Translator(iAm, dbHelper, getBaseContext());
        serverUDP = new ServerUDP(clientUDP, translator);
        serverUDP.start();

        Intent intent = new Intent(this, InitialActivity.class);
        startActivityForResult(intent, 1);


        dialogs = dbHelper.getDialogs();

        dialogAdapter = new DialogAdapter(this, dialogs);

        mainActivityTimer = new Timer();

        handler = new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
                switch (msg.what){
                    //нужно запустить IniActivity for Result из myTimerTask
                    case 1:
                        Intent intent = new Intent(MainActivity.this, InitialActivity.class);
                        intent.putExtra("unique", false);
                        startActivityForResult(intent, 1);
                        break;
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //выбор ника и картинки
            case 1:
                if(resultCode == RESULT_OK) {
                    String nick = data.getStringExtra("nick");
                    iAm.setNickName(nick);
                    int img = data.getIntExtra("img", 1);
                    iAm.setImageId(img);

                    int imgResId = getResources().getIdentifier("img"+img , "drawable", this.getPackageName());

                    ((ImageView)headerLayout.findViewById(R.id.imageHeader)).setImageResource(imgResId);
                    ((TextView)headerLayout.findViewById(R.id.nickHeader)).setText(nick);

                    if (myInfoTimerTask == null) {
                        myInfoTimerTask = new UserSenderTimerTask(iAm, handler, clientUDP);
                        if (MainActivity.isOnline(getBaseContext()))
                            mainActivityTimer.schedule(myInfoTimerTask, 1000, 1000);
                    } else {
                        myInfoTimerTask.setUser(iAm);
                    }
                    //если пользователь не нормально закрыл окно, то окно вызывается еще раз
                }else if(resultCode == RESULT_CANCELED){
                    Intent intent = new Intent(this, InitialActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
        }
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.imageNickSettings) {
            Intent intent = new Intent(this, InitialActivity.class);
            startActivityForResult(intent, 1);
            return true;
        }

        return false;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return OnlineListFragment.newInstance(Translator.users, dbHelper);
                case 1:
                    return DialogListFragment.newInstance(dialogAdapter, dbHelper);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show only 1 page.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ONLINE";
                case 1:
                    return "DIALOGS";
            }
            return null;
        }
    }

    //returns mac address in String format
    public static String getMacAddress(Context ctx) throws Exception{
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null) {
            return wifiInfo.getMacAddress();
        } else {
            throw new Exception( "No SD card available" );
        }
    }

    //check wifi network connection
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            return true; // есть соединение
        }
        else {
            return false; // нет соединения
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.changeNickImage) {
            Intent intent = new Intent(this, InitialActivity.class);
            startActivityForResult(intent, 1);
        }else if(id == R.id.clear_all){


            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Do you want delete all messages?");  // заголовок
            //ad.setMessage(message); // сообщение
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    db.delete(DataContract.MessagesEntry.TABLE_NAME, null, null);
                }
            });
            ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });
            ad.setCancelable(true);
            ad.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for(Dialog d: dialogs){
            dbHelper.regulateMessagesCount(d.getDialogId(), 100);
        }

        serverUDP.interrupt();
        mainActivityTimer.cancel();
        mainActivityTimer.purge();
        dbHelper.close();
        //ждем завершения потока сервера
        try {
            serverUDP.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clientUDP.close();

        Log.e("status", "onDestroy");
    }
}
