package ir.galaxycell.kahkeshan.UI;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import ir.galaxycell.kahkeshan.AVLoadingIndicatorView.AVLoadingIndicatorView;
import ir.galaxycell.kahkeshan.File.FileUtil;
import ir.galaxycell.kahkeshan.File.ImageBase64;
import ir.galaxycell.kahkeshan.Models.ResUploadImageProfileModel;
import ir.galaxycell.kahkeshan.Models.ResUserProfileInfoModel;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Utility.Utility;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Admin on 14/08/2017.
 */
public class EditProfile extends AppCompatActivity {

    private CircleImageView imageProfile;
    private LinearLayout changePhoto;
    private Dialog selectImageDialog,Loading,CheckNetwork;
    private Button gallery,camera;
    private LinearLayout mainLayout;
    private File actualImage;
    private File compressedImage;
    private ImageView cancel,save,dialogCancel;
    private EditText username,biography,link,phone,curentPassword,newPassword;
    private AVLoadingIndicatorView loading;
    private ResUserProfileInfoModel resUserProfileInfoModel;
    private ResUploadImageProfileModel resUploadImageProfileModel=new ResUploadImageProfileModel();
    private boolean updateInfo=false;
    private LinearLayout cnMainLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        resUserProfileInfoModel=new ResUserProfileInfoModel();
        resUserProfileInfoModel= (ResUserProfileInfoModel) getIntent().getSerializableExtra("resUserProfileInfoModel");

        //linke widget in edit profile to layout
        link();

        //socket emiter
        MainActivity.mSocket.on("resUploadProfileImage",resUploadProfileImage);
        MainActivity.mSocket.on("resUpdateUserProfileInfo",resUpdateUserProfileInfo);
        MainActivity.mSocket.on("resChangePassword",resChangePassword);


    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void link() {

        //Liner Layout
        changePhoto = (LinearLayout) findViewById(R.id.edit_profile_linerlayout_changeprofile);

        //CircleI mage View
        imageProfile = (CircleImageView) findViewById(R.id.edit_profile_imageview_imageprofile);

        //Image View
        cancel=(ImageView)findViewById(R.id.edit_profile_toolbar_imageview_cancel);
        save=(ImageView)findViewById(R.id.edit_profile_toolbar_imageview_save);

        //Edit Text
        username=(EditText)findViewById(R.id.edit_profile_edittext_username);
        biography=(EditText)findViewById(R.id.edit_profile_edittext_biography);
        link=(EditText)findViewById(R.id.edit_profile_edittext_link);
        phone=(EditText)findViewById(R.id.edit_profile_edittext_phonenumber);
        curentPassword=(EditText)findViewById(R.id.edit_profile_edittext_curentpassword);
        newPassword=(EditText)findViewById(R.id.edit_profile_edittext_newpassword);

        //set input type
        curentPassword.setTransformationMethod(new PasswordTransformationMethod());
        newPassword.setTransformationMethod(new PasswordTransformationMethod());

        //AVLoading Indicator View
        loading=(AVLoadingIndicatorView)findViewById(R.id.edit_profile_AVloading_loading);

        //create Lodin dialog
        Loading =new Dialog(EditProfile.this);
        Loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Loading.setContentView(R.layout.loading_dialog_layout);
        Loading.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Loading.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Loading.setCanceledOnTouchOutside(false);
        Loading.setCancelable(false);



        //load user image with Glide
        Glide.with(this)
                .load(resUserProfileInfoModel.profileImage)
                .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                .apply(RequestOptions.errorOf(R.drawable.cancel))
                .into(imageProfile);

        username.setText(resUserProfileInfoModel.username);
        biography.setText(resUserProfileInfoModel.biography);
        link.setText(resUserProfileInfoModel.link);
        phone.setText(MainActivity.userData.getPhone());


        //
        //////////CheckNetwork
        //
        //create CheckNetwork dialog
        CheckNetwork =new Dialog(EditProfile.this);
        CheckNetwork.requestWindowFeature(Window.FEATURE_NO_TITLE);
        CheckNetwork.setContentView(R.layout.check_network_connect_dialog_layout);
        CheckNetwork.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        CheckNetwork.getWindow().setBackgroundDrawableResource(R.drawable.boder_loding_layout);

        //define widget
        cnMainLayout=(LinearLayout)CheckNetwork.findViewById(R.id.check_network_connect_dialog_layout_linerlayout_mainlayout);

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) EditProfile.this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        cnMainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;
        //
        //////////CheckNetwork
        //

        //set on click listener
        onClick();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if(updateInfo)
        {
            MainActivity.mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
        }

        MainActivity.mSocket.off("resUploadProfileImage",resUploadProfileImage);
        MainActivity.mSocket.off("resUpdateUserProfileInfo",resUpdateUserProfileInfo);
        MainActivity.mSocket.off("resChangePassword",resChangePassword);

    }

    private void onClick() {
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                selectImage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(MainActivity.mSocket.connected())
                {
                    if(curentPassword.getText().length()==0&&newPassword.getText().length()==0)
                    {
                        save.setVisibility(View.GONE);
                        loading.setVisibility(View.VISIBLE);

                        JSONObject object=new JSONObject();

                        try
                        {

                            object.put("userid",MainActivity.userData.getUserid());
                            object.put("biography",biography.getText().toString());
                            object.put("link",link.getText().toString());

                            MainActivity.mSocket.emit("updateUserProfileInfo",object);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }

                    else
                    {
                        if(!curentPassword.getText().toString().equals(MainActivity.userData.getPassword()))
                        {
                            curentPassword.setError("رمز عبور فعلی اشتباه است");
                        }
                        else if(newPassword.getText().length()<6)
                        {
                            newPassword.setError("رمز عبور نمی تواند کمتر از 6 حرف باشد");
                        }
                        else
                        {
                            save.setVisibility(View.GONE);
                            loading.setVisibility(View.VISIBLE);

                            JSONObject object1=new JSONObject();
                            JSONObject object2=new JSONObject();

                            try
                            {
                                object1.put("userid",MainActivity.userData.getUserid());
                                object1.put("biography",biography.getText().toString());
                                object1.put("link",link.getText().toString());

                                object2.put("userid",MainActivity.userData.getUserid());
                                object2.put("newpassword",newPassword.getText().toString());

                                MainActivity.mSocket.emit("updateUserProfileInfo",object1);
                                MainActivity.mSocket.emit("changePassword",object2);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else
                {
                    if(!CheckNetwork.isShowing())
                    {
                        CheckNetwork.show();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(updateInfo)
                {
                    MainActivity.mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
                }
                finish();
                MainActivity.mSocket.off("resUploadProfileImage",resUploadProfileImage);
                MainActivity.mSocket.off("resUpdateUserProfileInfo",resUpdateUserProfileInfo);
                MainActivity.mSocket.off("resChangePassword",resChangePassword);

            }
        });
    }


    private void selectImage()
    {

        selectImageDialog =new Dialog(EditProfile.this,R.style.PauseDialog);
        selectImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        selectImageDialog.setContentView(R.layout.edit_profile_selectimage_dialog_layout);
        selectImageDialog.getWindow().setBackgroundDrawableResource(R.drawable.border_login_dialog_layout);
        selectImageDialog.setCanceledOnTouchOutside(false);

        //define widget
        mainLayout=(LinearLayout)selectImageDialog.findViewById(R.id.edit_profile_selectimage_layout_linerlayout_mainlayout);
        gallery=(Button)selectImageDialog.findViewById(R.id.edit_profile_selectimage_layout_button_gallery);
        camera=(Button)selectImageDialog.findViewById(R.id.edit_profile_selectimage_layout_button_camera);
        dialogCancel=(ImageView)selectImageDialog.findViewById(R.id.edit_profile_selectimage_layout_imageview_cancel);

        //set size
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) this).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mainLayout.getLayoutParams().width=displaymetrics.widthPixels-20;

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                selectImageDialog.dismiss();

                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                selectImageDialog.dismiss();

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                {
                    startActivityForResult(takePictureIntent,1);
                }
            }
        });

        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                selectImageDialog.dismiss();
            }
        });

        selectImageDialog.show();

    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {

            if (requestCode == 1)
            {

                try
                {
                    actualImage=FileUtil.from(this,data.getData());
                    compressedImage = new Compressor(this)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/Kahkeshan")
                            .compressToFile(actualImage);

                    imageUpload(EditProfile.this,compressedImage.toString());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
            else if (requestCode == 2)
            {

                try
                {
                    actualImage=FileUtil.from(this,data.getData());
                    compressedImage = new Compressor(this)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/Kahkeshan")
                            .compressToFile(actualImage);

                    imageUpload(EditProfile.this,compressedImage.toString());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

        }

    }


    private void imageUpload(final Context context, String path) throws IOException
    {

        if(MainActivity.mSocket.connected())
        {
            Loading.show();

            //encodeing image
            File fileImage=new File(path);
            // get type file from path file
            String [] k=path.split("\\.");
            String typeFileImage = k[k.length-1].toLowerCase();
            //init array with file length
            byte[] bytesImage = new byte[(int) fileImage.length()];
            FileInputStream fisImage = new FileInputStream(fileImage);
            fisImage.read(bytesImage); //read file into bytes[]
            fisImage.close();

            String encodedImage = ImageBase64
                    .with(context)
                    .noScale()
                    .encodeFile(path);


            try
            {

                JSONObject obj = new JSONObject();

                String [] name=resUserProfileInfoModel.profileImage.split("/");

                obj.put("userid",MainActivity.userData.getUserid());
                obj.put("encodedImage",bytesImage);
                obj.put("typeFileImage",typeFileImage);
                obj.put("profileImage",name[name.length-1]);
                MainActivity.mSocket.emit("uploadProfileImage",obj);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            if(!MainActivity.CheckNetwork.isShowing())
            {
                MainActivity.CheckNetwork.show();
            }
        }
    }



    private Emitter.Listener resUploadProfileImage=new Emitter.Listener() {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {

                    JSONObject data=(JSONObject)args[0];
                    try
                    {

                        resUploadImageProfileModel.status=data.getBoolean("status");
                        resUploadImageProfileModel.imageProfile=data.getString("profileImage");

                        //update  sendUserProfileInfoModel
                        resUserProfileInfoModel.profileImage=resUploadImageProfileModel.imageProfile;

                        if(resUploadImageProfileModel.status)
                        {
                            Loading.dismiss();

                            //load user image with Glide
                            Glide.with(getApplicationContext())
                                    .load(Utility.BaseUrl+resUploadImageProfileModel.imageProfile)
                                    .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                                    .apply(RequestOptions.errorOf(R.drawable.cancel))
                                    .into(imageProfile);

                            MainActivity.mSocket.emit("userProfileInfo",MainActivity.userData.getUserid());
                            finish();
                        }
                        else
                        {
                            Loading.dismiss();
                        }

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Loading.dismiss();
                    }

                }
            });

        }
    };

    private Emitter.Listener resUpdateUserProfileInfo=new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    JSONObject data=(JSONObject)args[0];

                    {
                        try
                        {
                            if(data.getBoolean("status"))
                            {
                                biography.setText(data.getString("biography"));
                                link.setText(data.getString("link"));

                                updateInfo=data.getBoolean("status");

                                save.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.GONE);

                                Toast.makeText(getApplicationContext(),"اطلاعات با موفقیت تغییر یافت",Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener resChangePassword=new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data=(JSONObject)args[0];

                    try
                    {
                        if(data.getBoolean("status"))
                        {
                            MainActivity.userData.setPassword(data.getString("password"));
                            Toast.makeText(getApplicationContext(),"پسور شما با موفقیت تغییر یافت",Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}
