package com.hdbar.hdbarapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hdbar.hdbarapp.R;
import com.hdbar.hdbarapp.adapters.CocktailsAdapter;
import com.hdbar.hdbarapp.adapters.CocktailsSingleAdapter;
import com.hdbar.hdbarapp.databinding.FragmentFavoriteBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteFragment extends Fragment {

    private FragmentFavoriteBinding binding;
    private List<Cocktail> cocktails;
    private FirebaseFirestore database;

    private final CocktailListener cocktailListener = new CocktailListener() {
        @Override
        public void onCocktailClicked(Cocktail cocktail) {
            Intent intent = new Intent(binding.getRoot().getContext(), CocktailPageActivity.class);
            intent.putExtra(Constants.KEY_COCKTAIL_ID, cocktail.id);
            startActivity(intent);
        }
    };

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance() {
        FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        listeners();

        changeAdapter(0);
    }

    private void init(){
        database = FirebaseFirestore.getInstance();
        binding = FragmentFavoriteBinding.inflate(getLayoutInflater());
        cocktails = new LinkedList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return binding.getRoot();
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        });
        binding.rowDouble.setOnClickListener(v->{
            binding.rowSingle.setColorFilter(ContextCompat.getColor(getActivity(), R.color.background_color_light), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.rowDouble.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            changeAdapter(0);
        });
        binding.rowSingle.setOnClickListener(v->{
            binding.rowSingle.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.rowDouble.setColorFilter(ContextCompat.getColor(getActivity(), R.color.background_color_light), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            changeAdapter(1);
        });
    }

    private void changeAdapter(Integer k){
        database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String cocktailName = document.getString(Constants.KEY_COCKTAIL_NAME);
                                String creator = document.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                                String recipe = document.get(Constants.KEY_COCKTAIL_RECIPE).toString();
                                String image = document.get(Constants.KEY_COCKTAIL_IMAGE).toString();
                                String rating = document.get(Constants.KEY_COCKTAIL_RATING).toString();
                                Cocktail a = new Cocktail(document.getId(),cocktailName,recipe,image,rating,creator);
                                cocktails.add(a);
                            }
                            if(k==0){
                                CocktailsAdapter adapter = new CocktailsAdapter(cocktails,cocktailListener);
                                binding.cocktailsRecyclerView.setAdapter(adapter);
                            }else{
                                CocktailsSingleAdapter adapter = new CocktailsSingleAdapter(cocktails,cocktailListener);
                                binding.cocktailsRecyclerView.setAdapter(adapter);
                            }
                            binding.cocktailsRecyclerView.setVisibility(View.VISIBLE);
                            binding.progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            Log.d("FCM", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}