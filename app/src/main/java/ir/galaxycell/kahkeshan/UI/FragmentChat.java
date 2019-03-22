package ir.galaxycell.kahkeshan.UI;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ir.galaxycell.kahkeshan.Adapter.Fragment_Chat_RecyclerView_Adapter;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreOrder;
import ir.galaxycell.kahkeshan.R;

/**
 * Created by Admin on 11/08/2017.
 */
public class FragmentChat extends Fragment {

    private View view;
    private List<ResLoadMoreOrder>orderList=new ArrayList<>();
    private int countOrderList=0;
    private RecyclerView recyclerView;
    private Fragment_Chat_RecyclerView_Adapter adapter;
    private Dialog Loading;
    private boolean flagLoadMore=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_chat,container,false);

        //linke widget in fragment home to layout
        link(view);

        //socket listener
        MainActivity.mSocket.on("resOrderInfo",resOrderInfo);
        MainActivity.mSocket.on("resLoadMoreOrder",resLoadMoreOrder);

        return view;
    }

    private void link(View view)
    {

        //Recycler View
        recyclerView=(RecyclerView)view.findViewById(R.id.fragment_chat_recyclerview_chat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        //layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

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
    }

    public void resetOrderList()
    {
        orderList.clear();
    }

    Emitter.Listener resOrderInfo=new Emitter.Listener() {
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
                            countOrderList=Integer.parseInt(data.getString("orderlength"));

                            if(MainActivity.mSocket.connected())
                            {
                                loadMoreOrder();
                            }
                            else
                            {
                                if(!MainActivity.CheckNetwork.isShowing())
                                {
                                    MainActivity.CheckNetwork.show();
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

    private void loadMoreOrder()
    {

        if((orderList.size()/2)==0 && countOrderList!=0)
        {
            //show loadin dialog
            Loading.show();

            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("userid",MainActivity.userData.getUserid());
                jsonObject.put("end",(countOrderList-1)-(orderList.size()/2));
                if(((countOrderList-1)-(orderList.size()/2))-14>0)
                {
                    jsonObject.put("start",((countOrderList-1)-(orderList.size()/2))-14);
                }
                else
                {
                    jsonObject.put("start",0);
                }

                MainActivity.mSocket.emit("loadMoreOrder",jsonObject);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if(flagLoadMore && (orderList.size()/2)<countOrderList)
        {
            flagLoadMore=false;
            //show loadin dialog
            Loading.show();

            JSONObject jsonObject=new JSONObject();
            try
            {
                jsonObject.put("userid",MainActivity.userData.getUserid());
                jsonObject.put("end",(countOrderList-1)-(orderList.size()/2));
                if(((countOrderList-1)-(orderList.size()/2))-14>0)
                {
                    jsonObject.put("start",((countOrderList-1)-(orderList.size()/2))-14);
                }
                else
                {
                    jsonObject.put("start",0);
                }

                MainActivity.mSocket.emit("loadMoreOrder",jsonObject);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    Emitter.Listener resLoadMoreOrder=new Emitter.Listener()
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
                            JSONArray jsonArray=data.getJSONArray("resOrder");
                            for(int i=jsonArray.length()-1;i>=0;i--)
                            {
                                ResLoadMoreOrder resLoadMoreOrder=new ResLoadMoreOrder();
                                resLoadMoreOrder.orderid=jsonArray.getJSONObject(i).getString("_id");
                                resLoadMoreOrder.userid=jsonArray.getJSONObject(i).getString("userid");
                                resLoadMoreOrder.username=jsonArray.getJSONObject(i).getString("username");
                                resLoadMoreOrder.typeOrder=jsonArray.getJSONObject(i).getString("typeorder");
                                resLoadMoreOrder.description=jsonArray.getJSONObject(i).getString("description");
                                resLoadMoreOrder.created_at=jsonArray.getJSONObject(i).getString("created_at");
                                resLoadMoreOrder.avatar=jsonArray.getJSONObject(i).getString("avatar");

                                ResLoadMoreOrder resLoadMoreOrder1=new ResLoadMoreOrder();
                                resLoadMoreOrder1.userid="5a682b6ee5bdd70edc14f181";
                                resLoadMoreOrder1.username="Operator";
                                resLoadMoreOrder1.typeOrder="توضیحات";
                                resLoadMoreOrder1.description="با سپاس از سفارش شما. همکاران ما در اسرع وقت برای انجام سفارش با شما از طریق تلگرام یا تلفن همراه تماس بر قرار می کنند. با سپاس از شکیبایی شما تیم پشتیبانی کهکشان";
                                resLoadMoreOrder1.created_at=jsonArray.getJSONObject(i).getString("created_at");
                                resLoadMoreOrder1.avatar="Data/Users/defultImageProfile/kahkeshan.jpg";

                                orderList.add(resLoadMoreOrder1);
                                orderList.add(resLoadMoreOrder);



                            }

                            if((orderList.size()/2)<=15)
                            {
                                adapter=new Fragment_Chat_RecyclerView_Adapter(getContext(),orderList);
                                recyclerView.setAdapter(adapter);
                            }
                            else
                            {
                                //adapter.notifyItemInserted(orderList.size());
                                adapter.notifyDataSetChanged();
                            }

                            Loading.dismiss();
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

    public void startLoadMore()
    {

        if(MainActivity.mSocket.connected())
        {
            flagLoadMore=true;
            loadMoreOrder();
        }
        else
        {
            if(!MainActivity.CheckNetwork.isShowing())
            {
                MainActivity.CheckNetwork.show();
            }
        }
    }
}
