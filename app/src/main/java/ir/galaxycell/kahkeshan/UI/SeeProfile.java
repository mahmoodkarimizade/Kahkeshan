package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.nkzawa.emitter.Emitter;
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.srain.cube.views.GridViewWithHeaderAndFooter;
import ir.galaxycell.kahkeshan.Adapter.Fragment_Profile_GridView_Adapter;
import ir.galaxycell.kahkeshan.Adapter.See_Profile_GridView_Adapter;
import ir.galaxycell.kahkeshan.InterFace.SeeprofileAdapterItemClickListener;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreFollowerAndFollowing;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreLibraryAndFavorites;
import ir.galaxycell.kahkeshan.Models.ResOtherLoadMoreLibrary;
import ir.galaxycell.kahkeshan.Models.ResOtherProfileInfoModel;
import ir.galaxycell.kahkeshan.Models.ResSeeProductModel;
import ir.galaxycell.kahkeshan.Models.ResUserProfileInfoModel;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Utility.Utility;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 10/09/2017.
 */
public class SeeProfile extends AppCompatActivity implements SeeprofileAdapterItemClickListener{

    private GridViewWithHeaderAndFooter gridView;
    private See_Profile_GridView_Adapter adapter;
    private String userId="",curentPage="";
    private List<ResOtherLoadMoreLibrary>listLibrary=new ArrayList<ResOtherLoadMoreLibrary>();
    private View headerView,footerView;
    private ResOtherProfileInfoModel resOtherProfileInfoModel=new ResOtherProfileInfoModel();
    private ImageView iMark,iMore;
    private CircleImageView iProfile;
    private TextView tUsername,tBiography,tLink,posts,following,follower;
    private RippleView rMark;
    private SuperSwipeRefreshLayout superSwipeRefreshLayout;
    private boolean flagLoadMore=false;
    private Dialog moreDialog,CheckNetwork;
    private boolean flagMark=false;
    private Dialog Loading;
    private LinearLayout cnMainLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_profile);

        //get data from intent
        userId=getIntent().getStringExtra("UserId");
        curentPage=getIntent().getStringExtra("curentPage");

        //linke widget to layout
        link();

        //listen to socket
        JSONObject object=new JSONObject();
        try
        {
            object.put("userid",userId);
            object.put("useridSee",MainActivity.userData.getUserid());
            MainActivity.mSocket.emit("otherProfileInfo",object);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        MainActivity.mSocket.on("resOtherProfileInfo",resOtherProfileInfo);
        MainActivity.mSocket.on("resOtherLoadMore",resOtherLoadMore);
        MainActivity.mSocket.on("resmMrkUser",resmMrkUser);
    }


    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        MainActivity.mSocket.off("resOtherProfileInfo",resOtherProfileInfo);
        MainActivity.mSocket.off("resOtherLoadMore",resOtherLoadMore);
        MainActivity.mSocket.off("resmMrkUser",resmMrkUser);
        if(curentPage.equals("fragmentProfile"))
        {
            MainActivity.mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
        }
        finish();
    }

    private void link()
    {

        //SuperS wipe Refresh Layout
        superSwipeRefreshLayout=(SuperSwipeRefreshLayout)findViewById(R.id.see_profile_swipe_refresh);
        View header = LayoutInflater.from(superSwipeRefreshLayout.getContext()).inflate(R.layout.swipe_refresh_header_layout, null);
        superSwipeRefreshLayout.setHeaderView(header);

        //Grid View With Header And Footer
        gridView=(GridViewWithHeaderAndFooter)findViewById(R.id.see_profile_gridview);
        LayoutInflater layoutInflater = LayoutInflater.from(SeeProfile.this);
        headerView = layoutInflater.inflate(R.layout.see_profile_gridview_headr_layout, null);
        footerView = layoutInflater.inflate(R.layout.see_profile_gridview_footer_layout, null);
        gridView.addHeaderView(headerView);
        gridView.addFooterView(footerView);

        //Ripple View
        rMark=(RippleView)headerView.findViewById(R.id.see_profile_gridview_headr_layout_rippview_mark);

        //Text View
        tUsername=(TextView) headerView.findViewById(R.id.see_profile_gridview_headr_layout_textview_username);
        tBiography=(TextView) headerView.findViewById(R.id.see_profile_gridview_headr_layout_textview_biography);
        tLink=(TextView) headerView.findViewById(R.id.see_profile_gridview_headr_layout_textview_link);
        posts=(TextView) headerView.findViewById(R.id.see_profile_gridview_headr_layout_textview_posts);
        following=(TextView) headerView.findViewById(R.id.see_profile_gridview_headr_layout_textview_following);
        follower=(TextView) headerView.findViewById(R.id.see_profile_gridview_headr_layout_textview_follower);

        //Circle Image View
        iProfile=(CircleImageView)headerView.findViewById(R.id.see_profile_gridview_headr_layout_imageview_imageprofile);

        //Image View
        iMark=(ImageView) headerView.findViewById(R.id.see_profile_gridview_headr_layout_imageview_mark);
        iMore=(ImageView)findViewById(R.id.see_profile_toolbar_imageview_more);

        //create Lodin dialog
        Loading =new Dialog(SeeProfile.this);
        Loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Loading.setContentView(R.layout.loading_dialog_layout);
        Loading.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Loading.setCanceledOnTouchOutside(false);
        //Loading.setCancelable(false);


        //
        //////////CheckNetwork
        //
        //create CheckNetwork dialog
        CheckNetwork =new Dialog(SeeProfile.this);
        CheckNetwork.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CheckNetwork.setContentView(R.layout.check_network_connect_dialog_layout);
        CheckNetwork.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CheckNetwork.getWindow().setBackgroundDrawableResource(R.drawable.boder_loding_layout);

        //define widget
        cnMainLayout=(LinearLayout)CheckNetwork.findViewById(R.id.check_network_connect_dialog_layout_linerlayout_mainlayout);

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) SeeProfile.this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        cnMainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;
        //
        //////////CheckNetwork
        //


        //on click listener
        onClick();

    }

    private void onClick()
    {
        rMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(MainActivity.mSocket.connected())
                {
                    JSONObject object=new JSONObject();

                    try
                    {
                        object.put("userId",userId);
                        object.put("userIdMarker",MainActivity.userData.getUserid());
                        object.put("markStatus",!flagMark);
                        MainActivity.mSocket.emit("markUser",object);
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

        iMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                moreDialog =new Dialog(SeeProfile.this,R.style.PauseDialog);
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

                download.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);

                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        moreDialog.dismiss();;
                    }
                });

                moreDialog.show();
            }
        });


        superSwipeRefreshLayout.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh()
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        superSwipeRefreshLayout.setRefreshing(false);
                    }
                },3000);
            }

            @Override
            public void onPullDistance(int i)
            {

            }

            @Override
            public void onPullEnable(boolean b)
            {

            }
        });


        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState)
            {

                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if (absListView.getLastVisiblePosition() >= adapter.getCount() - 1)
                    {
                        if(listLibrary.size()<Integer.parseInt(posts.getText().toString()))
                        {
                            flagLoadMore=true;
                            loadMore();
                        }

                    }

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {

            }
        });
    }


    Emitter.Listener resOtherProfileInfo=new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];

                    resOtherProfileInfoModel=new ResOtherProfileInfoModel();
                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            //set data recevie into resUserProfileInfoModel
                            resOtherProfileInfoModel.profileImage=Utility.BaseUrl+data.getString("profileImage");
                            resOtherProfileInfoModel.username=data.getString("username");
                            resOtherProfileInfoModel.biography=data.getString("biography");
                            resOtherProfileInfoModel.link=data.getString("link");
                            resOtherProfileInfoModel.countLibrary=data.getInt("countLibrary");
                            //resOtherProfileInfoModel.Following=data.getJSONArray("Following");
                            resOtherProfileInfoModel.countFollower=data.getInt("countFollower");
                            resOtherProfileInfoModel.countFollowing=data.getInt("countFollowing");
                            resOtherProfileInfoModel.flagMark=data.getBoolean("flagMark");

                            //set data receive to widget
                            Glide.with(SeeProfile.this)
                                    .load(resOtherProfileInfoModel.profileImage)
                                    .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                                    .apply(RequestOptions.errorOf(R.drawable.cancel))
                                    .into(iProfile);



                            tUsername.setText(resOtherProfileInfoModel.username);
                            tBiography.setText(resOtherProfileInfoModel.biography);
                            tLink.setText(resOtherProfileInfoModel.link);
                            posts.setText(resOtherProfileInfoModel.countLibrary+"");
                            following.setText(resOtherProfileInfoModel.countFollowing+"");
                            follower.setText(resOtherProfileInfoModel.countFollower+"");

                            //folllowing user or no
                            //checkFollowing();
                            if (resOtherProfileInfoModel.isFlagMark())
                            {
                                iMark.setImageResource(R.drawable.markprofileon);
                                flagMark=true;
                            }
                            else
                            {
                                iMark.setImageResource(R.drawable.markprofileoff);
                                flagMark=false;
                            }

                            loadMore();
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

    Emitter.Listener resOtherLoadMore=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject jsonObject=(JSONObject) args[0];

                    try
                    {
                        if(jsonObject.getBoolean("status"))
                        {

                            Loading.dismiss();

                            JSONArray jsonArray=jsonObject.getJSONArray("resProducts");
                            for(int i=jsonArray.length()-1;i>=0;i--)
                            {
                                ResOtherLoadMoreLibrary resOtherLoadMoreLibrary=new ResOtherLoadMoreLibrary();
                                resOtherLoadMoreLibrary.productId=jsonArray.getJSONObject(i).getString("_id");
                                resOtherLoadMoreLibrary.userId=jsonArray.getJSONObject(i).getString("userid");
                                resOtherLoadMoreLibrary.productImage=jsonArray.getJSONObject(i).getString("image");
                                resOtherLoadMoreLibrary.productBlock=jsonArray.getJSONObject(i).getString("block");

                                listLibrary.add(resOtherLoadMoreLibrary);

                            }

                            if(listLibrary.size()<=15)
                            {
                                adapter=new See_Profile_GridView_Adapter(SeeProfile.this,listLibrary);
                                gridView.setAdapter(adapter);
                            }
                            else
                            {
                                adapter.notifyDataSetChanged();
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

    private void loadMore()
    {
        if(MainActivity.mSocket.connected())
        {

            Loading.show();

            JSONObject jsonObject=new JSONObject();

            try
            {
                jsonObject.put("userid",userId);
                jsonObject.put("end",(resOtherProfileInfoModel.countLibrary-1)-listLibrary.size());
                if(((resOtherProfileInfoModel.countLibrary-1)-listLibrary.size())-14>0)
                {
                    jsonObject.put("start",((resOtherProfileInfoModel.countLibrary-1)-listLibrary.size())-14);
                }
                else
                {
                    jsonObject.put("start",0);
                }

                MainActivity.mSocket.emit("otherLoadMore",jsonObject);

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

    private void checkFollowing() throws JSONException {
        for (int i=0;i<resOtherProfileInfoModel.Following.length();i++)
        {
            if(resOtherProfileInfoModel.Following.getString(i).equals(MainActivity.userData.getUserid()))
            {
                flagMark=true;
                break;
            }
        }

        if (flagMark)
        {
            iMark.setImageResource(R.drawable.markprofileon);
        }
        else
        {
            iMark.setImageResource(R.drawable.markprofileoff);
        }
    }

    Emitter.Listener resmMrkUser=new Emitter.Listener() {
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
                            if(data.getString("mark").equals("mark"))
                            {
                                flagMark=true;
                                iMark.setImageResource(R.drawable.markprofileon);
                            }
                            else
                            {
                                flagMark=false;
                                iMark.setImageResource(R.drawable.markprofileoff);
                            }

                            //resetFollowerList
                            FragmentProfile fragmentProfile=(FragmentProfile) ((FragmentActivity)MainActivity.context).getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected5);
                            fragmentProfile.resetFollowerList();
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
    public void OnItemClickListener(int i)
    {
        ResLoadMoreLibraryAndFavorites resLoadMoreLibraryAndFavorites=new ResLoadMoreLibraryAndFavorites();
        resLoadMoreLibraryAndFavorites.productId=listLibrary.get(i).productId;
        resLoadMoreLibraryAndFavorites.userId=listLibrary.get(i).userId;
        resLoadMoreLibraryAndFavorites.productImage=listLibrary.get(i).productImage;
        resLoadMoreLibraryAndFavorites.productBlock=listLibrary.get(i).productBlock;

        FragmentProfile fragmentProfile=(FragmentProfile) ((FragmentActivity)MainActivity.context).getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected5);
        fragmentProfile.sendDataToSeeEditProductFromSeeProfile(resLoadMoreLibraryAndFavorites);

    }


}
