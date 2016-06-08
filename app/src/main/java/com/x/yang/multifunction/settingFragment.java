package com.x.yang.multifunction;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link settingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link settingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class settingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText number, G;
    private SettingProfile sp;
    private Button done;



    public settingFragment() {
        sp = SettingProfile.getSp();
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment settingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static settingFragment newInstance(String param1, String param2) {
        settingFragment fragment = new settingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        number =  (EditText) v.findViewById(R.id.numberOfVideo);
        int t = sp.getMaxNumberRecords();
       // number.setText(t);
        G = (EditText) v.findViewById(R.id.gravitylv);
        done = (Button) v.findViewById(R.id.settingdone);
        done.setOnClickListener(new changeSetting());
       // G.setText(sp.getG_level());
        return v;
    }




    @Override
    public void onDetach() {
        super.onDetach();

    }


    private class changeSetting implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(number.getText().length()!=0)
                sp.setMaxNumberRecords(Integer.getInteger(number.getText().toString()));
            else
                Toast.makeText(settingFragment.this.getActivity(),"can not be blank",Toast.LENGTH_LONG).show();
            if(number.getText().length()!=0)
                sp.setG_level(Integer.getInteger(G.getText().toString()));
            else
                Toast.makeText(settingFragment.this.getActivity(),"can not be blank",Toast.LENGTH_LONG).show();
            if(Integer.getInteger(number.getText().toString())>10){
                Toast.makeText(settingFragment.this.getActivity(),"high number may run out of memory space",Toast.LENGTH_LONG).show();
            }
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
