package ir.galaxycell.kahkeshan.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.galaxycell.kahkeshan.InterFace.FragmentChatViewHolderListener;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreOrder;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.UI.MainActivity;
import ir.galaxycell.kahkeshan.Utility.Utility;

/**
 * Created by Admin on 13/09/2017.
 */
public class Fragment_Chat_RecyclerView_Adapter extends RecyclerView.Adapter<Fragment_Chat_RecyclerView_Adapter.Fragment_Chat_RecyclerView_ViewHolder> {

    private Context context;
    private List<ResLoadMoreOrder>list;
    private View view;
    private FragmentChatViewHolderListener fragmentChatViewHolderListener;

    public Fragment_Chat_RecyclerView_Adapter(Context context,List<ResLoadMoreOrder>list)
    {
        this.context=context;
        this.list=list;
    }

    @Override
    public Fragment_Chat_RecyclerView_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        if(viewType==0)
        {
            LayoutInflater inflater=LayoutInflater.from(context);
            view=inflater.inflate(R.layout.fragment_chat_recyclerview_mechat_layout,parent,false);
        }
        else
        {
            LayoutInflater inflater=LayoutInflater.from(context);
            view=inflater.inflate(R.layout.fragment_chat_recyclerview_otherschat_layout,parent,false);
        }
        return new Fragment_Chat_RecyclerView_ViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(Fragment_Chat_RecyclerView_ViewHolder holder, int position)
    {

        holder.username.setText(list.get(position).username);
        String [] subStringDate=list.get(position).created_at.split("T");
        holder.date.setText(subStringDate[0]);
        holder.description.setText(list.get(position).getTypeOrder()+" : \n"+list.get(position).description);

        Glide.with(context)
                .load(Utility.BaseUrl+list.get(position).avatar)
                .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                .apply(RequestOptions.errorOf(R.drawable.cancel))
                .into(holder.imageProfile);

        /*if(getItemViewType(position)==0)
        {
            holder.username.setText(list.get(position).userid);
            String [] subStringDate=list.get(position).created_at.split("T");
            holder.date.setText(subStringDate[0]);
            holder.description.setText(list.get(position).getTypeOrder()+" : \n"+list.get(position).description);

            Glide.with(context)
                    .load(Utility.BaseUrl+list.get(position).avatar)
                    .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                    .apply(RequestOptions.errorOf(R.drawable.cancel))
                    .into(holder.imageProfile);
        }
        else
        {
            holder.username.setText(list.get(position).userid);
            String [] subStringDate=list.get(position).created_at.split("T");
            holder.date.setText(subStringDate[0]);
            holder.description.setText(list.get(position).description);

            Glide.with(context)
                    .load(Utility.BaseUrl+list.get(position).avatar)
                    .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                    .apply(RequestOptions.errorOf(R.drawable.cancel))
                    .into(holder.imageProfile);
        }*/

        if(position==list.size()-1)
        {
            fragmentChatViewHolderListener=(FragmentChatViewHolderListener)(Activity)context;
            fragmentChatViewHolderListener.seeZeroPosition();
        }

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if(list.get(position).getUserid().equals(MainActivity.userData.getUserid()))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    public class Fragment_Chat_RecyclerView_ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username,date,description;
        public CircleImageView imageProfile;

        public Fragment_Chat_RecyclerView_ViewHolder(View itemView,int viewType)
        {
            super(itemView);

            if(viewType==0)
            {
                username=(TextView)itemView.findViewById(R.id.fragment_chat_recyclerview_mechat_layout_textview_username);
                date=(TextView)itemView.findViewById(R.id.fragment_chat_recyclerview_mechat_layout_textview_date);
                description=(TextView)itemView.findViewById(R.id.fragment_chat_recyclerview_mechat_layout_textview_message);
                imageProfile=(CircleImageView)itemView.findViewById(R.id.fragment_chat_recyclerview_mechat_layout_imageview_imageprofile);
            }
            else
            {
                username=(TextView)itemView.findViewById(R.id.fragment_chat_recyclerview_otherschat_layout_textview_username);
                date=(TextView)itemView.findViewById(R.id.fragment_chat_recyclerview_otherschat_layout_textview_date);
                description=(TextView)itemView.findViewById(R.id.fragment_chat_recyclerview_otherschat_layout_textview_message);
                imageProfile=(CircleImageView)itemView.findViewById(R.id.fragment_chat_recyclerview_otherschat_layout_imageview_imageprofile);
            }
        }
    }
}
