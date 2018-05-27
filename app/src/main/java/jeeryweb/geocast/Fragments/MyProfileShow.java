package jeeryweb.geocast.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class MyProfileShow extends Fragment {


    private SharedPrefHandler sharedPrefHandler;

    private TextView occupationPP,agePP,genderPP,phonePP ,editTextButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_my_profile_show, container, false);

        sharedPrefHandler = new SharedPrefHandler(getContext());



        occupationPP = (TextView) view.findViewById(R.id.activity_myprofile_profession);
        agePP = (TextView) view.findViewById(R.id.activity_myprofile_age);
        genderPP = (TextView) view.findViewById(R.id.activity_myprofile_gender);
        phonePP = (TextView) view.findViewById(R.id.activity_myprofile_phno);
        editTextButton  = (TextView)view.findViewById(R.id.activity_myprofile_editbutton);


        if(sharedPrefHandler.getOccupation() != null)
            occupationPP.setText(sharedPrefHandler.getOccupation());
        if(sharedPrefHandler.getAge() != null)
            agePP.setText(sharedPrefHandler.getAge());
        if(sharedPrefHandler.getGender() != null)
            genderPP.setText(sharedPrefHandler.getGender());
        if(sharedPrefHandler.getPhoneNo() != null)
            phonePP.setText(sharedPrefHandler.getPhoneNo());

        editTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create fragment and give it an argument specifying the article it should show
                MyProfileEdit newFragment = new MyProfileEdit();
                Bundle args = new Bundle();
                //args.putInt(MyProfileEdit.ARG_POSITION, position);
                newFragment.setArguments(args);

                FragmentManager manager = getActivity().getSupportFragmentManager();

                FragmentTransaction transaction = manager.beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.profile_fragment_replace, newFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                //manager.popBackStack();


            }
        });


        // Inflate the layout for this fragment
        return view;

    }
}

