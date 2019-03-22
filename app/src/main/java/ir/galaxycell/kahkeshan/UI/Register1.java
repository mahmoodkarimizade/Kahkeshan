package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import ir.galaxycell.kahkeshan.Models.ReqRegisterModel;
import ir.galaxycell.kahkeshan.Models.ResRegisterModel;
import ir.galaxycell.kahkeshan.Network.KahkeshanProvider;
import ir.galaxycell.kahkeshan.Network.KahkeshanService;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Utility.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 09/08/2017.
 */
public class Register1 extends AppCompatActivity
{

    private Dialog registerDialog;
    private EditText username,phone,password,rePassword;
    private LinearLayout mainLayout;
    private Button login,next;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);

        registerDialog=new Dialog(Register1.this,R.style.PauseDialog);
        registerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        registerDialog.setContentView(R.layout.register1_dialog_layout);
        registerDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        registerDialog.getWindow().setBackgroundDrawableResource(R.drawable.border_login_dialog_layout);
        registerDialog.setCanceledOnTouchOutside(false);
        registerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface)
            {
                finish();
            }
        });


        //define widget
        mainLayout=(LinearLayout)registerDialog.findViewById(R.id.register1_dialog_layout_linerlayout_mainlayout);
        username=(EditText)registerDialog.findViewById(R.id.register1_dialog_layout_edittext_username);
        phone=(EditText)registerDialog.findViewById(R.id.register1_dialog_layout_edittext_phonenumber);
        password=(EditText)registerDialog.findViewById(R.id.register1_dialog_layout_edittext_password);
        rePassword=(EditText)registerDialog.findViewById(R.id.register1_dialog_layout_edittext_repassword);
        login=(Button)registerDialog.findViewById(R.id.register1_dialog_layout_button_login);
        next=(Button)registerDialog.findViewById(R.id.register1_dialog_layout_button_next);


        //set input type
        password.setTransformationMethod(new PasswordTransformationMethod());
        rePassword.setTransformationMethod(new PasswordTransformationMethod());

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;

        //set item if come back from register2
        username.setText(Utility.username);
        phone.setText(Utility.phone);
        password.setText(Utility.password);
        rePassword.setText(Utility.password);

        //on click listenr
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(username.getText().toString().trim().isEmpty())
                {
                    username.setError("نام کاربری نمی تواند خالی باشد");
                }
                else if (!isValidPhoneNumber(phone.getText().toString().trim()))
                {
                    phone.setError("شماره موبایل معتبر نیست");
                }
                else if(password.getText().toString().trim().isEmpty())
                {
                    password.setError("کلمه عبور نمی تواند خالی باشد");
                }
                else if(!password.getText().toString().trim().equals(rePassword.getText().toString().trim()))
                {
                    password.setError("کلمه عبور و تکرار کلمه عبور برابر نیستند ");
                    rePassword.setError("کلمه عبور و تکرار کلمه عبور برابر نیستند ");
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

                    Utility.username=username.getText().toString();
                    Utility.phone=phone.getText().toString();
                    Utility.password=password.getText().toString();

                    KahkeshanProvider SignupProvider=new KahkeshanProvider();
                    KahkeshanService SignupService =SignupProvider.Result();
                    ReqRegisterModel reqRegisterModel=new ReqRegisterModel();

                    reqRegisterModel.setUsername(username.getText().toString());
                    reqRegisterModel.setPhone(phone.getText().toString());
                    reqRegisterModel.setPassword(password.getText().toString());


                    Call<ResRegisterModel> call=SignupService.Signup(reqRegisterModel);

                    call.enqueue(new Callback<ResRegisterModel>() {
                        @Override
                        public void onResponse(Call<ResRegisterModel> call, Response<ResRegisterModel> response) {

                            if(response.isSuccess())
                            {
                                ResRegisterModel  resRegisterModel=response.body();
                                Toast.makeText(getApplicationContext(),"res: "+resRegisterModel.verifiedcode,Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Register1.this,Register2.class));
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),response.code()+"",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResRegisterModel> call, Throwable t)
                        {
                            Toast.makeText(getApplicationContext(),"response dochare moshkel shod"+t.getCause(),Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Register1.this,Login.class));
                finish();
            }
        });

        registerDialog.show();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber))
        {
            if(phoneNumber.length()<11 || phoneNumber.length()>11)
            {
                return false;
            }
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerDialog.dismiss();
    }
}
