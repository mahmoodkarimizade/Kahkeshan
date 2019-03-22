package ir.galaxycell.kahkeshan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.List;

import ir.galaxycell.kahkeshan.R;

/**
 * Created by Admin on 12/09/2017.
 */
public class SpinerAdapter extends BaseAdapter
{
    private Context context;
    private List<String>list;
    private View view;
    private TextView subSpinner;

    public SpinerAdapter(Context context,List<String>list)
    {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.fragment_home_spinner_layout,viewGroup,false);

        subSpinner=(TextView)view.findViewById(R.id.fragment_home_spinner_layout_textview_subspinner);
        subSpinner.setText(list.get(i).toString());

        return view;
    }
}
