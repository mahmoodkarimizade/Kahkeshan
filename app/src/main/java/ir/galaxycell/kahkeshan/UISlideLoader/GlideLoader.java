package ir.galaxycell.kahkeshan.UISlideLoader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import cn.lightsky.infiniteindicator.ImageLoader;
import ir.galaxycell.kahkeshan.R;

/**
 * Created by Admin on 12/09/2017.
 */
public class GlideLoader implements ImageLoader {

    public void initLoader(Context context) {

    }

    @Override
    public void load(Context context, ImageView targetView, Object res) {

        if (res instanceof String){
            targetView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(context)
                    .load((String) res)
                    .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                    .apply(RequestOptions.errorOf(R.drawable.cancel))
                    .into(targetView);
        }
    }
}
