package ch.fhnw.edu.emoba.spheropantherapp.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import ch.fhnw.edu.emoba.spheropantherapp.R;
import ch.fhnw.edu.emoba.spheropantherapp.components.SensorControllerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SensorFragment extends Fragment {

    private Boolean start = true;
    private SensorControllerView controllerView;

    public SensorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AimFragment.
     */
    public static SensorFragment newInstance() {
        SensorFragment fragment = new SensorFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        RelativeLayout controllerLayout = (RelativeLayout) view.findViewById(R.id.sensorControllerLayout);

        controllerView = new SensorControllerView(getActivity());
        controllerLayout.addView(controllerView);

        // Add action listener
        final Button button = (Button) view.findViewById(R.id.sensorButton);
        button.setText("Start Spheropanther");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (start) {
                controllerView.start();
                button.setText("Stop Spheropanther");
            } else {
                controllerView.stop();
                button.setText("Start Spheropanther");
            }
            start = !start;
            }
        });

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();

        if (controllerView != null) {
            controllerView.stop();
            start = true;
        }
    }
}
