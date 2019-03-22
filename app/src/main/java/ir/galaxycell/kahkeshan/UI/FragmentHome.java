package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.lightsky.infiniteindicator.IndicatorConfiguration;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;
import ir.galaxycell.kahkeshan.Adapter.SpinerAdapter;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.UISlideLoader.GlideLoader;


/**
 * Created by Admin on 11/08/2017.
 */
public class FragmentHome extends Fragment implements ViewPager.OnPageChangeListener,OnPageClickListener {

    private View view;
    private ArrayList<Page> pageViews;
    private InfiniteIndicator mAnimCircleIndicator;
    private Spinner typeOrder;
    private List<String>spinerList;
    private SpinerAdapter spinerAdapter;
    private EditText description;
    private RippleView rSearch,rRegister,rTranslation,rTyping,rOperator,rTelegram;
    private TextView tSearch,tRegister,tTranslation,tTyping;
    private Button sendOrder;
    private FrameLayout service;
    private FragmentHomeFragmentSearch fragmentHomeFragmentSearch;
    private FragmentHomeFragmentRegister fragmentHomeFragmentRegister;
    private FragmentHomeFragmentTranslation fragmentHomeFragmentTranslation;
    private FragmentHomeFragmentTyping fragmentHomeFragmentTyping;
    private Fragment fragment=null;
    private Class fragmentclass=null;
    private Dialog registerOrderDialog,loading;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_home,container,false);

        //linke widget in fragment home to layout
        link(view);

        fragmentHomeFragmentSearch=new FragmentHomeFragmentSearch();
        fragmentHomeFragmentRegister=new FragmentHomeFragmentRegister();
        fragmentHomeFragmentTranslation=new FragmentHomeFragmentTranslation();
        fragmentHomeFragmentTyping=new FragmentHomeFragmentTyping();

        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_home_framlayout_service,fragmentHomeFragmentSearch,"fragmentHomeFragmentSearch");
        fragmentTransaction.commit();

        //socket listener
        MainActivity.mSocket.on("resCreateOrder",resCreateOrder);


        return view;
    }

    private void link(View view)
    {

        //Infinite Indicator
        initData();
        mAnimCircleIndicator = (InfiniteIndicator) view.findViewById(R.id.indicator_default_circle);
        IndicatorConfiguration configuration = new IndicatorConfiguration.Builder()
                .imageLoader(new GlideLoader())
                .internal(4000)
                .isStopWhileTouch(true)
                .onPageChangeListener(this)
                .onPageClickListener(this)
                .direction(1)
                .position(IndicatorConfiguration.IndicatorPosition.Center_Bottom)
                .build();
        mAnimCircleIndicator.init(configuration);
        mAnimCircleIndicator.notifyDataChange(pageViews);

        //Spiner
        iniSpinerListData();
        typeOrder=(Spinner)view.findViewById(R.id.fragment_home_spiner_typeorder);
        spinerAdapter=new SpinerAdapter(getContext(),spinerList);
        typeOrder.setAdapter(spinerAdapter);

        //Edit text
        description=(EditText)view.findViewById(R.id.fragment_home_edittextview_description);

        //Ripple View
        rSearch=(RippleView)view.findViewById(R.id.fragment_home_rippleview_searchlayout);
        rRegister=(RippleView)view.findViewById(R.id.fragment_home_rippleview_registerlayout);
        rTranslation=(RippleView)view.findViewById(R.id.fragment_home_rippleview_translationlayout);
        rTyping=(RippleView)view.findViewById(R.id.fragment_home_rippleview_typinglayout);
        rOperator=(RippleView)view.findViewById(R.id.fragment_home_rippleview_operatorlayout);
        rTelegram=(RippleView)view.findViewById(R.id.fragment_home_rippleview_telegramlayout);

        //Text view
        tSearch=(TextView)view.findViewById(R.id.fragment_home_textview_search);
        tRegister=(TextView)view.findViewById(R.id.fragment_home_textview_register);
        tTranslation=(TextView)view.findViewById(R.id.fragment_home_textview_translation);
        tTyping=(TextView)view.findViewById(R.id.fragment_home_textview_typing);


        //Button
        sendOrder=(Button)view.findViewById(R.id.fragment_home_button_sendorder);

        //Frame Layout
        service=(FrameLayout)view.findViewById(R.id.fragment_home_framlayout_service);


        //set on click listener
        onClick();
    }

    private void onClick()
    {

        sendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(MainActivity.userData.getStatusUser().equals("")|| MainActivity.userData.getStatusUser().equals("false"))
                {
                    Intent gotoLogin=new Intent(getContext(),Login.class);
                    startActivity(gotoLogin);
                }
                else if(typeOrder.getSelectedItemPosition()==0)
                {
                    Toast.makeText(getContext(),"لطفا یک نوع سفارش انتخاب کنید",Toast.LENGTH_LONG).show();
                }
                else if(description.getText().toString().trim().equals(""))
                {
                    description.setError("توضیحات نمی تواند خالی باشد");
                }
                else
                {
                    if(MainActivity.mSocket.connected())
                    {
                        JSONObject object=new JSONObject();

                        try
                        {
                            object.put("userid",MainActivity.userData.getUserid());
                            object.put("typeOrder",typeOrder.getSelectedItem().toString());
                            object.put("description",description.getText().toString());

                            MainActivity.mSocket.emit("createOrder",object);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        if (!MainActivity.CheckNetwork.isShowing()) {
                            MainActivity.CheckNetwork.show();
                        }
                    }
                }

            }
        });

        rSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                tSearch.setTextColor(getResources().getColor(R.color.selectitemtools));
                tRegister.setTextColor(getResources().getColor(R.color.unselectitem));
                tTranslation.setTextColor(getResources().getColor(R.color.unselectitem));
                tTyping.setTextColor(getResources().getColor(R.color.unselectitem));

                //change fragment
                fragmentclass=FragmentHomeFragmentSearch.class;
                try
                {
                    fragment = (Fragment) fragmentclass.newInstance();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_right_exit);
                fragmentTransaction.replace(R.id.fragment_home_framlayout_service, fragment).commit();

            }
        });

        rRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                tSearch.setTextColor(getResources().getColor(R.color.unselectitem));
                tRegister.setTextColor(getResources().getColor(R.color.selectitemtools));
                tTranslation.setTextColor(getResources().getColor(R.color.unselectitem));
                tTyping.setTextColor(getResources().getColor(R.color.unselectitem));

                //change fragment
                fragmentclass=FragmentHomeFragmentRegister.class;
                try
                {
                    fragment = (Fragment) fragmentclass.newInstance();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_right_exit);
                fragmentTransaction.replace(R.id.fragment_home_framlayout_service, fragment).commit();
            }
        });

        rTranslation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                tSearch.setTextColor(getResources().getColor(R.color.unselectitem));
                tRegister.setTextColor(getResources().getColor(R.color.unselectitem));
                tTranslation.setTextColor(getResources().getColor(R.color.selectitemtools));
                tTyping.setTextColor(getResources().getColor(R.color.unselectitem));

                //change fragment
                fragmentclass=FragmentHomeFragmentTranslation.class;
                try
                {
                    fragment = (Fragment) fragmentclass.newInstance();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_right_exit);
                fragmentTransaction.replace(R.id.fragment_home_framlayout_service, fragment).commit();
            }
        });

        rTyping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                tSearch.setTextColor(getResources().getColor(R.color.unselectitem));
                tRegister.setTextColor(getResources().getColor(R.color.unselectitem));
                tTranslation.setTextColor(getResources().getColor(R.color.unselectitem));
                tTyping.setTextColor(getResources().getColor(R.color.selectitemtools));


                //change fragment
                fragmentclass=FragmentHomeFragmentTyping.class;
                try
                {
                    fragment = (Fragment) fragmentclass.newInstance();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_left_exit,
                        R.anim.fragment_slide_right_enter,
                        R.anim.fragment_slide_right_exit);
                fragmentTransaction.replace(R.id.fragment_home_framlayout_service, fragment).commit();
            }
        });


        rOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:09380676996"));
                startActivity(intent);
            }
        });

        rTelegram.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                final String appName = "org.telegram.messenger";
                final boolean isAppInstalled = isAppAvailable(getContext(), appName);
                if (isAppInstalled)
                {
                    Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=MrRadio"));
                    startActivity(Intent.createChooser(telegram, "Share with"));
                }
                else
                {
                    Toast.makeText(getContext(), "تلگرام نصب نیست", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void iniSpinerListData()
    {
        spinerList=new ArrayList<String>();
        spinerList.add("نوع سفارش");
        spinerList.add("تحقیق");
        spinerList.add("ثبت نام");
        spinerList.add("ترجمه");
        spinerList.add("تایپ");
    }



    private void initData() {
        pageViews = new ArrayList<>();
        pageViews.add(new Page("A", "https://raw.githubusercontent.com/lightSky/InfiniteIndicator/master/res/a.jpg",this));
        pageViews.add(new Page("B", "https://raw.githubusercontent.com/lightSky/InfiniteIndicator/master/res/b.jpg",this));
        pageViews.add(new Page("C", "https://raw.githubusercontent.com/lightSky/InfiniteIndicator/master/res/c.jpg",this));
        pageViews.add(new Page("D", "https://raw.githubusercontent.com/lightSky/InfiniteIndicator/master/res/d.jpg",this));

    }


    @Override
    public void onPageSelected(int position)
    {
        //do something
    }

    @Override
    public void onPageClick(int position, Page page)
    {
        //do something
        Toast.makeText(getContext(),""+position,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static boolean isAppAvailable(Context context, String appName)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    Emitter.Listener resCreateOrder=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    final JSONObject data=(JSONObject)args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {


                            registerOrderDialog =new Dialog(getContext(),R.style.PauseDialog);
                            registerOrderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            registerOrderDialog.setContentView(R.layout.fragment_home_order_verifiedcode_dialog_layout);
                            registerOrderDialog.getWindow().setBackgroundDrawableResource(R.drawable.border_login_dialog_layout);
                            registerOrderDialog.setCanceledOnTouchOutside(false);
                            registerOrderDialog.setCancelable(false);

                            //define widget
                            LinearLayout mainLayout=(LinearLayout)registerOrderDialog.findViewById(R.id.fragment_home_order_verifiedcode_dialog_layout_linerlayout_mainlayout);
                            final EditText code=(EditText)registerOrderDialog.findViewById(R.id.fragment_home_order_verifiedcode_dialog_layout_edittext_code);
                            Button registerorder=(Button)registerOrderDialog.findViewById(R.id.fragment_home_order_verifiedcode_dialog_layout_button_registerorder);
                            final TextView verifiedcode=(TextView)registerOrderDialog.findViewById(R.id.fragment_home_order_verifiedcode_dialog_layout_textview_verifiedcode);
                            ImageView dialogCancel=(ImageView)registerOrderDialog.findViewById(R.id.fragment_home_order_verifiedcode_dialog_layout_imageview_cancel);

                            //set size
                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                            mainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;

                            //set data
                            verifiedcode.setText(data.getString("verifiedcode").toString());

                            //on click listener
                            dialogCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    try
                                    {
                                        MainActivity.mSocket.emit("removeOrder",data.getString("orderid").toString());

                                        registerOrderDialog.dismiss();
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            registerorder.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View view)
                                {
                                    if(code.getText().toString().trim().equals(""))
                                    {
                                        code.setError("کد ارسالی نمی تواند خالی باشد");
                                    }
                                    else if(!code.getText().toString().equals(verifiedcode.getText().toString()))
                                    {
                                        code.setError("کد وارد شده با کد دریافتی برابر نیست");
                                    }
                                    else
                                    {
                                        JSONObject object=new JSONObject();
                                        try
                                        {
                                            object.put("orderid",data.getString("orderid").toString());
                                            object.put("verifiedcode",code.getText().toString());
                                            MainActivity.mSocket.emit("verifiedNewOrder",object);

                                            registerOrderDialog.dismiss();
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            });

                            registerOrderDialog.show();
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
}
