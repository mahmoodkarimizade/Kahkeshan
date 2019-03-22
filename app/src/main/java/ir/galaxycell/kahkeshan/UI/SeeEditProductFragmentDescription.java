package ir.galaxycell.kahkeshan.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ir.galaxycell.kahkeshan.R;

/**
 * Created by Admin on 03/09/2017.
 */
public class SeeEditProductFragmentDescription extends Fragment {

    private View view;
    private EditText description,hashtag;
    private boolean flagEdit=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.see_edit_product_fragment_description,container,false);

        //linke widget in fragment add description to layout
        link(view);

        return view;
    }

    private void link(View view)
    {
        //Edit Text
        description=(EditText)view.findViewById(R.id.see_edit_product_fragment_description_edittextview_description);
        hashtag=(EditText)view.findViewById(R.id.see_edit_product_fragment_description_edittextview_hashtag);

        description.setText(SeeEditProduct.resSeeProductModel.getProductDescription());
        hashtag.setText(SeeEditProduct.resSeeProductModel.getProductHashtag());

        if (flagEdit)
        {
            description.setEnabled(true);
            hashtag.setEnabled(true);

        }
        else
        {
            description.setEnabled(false);
            hashtag.setEnabled(false);
        }

        /*if(FragmentAdd.description.equals(""))
        {
            description.setText("");
        }
        else
        {
            description.setText(FragmentAdd.description);
        }*/

        //on click listener
        onClick();
    }

    private void onClick()
    {
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                SeeEditProduct.description=charSequence.toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        hashtag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                SeeEditProduct.shashtag=charSequence.toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void setFlagEdit(boolean flagEdit)
    {
        description.setEnabled(true);
        hashtag.setEnabled(true);
    }

    public void setDescription()
    {
        description.setText(SeeEditProduct.resSeeProductModel.getProductDescription());
        hashtag.setText(SeeEditProduct.resSeeProductModel.getProductHashtag());
    }

}
