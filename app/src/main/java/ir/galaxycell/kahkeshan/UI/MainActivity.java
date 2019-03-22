package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import ir.galaxycell.kahkeshan.InterFace.FragmentChatViewHolderListener;
import ir.galaxycell.kahkeshan.InterFace.NewsViewHolderListener;
import ir.galaxycell.kahkeshan.InterFace.mahmood;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Service.PushNotification;
import ir.galaxycell.kahkeshan.SharedPref.SharedPref;
import ir.galaxycell.kahkeshan.Utility.Utility;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,mahmood.SeeEditProductImageProfileClickListener,FragmentChatViewHolderListener,NewsViewHolderListener {


    public static SharedPref userData;
    private Fragment fragment=null;
    private Class fragmentclass=null;
    private FragmentHome fragmentHome;
    private FragmentLibrary fragmentLibrary;
    private FragmentAdd fragmentAdd;
    private FragmentChat fragmentChat;
    private FragmentProfile fragmentProfile;
    private FragmentNews fragmentNews;
    private FrameLayout fHome,fLibrary,fAdd,fChat,fProfile,fNews;
    private RippleView rHome,rLibrary,rAdd,rChat,rProfile;
    private ImageView iHome,iLibrary,iAdd,iChat,iProfile,iMenu,iSearch,iNews;
    private TextView tHome,tLibrary,tAdd,tChat,tProfile;
    private LinearLayout cnMainLayout;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    public static List<String>BackStack=new ArrayList<String>();
    public static String curentPage;
    public static Context context;
    public static Dialog CheckNetwork;
    private NotificationManager mNotificationManager;

    public static Socket mSocket;
    {
        try
        {
            mSocket = IO.socket(Utility.BaseUrl);
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //start pushnotification service is running
        if(!isMyServiceRunning(PushNotification.class))
        {
            //start servic
            Intent pushNotification=new Intent(MainActivity.this,PushNotification.class);
            startService(pushNotification);
        }

        //clear all notification
        clearNotification();

        //set activity
        context=MainActivity.this;

        //shared preferences
        userData=new SharedPref(this);

        //connect to server by socket
        mSocket.connect();


        //linke widget to layout
        link();

        //clear BackStack
        BackStack.clear();

        fragmentHome=new FragmentHome();
        fragmentLibrary=new FragmentLibrary();
        fragmentAdd=new FragmentAdd();
        fragmentChat=new FragmentChat();
        fragmentProfile=new FragmentProfile();
        fragmentNews=new FragmentNews();

        FragmentManager fragmentManager1=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction1=fragmentManager1.beginTransaction();
        fragmentTransaction1.add(R.id.main_framlayout_fragmentitemselected1,fragmentHome,"fragmentHome");
        BackStack.add("fragmentHome");
        curentPage="fragmentHome";
        fragmentTransaction1.commit();

        FragmentManager fragmentManager2=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction2=fragmentManager2.beginTransaction();
        fragmentTransaction2.add(R.id.main_framlayout_fragmentitemselected2,fragmentLibrary,"fragmentLibrary");
        fragmentTransaction2.commit();

        FragmentManager fragmentManager3=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction3=fragmentManager3.beginTransaction();
        fragmentTransaction3.add(R.id.main_framlayout_fragmentitemselected3,fragmentAdd,"fragmentAdd");
        fragmentTransaction3.commit();

        FragmentManager fragmentManager4=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction4=fragmentManager4.beginTransaction();
        fragmentTransaction4.add(R.id.main_framlayout_fragmentitemselected4,fragmentChat,"fragmentChat");
        fragmentTransaction4.commit();

        FragmentManager fragmentManager5=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction5=fragmentManager5.beginTransaction();
        fragmentTransaction5.add(R.id.main_framlayout_fragmentitemselected5,fragmentProfile,"fragmentProfile");
        fragmentTransaction5.commit();

        FragmentManager fragmentManager6=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction6=fragmentManager6.beginTransaction();
        fragmentTransaction6.add(R.id.main_framlayout_fragmentitemselected6,fragmentNews,"fragmentNews");
        fragmentTransaction6.commit();

        //
        ////socket listener
        //
        //product uploaded or no
        mSocket.on("resCreateProduct",resCreateProduct);
        //check connect to server or no
        mSocket.on("userConnect",userConnect);
        //Verified New Order
        mSocket.on("resVerifiedNewOrder",resVerifiedNewOrder);
        //listen to push notification
        //mSocket.on("resCreateNews",resCreateNews);


        //start news class when click on notification
        if(getIntent().getExtras()!=null)
        {
            /*Intent gotoNews=new Intent(MainActivity.this,FragmentNews.class);
            gotoNews.putExtra("Push","push");
            startActivity(gotoNews);

            FragmentNews.getInstance().finish();*/

            /*String message=getIntent().getStringExtra("Click");

            if(message.equals("clickService"))
            {
                //stop service
                stopService(new Intent(MainActivity.this, PushNotification.class));

                Intent gotoNews=new Intent(MainActivity.this,FragmentNews.class);
                gotoNews.putExtra("Push","push");
                startActivity(gotoNews);
            }
            else
            {
                Intent gotoNews=new Intent(MainActivity.this,FragmentNews.class);
                gotoNews.putExtra("Push","push");
                startActivity(gotoNews);
            }*/


        }




    }

    private void clearNotification()
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void link()
    {
        //Ripple View
        rHome=(RippleView)findViewById(R.id.main_activity_rippleview_homelayout);
        rLibrary=(RippleView)findViewById(R.id.main_activity_rippleview_librarylayout);
        rAdd=(RippleView)findViewById(R.id.main_activity_rippleview_addlayout);
        rChat=(RippleView)findViewById(R.id.main_activity_rippleview_chatlayout);
        rProfile=(RippleView)findViewById(R.id.main_activity_rippleview_profilelayout);

        //imageview
        iHome=(ImageView)findViewById(R.id.main_activity_imageview_home);
        iLibrary=(ImageView)findViewById(R.id.main_activity_imageview_library);
        iAdd=(ImageView)findViewById(R.id.main_activity_imageview_add);
        iChat=(ImageView)findViewById(R.id.main_activity_imageview_chat);
        iProfile=(ImageView)findViewById(R.id.main_activity_imageview_profile);
        iMenu=(ImageView)findViewById(R.id.main_activity_toolbar_imageview_menu);
        iNews=(ImageView)findViewById(R.id.main_activity_toolbar_imageview_news);
        iSearch=(ImageView)findViewById(R.id.main_activity_toolbar_imageview_search);

        //text view
        tHome=(TextView)findViewById(R.id.main_activity_textview_home);
        tLibrary=(TextView)findViewById(R.id.main_activity_textview_library);
        tAdd=(TextView)findViewById(R.id.main_activity_textview_add);
        tChat=(TextView)findViewById(R.id.main_activity_textview_chat);
        tProfile=(TextView)findViewById(R.id.main_activity_textview_profile);

        //Frame Layout
        fHome=(FrameLayout)findViewById(R.id.main_framlayout_fragmentitemselected1);
        fLibrary=(FrameLayout)findViewById(R.id.main_framlayout_fragmentitemselected2);
        fAdd=(FrameLayout)findViewById(R.id.main_framlayout_fragmentitemselected3);
        fChat=(FrameLayout)findViewById(R.id.main_framlayout_fragmentitemselected4);
        fProfile=(FrameLayout)findViewById(R.id.main_framlayout_fragmentitemselected5);
        fNews=(FrameLayout)findViewById(R.id.main_framlayout_fragmentitemselected6);
        fNews.bringToFront();
        fNews.invalidate();

        //navigation view
        navigationView=(NavigationView)findViewById(R.id.main_activity_navigationview_user);
        navigationView.setNavigationItemSelectedListener(this);
        //header navigation view item listerner
        HNVIL(navigationView.getHeaderView(0));

        //drawe layout
        drawerLayout=(DrawerLayout)findViewById(R.id.main_activity_drawerlayout_mainlayout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        toggle.syncState();

        //
        //////////CheckNetwork
        //
        //create CheckNetwork dialog
        CheckNetwork =new Dialog(MainActivity.this);
        CheckNetwork.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CheckNetwork.setContentView(R.layout.check_network_connect_dialog_layout);
        CheckNetwork.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CheckNetwork.getWindow().setBackgroundDrawableResource(R.drawable.boder_loding_layout);

        //define widget
        cnMainLayout=(LinearLayout)CheckNetwork.findViewById(R.id.check_network_connect_dialog_layout_linerlayout_mainlayout);

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) MainActivity.this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        cnMainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;
        //
        //////////CheckNetwork
        //

        //set on click listener
        onClick();
    }

    private void HNVIL(View headerView)
    {

    }

    private void onClick()
    {
        rHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                //change fragment
                String tag=BackStack.get(BackStack.size()-1);;
                if(!tag.equals("fragmentHome"))
                {
                    if(BackStack.size()>=2)
                    {
                        for(int i=1;i<BackStack.size();i++)
                        {
                            BackStack.remove(i);
                        }
                    }

                    iHome.setImageResource(R.drawable.home_on);
                    iLibrary.setImageResource(R.drawable.library_off);
                    iAdd.setImageResource(R.drawable.add_off);
                    iChat.setImageResource(R.drawable.chat_off);
                    iProfile.setImageResource(R.drawable.profile_off);

                    tHome.setTextColor(getResources().getColor(R.color.selectitem));
                    tLibrary.setTextColor(getResources().getColor(R.color.unselectitem));
                    tAdd.setTextColor(getResources().getColor(R.color.unselectitem));
                    tChat.setTextColor(getResources().getColor(R.color.unselectitem));
                    tProfile.setTextColor(getResources().getColor(R.color.unselectitem));


                    fHome.setVisibility(View.VISIBLE);
                    fLibrary.setVisibility(View.GONE);
                    fAdd.setVisibility(View.GONE);
                    fChat.setVisibility(View.GONE);
                    fProfile.setVisibility(View.GONE);
                    fNews.setVisibility(View.GONE);

                    iSearch.setVisibility(View.GONE);


                }

            }
        });

        rLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(userData.getStatusUser().equals("")|| userData.getStatusUser().equals("false"))
                {
                    Intent gotoLogin=new Intent(MainActivity.this,Login.class);
                    startActivity(gotoLogin);
                }
                else
                {
                    if(mSocket.connected())
                    {
                        //change fragment
                        String tag=BackStack.get(BackStack.size()-1);;
                        if(!tag.equals("fragmentLibrary"))
                        {
                            if(BackStack.size()>=2)
                            {
                                for(int i=1;i<BackStack.size();i++)
                                {
                                    BackStack.remove(i);
                                }
                            }

                            iHome.setImageResource(R.drawable.home_off);
                            iLibrary.setImageResource(R.drawable.library_on);
                            iAdd.setImageResource(R.drawable.add_off);
                            iChat.setImageResource(R.drawable.chat_off);
                            iProfile.setImageResource(R.drawable.profile_off);

                            tHome.setTextColor(getResources().getColor(R.color.unselectitem));
                            tLibrary.setTextColor(getResources().getColor(R.color.selectitem));
                            tAdd.setTextColor(getResources().getColor(R.color.unselectitem));
                            tChat.setTextColor(getResources().getColor(R.color.unselectitem));
                            tProfile.setTextColor(getResources().getColor(R.color.unselectitem));

                            fHome.setVisibility(View.GONE);
                            fLibrary.setVisibility(View.VISIBLE);
                            fAdd.setVisibility(View.GONE);
                            fChat.setVisibility(View.GONE);
                            fProfile.setVisibility(View.GONE);
                            fNews.setVisibility(View.GONE);

                            iSearch.setVisibility(View.VISIBLE);

                            BackStack.add("fragmentLibrary");
                            curentPage="fragmentLibrary";

                            //reset library list
                            FragmentLibrary fragmentLibrary=(FragmentLibrary) getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected2);
                            fragmentLibrary.resetListLibrary();

                            JSONObject object=new JSONObject();

                            try
                            {
                                object.put("userid",MainActivity.userData.getUserid());
                                mSocket.emit("libraryInfo",object);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        if(!CheckNetwork.isShowing())
                        {
                            CheckNetwork.show();
                        }
                    }
                }
            }
        });

        rAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                //change fragment
                String tag=BackStack.get(BackStack.size()-1);;
                if(!tag.equals("fragmentAdd"))
                {
                    if(BackStack.size()>=2)
                    {
                        for(int i=1;i<BackStack.size();i++)
                        {
                            BackStack.remove(i);
                        }
                    }

                    iHome.setImageResource(R.drawable.home_off);
                    iLibrary.setImageResource(R.drawable.library_off);
                    iAdd.setImageResource(R.drawable.add_on);
                    iChat.setImageResource(R.drawable.chat_off);
                    iProfile.setImageResource(R.drawable.profile_off);

                    tHome.setTextColor(getResources().getColor(R.color.unselectitem));
                    tLibrary.setTextColor(getResources().getColor(R.color.unselectitem));
                    tAdd.setTextColor(getResources().getColor(R.color.selectitem));
                    tChat.setTextColor(getResources().getColor(R.color.unselectitem));
                    tProfile.setTextColor(getResources().getColor(R.color.unselectitem));

                    fHome.setVisibility(View.GONE);
                    fLibrary.setVisibility(View.GONE);
                    fAdd.setVisibility(View.VISIBLE);
                    fChat.setVisibility(View.GONE);
                    fProfile.setVisibility(View.GONE);
                    fNews.setVisibility(View.GONE);

                    iSearch.setVisibility(View.GONE);

                    FragmentAdd.description="";
                    FragmentAdd.imagePath="";
                    FragmentAdd.filepath="";
                    FragmentAdd.shashtag="";
                    FragmentAdd.hashtag.clear();
                    FragmentAdd.hashtagFinal="";

                    //change fragment
                    fragmentclass=FragmentAdd.class;
                    try
                    {
                        fragment = (Fragment) fragmentclass.newInstance();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_framlayout_fragmentitemselected3, fragment).commit();

                    BackStack.add("fragmentAdd");
                    curentPage="fragmentAdd";

                }
            }
        });

        rChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(userData.getStatusUser().equals("")|| userData.getStatusUser().equals("false"))
                {
                    Intent gotoLogin=new Intent(MainActivity.this,Login.class);
                    startActivity(gotoLogin);
                }
                else
                {
                    if(mSocket.connected())
                    {
                        //change fragment
                        String tag=BackStack.get(BackStack.size()-1);;
                        if(!tag.equals("fragmentChat"))
                        {
                            if(BackStack.size()>=2)
                            {
                                for(int i=1;i<BackStack.size();i++)
                                {
                                    BackStack.remove(i);
                                }
                            }

                            iHome.setImageResource(R.drawable.home_off);
                            iLibrary.setImageResource(R.drawable.library_off);
                            iAdd.setImageResource(R.drawable.add_off);
                            iChat.setImageResource(R.drawable.chat_on);
                            iProfile.setImageResource(R.drawable.profile_off);

                            tHome.setTextColor(getResources().getColor(R.color.unselectitem));
                            tLibrary.setTextColor(getResources().getColor(R.color.unselectitem));
                            tAdd.setTextColor(getResources().getColor(R.color.unselectitem));
                            tChat.setTextColor(getResources().getColor(R.color.selectitem));
                            tProfile.setTextColor(getResources().getColor(R.color.unselectitem));

                            fHome.setVisibility(View.GONE);
                            fLibrary.setVisibility(View.GONE);
                            fAdd.setVisibility(View.GONE);
                            fChat.setVisibility(View.VISIBLE);
                            fProfile.setVisibility(View.GONE);
                            fNews.setVisibility(View.GONE);

                            iSearch.setVisibility(View.GONE);

                            BackStack.add("fragmentChat");
                            curentPage="fragmentChat";

                            MainActivity.mSocket.emit("orderInfo",MainActivity.userData.getUserid());
                        }

                    }
                    else
                    {
                        if(!CheckNetwork.isShowing())
                        {
                            CheckNetwork.show();
                        }
                    }
                }

            }
        });

        rProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(userData.getStatusUser().equals("")|| userData.getStatusUser().equals("false"))
                {
                    Intent gotoLogin=new Intent(MainActivity.this,Login.class);
                    startActivity(gotoLogin);
                }
                else
                {
                    if(mSocket.connected())
                    {
                        //change fragment
                        String tag=BackStack.get(BackStack.size()-1);
                        if(!tag.equals("fragmentProfile"))
                        {
                            if(BackStack.size()>=2)
                            {
                                for(int i=1;i<BackStack.size();i++)
                                {
                                    BackStack.remove(i);
                                }
                            }

                            iHome.setImageResource(R.drawable.home_off);
                            iLibrary.setImageResource(R.drawable.library_off);
                            iAdd.setImageResource(R.drawable.add_off);
                            iChat.setImageResource(R.drawable.chat_off);
                            iProfile.setImageResource(R.drawable.profile_on);

                            tHome.setTextColor(getResources().getColor(R.color.unselectitem));
                            tLibrary.setTextColor(getResources().getColor(R.color.unselectitem));
                            tAdd.setTextColor(getResources().getColor(R.color.unselectitem));
                            tChat.setTextColor(getResources().getColor(R.color.unselectitem));
                            tProfile.setTextColor(getResources().getColor(R.color.selectitem));

                            fHome.setVisibility(View.GONE);
                            fLibrary.setVisibility(View.GONE);
                            fAdd.setVisibility(View.GONE);
                            fChat.setVisibility(View.GONE);
                            fProfile.setVisibility(View.VISIBLE);
                            fNews.setVisibility(View.GONE);

                            iSearch.setVisibility(View.GONE);

                            BackStack.add("fragmentProfile");
                            curentPage="fragmentProfile";

                            mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
                        }
                    }
                    else
                    {
                        if(!CheckNetwork.isShowing())
                        {
                            CheckNetwork.show();
                        }
                    }

                }

            }
        });

        iMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        iSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this,Search.class));
            }
        });

        iNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(mSocket.connected())
                {
                    //change fragment
                    String tag=BackStack.get(BackStack.size()-1);
                    if(!tag.equals("fragmentNews"))
                    {
                        if(BackStack.size()>=2)
                        {
                            for(int i=1;i<BackStack.size();i++)
                            {
                                BackStack.remove(i);
                            }
                        }



                        fHome.setVisibility(View.GONE);
                        fLibrary.setVisibility(View.GONE);
                        fAdd.setVisibility(View.GONE);
                        fChat.setVisibility(View.GONE);
                        fProfile.setVisibility(View.GONE);
                        fNews.setVisibility(View.VISIBLE);

                        iSearch.setVisibility(View.GONE);

                        BackStack.add("fragmentNews");
                        curentPage="fragmentNews";

                        FragmentNews fragmentNews=(FragmentNews) getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected6);
                        fragmentNews.reSetResNewsModelList();

                        mSocket.emit("getLastNewsId","");
                    }


                }
                else
                {
                    if(!CheckNetwork.isShowing())
                    {
                        CheckNetwork.show();
                    }
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {

        if(BackStack.size() ==2 && !drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            String tag=BackStack.get(BackStack.size()-2);
            if(tag.equals("fragmentHome"))
            {

                BackStack.remove(1);

                iHome.setImageResource(R.drawable.home_on);
                iLibrary.setImageResource(R.drawable.library_off);
                iAdd.setImageResource(R.drawable.add_off);
                iChat.setImageResource(R.drawable.chat_off);
                iProfile.setImageResource(R.drawable.profile_off);

                tHome.setTextColor(getResources().getColor(R.color.selectitem));
                tLibrary.setTextColor(getResources().getColor(R.color.unselectitem));
                tAdd.setTextColor(getResources().getColor(R.color.unselectitem));
                tChat.setTextColor(getResources().getColor(R.color.unselectitem));
                tProfile.setTextColor(getResources().getColor(R.color.unselectitem));

                //change fragment
                fHome.setVisibility(View.VISIBLE);
                fLibrary.setVisibility(View.GONE);
                fAdd.setVisibility(View.GONE);
                fChat.setVisibility(View.GONE);
                fProfile.setVisibility(View.GONE);
                fNews.setVisibility(View.GONE);

                iSearch.setVisibility(View.GONE);

                curentPage="fragmentHome";

            }
        }


        else if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if(BackStack.size() ==1)
        {
            //int pid=android.os.Process.myPid();
            //android.os.Process.killProcess(pid);
            //System.exit(0);
            super.onBackPressed();
        }
    }

    Emitter.Listener userConnect=new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];

                    try
                    {
                        if (data.getBoolean("connect"))
                        {

                            if(CheckNetwork.isShowing())
                            {
                                CheckNetwork.dismiss();
                            }
                            switch (curentPage)
                            {
                                case "fragmentHome":break;
                                case "fragmentLibrary":break;
                                case "fragmentAdd":break;
                                case "fragmentChat":break;
                                case "fragmentProfile":
                                    mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
                                    break;

                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            });

        }
    };

    Emitter.Listener resCreateProduct=new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    JSONObject data= (JSONObject) args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            //caecl loading dialog
                            Toast.makeText(getApplicationContext(),"مورد ارسالی شما با موفقیت بارگذاری شد",Toast.LENGTH_LONG).show();
                            FragmentAdd fragmentAdd=(FragmentAdd) getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected3);
                                fragmentAdd.cancelLoadingDialog();

                            //reset library list
                            FragmentProfile fragmentProfile=(FragmentProfile) getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected5);
                            fragmentProfile.resetLibraryList();

                            //change fragment
                            String tag=BackStack.get(BackStack.size()-1);;
                            if(!tag.equals("fragmentProfile"))
                            {
                                if(BackStack.size()>=2)
                                {
                                    for(int i=1;i<BackStack.size();i++)
                                    {
                                        BackStack.remove(i);
                                    }
                                }

                                iHome.setImageResource(R.drawable.home_off);
                                iLibrary.setImageResource(R.drawable.library_off);
                                iAdd.setImageResource(R.drawable.add_off);
                                iChat.setImageResource(R.drawable.chat_off);
                                iProfile.setImageResource(R.drawable.profile_on);

                                tHome.setTextColor(getResources().getColor(R.color.unselectitem));
                                tLibrary.setTextColor(getResources().getColor(R.color.unselectitem));
                                tAdd.setTextColor(getResources().getColor(R.color.unselectitem));
                                tChat.setTextColor(getResources().getColor(R.color.unselectitem));
                                tProfile.setTextColor(getResources().getColor(R.color.selectitem));

                                fHome.setVisibility(View.GONE);
                                fLibrary.setVisibility(View.GONE);
                                fAdd.setVisibility(View.GONE);
                                fChat.setVisibility(View.GONE);
                                fProfile.setVisibility(View.VISIBLE);

                                iSearch.setVisibility(View.GONE);

                                BackStack.add("fragmentProfile");
                                curentPage="fragmentProfile";

                                mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        FragmentAdd fragmentAdd=(FragmentAdd) getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected3);
                        fragmentAdd.cancelLoadingDialog();
                        Toast.makeText(getApplicationContext(),"مورد ارسالی متاسفانه بارگذاری نشد لطفا دوباره امتحان کنید",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    };


    public void OnImageProfileClickListener()
    {

        if(mSocket.connected())
        {
            //change fragment
            String tag=BackStack.get(BackStack.size()-1);;
            if(!tag.equals("fragmentProfile"))
            {
                if(BackStack.size()>=2)
                {
                    for(int i=1;i<BackStack.size();i++)
                    {
                        BackStack.remove(i);
                    }
                }

                iHome.setImageResource(R.drawable.home_off);
                iLibrary.setImageResource(R.drawable.library_off);
                iAdd.setImageResource(R.drawable.add_off);
                iChat.setImageResource(R.drawable.chat_off);
                iProfile.setImageResource(R.drawable.profile_on);

                tHome.setTextColor(getResources().getColor(R.color.unselectitem));
                tLibrary.setTextColor(getResources().getColor(R.color.unselectitem));
                tAdd.setTextColor(getResources().getColor(R.color.unselectitem));
                tChat.setTextColor(getResources().getColor(R.color.unselectitem));
                tProfile.setTextColor(getResources().getColor(R.color.selectitem));

                fHome.setVisibility(View.GONE);
                fLibrary.setVisibility(View.GONE);
                fAdd.setVisibility(View.GONE);
                fChat.setVisibility(View.GONE);
                fProfile.setVisibility(View.VISIBLE);

                iSearch.setVisibility(View.GONE);

                BackStack.add("fragmentProfile");
                curentPage="fragmentProfile";

                mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
            }
        }
        else
        {
            if(!CheckNetwork.isShowing())
            {
                CheckNetwork.show();
            }
        }

    }


    Emitter.Listener resVerifiedNewOrder=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];
                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            Toast.makeText(getApplicationContext(),"با سپاس از شما سفارش با موفقیت ثبت شد",Toast.LENGTH_LONG).show();

                            //reset Order List
                            FragmentChat fragmentChat=(FragmentChat) getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected4);
                            fragmentChat.resetOrderList();

                            //change fragment
                            String tag=BackStack.get(BackStack.size()-1);;
                            if(!tag.equals("fragmentChat"))
                            {
                                if(BackStack.size()>=2)
                                {
                                    for(int i=1;i<BackStack.size();i++)
                                    {
                                        BackStack.remove(i);
                                    }
                                }

                                iHome.setImageResource(R.drawable.home_off);
                                iLibrary.setImageResource(R.drawable.library_off);
                                iAdd.setImageResource(R.drawable.add_off);
                                iChat.setImageResource(R.drawable.chat_on);
                                iProfile.setImageResource(R.drawable.profile_off);

                                tHome.setTextColor(getResources().getColor(R.color.unselectitem));
                                tLibrary.setTextColor(getResources().getColor(R.color.unselectitem));
                                tAdd.setTextColor(getResources().getColor(R.color.unselectitem));
                                tChat.setTextColor(getResources().getColor(R.color.selectitem));
                                tProfile.setTextColor(getResources().getColor(R.color.unselectitem));

                                fHome.setVisibility(View.GONE);
                                fLibrary.setVisibility(View.GONE);
                                fAdd.setVisibility(View.GONE);
                                fChat.setVisibility(View.VISIBLE);
                                fProfile.setVisibility(View.GONE);

                                iSearch.setVisibility(View.GONE);

                                BackStack.add("fragmentChat");
                                curentPage="fragmentChat";
                            }



                            MainActivity.mSocket.emit("orderInfo",MainActivity.userData.getUserid());
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void seeZeroPosition()
    {
        //reset Order List
        FragmentChat fragmentChat=(FragmentChat) getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected4);
        fragmentChat.startLoadMore();
    }

    /*Emitter.Listener resCreateNews=new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            v.vibrate(900);

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);

                            mBuilder.setSmallIcon(R.drawable.home_off);
                            mBuilder.setContentTitle("Notification Alert, Click Me!");
                            mBuilder.setContentText("Hi, This is Android Notification Detail!");
                            mBuilder.setAutoCancel(true);

                            Intent resultIntent = new Intent(MainActivity.this, MainActivity.class);
                            resultIntent.putExtra("Click","click");
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
                            stackBuilder.addParentStack(MainActivity.class);

                            // Adds the Intent that starts the Activity to the top of the stack
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);


                            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            // notificationID allows you to update the notification later on.
                            mNotificationManager.notify(0, mBuilder.build());
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    };*/


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void seeLastPositionNews()
    {
        FragmentNews fragmentNews=(FragmentNews) getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected6);
        fragmentNews.seeLastPosition();
    }
}
