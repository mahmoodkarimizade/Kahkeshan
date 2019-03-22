package ir.galaxycell.kahkeshan.CustomFontApplication;

import android.app.Application;

import ir.galaxycell.kahkeshan.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Admin on 08/08/2017.
 */
public class CustomFontApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initalize Calligraphy
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/IRANSansMobileNormal.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}
