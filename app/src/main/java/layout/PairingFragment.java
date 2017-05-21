package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.fhnw.edu.emoba.spheropantherapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PairingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PairingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PairingFragment extends Fragment {

    public PairingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PairingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PairingFragment newInstance(String param1, String param2) {
        PairingFragment fragment = new PairingFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_pairing, container, false);
        return rootView;
    }
}
