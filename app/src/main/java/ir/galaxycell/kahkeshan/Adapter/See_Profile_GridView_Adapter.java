package ir.galaxycell.kahkeshan.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ir.galaxycell.kahkeshan.GetScreenSize.GetSizeScreen;
import ir.galaxycell.kahkeshan.InterFace.SeeprofileAdapterItemClickListener;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreLibraryAndFavorites;
import ir.galaxycell.kahkeshan.Models.ResOtherLoadMoreLibrary;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.UI.SeeProfile;
import ir.galaxycell.kahkeshan.Utility.Utility;

/**
 * Created by Admin on 10/09/2017.
 */
public class See_Profile_GridView_Adapter extends BaseAdapter
{
    private Context context;
    private List<ResOtherLoadMoreLibrary>list;
    private ImageView image;
    private SeeprofileAdapterItemClickListener seeprofileAdapterItemClickListener;

    public See_Profile_GridView_Adapter(Context context,List<ResOtherLoadMoreLibrary>list)
    {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int i)
    {
        return list.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.see_profile_gridview_library_layout,viewGroup,false);

        image=(ImageView)view.findViewById(R.id.see_profile_gridview_library_imageview_image);

        //set size
        setSize();

        Glide.with(context)
                .load(Utility.BaseUrl+list.get(i).getProductImage())
                .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                .apply(RequestOptions.errorOf(R.drawable.cancel))
                .into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Activity activity=(Activity)context;
                seeprofileAdapterItemClickListener=(SeeprofileAdapterItemClickListener)activity;
                seeprofileAdapterItemClickListener.OnItemClickListener(i);
            }
        });


        return view;
    }

    private void setSize()
    {
        GetSizeScreen getSizeScreen=new GetSizeScreen(context);

        if(getSizeScreen.height>=480&&getSizeScreen.height<800)
        {

            //image //mdpi in pixel
            image.getLayoutParams().width=112;
            image.getLayoutParams().height=112;;
        }
        else if(getSizeScreen.height>=800&& getSizeScreen.height<1000)
        {
            //image //hdpi in pixel
            image.getLayoutParams().width=168;
            image.getLayoutParams().height=168;
        }
        else if(getSizeScreen.height>=1000&& getSizeScreen.height<1600)
        {

            //image //xhdpi in pixel
            image.getLayoutParams().width=224;
            image.getLayoutParams().height=224;

        }
        else if(getSizeScreen.height>=1600&& getSizeScreen.height<1920)
        {

            //image //xxhdpi in pixel
            image.getLayoutParams().width=336;
            image.getLayoutParams().height=336;
        }
        else
        {
            //image //xxxhdpi in pixel
            image.getLayoutParams().width=448;
            image.getLayoutParams().height=448;
        }
    }
}
