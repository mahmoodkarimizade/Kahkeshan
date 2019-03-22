package ir.galaxycell.kahkeshan.Network;

import ir.galaxycell.kahkeshan.Utility.Utility;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 11/08/2017.
 */
public class KahkeshanProvider {
    private KahkeshanService kahkeshanService;

    public KahkeshanProvider()
    {
        OkHttpClient okHttpClient=new OkHttpClient();

        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Utility.BaseUrl)
                .client(okHttpClient)
                .build();

        kahkeshanService=retrofit.create(KahkeshanService.class);
    }

    public KahkeshanService Result()
    {
        return kahkeshanService;
    }
}
