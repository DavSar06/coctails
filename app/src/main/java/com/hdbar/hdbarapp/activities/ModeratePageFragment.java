package com.hdbar.hdbarapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.adapters.CocktailsAdapter;
import com.hdbar.hdbarapp.adapters.CocktailsSingleAdapter;
import com.hdbar.hdbarapp.adapters.ModeratePageAdapter;
import com.hdbar.hdbarapp.databinding.FragmentModeratePageBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class ModeratePageFragment extends Fragment {

    private FragmentModeratePageBinding binding;
    private List<Cocktail> cocktails;
    private FirebaseFirestore database;
    private ModeratePageAdapter adapter;

    private final CocktailListener cocktailListener = new CocktailListener() {
        @Override
        public void onCocktailClicked(Cocktail cocktail) {
            Intent intent = new Intent(binding.getRoot().getContext(), ModerateActivity.class);
            intent.putExtra(Constants.KEY_COCKTAIL_ID, cocktail.id);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    };

    public ModeratePageFragment() {
        // Required empty public constructor
    }

    public static ModeratePageFragment newInstance(String param1, String param2) {
        return new ModeratePageFragment();
    }


    private void init(){
        binding = FragmentModeratePageBinding.inflate(getLayoutInflater());
        database = FirebaseFirestore.getInstance();
        cocktails = new ArrayList<>();
        adapter = new ModeratePageAdapter(cocktails,cocktailListener);
        binding.cocktailsRecyclerView.setAdapter(adapter);
        updateRecyclerView();
    }

    private void updateRecyclerView(){
        binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
        binding.progressBar.setVisibility(View.VISIBLE);
        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .whereEqualTo(Constants.KEY_COCKTAIL_STATUS,Constants.KEY_COCKTAIL_STATUS_PENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                String cocktailName = document.getString(Constants.KEY_COCKTAIL_NAME);
                                String creator = document.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                                String recipe = document.get(Constants.KEY_COCKTAIL_RECIPE).toString();
                                String image = document.get(Constants.KEY_COCKTAIL_IMAGE).toString();
                                String rating = document.get(Constants.KEY_COCKTAIL_RATING).toString();
                                Cocktail temp = new Cocktail(document.getId(),cocktailName,recipe,image,rating,creator);
                                cocktails.add(temp);
                            }
                        }else {
                            Log.d("Exception",task.getException().toString());
                        }
                    }
                });
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if(cocktails.size()==0){
                    binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.textErrorMessage.setVisibility(View.VISIBLE);
                }else {
                    binding.cocktailsRecyclerView.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.textErrorMessage.setVisibility(View.INVISIBLE);
                }
            }
        },1000);
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        listeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return binding.getRoot();
    }
}