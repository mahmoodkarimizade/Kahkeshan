package ir.galaxycell.kahkeshan.Network;

import ir.galaxycell.kahkeshan.Models.ReqRegisterModel;
import ir.galaxycell.kahkeshan.Models.ReqSignInModel;
import ir.galaxycell.kahkeshan.Models.ReqVerifiedModel;
import ir.galaxycell.kahkeshan.Models.ResRegisterModel;
import ir.galaxycell.kahkeshan.Models.ResSignInModel;
import ir.galaxycell.kahkeshan.Models.ResVerifiedModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Admin on 11/08/2017.
 */
public interface KahkeshanService {


    @POST("signup")
    Call<ResRegisterModel> Signup(@Body ReqRegisterModel reqRegisterModel);

    @POST("verifiedNewUser")
    Call<ResVerifiedModel> VerifiedNewUser(@Body ReqVerifiedModel reqVerifiedModel);

    @POST("signin")
    Call<ResSignInModel> Signin(@Body ReqSignInModel reqSignIn);
}
