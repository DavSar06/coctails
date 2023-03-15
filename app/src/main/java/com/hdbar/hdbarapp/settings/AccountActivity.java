package com.hdbar.hdbarapp.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.activities.ChooseImageActivity;
import com.hdbar.hdbarapp.activities.CocktailPageActivity;
import com.hdbar.hdbarapp.activities.MainActivity;
import com.hdbar.hdbarapp.adapters.CocktailsAdapter;
import com.hdbar.hdbarapp.adapters.OthersAdapter;
import com.hdbar.hdbarapp.adapters.TopTenOfWeekAdapter;
import com.hdbar.hdbarapp.databinding.ActivityAccountBinding;
import com.hdbar.hdbarapp.databinding.ActivityHelpAndSupportBinding;
import com.hdbar.hdbarapp.databinding.ActivityLanguagesBinding;
import com.hdbar.hdbarapp.databinding.FragmentHomeBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.AlwaysOnRun;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AccountActivity extends AppCompatActivity {

    private ActivityAccountBinding binding;
    private TextView back;
    private FirebaseFirestore database;
    private List<Cocktail> cocktails;
    private String uid;
    private RecyclerView recyclerViewOthers;
    private TopTenOfWeekAdapter adapter;
    private OthersAdapter othersAdapter;
    private RecyclerView unScroll;

    private final CocktailListener cocktailListener = new CocktailListener() {
        @Override
        public void onCocktailClicked(Cocktail cocktail) {
            Intent intent = new Intent(binding.getRoot().getContext(), CocktailPageActivity.class);
            intent.putExtra(Constants.KEY_COCKTAIL_ID, cocktail.id);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        init();
        listeners();
        AlwaysOnRun.AlwaysRun(this);
    }


    private void init(){
        database = FirebaseFirestore.getInstance();
        back = binding.backAboutUs;
        cocktails = new LinkedList<>();
        uid = FirebaseAuth.getInstance().getUid();
        setUserData();

        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .whereEqualTo(Constants.KEY_STATUS,Constants.KEY_COCKTAIL_STATUS_APPROVED)
                .whereEqualTo(Constants.KEY_COCKTAIL_CREATOR_ID,uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cocktails = new LinkedList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String cocktailName = document.getString(Constants.KEY_COCKTAIL_NAME);
                                String creator = document.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                                ArrayList<String> recipe = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_RECIPE);
                                String rating_count = document.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                                ArrayList<String> image = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_IMAGE);
                                ArrayList<String> tags = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_TAGS);
                                String rating = document.get(Constants.KEY_COCKTAIL_RATING).toString();
                                Cocktail a = new Cocktail(document.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags);
                                cocktails.add(a);
                                Log.d("GG", "ok");
                            }
                            binding.recyclerviewYourCocktails.setAdapter(new CocktailsAdapter(cocktails,cocktailListener));
                            Log.d("GG", cocktails.size()  + "");
                        } else {
                            Log.d("FCM", "Error getting documents: ", task.getException());
                        }
                    }
                });



    }


    private void setUserData(){
        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            binding.accountNickname.setHint(task.getResult().get(Constants.KEY_USERNAME).toString());
                            binding.accountType.setText("User type: "+task.getResult().get(Constants.KEY_STATUS).toString().toUpperCase());
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReference(task.getResult().get(Constants.KEY_USER_IMAGE).toString()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Glide.with(binding.userImage).load(task.getResult()).into(binding.userImage);
                                }
                            });
                        }
                        else {
                            Log.e("Exception", task.getException().getMessage());
                        }
                    }
                });
    }

    public void listeners(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChooseImageActivity.class);
                startActivity(intent);
            }
        });
    }
}