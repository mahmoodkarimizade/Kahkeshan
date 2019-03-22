package ir.galaxycell.kahkeshan.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import ir.galaxycell.kahkeshan.R;

/**
 * Created by Admin on 03/09/2017.
 */
public class SeeEditProductFragmentFile extends Fragment {

    private View view;
    private ImageView productfile;
    private TextView titleFile,addressFile;
    private ArrayList<String> docPaths = new ArrayList<>();
    private File file;
    private boolean flagEdit=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.see_edit_product_fragment_file,container,false);


        //linke widget in fragment add file to layout
        link(view);

        return view;
    }

    private void link(View view)
    {
        //Image View
        productfile=(ImageView)view.findViewById(R.id.see_edit_product_fragment_file_imageview_productfile);
        // get type file from path file
        String [] m=SeeEditProduct.resSeeProductModel.getProductPathFile().split("\\.");
        String typeFile = m[m.length-1].toLowerCase();
        switch (typeFile)
        {
            case "pdf":
                productfile.setImageResource(R.drawable.ic_pdf);
                break;
            case "doc":
                productfile.setImageResource(R.drawable.ic_word);
                break;
            case "docx":
                productfile.setImageResource(R.drawable.ic_word);
                break;
            case "ppt":
                productfile.setImageResource(R.drawable.ic_ppt);
                break;
            case "pptx":
                productfile.setImageResource(R.drawable.ic_ppt);
                break;
            case "xls":
                productfile.setImageResource(R.drawable.ic_excel);
                break;
            case "xlsx":
                productfile.setImageResource(R.drawable.ic_excel);
                break;
            case "txt":
                productfile.setImageResource(R.drawable.ic_txt);
                break;
        }

        if(flagEdit)
        {
            productfile.setEnabled(true);
        }
        else
        {
            productfile.setEnabled(false);
        }

        //Text View
        titleFile=(TextView)view.findViewById(R.id.see_edit_product_fragment_file_textview_titlefile);
        addressFile=(TextView)view.findViewById(R.id.see_edit_product_fragment_file_textview_addressfile);

/*        if (FragmentAdd.filepath.equals(""))
        {
            productfile.setImageResource(R.drawable.documentadd);
            titleFile.setVisibility(View.GONE);
            addressFile.setVisibility(View.GONE);
        }
        else
        {

            productfile.setImageResource(R.drawable.ic_ppt);
            titleFile.setVisibility(View.VISIBLE);
            addressFile.setVisibility(View.VISIBLE);
            addressFile.setText(FragmentAdd.filepath);
            // get type file from path file
            String [] m=FragmentAdd.filepath.split("\\.");
            String typeFile = m[m.length-1].toLowerCase();

            switch (typeFile)
            {
                case "pdf":
                    productfile.setImageResource(R.drawable.ic_pdf);
                    break;
                case "doc":
                    productfile.setImageResource(R.drawable.ic_word);
                    break;
                case "docx":
                    productfile.setImageResource(R.drawable.ic_word);
                    break;
                case "ppt":
                    productfile.setImageResource(R.drawable.ic_ppt);
                    break;
                case "pptx":
                    productfile.setImageResource(R.drawable.ic_ppt);
                    break;
                case "xls":
                    productfile.setImageResource(R.drawable.ic_excel);
                    break;
                case "xlsx":
                    productfile.setImageResource(R.drawable.ic_excel);
                    break;
                case "txt":
                    productfile.setImageResource(R.drawable.ic_txt);
                    break;
            }
        }*/

        // set co click listenr
        onClick();
    }

    private void onClick()
    {
        productfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                showFileChooser();

            }
        });


    }

    private void showFileChooser()
    {

        FilePickerBuilder.getInstance().setMaxCount(1)
                .setActivityTheme(R.style.AppTheme)
                .pickFile(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if(resultCode== getActivity().RESULT_OK && data!=null)
        {
            docPaths = new ArrayList<>();
            docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));


            // get type file from path file
            String [] m=docPaths.get(0).split("\\.");
            String typeFile = m[m.length-1].toLowerCase();


            file=new File(docPaths.get(0));
            int file_size = Integer.parseInt(String.valueOf(file.length()/1024));

            if(file_size>=3000)
            {
                Toast.makeText(getContext(),"اندازه فایل نباید بزرگتر از 2 مگابایت باشد",Toast.LENGTH_LONG).show();
            }
            else
            {
                SeeEditProduct.filepath=docPaths.get(0);

                titleFile.setVisibility(View.VISIBLE);
                addressFile.setVisibility(View.VISIBLE);
                addressFile.setText(docPaths.get(0));

                switch (typeFile)
                {
                    case "pdf":
                        productfile.setImageResource(R.drawable.ic_pdf);
                        break;
                    case "doc":
                        productfile.setImageResource(R.drawable.ic_word);
                        break;
                    case "docx":
                        productfile.setImageResource(R.drawable.ic_word);
                        break;
                    case "ppt":
                        productfile.setImageResource(R.drawable.ic_ppt);
                        break;
                    case "pptx":
                        productfile.setImageResource(R.drawable.ic_ppt);
                        break;
                    case "xls":
                        productfile.setImageResource(R.drawable.ic_excel);
                        break;
                    case "xlsx":
                        productfile.setImageResource(R.drawable.ic_excel);
                        break;
                    case "txt":
                        productfile.setImageResource(R.drawable.ic_txt);
                        break;
                }
            }


        }
    }

    public void setFlagEdit(boolean flagEdit)
    {
        productfile.setEnabled(true);
    }


}
