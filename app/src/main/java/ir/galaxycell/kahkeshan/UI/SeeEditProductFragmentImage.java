package ir.galaxycell.kahkeshan.UI;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;
import ir.galaxycell.kahkeshan.File.FileUtil;
import ir.galaxycell.kahkeshan.R;
import ir.galaxycell.kahkeshan.Utility.Utility;

/**
 * Created by Admin on 03/09/2017.
 */
public class SeeEditProductFragmentImage extends Fragment {

    private View view;
    private Dialog selectImageDialog;
    private File actualImage,compressedImage;
    private Button gallery,camera;
    private LinearLayout mainLayout;
    private ImageView dialogCancel,productImage;
    private boolean flagEdit=false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.see_edit_product_fragment_image,container,false);


        //linke widget in fragment add image to layout
        link(view);
        return view;
    }

    private void link(View view)
    {
        productImage=(ImageView)view.findViewById(R.id.see_edit_product_fragment_image_imageview_productimage);
        if(flagEdit)
        {
            productImage.setEnabled(true);
        }
        else
        {
            productImage.setEnabled(false);
        }

        //load user image with Glide
        Glide.with(this)
                .load(Utility.BaseUrl+SeeEditProduct.resLoadMoreLibraryAndFavorites.getProductImage())
                .apply(RequestOptions.placeholderOf(R.drawable.galaxywallpapers))
                .apply(RequestOptions.errorOf(R.drawable.cancel))
                .into(productImage);

/*        if (FragmentAdd.imagePath.equals(""))
        {
            productImage.setImageResource(R.drawable.imageadd);
        }
        else
        {
            Bitmap thumbnail = (BitmapFactory.decodeFile(FragmentAdd.imagePath));
            productImage.setImageBitmap(thumbnail);
        }*/

        // set co click listenr
        onClick();
    }

    private void onClick()
    {
        productImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectImage();
            }
        });

    }

    private void selectImage()
    {

        selectImageDialog =new Dialog(getContext(),R.style.PauseDialog);
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
        (getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
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
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK)
        {

            if (requestCode == 1)
            {

                try
                {
                    actualImage= FileUtil.from(getContext(),data.getData());
                    compressedImage = new Compressor(getContext())
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/Kahkeshan")
                            .compressToFile(actualImage);

                    Bitmap thumbnail = (BitmapFactory.decodeFile(compressedImage.toString()));
                    productImage.setImageBitmap(thumbnail);

                    SeeEditProduct.imagePath=compressedImage.toString();
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
                    actualImage=FileUtil.from(getContext(),data.getData());
                    compressedImage = new Compressor(getContext())
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/Kahkeshan")
                            .compressToFile(actualImage);

                    Bitmap thumbnail = (BitmapFactory.decodeFile(compressedImage.toString()));
                    productImage.setImageBitmap(thumbnail);

                    SeeEditProduct.imagePath=compressedImage.toString();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }

    }

    public void setFlagEdit(boolean flagEdit)
    {
        productImage.setEnabled(true);
    }


}
