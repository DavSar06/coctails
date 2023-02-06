package com.hdbar.hdbarapp.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.FragmentSettingsBinding;
import com.hdbar.hdbarapp.settings.AboutUsActivity;
import com.hdbar.hdbarapp.settings.AccountActivity;
import com.hdbar.hdbarapp.settings.HelpAndSupportActivity;
import com.hdbar.hdbarapp.settings.LanguagesActivity;
import com.hdbar.hdbarapp.settings.PaymentActivity;
import com.hdbar.hdbarapp.settings.PrivacyPolicyActivity;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {


    private EditText search;
    private RelativeLayout moderators,account,languages,privacyAndSecurity,helpAndSupport,aboutUs,logOut, payment, notification;
    private SwitchCompat notifications;
    private boolean isNormal = true;
    private FragmentSettingsBinding binding;
    private FirebaseFirestore database;





    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentSettingsBinding.inflate(getLayoutInflater());

        init();
        onClick();

        getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void init(){
        database = FirebaseFirestore.getInstance();
        moderators = binding.moderatorsSettingsBtn;
        search = binding.settingsSearch;
        account = binding.accountSettingsBtn;
        languages = binding.languagesSettingsBtn;
        privacyAndSecurity = binding.privacySettingsBtn;
        helpAndSupport = binding.helpAndSupportBtn;
        aboutUs = binding.aboutusSettingsBtn;
        payment = binding.paymentBtn;
        logOut = binding.logoutBtn;
        notification = binding.notificationsSettingsBtn;
        notifications = binding.settingsNotificationsSwitch;

        new Thread(new Runnable() {
            @Override
            public void run() {
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .document(FirebaseAuth.getInstance().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult().get(Constants.KEY_STATUS).equals(Constants.KEY_STATUS_ADMINISTRATOR)){
                                        moderators.setVisibility(View.VISIBLE);
                                        binding.viewModeratorTop.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
            }
        }).start();

    }

    private void  onClick(){
        moderators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ModeratorsActivity.class);
                startActivity(intent);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LanguagesActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        privacyAndSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        helpAndSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpAndSupportActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getActivity(),LoginActivity.class);
                startActivity(i);
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isNormal)
                    Log.d("In", "true");
                else
                    Log.d("In", "false");


                isNormal = !isNormal;
            }
        });

    }
}