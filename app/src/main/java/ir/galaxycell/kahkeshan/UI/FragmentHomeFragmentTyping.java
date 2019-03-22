package ir.galaxycell.kahkeshan.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.galaxycell.kahkeshan.R;

/**
 * Created by Admin on 12/09/2017.
 */
public class FragmentHomeFragmentTyping extends Fragment {

    private View view;
    private TextView description;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view=inflater.inflate(R.layout.fragment_home_fragment_service,container,false);

        description=(TextView)view.findViewById(R.id.fragment_home_fragment_service_textview_description);
        description.setText("FragmentHomeFragmentTyping");
        return view;
    }
}
