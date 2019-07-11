package ch.fhnw.edu.emoba.spheropantherapp.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import ch.fhnw.edu.emoba.spheropantherapp.R;
import ch.fhnw.edu.emoba.spheropantherapp.components.TouchControllerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TouchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TouchFragment extends Fragment implements RobotControlFragment {

    private static TouchFragment fragment;

    private TouchControllerView controllerView;

    public TouchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TouchFragment.
     */
    public static TouchFragment instance() {
        if (fragment == null) {
            fragment = new TouchFragment();
        }

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
        View view = inflater.inflate(R.layout.fragment_touch, container, false);

        RelativeLayout controllerLayout = (RelativeLayout) view.findViewById(R.id.touchControllerLayout);

        controllerView = new TouchControllerView(getActivity());
        controllerLayout.addView(controllerView);

        return view;
    }

    public void start() {
        controllerView.startRobotControlThread();
    }

    public void stop() {
        controllerView.stopRobotControlThread();
    }
}
