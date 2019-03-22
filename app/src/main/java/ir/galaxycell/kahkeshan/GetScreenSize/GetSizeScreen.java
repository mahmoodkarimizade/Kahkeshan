package ir.galaxycell.kahkeshan.GetScreenSize;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Admin on 11/12/2016.
 */
public class GetSizeScreen {

    public static int dpi,height,width;

    public GetSizeScreen(Context context)
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        dpi=displaymetrics.densityDpi;
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
    }
}
