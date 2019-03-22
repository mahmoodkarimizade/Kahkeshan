package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.galaxycell.kahkeshan.AVLoadingIndicatorView.AVLoadingIndicatorView;
import ir.galaxycell.kahkeshan.InterFace.mahmood;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreLibraryAndFavorites;
import ir.galaxycell.kahkeshan.Models.ResSeeProductModel;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Utility.Utility;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 03/09/2017.
 */
public class SeeEditProduct extends AppCompatActivity implements TabLayout.OnTabSelectedListener , ViewPager.OnPageChangeListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Typeface typeface;
    private ViewPagerAdapter_TabLayout_Add adapter;
    public static ResLoadMoreLibraryAndFavorites resLoadMoreLibraryAndFavorites;
    public static ResSeeProductModel resSeeProductModel;
    private ImageView iLike,iShare,iMore,iSave;
    private CircleImageView imageProfile;
    private RippleView rLike,rShare,rMore,rSave;
    private AVLoadingIndicatorView loading;
    private TextView tLike,tShare,tTitle;
    private boolean flagLike=false,flagEdit=false;
    private String curentPage="";
    private Dialog moreDialog,sureDialog,CheckNetwork;
    private RelativeLayout buttonbar;
    public static String description="",imagePath="",filepath="",hashtagFinal="",shashtag="";
    public List<String> hashtag=new ArrayList<>();
    private Dialog Loading;
    private String typeFileDocument="",typeFileImage="";
    private byte[] bytesImage,bytesDocument;
    private LinearLayout cnMainLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_edit_product);

        //get data from intent
        resSeeProductModel=new ResSeeProductModel();
        resLoadMoreLibraryAndFavorites=new ResLoadMoreLibraryAndFavorites();
        curentPage= getIntent().getStringExtra("curentPage");
        resSeeProductModel=(ResSeeProductModel) getIntent().getSerializableExtra("resSeeProductModel");
        resLoadMoreLibraryAndFavorites= (ResLoadMoreLibraryAndFavorites) getIntent().getSerializableExtra("resLoadMoreLibraryAndFavorites");


        //socket emiter
        MainActivity.mSocket.on("resLikeProduct",resLikeProduct);
        MainActivity.mSocket.on("resDeleteProduct",resDeleteProduct);
        MainActivity.mSocket.on("resEditProduct",resEditProduct);



        //define type face
        typeface=Typeface.createFromAsset(getAssets(),"fonts/IRANSansMobileNormal.ttf");

        //linke widget in fragment home to layout
        link();

    }

    private void link()
    {
        //tab layout
        tabLayout=(TabLayout)findViewById(R.id.see_edit_product_tabs);
        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("عکس"));
        tabLayout.addTab(tabLayout.newTab().setText("توضیحات"));
        tabLayout.addTab(tabLayout.newTab().setText("فایل"));
        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);


        //view pager
        viewPager=(ViewPager)findViewById(R.id.see_edit_product_viewpager);
        //Creating our pager adapter
        adapter = new ViewPagerAdapter_TabLayout_Add(getSupportFragmentManager(), tabLayout.getTabCount());
        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.setRotation(180);
        customTabView();
        //Adding OnPageChangeListener
        viewPager.setOnPageChangeListener(this);

        //Image View
        iMore=(ImageView)findViewById(R.id.see_edit_product_toolbar_imageview_more);
        iLike=(ImageView)findViewById(R.id.see_edit_product_imageview_like);
        iShare=(ImageView)findViewById(R.id.see_edit_product_imageview_share);
        iSave=(ImageView)findViewById(R.id.see_edit_product_toolbar_imageview_save);

        //Circle Image View
        imageProfile=(CircleImageView)findViewById(R.id.see_edit_product_imageview_imageprofile);

        //Ripple View
        rMore=(RippleView) findViewById(R.id.see_edit_product_rippleview_morelayout);
        rLike=(RippleView)findViewById(R.id.see_edit_product_rippleview_likelayout);
        rShare=(RippleView)findViewById(R.id.see_edit_product_rippleview_morelayout);
        rSave=(RippleView)findViewById(R.id.see_edit_product_rippleview_savelayout);

        //AVLoading Indicator View
        loading=(AVLoadingIndicatorView)findViewById(R.id.see_edit_product_toolbar_AVloading_loading);

        //Text view
        tLike=(TextView)findViewById(R.id.see_edit_product_textview_countlike);
        tShare=(TextView)findViewById(R.id.see_edit_product_textview_share);
        tTitle=(TextView)findViewById(R.id.see_edit_product_toolbar_textview_title);

        //Liner Layout
        buttonbar=(RelativeLayout)findViewById(R.id.see_edit_product_relativelayout_buttonbar);



        //liked product or no
        //flagLike=likeProduct();
        flagLike=resSeeProductModel.isFlagLike();
        //first set
        if(!curentPage.equals("fragmentProfile:library"))
        {
            imageProfile.setVisibility(View.VISIBLE);

            //load user image with Glide
            Glide.with(this)
                    .load(Utility.BaseUrl+resSeeProductModel.productUserImage)
                    .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                    .apply(RequestOptions.errorOf(R.drawable.cancel))
                    .into(imageProfile);
        }
        if(flagLike)
        {
            iLike.setImageResource(R.drawable.favoriteprofileon);
        }
        else
        {
            iLike.setImageResource(R.drawable.favoriteprofileoff);
        }

        tLike.setText(resSeeProductModel.countLike);


        //create Lodin dialog
        Loading =new Dialog(this);
        Loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Loading.setContentView(R.layout.loading_dialog_layout);
        Loading.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Loading.setCanceledOnTouchOutside(false);
        Loading.setCancelable(false);


        //
        //////////CheckNetwork
        //
        //create CheckNetwork dialog
        CheckNetwork =new Dialog(SeeEditProduct.this);
        CheckNetwork.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CheckNetwork.setContentView(R.layout.check_network_connect_dialog_layout);
        CheckNetwork.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CheckNetwork.getWindow().setBackgroundDrawableResource(R.drawable.boder_loding_layout);

        //define widget
        cnMainLayout=(LinearLayout)CheckNetwork.findViewById(R.id.check_network_connect_dialog_layout_linerlayout_mainlayout);

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) SeeEditProduct.this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        cnMainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;
        //
        //////////CheckNetwork
        //

        //set on click listener
        onClick();

    }

    private void onClick()
    {
        rLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(MainActivity.mSocket.connected())
                {
                    JSONObject object=new JSONObject();

                    try
                    {
                        object.put("productid",resLoadMoreLibraryAndFavorites.getProductId());
                        object.put("userid",MainActivity.userData.getUserid());
                        object.put("likestatus",!flagLike);

                        MainActivity.mSocket.emit("likeProduct",object);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
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

        if(!curentPage.equals("DontShowImageProfile"))
        {
            imageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if(resLoadMoreLibraryAndFavorites.getUserId().equals(MainActivity.userData.getUserid()))
                    {

                        mahmood.getInstance().changeState(true);
                        MainActivity.mSocket.off("resLikeProduct",resLikeProduct);
                        MainActivity.mSocket.off("resDeleteProduct",resDeleteProduct);
                        MainActivity.mSocket.off("resEditProduct",resEditProduct);
                        finish();
                    }
                    else
                    {
                        Intent gotoSeeProfile=new Intent(SeeEditProduct.this, SeeProfile.class);
                        gotoSeeProfile.putExtra("UserId",resLoadMoreLibraryAndFavorites.getUserId());
                        gotoSeeProfile.putExtra("curentPage", MainActivity.curentPage);
                        startActivity(gotoSeeProfile);

                    }
                }
            });
        }


        iMore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                moreDialog =new Dialog(SeeEditProduct.this,R.style.PauseDialog);
                moreDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                moreDialog.setContentView(R.layout.see_edit_product_dialog_layout);
                moreDialog.getWindow().setBackgroundDrawableResource(R.drawable.border_login_dialog_layout);
                moreDialog.setCanceledOnTouchOutside(false);

                //define widget
                LinearLayout mainLayout=(LinearLayout)moreDialog.findViewById(R.id.see_edit_product_dialog_layout_linerlayout_mainlayout);
                Button download=(Button)moreDialog.findViewById(R.id.see_edit_product_dialog_layout_button_download);
                Button edit=(Button)moreDialog.findViewById(R.id.see_edit_product_dialog_layout_button_edit);
                Button report=(Button)moreDialog.findViewById(R.id.see_edit_product_dialog_layout_button_report);
                Button delete=(Button)moreDialog.findViewById(R.id.see_edit_product_dialog_layout_button_delete);
                ImageView dialogCancel=(ImageView)moreDialog.findViewById(R.id.see_edit_product_dialog_layout_imageview_cancel);

                //set size
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                mainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;

                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        if(MainActivity.mSocket.connected())
                        {
                            JSONObject object=new JSONObject();
                            try
                            {
                                object.put("productid",resLoadMoreLibraryAndFavorites.getProductId());
                                object.put("useridSee",MainActivity.userData.getUserid());
                                MainActivity.mSocket.emit("downloadRate",object);

                                moreDialog.dismiss();
                                Loading.show();
                                new DownloadFileFromURL().execute(Utility.BaseUrl+resSeeProductModel.getProductPathFile());
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
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

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        flagEdit=true;
                        rSave.setVisibility(View.VISIBLE);
                        rMore.setVisibility(View.GONE);
                        buttonbar.setVisibility(View.GONE);
                        tTitle.setVisibility(View.VISIBLE);
                        moreDialog.dismiss();

                        SeeEditProductFragmentImage seeEditProductFragmentImage = (SeeEditProductFragmentImage) adapter.getItem(0);
                        SeeEditProductFragmentDescription seeEditProductFragmentDescription = (SeeEditProductFragmentDescription) adapter.getItem(1);
                        SeeEditProductFragmentFile seeEditProductFragmentFile = (SeeEditProductFragmentFile) adapter.getItem(2);

                        seeEditProductFragmentImage.setFlagEdit(flagEdit);
                        seeEditProductFragmentDescription.setFlagEdit(flagEdit);
                        seeEditProductFragmentFile.setFlagEdit(flagEdit);

                        filepath="";
                        imagePath="";
                        description="";
                        shashtag="";
                        hashtagFinal="";

                        Toast.makeText(getApplicationContext(),"می توانید ویرایش کنید",Toast.LENGTH_LONG).show();

                    }
                });

                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                    }
                });

                delete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        moreDialog.dismiss();

                        sureDialog =new Dialog(SeeEditProduct.this,R.style.PauseDialog);
                        sureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        sureDialog.setContentView(R.layout.sure_dialog);
                        sureDialog.getWindow().setBackgroundDrawableResource(R.drawable.border_login_dialog_layout);
                        sureDialog.setCanceledOnTouchOutside(false);

                        //define widget
                        LinearLayout mainLayout=(LinearLayout)sureDialog.findViewById(R.id.sure_dialog_layout_linerlayout_mainlayout);
                        Button yes=(Button)sureDialog.findViewById(R.id.sure_dialog_layout_button_yes);
                        Button no=(Button)sureDialog.findViewById(R.id.sure_dialog_layout_button_no);
                        TextView title=(TextView)sureDialog.findViewById(R.id.sure_dialog_textview_title);

                        title.setText("این پست حذف شود ؟");

                        //set size
                        DisplayMetrics displaymetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                        mainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;

                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                if(MainActivity.mSocket.connected())
                                {
                                    JSONObject object=new JSONObject();
                                    try
                                    {
                                        sureDialog.dismiss();
                                        object.put("productid",resLoadMoreLibraryAndFavorites.getProductId());
                                        object.put("userid",MainActivity.userData.getUserid());
                                        MainActivity.mSocket.emit("deleteProduct",object);
                                    }
                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
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

                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                sureDialog.dismiss();
                            }
                        });

                        sureDialog.show();
                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        moreDialog.dismiss();
                    }
                });


                //which item show?
                if(curentPage.equals("fragmentProfile:library"))
                {
                    report.setVisibility(View.GONE);
                }
                else
                {
                    edit.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    if(resLoadMoreLibraryAndFavorites.getUserId().equals(MainActivity.userData.getUserid()))
                    {
                        report.setVisibility(View.GONE);
                    }
                }

                moreDialog.show();
            }
        });

        iSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                createHashtag();

                if(filepath.equals("")&&imagePath.equals("")&&description.equals("") && hashtagFinal.equals(""))
                {

                }
                else
                {
                    if(hashtag.size()!=0)
                    {
                        if(hashtag.size()<3)
                        {
                            Toast.makeText(SeeEditProduct.this,"کلمات مهم مورد ارسالی نمی تواند کمتر از 3 کلمه باشد",Toast.LENGTH_LONG).show();
                            TabLayout.Tab tab = tabLayout.getTabAt(1);
                            tab.select();
                            viewPager.setCurrentItem(tab.getPosition());
                        }
                        else if(hashtag.size()>10)
                        {
                            Toast.makeText(SeeEditProduct.this,"کلمات مهم مورد ارسالی نمی تواند بیشتر از 10 کلمه باشد",Toast.LENGTH_LONG).show();
                            TabLayout.Tab tab = tabLayout.getTabAt(1);
                            tab.select();
                            viewPager.setCurrentItem(tab.getPosition());
                        }
                        else if(MainActivity.mSocket.connected())
                        {
                            try
                            {
                                uploadFile();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
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
                    else if(MainActivity.mSocket.connected())
                    {
                        try
                        {
                            uploadFile();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
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
    }

    private void createHashtag()
    {
        if(!shashtag.equals(""))
        {
            String [] hashtag1=shashtag.toLowerCase().split(" ");
            List<String>listHashtag= new ArrayList<String>(Arrays.asList(hashtag1));
            for (int i=0;i<listHashtag.size();i++)
            {
                if(!listHashtag.get(i).equals(""))
                {
                    hashtag.add(listHashtag.get(i));
                }
            }

            hashtagFinal = TextUtils.join(",", hashtag);
        }
        else
        {
            shashtag="";
        }
    }

    private void uploadFile()throws IOException
    {
        Loading.show();


        if(imagePath.equals(""))
        {
            bytesImage=new byte[0];
        }
        else
        {
            //encodeing image
            File fileImage=new File(imagePath);
            // get type file from path file
            String [] k=imagePath.split("\\.");
            typeFileImage = k[k.length-1].toLowerCase();
            //init array with file length
            bytesImage = new byte[(int) fileImage.length()];
            FileInputStream fisImage = new FileInputStream(fileImage);
            fisImage.read(bytesImage); //read file into bytes[]
            fisImage.close();
        }



        if(filepath.equals(""))
        {
            bytesDocument=new byte[0];
        }
        else
        {
            //encodeing file
            File fileDocument=new File(filepath);
            // get type file from path file
            String [] m=filepath.split("\\.");
            typeFileDocument = m[m.length-1].toLowerCase();
            //init array with file length
            bytesDocument = new byte[(int) fileDocument.length()];
            FileInputStream fisDocument = new FileInputStream(fileDocument);
            fisDocument.read(bytesDocument); //read file into bytes[]
            fisDocument.close();
        }

        try
        {
            JSONObject obj = new JSONObject();

            obj.put("productid",resLoadMoreLibraryAndFavorites.getProductId());
            obj.put("encodedImage",bytesImage);
            obj.put("encodedFile",bytesDocument);
            obj.put("description",description);
            obj.put("hashtag",hashtagFinal);
            obj.put("typeFileDocument",typeFileDocument);
            obj.put("typeFileImage",typeFileImage);
            MainActivity.mSocket.emit("editProduct",obj);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


    }

    private boolean likeProduct()
    {
        for(int i=0;i<resSeeProductModel.listProductLike.size();i++)
        {

            if(resSeeProductModel.listProductLike.get(i).equals(MainActivity.userData.getUserid()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position)
    {

        TabLayout.Tab tab = tabLayout.getTabAt(position);
        tab.select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab)
    {
        //tab.select();
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    private void customTabView()
    {

        TabLayout.Tab tab1 = tabLayout.getTabAt(0);
        TabLayout.Tab tab2 = tabLayout.getTabAt(1);
        TabLayout.Tab tab3 = tabLayout.getTabAt(2);

        //select tab 1
        tabLayout.getTabAt(0).select();
        viewPager.setCurrentItem(0);

        LayoutInflater inflater1=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tab1View=inflater1.inflate(R.layout.tabview_tab_layout,null);
        View tab2View=inflater1.inflate(R.layout.tabview_tab_layout,null);
        View tab3View=inflater1.inflate(R.layout.tabview_tab_layout,null);


        TextView text1View=(TextView)tab1View.findViewById(R.id.tabview_tab_layout_textview_title);
        TextView text2View=(TextView)tab2View.findViewById(R.id.tabview_tab_layout_textview_title);
        TextView text3View=(TextView)tab3View.findViewById(R.id.tabview_tab_layout_textview_title);


        text1View.setText("عکس");
        text2View.setText("توضیحات");
        text3View.setText("فایل");


        //set type face

        text1View.setTypeface(typeface);
        text2View.setTypeface(typeface);
        text3View.setTypeface(typeface);


        tab1.setCustomView(tab1View);
        tab2.setCustomView(tab2View);
        tab3.setCustomView(tab3View);

    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        if(flagEdit)
        {
            //come back to first time
            flagEdit=false;
            rSave.setVisibility(View.GONE);
            rMore.setVisibility(View.VISIBLE);
            buttonbar.setVisibility(View.VISIBLE);
            tTitle.setVisibility(View.GONE);

            SeeEditProductFragmentDescription seeEditProductFragmentDescription = (SeeEditProductFragmentDescription) adapter.getItem(1);
            seeEditProductFragmentDescription.setDescription();

            viewPager.setAdapter(adapter);
            tabLayout.getTabAt(0).select();
            viewPager.setCurrentItem(0);

            filepath="";
            imagePath="";
            description="";
            shashtag="";
            hashtagFinal="";
        }

        else if(curentPage.equals("fragmentProfile")||curentPage.equals("fragmentProfile:library")||curentPage.equals("DontShowImageProfile")||curentPage.equals("FragmentLibrary"))
        {
            super.onBackPressed();

            MainActivity.mSocket.off("resLikeProduct",resLikeProduct);
            MainActivity.mSocket.off("resDeleteProduct",resDeleteProduct);
            MainActivity.mSocket.off("resEditProduct",resEditProduct);

            if(MainActivity.curentPage.equals("fragmentProfile"))
            {
                MainActivity.mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
            }

            filepath="";
            imagePath="";
            description="";
            shashtag="";
            hashtagFinal="";

            finish();
        }

    }

    //Extending FragmentStatePagerAdapter
    public class ViewPagerAdapter_TabLayout_Add extends FragmentStatePagerAdapter {

        //integer to count number of tabs
        int tabCount;
        SeeEditProductFragmentImage tab1 = new SeeEditProductFragmentImage();
        SeeEditProductFragmentDescription tab2 = new SeeEditProductFragmentDescription();
        SeeEditProductFragmentFile tab3 = new SeeEditProductFragmentFile();

        //Constructor to the class
        public ViewPagerAdapter_TabLayout_Add(FragmentManager fm, int tabCount) {
            super(fm);
            //Initializing tab count
            this.tabCount= tabCount;
        }

        //Overriding method getItem
        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            switch (position) {
                case 0:
                    return tab1;
                case 1:
                    return tab2;
                case 2:
                    return tab3;
                default:
                    return null;
            }
        }

        //Overriden method getCount to get the number of tabs
        @Override
        public int getCount() {
            return tabCount;
        }
    }


    Emitter.Listener resLikeProduct=new Emitter.Listener()
    {
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
                            if (data.getString("like").equals("like"))
                            {
                                iLike.setImageResource(R.drawable.favoriteprofileon);
                                tLike.setText((Integer.parseInt(tLike.getText().toString())+1)+"");
                                flagLike=true;

                            }
                            else
                            {
                                iLike.setImageResource(R.drawable.favoriteprofileoff);
                                tLike.setText((Integer.parseInt(tLike.getText().toString())-1)+"");
                                flagLike=false;
                            }

                            //resetFavoriteList
                            FragmentProfile fragmentProfile=(FragmentProfile) ((FragmentActivity)MainActivity.context).getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected5);
                            fragmentProfile.resetFavoriteList();
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

    Emitter.Listener resDeleteProduct=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

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

                            Toast.makeText(getApplicationContext(),"پست شما با موفقیت حذف شد",Toast.LENGTH_LONG).show();
                            //reset Library List and Favorites List
                            FragmentProfile fragmentProfile=(FragmentProfile) ((FragmentActivity)MainActivity.context).getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected5);
                            fragmentProfile.resetLibraryList();
                            fragmentProfile.resetFavoriteList();

                            MainActivity.mSocket.off("resLikeProduct",resLikeProduct);
                            MainActivity.mSocket.off("resDeleteProduct",resDeleteProduct);
                            MainActivity.mSocket.off("resEditProduct",resEditProduct);
                            MainActivity.mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
                            finish();
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


    Emitter.Listener resEditProduct=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            Loading.dismiss();
                            Toast.makeText(getApplicationContext(),"پست شما با موفقیت به روز شد",Toast.LENGTH_LONG).show();
                            //reset Library List
                            FragmentProfile fragmentProfile=(FragmentProfile) ((FragmentActivity)MainActivity.context).getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected5);
                            fragmentProfile.resetLibraryList();
                            fragmentProfile.resetFavoriteList();

                            MainActivity.mSocket.off("resLikeProduct",resLikeProduct);
                            MainActivity.mSocket.off("resDeleteProduct",resDeleteProduct);
                            MainActivity.mSocket.off("resEditProduct",resEditProduct);
                            MainActivity.mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());

                            filepath="";
                            imagePath="";
                            description="";
                            shashtag="";
                            hashtagFinal="";

                            finish();

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




    /**
     * Background Async Task to download file
     * */
    public class DownloadFileFromURL extends AsyncTask<String, String, String> {

        //Before starting background thread Show Progress Bar Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(progress_bar_type);
        }


         //Downloading file in background thread
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),8192);

                // get type file from path file
                String [] m=resSeeProductModel.getProductPathFile().split("\\.");
                String typeFile = m[m.length-1].toLowerCase();
                String fileName=resLoadMoreLibraryAndFavorites.getProductId();


                // create folder in sdk for save download files
                File myDirectory = new File(Environment.getExternalStorageDirectory(), "/Kahkeshan");
                if(!myDirectory.exists())
                {
                    myDirectory.mkdirs();
                }

                // Output stream
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/Kahkeshan/"+fileName+"."+typeFile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        //Updating progress bar
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //pDialog.setProgress(Integer.parseInt(progress[0]));
        }


        //After completing background task Dismiss the progress dialog

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            //dismissDialog(progress_bar_type);
            Loading.dismiss();
            Toast.makeText(getApplicationContext(),"فایل شما با موفقیت دانلود شد",Toast.LENGTH_LONG).show();

        }

    }


}
