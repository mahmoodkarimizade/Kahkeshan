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
 * Created by Admin on 25/08/2017.
 */
public class FragmentAddDescription extends Fragment {

    private View view;
    private EditText description,hashtag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_add_description,container,false);

        //linke widget in fragment add description to layout
        link(view);

        return view;
    }

    private void link(View view)
    {
        //Edit Text
        description=(EditText)view.findViewById(R.id.fragment_add_description_edittextview_description);
        hashtag=(EditText)view.findViewById(R.id.fragment_add_description_edittextview_hashtag);


        if(FragmentAdd.description.equals(""))
        {
            description.setText("");
        }
        else
        {
            description.setText(FragmentAdd.description);
        }

        if(FragmentAdd.shashtag.equals(""))
        {
            hashtag.setText("");
        }
        else
        {
            hashtag.setText(FragmentAdd.shashtag);
        }

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
                FragmentAdd.description=charSequence.toString();

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

                FragmentAdd.shashtag=charSequence.toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}
