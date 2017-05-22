package ch.fhnw.edu.emoba.spheropantherapp.layout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import ch.fhnw.edu.emoba.spheropantherapp.R;
import ch.fhnw.edu.emoba.spheropantherapp.components.AimControllerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AimFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AimFragment extends Fragment implements RobotFragment {

    private AimControllerView controllerView;

    public AimFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AimFragment.
     */
    public static AimFragment newInstance() {
        AimFragment fragment = new AimFragment();

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
        View view = inflater.inflate(R.layout.fragment_aim, container, false);

        RelativeLayout controllerLayout = (RelativeLayout) view.findViewById(R.id.aimControllerLayout);

        controllerView = new AimControllerView(getActivity());
        controllerLayout.addView(controllerView);

        FloatingActionButton zeroHeadingButton = (FloatingActionButton) view.findViewById(R.id.zeroHeadingButton);
        zeroHeadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controllerView.setZeroHeading();
            }
        });

        return view;
    }

    public void start() {
        controllerView.startRobotControlThread();
    }

    public void stop() {
        controllerView.stopRobotControlThread();
    }
}
