package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import ir.galaxycell.kahkeshan.Adapter.News_RecyclerView_Adapter;
import ir.galaxycell.kahkeshan.InterFace.NewsViewHolderListener;
import ir.galaxycell.kahkeshan.Models.ReqNewsModel;
import ir.galaxycell.kahkeshan.Models.ResNewsModel;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Utility.Utility;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 11/08/2017.
 */
public class FragmentNews extends Fragment
{

    private View view;
    private Button newsButton;
    private EditText contentNews;
    private  News_RecyclerView_Adapter newsRecyclerViewAdapter;
    private RecyclerView news;
    private  List<ResNewsModel> resNewsModelList=new ArrayList<ResNewsModel>();
    private String lastNewsId="";
    private int newsLength;
    private Dialog Loading,CheckNetwork;
    private LinearLayout cnMainLayout;
    private boolean flagLoadMore=false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.news,container,false);

        //linke widget to layout
        link();


        //socket off when click on push notification
        /*if(getIntent().getExtras()!=null)
        {
            MainActivity.mSocket.off("resGetLastNewsId");
            MainActivity.mSocket.off("resLoadMoreNews");
        }*/

        //socket listener
        MainActivity.mSocket.on("resGetLastNewsId",resGetLastNewsId);
        MainActivity.mSocket.on("resLoadMoreNews",resLoadMoreNews);

        return view;
    }



   /* @Override
    public void onBackPressed() {
        super.onBackPressed();

        MainActivity.mSocket.off("resGetLastNewsId",resGetLastNewsId);
        MainActivity.mSocket.off("resLoadMoreNews",resLoadMoreNews);

        Loading.dismiss();

    }*/

    private void link()
    {
        newsButton=(Button)view.findViewById(R.id.news_button_news);

        contentNews=(EditText)view.findViewById(R.id.news_edittext_contentnews);

        //Recycler View
        news=(RecyclerView)view.findViewById(R.id.news_recyclerview_news);
        news.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        //create Lodin dialog
        Loading =new Dialog(getContext());
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
        CheckNetwork =new Dialog(getContext());
        CheckNetwork.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CheckNetwork.setContentView(R.layout.check_network_connect_dialog_layout);
        CheckNetwork.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CheckNetwork.getWindow().setBackgroundDrawableResource(R.drawable.boder_loding_layout);

        //define widget
        cnMainLayout=(LinearLayout)CheckNetwork.findViewById(R.id.check_network_connect_dialog_layout_linerlayout_mainlayout);

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        cnMainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;
        //
        //////////CheckNetwork
        //

        //set on click listener
        onClick();
    }

    private void onClick()
    {
        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                JSONObject jsonObject=new JSONObject();

                try
                {
                    jsonObject.put("userid",MainActivity.userData.getUserid());
                    jsonObject.put("subject","ثبت نام کنکور");
                    jsonObject.put("description",contentNews.getText().toString());
                    jsonObject.put("link","ثبت نام کنکور");

                    MainActivity.mSocket.emit("createNews",jsonObject);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    public Emitter.Listener resGetLastNewsId=new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            //show loadin dialog
                            Loading.show();

                            //check last news id exist?
                            if(data.getString("lastNewsId").equals("0"))
                            {
                                Toast.makeText(getContext(),"FragmentNews dont exist!",Toast.LENGTH_LONG).show();
                                //dismiss loading dialog
                                Loading.dismiss();
                            }
                            else
                            {
                                // call load more
                                JSONObject object=new JSONObject();
                                object.put("newsId",data.getString("lastNewsId"));
                                object.put("status","0");
                                MainActivity.mSocket.emit("loadMoreNews",object);
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

    public  Emitter.Listener resLoadMoreNews=new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            //dismiss loading dialog
                            Loading.dismiss();

                            newsLength=data.getInt("newsLength");
                            JSONArray jsonArrayNews=data.getJSONArray("resNews");

                            //last id
                            lastNewsId=jsonArrayNews.getJSONObject(jsonArrayNews.length()-1).getString("_id");

                            for (int i=0;i<jsonArrayNews.length();i++)
                            {
                                ResNewsModel resNewsModel=new ResNewsModel();
                                resNewsModel.userid=jsonArrayNews.getJSONObject(i).getString("userid");
                                resNewsModel.subject=jsonArrayNews.getJSONObject(i).getString("subject");
                                resNewsModel.description=jsonArrayNews.getJSONObject(i).getString("description");
                                resNewsModel.created_at=jsonArrayNews.getJSONObject(i).getString("created_at");
                                resNewsModel.block=jsonArrayNews.getJSONObject(i).getString("block");

                                resNewsModelList.add(resNewsModel);
                            }

                            if(resNewsModelList.size()<=16)
                            {
                                newsRecyclerViewAdapter=new News_RecyclerView_Adapter(getContext(),resNewsModelList);
                                news.setAdapter(newsRecyclerViewAdapter);
                            }
                            else
                            {
                                newsRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void loadMoreNews()
    {
        if(newsLength!=0)
        {
            //show loading dialog
            Loading.show();

            try
            {
                // call load more
                JSONObject object=new JSONObject();
                object.put("newsId",lastNewsId);
                object.put("status","1");
                MainActivity.mSocket.emit("loadMoreNews",object);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }


    public void seeLastPosition()
    {
        if(MainActivity.mSocket.connected())
        {
            flagLoadMore=true;
            loadMoreNews();
        }
        else
        {
            if(!CheckNetwork.isShowing())
            {
                CheckNetwork.show();
            }
        }
    }

    public void reSetResNewsModelList()
    {
        resNewsModelList.clear();
    }
}
