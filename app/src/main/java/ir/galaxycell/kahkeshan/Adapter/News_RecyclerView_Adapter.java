package ir.galaxycell.kahkeshan.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.galaxycell.kahkeshan.InterFace.NewsViewHolderListener;
import ir.galaxycell.kahkeshan.Models.ResNewsModel;
import ir.galaxycell.kahkeshan.R;

/**
 * Created by Admin on 05/11/2017.
 */
public class News_RecyclerView_Adapter extends RecyclerView.Adapter<News_RecyclerView_Adapter.News_RecyclerView_ViewHolder>
{
    private Context context;
    private List<ResNewsModel>list;
    private View view;
    private NewsViewHolderListener newsViewHolderListener;

    public News_RecyclerView_Adapter(Context context, List<ResNewsModel>list)
    {
        this.context=context;
        this.list=list;
    }

    @Override
    public News_RecyclerView_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.news_recyclerview_layout,parent,false);

        return new News_RecyclerView_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(News_RecyclerView_ViewHolder holder, int position)
    {
        holder.subject.setText(list.get(position).getSubject());
        String [] subStringDate=list.get(position).created_at.split("T");
        holder.date.setText(subStringDate[0]);
        holder.descriptions.setText(list.get(position).getDescription());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

        if(position==list.size()-1)
        {
            newsViewHolderListener=(NewsViewHolderListener)(Activity)context;
            newsViewHolderListener.seeLastPositionNews();
        }
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public class News_RecyclerView_ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView subject,date,descriptions;
        public ImageView more;

        public News_RecyclerView_ViewHolder(View itemView)
        {
            super(itemView);

            subject=(TextView)itemView.findViewById(R.id.news_recyclerview_news_textview_subject);
            date=(TextView)itemView.findViewById(R.id.news_recyclerview_news_textview_date);
            descriptions=(TextView)itemView.findViewById(R.id.news_recyclerview_news_textview_description);
            more=(ImageView)itemView.findViewById(R.id.news_recyclerview_news_imageview_more);
        }
    }
}
