package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.zelory.compressor.Compressor;
import ir.galaxycell.kahkeshan.File.FileUtil;
import ir.galaxycell.kahkeshan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 11/08/2017.
 */
public class FragmentAdd extends Fragment implements TabLayout.OnTabSelectedListener , ViewPager.OnPageChangeListener{

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button send;
    private Typeface typeface;
    private Dialog Loading;
    public static String description="",shashtag="",imagePath="",filepath="",hashtagFinal="";
    public static List<String>hashtag=new ArrayList<>();
    private File compressedImage;
    private ViewPagerAdapter_TabLayout_Add adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_add,container,false);

        //define type face
        typeface=Typeface.createFromAsset(getContext().getAssets(),"fonts/IRANSansMobileNormal.ttf");

        //linke widget in fragment home to layout
        link(view);

        return view;
    }


    private void link(View view)
    {
        //button
        send=(Button)view.findViewById(R.id.fragment_add_button_send);

        //tab layout
        tabLayout=(TabLayout)view.findViewById(R.id.fragment_add_tabs);
        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("عکس"));
        tabLayout.addTab(tabLayout.newTab().setText("توضیحات"));
        tabLayout.addTab(tabLayout.newTab().setText("فایل"));
        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);


        //view pager
        viewPager=(ViewPager)view.findViewById(R.id.fragment_add_viewpager);
        //Creating our pager adapter
        adapter = new ViewPagerAdapter_TabLayout_Add(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.setRotation(180);
        customTabView();
        //Adding OnPageChangeListener
        viewPager.setOnPageChangeListener(this);

        //create Lodin dialog
        Loading =new Dialog(getContext());
        Loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Loading.setContentView(R.layout.loading_dialog_layout);
        Loading.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Loading.setCanceledOnTouchOutside(false);
        Loading.setCancelable(false);



        //set on click listener
        onClick();
    }

    private void onClick()
    {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                createHashtag();

                if(imagePath.equals(""))
                {
                    Toast.makeText(getContext(),"لطفا عکسی برای مورد ارسالی انتخاب کنبد",Toast.LENGTH_LONG).show();
                    TabLayout.Tab tab = tabLayout.getTabAt(0);
                    tab.select();
                    viewPager.setCurrentItem(tab.getPosition());
                }
                else if(description.equals(""))
                {
                    Toast.makeText(getContext(),"لطفا توضیحات مورد ارسالی را پر کنید",Toast.LENGTH_LONG).show();
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.select();
                    viewPager.setCurrentItem(tab.getPosition());
                }
                else if(hashtag.size()<3)
                {
                    Toast.makeText(getContext(),"کلمات مهم مورد ارسالی نمی تواند کمتر از 3 کلمه باشد",Toast.LENGTH_LONG).show();
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.select();
                    viewPager.setCurrentItem(tab.getPosition());
                }
                else if(hashtag.size()>10)
                {
                    Toast.makeText(getContext(),"کلمات مهم مورد ارسالی نمی تواند بیشتر از 10 کلمه باشد",Toast.LENGTH_LONG).show();
                    TabLayout.Tab tab = tabLayout.getTabAt(1);
                    tab.select();
                    viewPager.setCurrentItem(tab.getPosition());
                }
                else if(filepath.equals(""))
                {
                    Toast.makeText(getContext(),"لطفا فایلی برای مورد ارسالی انتخاب کنبد",Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(),description,Toast.LENGTH_LONG).show();
                    TabLayout.Tab tab = tabLayout.getTabAt(2);
                    tab.select();
                    viewPager.setCurrentItem(tab.getPosition());
                }
                else
                {

                    try
                    {
                        uploadFile();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                        Loading.dismiss();
                    }

                }

            }
        });

    }

    private void createHashtag()
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

        LayoutInflater inflater1=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    //Extending FragmentStatePagerAdapter
    public class ViewPagerAdapter_TabLayout_Add extends FragmentStatePagerAdapter {

        //integer to count number of tabs
        int tabCount;
        FragmentAddImage tab1 = new FragmentAddImage();
        FragmentAddDescription tab2 = new FragmentAddDescription();
        FragmentAddFile tab3 = new FragmentAddFile();

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

    private void uploadFile()throws IOException
    {

        if(MainActivity.mSocket.connected())
        {
            Loading.show();

            //encodeing image
            File fileImage=new File(imagePath);
            // get type file from path file
            String [] k=imagePath.split("\\.");
            String typeFileImage = k[k.length-1].toLowerCase();
            //init array with file length
            byte[] bytesImage = new byte[(int) fileImage.length()];
            FileInputStream fisImage = new FileInputStream(fileImage);
            fisImage.read(bytesImage); //read file into bytes[]
            fisImage.close();


            //encodeing file
            File fileDocument=new File(filepath);
            // get type file from path file
            String [] m=filepath.split("\\.");
            String typeFileDocument = m[m.length-1].toLowerCase();
            //init array with file length
            byte[] bytesDocument = new byte[(int) fileDocument.length()];
            FileInputStream fisDocument = new FileInputStream(fileDocument);
            fisDocument.read(bytesDocument); //read file into bytes[]
            fisDocument.close();


            try
            {
                JSONObject obj = new JSONObject();

                obj.put("userid",MainActivity.userData.getUserid());
                obj.put("encodedImage",bytesImage);
                obj.put("encodedFile",bytesDocument);
                obj.put("description",description);
                obj.put("hashtag",hashtagFinal);
                obj.put("typeFileDocument",typeFileDocument);
                obj.put("typeFileImage",typeFileImage);
                MainActivity.mSocket.emit("createProduct",obj);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            if(!MainActivity.CheckNetwork.isShowing())
            {
                MainActivity.CheckNetwork.show();
            }
        }
    }

    public void cancelLoadingDialog()
    {
        Loading.dismiss();
    }
}
