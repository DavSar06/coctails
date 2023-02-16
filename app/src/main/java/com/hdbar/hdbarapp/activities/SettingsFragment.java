package com.hdbar.hdbarapp.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.databinding.FragmentSettingsBinding;
import com.hdbar.hdbarapp.settings.AboutUsActivity;
import com.hdbar.hdbarapp.settings.AccountActivity;
import com.hdbar.hdbarapp.settings.HelpAndSupportActivity;
import com.hdbar.hdbarapp.settings.LanguagesActivity;
import com.hdbar.hdbarapp.settings.PaymentActivity;
import com.hdbar.hdbarapp.settings.PrivacyPolicyActivity;
import com.hdbar.hdbarapp.utilities.Constants;

import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {


    private EditText search;
    private RelativeLayout moderators,languages,privacyAndSecurity,helpAndSupport,aboutUs,logOut, payment, notification,account;
    private TextView email_settings, user_name_settings;
    private SwitchCompat notifications;
    private boolean isNormal = true;
    private FragmentSettingsBinding binding;
    private FirebaseFirestore database;
    private String userId;
    private String useremail;





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
;


        setUserInfo();

        getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    private void setUserInfo(){
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            useremail = task.getResult().get(Constants.KEY_EMAIL).toString();
                            binding.profileEmailSettings.setText(useremail);
                            user_name_settings.setText(task.getResult().get(Constants.KEY_USERNAME).toString());
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReference(task.getResult().get(Constants.KEY_USER_IMAGE).toString()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Glide.with(binding.profilePictureSettings).load(task.getResult()).into(binding.profilePictureSettings);
                                }
                            });
                        }
                        else {
                            Log.e("Exception", task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void init(){
        database = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
        moderators = binding.moderatorsSettingsBtn;
        languages = binding.languagesSettingsBtn;
        privacyAndSecurity = binding.privacySettingsBtn;
        helpAndSupport = binding.helpAndSupportBtn;
        aboutUs = binding.aboutusSettingsBtn;
        account = binding.accountSettings;
        email_settings = binding.profileEmailSettings;
        user_name_settings = binding.profileNameSettings;
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

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AccountActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
            }
        });

        moderators.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ModeratorsActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
            }
        });


        languages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LanguagesActivity.class);
                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
            }
        });

        privacyAndSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
            }
        });

        helpAndSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpAndSupportActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intent);
                //getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
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