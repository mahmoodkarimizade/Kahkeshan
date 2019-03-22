package ir.galaxycell.kahkeshan.UI;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
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
import ir.galaxycell.kahkeshan.Models.ResLoadMoreFollowerAndFollowing;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreLibraryAndFavorites;
import ir.galaxycell.kahkeshan.Models.ResOtherLoadMoreLibrary;
import ir.galaxycell.kahkeshan.Models.ResSeeProductModel;
import ir.galaxycell.kahkeshan.Models.ResUserProfileInfoModel;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Utility.Utility;

/**
 * Created by Admin on 11/08/2017.
 */
public class FragmentProfile extends Fragment {

    private View view;
    private GridViewWithHeaderAndFooter gridView;
    private List<String>list=new ArrayList<String>();
    private ImageView iLibnary,iFavorite,iFollower,iFollwing;
    private CircleImageView iProfile;
    private TextView tLibnary,tFavorite,tFollower,tFollwing,tUsername,tBiography,tLink,posts,following,follower;
    private RippleView rLibnary,rFavorite,rFollower,rFollwing,rEditProfile;
    private SuperSwipeRefreshLayout superSwipeRefreshLayout;
    private ResUserProfileInfoModel resUserProfileInfoModel=new ResUserProfileInfoModel();
    public static String curentPage="library";
    private List<ResLoadMoreLibraryAndFavorites>libraryList=new ArrayList<>();
    private List<ResLoadMoreLibraryAndFavorites>favoriteList=new ArrayList<>();
    private List<ResLoadMoreFollowerAndFollowing>followingList=new ArrayList<>();
    private List<ResLoadMoreFollowerAndFollowing>followerList=new ArrayList<>();
    private Fragment_Profile_GridView_Adapter gridViewAdapter;
    private View headerView,footerView;
    private boolean flagLoadMore=false;
    private Intent gotoSeeEditProduct;
    private ResLoadMoreLibraryAndFavorites resLoadMoreLibraryAndFavorites;
    private Dialog Loading;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_profile,container,false);

        //linke widget in fragment profile to layout
        link(view);

        //socket listener
        MainActivity.mSocket.on("resUserProfileInfo",resUserProfileInfo);
        MainActivity.mSocket.on("resLoadMore",resLoadMore);
        MainActivity.mSocket.on("resSeeProduct",resSeeProduct);

        return view;
    }



    private void link(View view)
    {

        //SuperS wipe Refresh Layout
        superSwipeRefreshLayout=(SuperSwipeRefreshLayout)view.findViewById(R.id.fragment_profile_swipe_refresh);
        View header = LayoutInflater.from(superSwipeRefreshLayout.getContext()).inflate(R.layout.swipe_refresh_header_layout, null);
        superSwipeRefreshLayout.setHeaderView(header);

        //Grid View With Header And Footer
        gridView=(GridViewWithHeaderAndFooter)view.findViewById(R.id.fragment_profile_gridview);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        headerView = layoutInflater.inflate(R.layout.fragment_profile_gridview_headr_layout, null);
        footerView = layoutInflater.inflate(R.layout.fragment_profile_gridview_footer_layout, null);
        gridView.addHeaderView(headerView);
        gridView.addFooterView(footerView);

        //Ripple View
        rLibnary=(RippleView)headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_rippview_libraryprofile);
        rFavorite=(RippleView)headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_rippview_favoritesprofile);
        rFollwing=(RippleView)headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_rippview_followingprofile);
        rFollower=(RippleView)headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_rippview_followersprofile);
        rEditProfile=(RippleView)headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_rippview_editprofile);

        //Text View
        tLibnary=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_libraryprofile);
        tFavorite=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_favoritesprofile);
        tFollwing=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_followingprofile);
        tFollower=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_followersprofile);
        tUsername=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_username);
        tBiography=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_biography);
        tLink=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_link);
        posts=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_posts);
        following=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_following);
        follower=(TextView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_textview_follower);

        //Circle Image View
        iProfile=(CircleImageView)headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_imageview_imageprofile);

        //Image View
        iLibnary=(ImageView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_imageview_libraryprofile);
        iFavorite=(ImageView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_imageview_favoritesprofile);
        iFollwing=(ImageView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_imageview_followingprofile);
        iFollower=(ImageView) headerView.findViewById(R.id.fragment_profile_gridview_headr_layout_imageview_followersprofile);

        //set list gridview
        /*setlistgridview();
        Fragment_Profile_GridView_Adapter gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),list);
        gridView.setAdapter(gridViewAdapter);*/
        //create null list
        List<ResLoadMoreFollowerAndFollowing>list1=new ArrayList<>();
        gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),libraryList,list1,curentPage);


        //create Lodin dialog
        Loading =new Dialog(getContext());
        Loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Loading.setContentView(R.layout.loading_dialog_layout);
        Loading.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Loading.setCanceledOnTouchOutside(false);
        //Loading.setCancelable(false);


        //set on click listener
        onClick();
    }

    private void setlistgridview()
    {
        for (int i=0;i<15;i++)
        {
            list.add(""+i);
        }
    }

    private void onClick()
    {

        rLibnary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                iLibnary.setImageResource(R.drawable.libraryprofileon);
                iFavorite.setImageResource(R.drawable.favoriteprofileoff);
                iFollwing.setImageResource(R.drawable.followingprofileoff);
                iFollower.setImageResource(R.drawable.followerprofileoff);

                tLibnary.setTextColor(getResources().getColor(R.color.selectitemtools));
                tFavorite.setTextColor(getResources().getColor(R.color.selectitem));
                tFollwing.setTextColor(getResources().getColor(R.color.selectitem));
                tFollower.setTextColor(getResources().getColor(R.color.selectitem));

                curentPage="library";

                loadTollsProfileData();

                resetFollowingList();

            }
        });

        rFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                iLibnary.setImageResource(R.drawable.library_on);
                iFavorite.setImageResource(R.drawable.favoriteprofileon);
                iFollwing.setImageResource(R.drawable.followingprofileoff);
                iFollower.setImageResource(R.drawable.followerprofileoff);

                tLibnary.setTextColor(getResources().getColor(R.color.selectitem));
                tFavorite.setTextColor(getResources().getColor(R.color.selectitemtools));
                tFollwing.setTextColor(getResources().getColor(R.color.selectitem));
                tFollower.setTextColor(getResources().getColor(R.color.selectitem));

                curentPage="favorites";

                loadTollsProfileData();

                resetFollowingList();
            }
        });

        rFollwing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iLibnary.setImageResource(R.drawable.library_on);
                iFavorite.setImageResource(R.drawable.favoriteprofileoff);
                iFollwing.setImageResource(R.drawable.followingprofileon);
                iFollower.setImageResource(R.drawable.followerprofileoff);

                tLibnary.setTextColor(getResources().getColor(R.color.selectitem));
                tFavorite.setTextColor(getResources().getColor(R.color.selectitem));
                tFollwing.setTextColor(getResources().getColor(R.color.selectitemtools));
                tFollower.setTextColor(getResources().getColor(R.color.selectitem));

                curentPage="following";

                loadTollsProfileData();
            }
        });

        rFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iLibnary.setImageResource(R.drawable.library_on);
                iFavorite.setImageResource(R.drawable.favoriteprofileoff);
                iFollwing.setImageResource(R.drawable.followingprofileoff);
                iFollower.setImageResource(R.drawable.followerprofileon);

                tLibnary.setTextColor(getResources().getColor(R.color.selectitem));
                tFavorite.setTextColor(getResources().getColor(R.color.selectitem));
                tFollwing.setTextColor(getResources().getColor(R.color.selectitem));
                tFollower.setTextColor(getResources().getColor(R.color.selectitemtools));

                curentPage="follower";

                loadTollsProfileData();

                resetFollowingList();
            }
        });


        rEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent gotoEditProfile=new Intent(getActivity(),EditProfile.class);

                gotoEditProfile.putExtra("resUserProfileInfoModel",resUserProfileInfoModel);
                startActivity(gotoEditProfile);
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
                    if (absListView.getLastVisiblePosition() >= gridViewAdapter.getCount() - 1)
                    {
                        switch (curentPage)
                        {
                            case "library":
                                if(libraryList.size()<Integer.parseInt(tLibnary.getText().toString()))
                                {
                                    flagLoadMore=true;
                                    loadTollsProfileData();
                                }
                                break;
                            case "favorites":
                                if(favoriteList.size()<Integer.parseInt(tFavorite.getText().toString()))
                                {
                                    flagLoadMore=true;
                                    loadTollsProfileData();
                                }
                                break;
                            case "following":
                                if(followingList.size()<Integer.parseInt(tFavorite.getText().toString()))
                                {
                                    flagLoadMore=true;
                                    loadTollsProfileData();
                                    Toast.makeText(getContext(),"OK",Toast.LENGTH_LONG).show();
                                }
                                break;
                            case "follower":
                                if(followerList.size()<Integer.parseInt(tFavorite.getText().toString()))
                                {
                                    flagLoadMore=true;
                                    loadTollsProfileData();
                                    Toast.makeText(getContext(),"OK",Toast.LENGTH_LONG).show();
                                }
                                break;
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


    private Emitter.Listener resUserProfileInfo=new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {

            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];
                    resUserProfileInfoModel=new ResUserProfileInfoModel();
                    try
                    {

                        //set data recevie into resUserProfileInfoModel
                        resUserProfileInfoModel.profileImage=Utility.BaseUrl+data.getString("profileImage");
                        resUserProfileInfoModel.username=data.getString("username");
                        resUserProfileInfoModel.biography=data.getString("biography");
                        resUserProfileInfoModel.link=data.getString("link");
                        resUserProfileInfoModel.countLibrary=data.getInt("countLibrary");
                        resUserProfileInfoModel.countFavorites=data.getInt("countFavorites");
                        resUserProfileInfoModel.countFollowing=data.getInt("countFollowing");
                        resUserProfileInfoModel.countFollower=data.getInt("countFollower");

                        //set data receive to widget
                        Glide.with(getContext())
                                .load(resUserProfileInfoModel.profileImage)
                                .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                                .apply(RequestOptions.errorOf(R.drawable.cancel))
                                .into(iProfile);



                        tUsername.setText(resUserProfileInfoModel.username);
                        tBiography.setText(resUserProfileInfoModel.biography);
                        tLink.setText(resUserProfileInfoModel.link);
                        posts.setText(resUserProfileInfoModel.countLibrary+"");
                        following.setText(resUserProfileInfoModel.countFollowing+"");
                        follower.setText(resUserProfileInfoModel.countFollower+"");
                        tLibnary.setText(resUserProfileInfoModel.countLibrary+"");
                        tFavorite.setText(resUserProfileInfoModel.countFavorites+"");
                        tFollwing.setText(resUserProfileInfoModel.countFollowing+"");
                        tFollower.setText(resUserProfileInfoModel.countFollower+"");

                        loadTollsProfileData();



                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    private void loadTollsProfileData()
    {

        if(MainActivity.mSocket.connected())
        {
            switch (curentPage)
            {
                case "library":
                    if(libraryList.size()==0 && !tLibnary.getText().toString().equals("0"))
                    {
                        Loading.show();

                        //load more from 0 to 15
                        JSONObject jsonObject=createLibraryObject();
                        MainActivity.mSocket.emit("loadMore",jsonObject);
                    }
                    else if(flagLoadMore)
                    {
                        Loading.show();
                        flagLoadMore=false;

                        //load more from 16 to ...
                        JSONObject jsonObject=createLibraryObject();
                        MainActivity.mSocket.emit("loadMore",jsonObject);
                    }
                    else
                    {
                        //create null list
                        List<ResLoadMoreFollowerAndFollowing>list1=new ArrayList<>();

                        gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),libraryList,list1,curentPage);
                        gridView.setAdapter(gridViewAdapter);

                    }
                    break;
                case "favorites":
                    if(favoriteList.size()==0 && !tFavorite.getText().toString().equals("0"))
                    {
                        Loading.show();
                        //load more from 0 to 15
                        JSONObject jsonObject=createFavoritesObject();
                        MainActivity.mSocket.emit("loadMore",jsonObject);
                    }
                    else if(flagLoadMore)
                    {
                        Loading.show();
                        flagLoadMore=false;

                        //load more from 16 to ...
                        JSONObject jsonObject=createFavoritesObject();
                        MainActivity.mSocket.emit("loadMore",jsonObject);
                    }
                    else
                    {
                        //create null list
                        List<ResLoadMoreFollowerAndFollowing>list1=new ArrayList<>();

                        gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),favoriteList,list1,curentPage);
                        gridView.setAdapter(gridViewAdapter);
                    }
                    break;
                case "following":
                    if(followingList.size()==0 && !tFollwing.getText().toString().equals("0"))
                    {
                        Loading.show();
                        //load more from 0 to 15
                        JSONObject jsonObject=createFollowingObject();
                        MainActivity.mSocket.emit("loadMore",jsonObject);
                    }
                    else if(flagLoadMore)
                    {
                        Loading.show();
                        flagLoadMore=false;

                        //load more from 16 to ...
                        JSONObject jsonObject=createFollowingObject();
                        MainActivity.mSocket.emit("loadMore",jsonObject);
                    }
                    break;
                case "follower":
                    if(followerList.size()==0 && !tFollower.getText().toString().equals("0"))
                    {
                        Loading.show();
                        //load more from 0 to 15
                        JSONObject jsonObject=createFollowerObject();
                        MainActivity.mSocket.emit("loadMore",jsonObject);
                    }
                    else if(flagLoadMore)
                    {
                        Loading.show();
                        flagLoadMore=false;

                        //load more from 16 to ...
                        JSONObject jsonObject=createFollowerObject();
                        MainActivity.mSocket.emit("loadMore",jsonObject);
                    }
                    else
                    {
                        //create null list
                        List<ResLoadMoreLibraryAndFavorites>list=new ArrayList<>();

                        gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),list,followerList,curentPage);
                        gridView.setAdapter(gridViewAdapter);
                    }
                    break;
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


    private JSONObject createFollowingObject()
    {
        JSONObject jsonObject=new JSONObject();

        try
        {
            //int end;
            jsonObject.put("userid",MainActivity.userData.getUserid());
            jsonObject.put("curentPage",curentPage);
            jsonObject.put("end",(resUserProfileInfoModel.countFollowing-1)-followingList.size());
            if(((resUserProfileInfoModel.countFollowing-1)-followingList.size())-14>0)
            {
                jsonObject.put("start",((resUserProfileInfoModel.countFollowing-1)-followingList.size())-14);
            }
            else
            {
                jsonObject.put("start",0);
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private JSONObject createFollowerObject()
    {
        JSONObject jsonObject=new JSONObject();

        try
        {
            //int end;
            jsonObject.put("userid",MainActivity.userData.getUserid());
            jsonObject.put("curentPage",curentPage);
            jsonObject.put("end",(resUserProfileInfoModel.countFollower-1)-followerList.size());
            if(((resUserProfileInfoModel.countFollower-1)-followerList.size())-14>0)
            {
                jsonObject.put("start",((resUserProfileInfoModel.countFollower-1)-followerList.size())-14);
            }
            else
            {
                jsonObject.put("start",0);
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private JSONObject createFavoritesObject()
    {
        JSONObject jsonObject=new JSONObject();

        try
        {
            //int end;
            jsonObject.put("userid",MainActivity.userData.getUserid());
            jsonObject.put("curentPage",curentPage);
            jsonObject.put("end",(resUserProfileInfoModel.countFavorites-1)-favoriteList.size());
            if(((resUserProfileInfoModel.countFavorites-1)-favoriteList.size())-14>0)
            {
                jsonObject.put("start",((resUserProfileInfoModel.countFavorites-1)-favoriteList.size())-14);
            }
            else
            {
                jsonObject.put("start",0);
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private JSONObject createLibraryObject()
    {
        JSONObject jsonObject=new JSONObject();

        try
        {
            //int end;
            jsonObject.put("userid",MainActivity.userData.getUserid());
            jsonObject.put("curentPage",curentPage);
            jsonObject.put("end",(resUserProfileInfoModel.countLibrary-1)-libraryList.size());
            if(((resUserProfileInfoModel.countLibrary-1)-libraryList.size())-14>0)
            {
                jsonObject.put("start",((resUserProfileInfoModel.countLibrary-1)-libraryList.size())-14);
            }
            else
            {
                jsonObject.put("start",0);
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private Emitter.Listener resLoadMore=new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    JSONObject jsonObject=(JSONObject) args[0];
                    try
                    {
                        if(jsonObject.getBoolean("status"))
                        {

                            Loading.dismiss();

                            if(curentPage.equals("library")||curentPage.equals("favorites"))
                            {
                                JSONArray jsonArray=jsonObject.getJSONArray("resProducts");

                                for(int i=jsonArray.length()-1;i>=0;i--)
                                {
                                    ResLoadMoreLibraryAndFavorites resLoadMoreLibraryAndFavorites=new ResLoadMoreLibraryAndFavorites();
                                    resLoadMoreLibraryAndFavorites.productId=jsonArray.getJSONObject(i).getString("_id");
                                    resLoadMoreLibraryAndFavorites.userId=jsonArray.getJSONObject(i).getString("userid");
                                    resLoadMoreLibraryAndFavorites.productImage=jsonArray.getJSONObject(i).getString("image");
                                    resLoadMoreLibraryAndFavorites.productBlock=jsonArray.getJSONObject(i).getString("block");


                                    if(curentPage.equals("library"))
                                    {
                                        libraryList.add(resLoadMoreLibraryAndFavorites);

                                    }
                                    else
                                    {
                                        favoriteList.add(resLoadMoreLibraryAndFavorites);
                                    }

                                }

                                if(curentPage.equals("library"))
                                {
                                    if(libraryList.size()<=15)
                                    {
                                        //create null list
                                        List<ResLoadMoreFollowerAndFollowing>list1=new ArrayList<>();
                                        gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),libraryList,list1,curentPage);
                                        gridView.setAdapter(gridViewAdapter);
                                    }
                                    else
                                    {
                                        gridViewAdapter.notifyDataSetChanged();
                                    }
                                }
                                else
                                {
                                    if(favoriteList.size()<=15)
                                    {
                                        //create null list
                                        List<ResLoadMoreFollowerAndFollowing>list1=new ArrayList<>();
                                        gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),favoriteList,list1,curentPage);
                                        gridView.setAdapter(gridViewAdapter);
                                    }
                                    else
                                    {
                                        gridViewAdapter.notifyDataSetChanged();
                                    }
                                }

                            }
                            else
                            {
                                JSONArray jsonArrayUser=jsonObject.getJSONArray("resUsers");
                                for(int i=jsonArrayUser.length()-1;i>=0;i--)
                                {
                                    ResLoadMoreFollowerAndFollowing resLoadMoreFollowerAndFollowing=new ResLoadMoreFollowerAndFollowing();
                                    resLoadMoreFollowerAndFollowing.userId=jsonArrayUser.getJSONObject(i).getString("_id");
                                    resLoadMoreFollowerAndFollowing.username=jsonArrayUser.getJSONObject(i).getString("username");
                                    resLoadMoreFollowerAndFollowing.avatar=jsonArrayUser.getJSONObject(i).getString("avatar");
                                    resLoadMoreFollowerAndFollowing.block=jsonArrayUser.getJSONObject(i).getString("block");

                                    if(curentPage.equals("following"))
                                    {
                                        followingList.add(resLoadMoreFollowerAndFollowing);

                                    }
                                    else
                                    {
                                        followerList.add(resLoadMoreFollowerAndFollowing);
                                    }
                                }

                                if(curentPage.equals("following"))
                                {
                                    if(followingList.size()<=15)
                                    {
                                        //create null list
                                        List<ResLoadMoreLibraryAndFavorites>list=new ArrayList<>();
                                        gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),list,followingList,curentPage);
                                        gridView.setAdapter(gridViewAdapter);
                                    }
                                    else
                                    {
                                        gridViewAdapter.notifyDataSetChanged();
                                    }
                                }
                                else
                                {
                                    if(followerList.size()<=15)
                                    {
                                        //create null list
                                        List<ResLoadMoreLibraryAndFavorites>list=new ArrayList<>();
                                        gridViewAdapter=new Fragment_Profile_GridView_Adapter(getContext(),list,followerList,curentPage);
                                        gridView.setAdapter(gridViewAdapter);
                                    }
                                    else
                                    {
                                        gridViewAdapter.notifyDataSetChanged();
                                    }
                                }
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

    public void resetLibraryList()
    {
        libraryList.clear();

        iLibnary.setImageResource(R.drawable.libraryprofileon);
        iFavorite.setImageResource(R.drawable.favoriteprofileoff);
        iFollwing.setImageResource(R.drawable.followingprofileoff);
        iFollower.setImageResource(R.drawable.followerprofileoff);

        tLibnary.setTextColor(getResources().getColor(R.color.selectitemtools));
        tFavorite.setTextColor(getResources().getColor(R.color.selectitem));
        tFollwing.setTextColor(getResources().getColor(R.color.selectitem));
        tFollower.setTextColor(getResources().getColor(R.color.selectitem));

        curentPage="library";
    }

    public  void resetFavoriteList()
    {
        favoriteList.clear();
    }

    public void resetFollowerList()
    {
        followerList.clear();
    }

    public void resetFollowingList()
    {
        followingList.clear();
    }

    public void sendDataToSeeEditProduct(int i)
    {
        if(MainActivity.mSocket.connected())
        {
            JSONObject object=new JSONObject();
            switch (curentPage)
            {
                case "library":
                    //socket emiter
                    try
                    {
                        object.put("productid",libraryList.get(i).getProductId());
                        object.put("userid",libraryList.get(i).getUserId());
                        object.put("useridSee",MainActivity.userData.getUserid());
                        MainActivity.mSocket.emit("seeProduct",object);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }



                    gotoSeeEditProduct=new Intent(getContext(), SeeEditProduct.class);
                    gotoSeeEditProduct.putExtra("resLoadMoreLibraryAndFavorites",libraryList.get(i));
                    gotoSeeEditProduct.putExtra("curentPage", MainActivity.curentPage+":"+curentPage);

                    break;
                case "favorites":

                    //socket emiter
                    try
                    {
                        object.put("productid",favoriteList.get(i).getProductId());
                        object.put("userid",favoriteList.get(i).getUserId());
                        object.put("useridSee",MainActivity.userData.getUserid());
                        MainActivity.mSocket.emit("seeProduct",object);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }


                    gotoSeeEditProduct=new Intent(getContext(), SeeEditProduct.class);
                    gotoSeeEditProduct.putExtra("resLoadMoreLibraryAndFavorites",favoriteList.get(i));
                    gotoSeeEditProduct.putExtra("curentPage", MainActivity.curentPage);
                    break;
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


    public void sendDataToSeeEditProductFromSeeProfile(ResLoadMoreLibraryAndFavorites resLoadMoreLibraryAndFavorites)
    {

        if(MainActivity.mSocket.connected())
        {
            JSONObject object=new JSONObject();
            try
            {
                object.put("productid",resLoadMoreLibraryAndFavorites.getProductId());
                object.put("userid",resLoadMoreLibraryAndFavorites.getUserId());
                object.put("useridSee",MainActivity.userData.getUserid());
                MainActivity.mSocket.emit("seeProduct",object);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            gotoSeeEditProduct=new Intent(getContext(), SeeEditProduct.class);
            gotoSeeEditProduct.putExtra("resLoadMoreLibraryAndFavorites",resLoadMoreLibraryAndFavorites);
            gotoSeeEditProduct.putExtra("curentPage","DontShowImageProfile");
        }
        else
        {
            if(!MainActivity.CheckNetwork.isShowing())
            {
                MainActivity.CheckNetwork.show();
            }
        }
    }


    public void sendDataToSeeEditProductFromFragmentLibrary(ResLoadMoreLibraryAndFavorites resLoadMoreLibraryAndFavorites)
    {

        if(MainActivity.mSocket.connected())
        {
            JSONObject object=new JSONObject();
            try
            {
                object.put("productid",resLoadMoreLibraryAndFavorites.getProductId());
                object.put("userid",resLoadMoreLibraryAndFavorites.getUserId());
                object.put("useridSee",MainActivity.userData.getUserid());
                MainActivity.mSocket.emit("seeProduct",object);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            gotoSeeEditProduct=new Intent(getContext(), SeeEditProduct.class);
            gotoSeeEditProduct.putExtra("resLoadMoreLibraryAndFavorites",resLoadMoreLibraryAndFavorites);
            gotoSeeEditProduct.putExtra("curentPage","FragmentLibrary");
        }
        else
        {
            if(!MainActivity.CheckNetwork.isShowing())
            {
                MainActivity.CheckNetwork.show();
            }
        }
    }


    Emitter.Listener resSeeProduct=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data= (JSONObject) args[0];
                    ResSeeProductModel resSeeProductModel=new ResSeeProductModel();
                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            resSeeProductModel.productUserImage=data.getString("avatar");
                            resSeeProductModel.productDate=data.getString("created_at");
                            resSeeProductModel.productPrice=data.getString("price");
                            resSeeProductModel.productDescription=data.getString("description");
                            resSeeProductModel.productPathFile=data.getString("pathfile");
                            //resSeeProductModel.setProductLike(data.getJSONArray("like"));
                            resSeeProductModel.productHashtag=data.getString("hashtag");
                            resSeeProductModel.countLike=data.getString("countLike");
                            resSeeProductModel.flagLike=data.getBoolean("flagLike");

                            gotoSeeEditProduct.putExtra("resSeeProductModel",resSeeProductModel);
                            getContext().startActivity(gotoSeeEditProduct);
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
