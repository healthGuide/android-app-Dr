package rkapoors.healthguide_dr;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ScheduleFragment extends Fragment {

    private static View mView;
    ImageView schedico;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.activity_schedule_fragment, container, false);
        FloatingActionButton fab = (FloatingActionButton)mView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(getActivity(), notification.class);
                startActivity(intent);
            }
        });

        schedico = (ImageView)mView.findViewById(R.id.sched);
        schedico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent schedact = new Intent(getActivity(),schedfetch.class);
                startActivity(schedact);
            }
        });

        return  mView;
    }
}
