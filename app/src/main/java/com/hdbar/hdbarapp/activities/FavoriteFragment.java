package com.hdbar.hdbarapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
import com.hdbar.hdbarapp.utilities.SearchHelper;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
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
    private String uid;
    Integer adapterStatus = 0;
    Integer i;

    private final CocktailListener cocktailListener = new CocktailListener() {
        @Override
        public void onCocktailClicked(Cocktail cocktail) {
            Intent intent = new Intent(binding.getRoot().getContext(), CocktailPageActivity.class);
            intent.putExtra(Constants.KEY_COCKTAIL_ID, cocktail.id);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
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
        getCocktails();

        changeAdapter(adapterStatus,cocktails);
    }

    private void init(){
        uid = FirebaseAuth.getInstance().getUid();
        database = FirebaseFirestore.getInstance();
        binding = FragmentFavoriteBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return binding.getRoot();
    }

    private void getCocktails(){
        cocktails = new LinkedList<>();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.textErrorMessage.setVisibility(View.INVISIBLE);
        binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
        database.collection(Constants.KEY_COLLECTION_FAVORITES)
                .whereEqualTo(Constants.KEY_USER_UID,uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        cocktails = new LinkedList<>();
                        i = 0;
                        for(DocumentSnapshot snapshot: queryDocumentSnapshots){
                            i++;
                            database.collection(Constants.KEY_COLLECTION_COCKTAILS)
                                    .document(snapshot.get(Constants.KEY_COCKTAIL_ID).toString())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot document) {
                                            String cocktailName = document.getString(Constants.KEY_COCKTAIL_NAME);
                                            String creator = document.getString(Constants.KEY_COCKTAIL_CREATOR_NAME);
                                            ArrayList<String> recipe = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_RECIPE);
                                            String rating_count = document.get(Constants.KEY_COCKTAIL_HOW_MANY_RATES).toString();
                                            ArrayList<String> image = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_IMAGE);
                                            ArrayList<String> tags = (ArrayList<String>) document.get(Constants.KEY_COCKTAIL_TAGS);
                                            String rating = document.get(Constants.KEY_COCKTAIL_RATING).toString();
                                            Cocktail a = new Cocktail(document.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags);
                                            cocktails.add(a);
                                            if(i==queryDocumentSnapshots.size()){
                                                changeAdapter(adapterStatus,cocktails);
                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        });
        binding.rowDouble.setOnClickListener(v->{
            if(adapterStatus!=0){
                binding.rowSingle.setColorFilter(ContextCompat.getColor(getActivity(), R.color.background_color_light), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.rowDouble.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
                binding.textErrorMessage.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                adapterStatus = 0;
                changeAdapter(adapterStatus,cocktails);
            }
        });
        binding.rowSingle.setOnClickListener(v->{
            if(adapterStatus!=1){
                binding.rowSingle.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.rowDouble.setColorFilter(ContextCompat.getColor(getActivity(), R.color.background_color_light), android.graphics.PorterDuff.Mode.SRC_IN);
                binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
                binding.textErrorMessage.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                adapterStatus = 1;
                changeAdapter(adapterStatus,cocktails);
            }
        });
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Cocktail> searchResult = SearchHelper.searchInCocktails(charSequence.toString(), cocktails);
                changeAdapter(adapterStatus,searchResult);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void changeAdapter(Integer k,List<Cocktail> cocktails){
        if(cocktails.isEmpty()){
            binding.cocktailsRecyclerView.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textErrorMessage.setVisibility(View.VISIBLE);
        }else {
            if(k==0){
                CocktailsAdapter adapter = new CocktailsAdapter(cocktails,cocktailListener);
                binding.cocktailsRecyclerView.setAdapter(adapter);
            }else{
                CocktailsSingleAdapter adapter = new CocktailsSingleAdapter(cocktails,cocktailListener);
                binding.cocktailsRecyclerView.setAdapter(adapter);
            }
            binding.cocktailsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textErrorMessage.setVisibility(View.INVISIBLE);
        }
    }
}