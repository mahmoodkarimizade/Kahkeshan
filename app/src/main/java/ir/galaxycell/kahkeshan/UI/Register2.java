package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import ir.galaxycell.kahkeshan.Models.ReqRegisterModel;
import ir.galaxycell.kahkeshan.Models.ReqVerifiedModel;
import ir.galaxycell.kahkeshan.Models.ResRegisterModel;
import ir.galaxycell.kahkeshan.Models.ResVerifiedModel;
import ir.galaxycell.kahkeshan.Network.KahkeshanProvider;
import ir.galaxycell.kahkeshan.Network.KahkeshanService;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Utility.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 10/08/2017.
 */
public class Register2 extends AppCompatActivity {

    private Dialog registerDialog;
    private LinearLayout mainLayout,getCodeLayout;
    private TextView getCode,countDownTimer;
    private EditText code;
    private Button back,register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2);


        registerDialog=new Dialog(Register2.this,R.style.PauseDialog);
        registerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        registerDialog.setContentView(R.layout.register2_dialog_layout);
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
        mainLayout=(LinearLayout)registerDialog.findViewById(R.id.register2_dialog_layout_linerlayout_mainlayout);
        getCodeLayout=(LinearLayout)registerDialog.findViewById(R.id.register2_dialog_layout_linerlayout_getcodelayout);
        code=(EditText)registerDialog.findViewById(R.id.register2_dialog_layout_edittext_code);
        getCode=(TextView)registerDialog.findViewById(R.id.register2_dialog_layout_textview_getcode);
        countDownTimer=(TextView)registerDialog.findViewById(R.id.register2_dialog_layout_textview_countdowntimer);
        back=(Button)registerDialog.findViewById(R.id.register2_dialog_layout_button_back);
        register=(Button)registerDialog.findViewById(R.id.register2_dialog_layout_button_register);

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;



        registerDialog.show();

        //set on click listener

        new CountDownTimer(10000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                countDownTimer.setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish()
            {
                countDownTimer.setText("0");

                getCodeLayout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.RotateInDownRight)
                        .duration(500)
                        .playOn(registerDialog.findViewById(R.id.register2_dialog_layout_linerlayout_getcodelayout));
            }
        }.start();

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(code.getText().toString().trim().isEmpty())
                {
                    code.setError("فیلد کد ارسالی نمی تواند خالی باشد");
                }
                else if(code.getText().length()<4)
                {
                    code.setError("کد ارسالی باید بزرگتر از 4 رقم باشد");
                }
                else
                {
                    KahkeshanProvider SignupProvider=new KahkeshanProvider();
                    KahkeshanService SignupService =SignupProvider.Result();
                    final ReqVerifiedModel reqVerifiedModel=new ReqVerifiedModel();

                    reqVerifiedModel.setPhone(Utility.phone);
                    reqVerifiedModel.setVerifiedcode(code.getText().toString().trim());

                    Call<ResVerifiedModel> call=SignupService.VerifiedNewUser(reqVerifiedModel);

                    call.enqueue(new Callback<ResVerifiedModel>() {
                        @Override
                        public void onResponse(Call<ResVerifiedModel> call, Response<ResVerifiedModel> response) {

                            if(response.isSuccess())
                            {
                                ResVerifiedModel  resVerifiedModel=response.body();
                                Toast.makeText(getApplicationContext(),"verified user "+resVerifiedModel.verifiedtype +"userid "+resVerifiedModel.userid,Toast.LENGTH_LONG).show();
                                MainActivity.userData.setStatusUser("true");
                                MainActivity.userData.setUserid(resVerifiedModel.userid);
                                MainActivity.userData.setPassword(resVerifiedModel.password);
                                MainActivity.userData.setPhone(resVerifiedModel.phone);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),response.code()+"",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResVerifiedModel> call, Throwable t)
                        {
                            Toast.makeText(getApplicationContext(),"response dochare moshkel shod"+t.getCause(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(Register2.this,Register1.class));
                finish();
            }
        });


        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                YoYo.with(Techniques.RotateOutUpRight)
                        .duration(500)
                        .playOn(registerDialog.findViewById(R.id.register2_dialog_layout_linerlayout_getcodelayout));

                new CountDownTimer(10000, 1000)
                {
                    @Override
                    public void onTick(long millisUntilFinished)
                    {
                        countDownTimer.setText("" + millisUntilFinished / 1000);
                    }

                    @Override
                    public void onFinish()
                    {
                        countDownTimer.setText("0");

                        YoYo.with(Techniques.RotateInDownRight)
                                .duration(500)
                                .playOn(registerDialog.findViewById(R.id.register2_dialog_layout_linerlayout_getcodelayout));

                    }
                }.start();

                KahkeshanProvider SignupProvider=new KahkeshanProvider();
                KahkeshanService SignupService =SignupProvider.Result();
                ReqRegisterModel reqRegisterModel=new ReqRegisterModel();

                reqRegisterModel.setUsername(Utility.username);
                reqRegisterModel.setPhone(Utility.phone);
                reqRegisterModel.setPassword(Utility.password);


                Call<ResRegisterModel> call=SignupService.Signup(reqRegisterModel);

                call.enqueue(new Callback<ResRegisterModel>() {
                    @Override
                    public void onResponse(Call<ResRegisterModel> call, Response<ResRegisterModel> response) {

                        if(response.isSuccess())
                        {
                            ResRegisterModel  resRegisterModel=response.body();
                            Toast.makeText(getApplicationContext(),"res: "+resRegisterModel.verifiedcode,Toast.LENGTH_LONG).show();
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
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerDialog.dismiss();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
