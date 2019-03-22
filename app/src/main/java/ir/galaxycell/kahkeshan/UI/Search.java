package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import ir.galaxycell.kahkeshan.InterFace.SearchLibraryViewHolderListener;
import ir.galaxycell.kahkeshan.Models.ResOtherLoadMoreLibrary;
import ir.galaxycell.kahkeshan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 28/09/2017.
 */
public class Search extends AppCompatActivity implements SearchLibraryViewHolderListener
{

    private GridViewWithHeaderAndFooter gridView;
    private Fragment_Library_GridView_Adapter adapter;
    private List<ResOtherLoadMoreLibrary> listSearch=new ArrayList<ResOtherLoadMoreLibrary>();
    private List<String>listId=new ArrayList<String>();
    private Dialog Loading,CheckNetwork;
    private LinearLayout cnMainLayout;
    private SuperSwipeRefreshLayout superSwipeRefreshLayout;
    private SearchView searchView;
    private ImageView search;
    private String lastContentSearch="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        //linke widget in fragment home to layout
        link();

        //socket listener
        MainActivity.mSocket.on("resSearch",resSearch);
        MainActivity.mSocket.on("resLoadMoreSearch",resLoadMoreSearch);
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
        MainActivity.mSocket.off("resSearch",resSearch);
        MainActivity.mSocket.off("resLoadMoreSearch",resLoadMoreSearch);
        Loading.dismiss();
        finish();
    }

    private void link()
    {
        //Grid View
        gridView=(GridViewWithHeaderAndFooter)findViewById(R.id.search_gridview);
        LayoutInflater layoutInflater = LayoutInflater.from(Search.this);
        View footerView = layoutInflater.inflate(R.layout.fragment_profile_gridview_footer_layout, null);
        gridView.addFooterView(footerView);

        //SuperS wipe Refresh Layout
        superSwipeRefreshLayout=(SuperSwipeRefreshLayout)findViewById(R.id.search_swipe_refresh);
        View header = LayoutInflater.from(superSwipeRefreshLayout.getContext()).inflate(R.layout.swipe_refresh_header_layout, null);
        superSwipeRefreshLayout.setHeaderView(header);

        //Search View
        searchView=(SearchView)findViewById(R.id.search_searchview_search);

        //Image View
        search=(ImageView)findViewById(R.id.search_toolbar_imageview_search);

        //create Lodin dialog
        Loading =new Dialog(Search.this);
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
        CheckNetwork =new Dialog(Search.this);
        CheckNetwork.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CheckNetwork.setContentView(R.layout.check_network_connect_dialog_layout);
        CheckNetwork.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CheckNetwork.getWindow().setBackgroundDrawableResource(R.drawable.boder_loding_layout);

        //define widget
        cnMainLayout=(LinearLayout)CheckNetwork.findViewById(R.id.check_network_connect_dialog_layout_linerlayout_mainlayout);

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) Search.this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        cnMainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;
        //
        //////////CheckNetwork
        //

        //set on click listener
        onClick();
    }

    private void onClick()
    {
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(searchView.getQuery().toString().trim().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"محتوای جست و جو نمی تواند خالی باشد",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(MainActivity.mSocket.connected())
                    {
                        if(!lastContentSearch.equals(searchView.getQuery().toString()))
                        {
                            JSONObject object=new JSONObject();

                            try
                            {
                                listId.clear();
                                listSearch.clear();

                                object.put("searchContent",searchView.getQuery().toString());
                                MainActivity.mSocket.emit("search",object);
                                lastContentSearch=searchView.getQuery().toString();
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

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState)
            {

                if (scrollState == SCROLL_STATE_IDLE)
                {
                    if (absListView.getLastVisiblePosition() >= adapter.getCount() - 1)
                    {
                        if(listSearch.size()<listId.size())
                        {
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

    private void loadMore()
    {
        if(MainActivity.mSocket.connected())
        {

            if (!Search.this.isFinishing())
            {
                Loading.show();
            }


            JSONObject jsonObject=new JSONObject();
            JSONArray jsonArray=new JSONArray();


            try
            {
                if (listId.size()<=30)
                {
                    List<String>subList=listId.subList(0,listId.size());
                    for(int i=0;i<subList.size();i++)
                    {
                        jsonArray.put(subList.get(i));
                    }

                    jsonObject.put("productId",jsonArray);
                    MainActivity.mSocket.emit("loadMoreSearch",jsonObject);

                }
                else
                {
                    if(listSearch.size()+29>listId.size())
                    {
                        List<String>subList=listId.subList(listSearch.size(),listId.size());
                        for(int i=0;i<subList.size();i++)
                        {
                            jsonArray.put(subList.get(i));
                        }

                        jsonObject.put("productId",jsonArray);
                        MainActivity.mSocket.emit("loadMoreSearch",jsonObject);
                    }
                    else
                    {
                        List<String>subList=listId.subList(listSearch.size(),listSearch.size()+30);
                        for(int i=0;i<subList.size();i++)
                        {
                            jsonArray.put(subList.get(i));
                        }

                        jsonObject.put("productId",jsonArray);
                        MainActivity.mSocket.emit("loadMoreSearch",jsonObject);
                    }

                }

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

    private Emitter.Listener resLoadMoreSearch =new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable() {
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

                            for(int i=0;i<jsonArray.length();i++)
                            {
                                ResOtherLoadMoreLibrary resOtherLoadMoreLibrary=new ResOtherLoadMoreLibrary();
                                resOtherLoadMoreLibrary.productId=jsonArray.getJSONObject(i).getString("_id");
                                resOtherLoadMoreLibrary.userId=jsonArray.getJSONObject(i).getString("userid");
                                resOtherLoadMoreLibrary.productImage=jsonArray.getJSONObject(i).getString("image");
                                resOtherLoadMoreLibrary.productBlock=jsonArray.getJSONObject(i).getString("block");

                                listSearch.add(resOtherLoadMoreLibrary);
                            }

                            if(listSearch.size()<=30)
                            {
                                adapter=new Fragment_Library_GridView_Adapter(Search.this,listSearch);
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

    private Emitter.Listener resSearch=new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    JSONObject jsonObject=(JSONObject) args[0];

                    try
                    {
                        if(jsonObject.getBoolean("status"))
                        {
                            for(int i=0;i<jsonObject.getJSONArray("resProductsId").length();i++)
                            {
                                listId.add(jsonObject.getJSONArray("resProductsId").getJSONObject(i).getString("_id"));
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




    @Override
    public void seeLastPosition(int i)
    {

    }
}
