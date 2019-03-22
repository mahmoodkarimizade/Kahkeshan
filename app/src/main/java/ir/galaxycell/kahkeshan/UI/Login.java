package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ir.galaxycell.kahkeshan.Models.ReqSignInModel;
import ir.galaxycell.kahkeshan.Models.ResSignInModel;
import ir.galaxycell.kahkeshan.Network.KahkeshanProvider;
import ir.galaxycell.kahkeshan.Network.KahkeshanService;
import ir.galaxycell.kahkeshan.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 07/08/2017.
 */
public class Login extends AppCompatActivity {

    private Dialog loginDialog;
    private EditText username,password;
    private LinearLayout mainLayout;
    private Button login,register;
    private TextView forgetPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        loginDialog=new Dialog(Login.this,R.style.PauseDialog);
        loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loginDialog.setContentView(R.layout.login_dialog_layout);
        loginDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        loginDialog.getWindow().setBackgroundDrawableResource(R.drawable.border_login_dialog_layout);
        loginDialog.setCanceledOnTouchOutside(false);
        loginDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                finish();
            }
        });


        //define widget
        mainLayout=(LinearLayout)loginDialog.findViewById(R.id.login_dialog_layout_linerlayout_mainlayout);
        username=(EditText)loginDialog.findViewById(R.id.login_dialog_layout_edittext_username);
        password=(EditText)loginDialog.findViewById(R.id.login_dialog_layout_edittext_password);
        login=(Button)loginDialog.findViewById(R.id.login_dialog_layout_button_login);
        register=(Button)loginDialog.findViewById(R.id.login_dialog_layout_button_register);
        forgetPassword=(TextView)loginDialog.findViewById(R.id.login_dialog_layout_textview_forgetpassword);


        //set input type
        password.setTransformationMethod(new PasswordTransformationMethod());

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;

        //on click listenr
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(username.getText().toString().trim().isEmpty())
                {
                    username.setError("نام کاربری نمی تواند خالی باشد");
                }
                else if(password.getText().toString().trim().isEmpty())
                {
                    password.setError("کلمه عبور نمی تواند خالی باشد");
                }
                else if(username.getText().length()<6)
                {
                    username.setError("نام کاربری باید بیشتر از 6 حرف باشد");
                }
                else if(password.getText().length()<6)
                {
                    password.setError("کلمه عبور باید بیشتر از 6 حرف باشد");
                }
                else
                {
                    KahkeshanProvider SigninProvider=new KahkeshanProvider();
                    KahkeshanService SigninService=SigninProvider.Result();

                    final ReqSignInModel reqSignInModel=new ReqSignInModel();
                    reqSignInModel.setUsername(username.getText().toString().trim());
                    reqSignInModel.setPassword(password.getText().toString().trim());

                    Call<ResSignInModel>call=SigninService.Signin(reqSignInModel);
                    call.enqueue(new Callback<ResSignInModel>() {
                        @Override
                        public void onResponse(Call<ResSignInModel> call, Response<ResSignInModel> response)
                        {
                            ResSignInModel resSignInModel=response.body();

                            if(resSignInModel.loginstatus.equals("true"))
                            {
                                Toast.makeText(getApplicationContext(),resSignInModel.description,Toast.LENGTH_LONG).show();
                                MainActivity.userData.setStatusUser("true");
                                MainActivity.userData.setUserid(resSignInModel.userid);
                                MainActivity.userData.setPassword(resSignInModel.password);
                                MainActivity.userData.setPhone(resSignInModel.phone);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),resSignInModel.description,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResSignInModel> call, Throwable t)
                        {
                            Toast.makeText(getApplicationContext(),"response dochare moshkel shod"+t.getCause(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Login.this,Register1.class));
                finish();
            }
        });

        loginDialog.show();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginDialog.dismiss();
    }
}
