package ir.galaxycell.kahkeshan.UI;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import ir.galaxycell.kahkeshan.Adapter.Fragment_Library_GridView_Adapter;
import ir.galaxycell.kahkeshan.Adapter.See_Profile_GridView_Adapter;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreLibraryAndFavorites;
import ir.galaxycell.kahkeshan.Models.ResOtherLoadMoreLibrary;
import ir.galaxycell.kahkeshan.R;

/**
 * Created by Admin on 11/08/2017.
 */
public class FragmentLibrary extends Fragment {

    private View view;
    private int countLibraryAll=0,countLibraryHistory=0;
    private GridViewWithHeaderAndFooter gridView;
    private Fragment_Library_GridView_Adapter adapter;
    private List<ResOtherLoadMoreLibrary> listLibrary=new ArrayList<ResOtherLoadMoreLibrary>();
    private List<ResOtherLoadMoreLibrary> listLibraryAll=new ArrayList<ResOtherLoadMoreLibrary>();
    private List<ResOtherLoadMoreLibrary> listLibraryHistory=new ArrayList<ResOtherLoadMoreLibrary>();
    private Dialog Loading;
    private SuperSwipeRefreshLayout superSwipeRefreshLayout;
    private boolean flagLoadMore=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_library,container,false);

        //linke widget in fragment home to layout
        link(view);

        //socket listener
        MainActivity.mSocket.on("resLibraryInfo",resLibraryInfo);
        MainActivity.mSocket.on("resloadMoreFragmentLibrary",resloadMoreFragmentLibrary);

        return view;
    }

    private void link(View view)
    {
        //Grid View
        gridView=(GridViewWithHeaderAndFooter)view.findViewById(R.id.fragment_library_gridview);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View footerView = layoutInflater.inflate(R.layout.fragment_profile_gridview_footer_layout, null);
        gridView.addFooterView(footerView);

        //SuperS wipe Refresh Layout
        superSwipeRefreshLayout=(SuperSwipeRefreshLayout)view.findViewById(R.id.fragment_library_swipe_refresh);
        View header = LayoutInflater.from(superSwipeRefreshLayout.getContext()).inflate(R.layout.swipe_refresh_header_layout, null);
        superSwipeRefreshLayout.setHeaderView(header);

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

    private void onClick()
    {
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState)
            {

                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if (absListView.getLastVisiblePosition() >= adapter.getCount() - 1)
                    {
                        if(listLibrary.size()<countLibraryHistory+countLibraryAll)
                        {
                            flagLoadMore=true;
                            libraryLoadMore();
                        }

                    }

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {

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
    }

    Emitter.Listener resLibraryInfo=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {

                            countLibraryAll=data.getInt("countLibraryAll");
                            //Toast.makeText(getContext(),countLibraryAll+" : from of all",Toast.LENGTH_LONG).show();
                            countLibraryHistory=data.getInt("countLibraryHistory");
                            //Toast.makeText(getContext(),countLibraryHistory+" : from of history",Toast.LENGTH_LONG).show();

                            libraryLoadMore();

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

    private void libraryLoadMore()
    {
        if(MainActivity.mSocket.connected())
        {
            Loading.show();

            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("userid",MainActivity.userData.getUserid());

                if (countLibraryHistory!=0 && listLibraryHistory.size()<countLibraryHistory)
                {
                    jsonObject.put("fromOf","history");
                    jsonObject.put("start",listLibraryHistory.size());
                    if(countLibraryHistory>30)
                    {
                        jsonObject.put("end",listLibraryHistory.size()+29);
                    }
                    else
                    {
                        jsonObject.put("end",countLibraryHistory-1);
                    }

                    MainActivity.mSocket.emit("loadMoreFragmentLibrary",jsonObject);
                }
                else if(countLibraryAll!=0 && listLibraryAll.size()<countLibraryAll)
                {
                    jsonObject.put("fromOf","all");
                    jsonObject.put("start",listLibraryAll.size());
                    if(countLibraryAll>30)
                    {
                        jsonObject.put("end",listLibraryAll.size()+29);
                    }
                    else
                    {
                        jsonObject.put("end",countLibraryAll-1);
                    }

                    MainActivity.mSocket.emit("loadMoreFragmentLibrary",jsonObject);
                }

            } catch (JSONException e)
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

    Emitter.Listener resloadMoreFragmentLibrary=new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject) args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            Loading.dismiss();

                            JSONArray jsonArray=data.getJSONArray("resProducts");
                            if(data.getString("fromOf").equals("history"))
                            {
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    ResOtherLoadMoreLibrary resOtherLoadMoreLibrary=new ResOtherLoadMoreLibrary();
                                    resOtherLoadMoreLibrary.productId=jsonArray.getJSONObject(i).getString("_id");
                                    resOtherLoadMoreLibrary.userId=jsonArray.getJSONObject(i).getString("userid");
                                    resOtherLoadMoreLibrary.productImage=jsonArray.getJSONObject(i).getString("image");
                                    resOtherLoadMoreLibrary.productBlock=jsonArray.getJSONObject(i).getString("block");

                                    listLibraryHistory.add(resOtherLoadMoreLibrary);
                                }

                                listLibrary.addAll(listLibraryHistory);

                            }
                            else
                            {
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    ResOtherLoadMoreLibrary resOtherLoadMoreLibrary=new ResOtherLoadMoreLibrary();
                                    resOtherLoadMoreLibrary.productId=jsonArray.getJSONObject(i).getString("_id");
                                    resOtherLoadMoreLibrary.userId=jsonArray.getJSONObject(i).getString("userid");
                                    resOtherLoadMoreLibrary.productImage=jsonArray.getJSONObject(i).getString("image");
                                    resOtherLoadMoreLibrary.productBlock=jsonArray.getJSONObject(i).getString("block");

                                    listLibraryAll.add(resOtherLoadMoreLibrary);
                                }

                                listLibrary.addAll(listLibraryAll);
                            }

                            if(listLibrary.size()<=30)
                            {
                                adapter=new Fragment_Library_GridView_Adapter(getContext(),listLibrary);
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

    public void sendDataToSeeEditProduct(ResOtherLoadMoreLibrary resOtherLoadMoreLibrary)
    {
        ResLoadMoreLibraryAndFavorites resLoadMoreLibraryAndFavorites=new ResLoadMoreLibraryAndFavorites();
        resLoadMoreLibraryAndFavorites.productId=resOtherLoadMoreLibrary.productId;
        resLoadMoreLibraryAndFavorites.userId=resOtherLoadMoreLibrary.userId;
        resLoadMoreLibraryAndFavorites.productImage=resOtherLoadMoreLibrary.productImage;
        resLoadMoreLibraryAndFavorites.productBlock=resOtherLoadMoreLibrary.productBlock;

        FragmentProfile fragmentProfile=(FragmentProfile) ((FragmentActivity)MainActivity.context).getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected5);
        fragmentProfile.sendDataToSeeEditProductFromFragmentLibrary(resLoadMoreLibraryAndFavorites);
    }

    public void resetListLibrary()
    {
        listLibrary.clear();
        listLibraryAll.clear();
        listLibraryHistory.clear();

    }
}
