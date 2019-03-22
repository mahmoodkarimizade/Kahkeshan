package ir.galaxycell.kahkeshan.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.galaxycell.kahkeshan.GetScreenSize.GetSizeScreen;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreFollowerAndFollowing;
import ir.galaxycell.kahkeshan.Models.ResLoadMoreLibraryAndFavorites;
import ir.galaxycell.kahkeshan.Models.ResSeeProductModel;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.UI.FragmentProfile;
import ir.galaxycell.kahkeshan.UI.MainActivity;
import ir.galaxycell.kahkeshan.UI.SeeEditProduct;
import ir.galaxycell.kahkeshan.UI.SeeEditProductFragmentDescription;
import ir.galaxycell.kahkeshan.UI.SeeEditProductFragmentFile;
import ir.galaxycell.kahkeshan.UI.SeeProfile;
import ir.galaxycell.kahkeshan.Utility.Utility;

/**
 * Created by Admin on 13/08/2017.
 */
public class Fragment_Profile_GridView_Adapter extends BaseAdapter
{
    private List<ResLoadMoreLibraryAndFavorites> list;
    private List<ResLoadMoreFollowerAndFollowing>list1;
    private Context context;
    private ImageView image;
    private String curentPage;
    private CircleImageView imageprofile;
    private TextView username;

    public Fragment_Profile_GridView_Adapter(Context context, List<ResLoadMoreLibraryAndFavorites>list, List<ResLoadMoreFollowerAndFollowing>list1, String curentPage)
    {
        this.context=context;
        this.list=list;
        this.list1=list1;
        this.curentPage=curentPage;
    }

    @Override
    public int getCount()
    {
        if(curentPage.equals("library")||curentPage.equals("favorites"))
        {
            return list.size();
        }
        else
        {
            return list1.size();
        }

    }

    @Override
    public Object getItem(int i)
    {
        if(curentPage.equals("library")||curentPage.equals("favorites"))
        {
            return list.get(i);
        }
        else
        {
            return list1.get(i);
        }

    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        if(curentPage.equals("library")||curentPage.equals("favorites"))
        {
            view=inflater.inflate(R.layout.fragment_profile_gridview_libraryandfavoriteslayout,viewGroup,false);
        }
        else
        {
            view=inflater.inflate(R.layout.fragment_profile_gridview_followinandfollowerlayout,viewGroup,false);
        }


        //linke widget in grid view profile to layout
        link(view,i);

        return view;
    }

    private void link(View view, final int i)
    {
        if(curentPage.equals("library")||curentPage.equals("favorites"))
        {
            image=(ImageView)view.findViewById(R.id.fragment_profile_gridview_libraryandfavoriteslayout_imageview_image);

            //set size
            setSize();

            Glide.with(context)
                    .load(Utility.BaseUrl+list.get(i).getProductImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                    .apply(RequestOptions.errorOf(R.drawable.cancel))
                    .into(image);


            //on click listener
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    //
                    FragmentProfile fragmentProfile=(FragmentProfile) ((FragmentActivity)MainActivity.context).getSupportFragmentManager().findFragmentById(R.id.main_framlayout_fragmentitemselected5);
                    fragmentProfile.sendDataToSeeEditProduct(i);


                }
            });
        }
        else
        {
            imageprofile=(CircleImageView) view.findViewById(R.id.fragment_profile_gridview_followinandfollowerlayout_imageview_imageprofile);
            username=(TextView)view.findViewById(R.id.fragment_profile_gridview_followinandfollowerlayout_textview_username);

            Glide.with(context)
                    .load(Utility.BaseUrl+list1.get(i).getAvatar())
                    .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                    .apply(RequestOptions.errorOf(R.drawable.cancel))
                    .into(imageprofile);

            username.setText(list1.get(i).getUsername());

            imageprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {

                    if(MainActivity.mSocket.connected())
                    {
                        Intent gotoSeeProfile=new Intent(context, SeeProfile.class);
                        gotoSeeProfile.putExtra("UserId",list1.get(i).getUserId());
                        gotoSeeProfile.putExtra("curentPage", MainActivity.curentPage);
                        context.startActivity(gotoSeeProfile);
                    }
                    else
                    {
                        if(!MainActivity.CheckNetwork.isShowing())
                        {
                            MainActivity.CheckNetwork.show();
                        }
                    }
                }
            });


        }
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
