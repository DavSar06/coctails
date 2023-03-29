package com.hdbar.hdbarapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.hdbar.hdbarapp.adapters.OthersAdapter;
import com.hdbar.hdbarapp.adapters.TopTenOfWeekAdapter;
import com.hdbar.hdbarapp.databinding.FragmentHomeBinding;
import com.hdbar.hdbarapp.listeners.CocktailListener;
import com.hdbar.hdbarapp.models.Cocktail;
import com.hdbar.hdbarapp.utilities.Constants;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {



    private FragmentHomeBinding  binding;
    private List<Cocktail> cocktails;
    private FirebaseFirestore database;
    private RecyclerView recyclerViewOthers;
    private TopTenOfWeekAdapter adapter;
    private OthersAdapter othersAdapter;
    private RecyclerView unScroll;

    private Comparator<Cocktail> ratingComparator = new Comparator<Cocktail>() {
        @Override
        public int compare(Cocktail cocktail, Cocktail t1) {
            return t1.rating.compareTo(cocktail.rating);
        }
    };

    private Comparator<Cocktail> dateComparator = new Comparator<Cocktail>() {
        @Override
        public int compare(Cocktail cocktail, Cocktail t1) {
            return t1.date.compareTo(cocktail.date);
        }
    };

    private final CocktailListener cocktailListener = new CocktailListener() {
        @Override
        public void onCocktailClicked(Cocktail cocktail) {
            Intent intent = new Intent(binding.getRoot().getContext(), CocktailPageActivity.class);
            intent.putExtra(Constants.KEY_COCKTAIL_ID, cocktail.id);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
    };

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        init();
        listeners();
        super.onCreate(savedInstanceState);
    }
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    private void glassTypes(String type){
       Intent intent = new Intent(getActivity(), GlassTypeActivity.class);
       intent.putExtra(Constants.KEY_GLASS_TYPE,type);
       startActivity(intent);
       getActivity().overridePendingTransition(R.anim.right_to_left_in,R.anim.right_to_left_out);
    }

    private void init(){
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        database = FirebaseFirestore.getInstance();
        cocktails = new LinkedList<>();
        unScroll = binding.recyclerviewOthers;

        database.collection(Constants.KEY_COLLECTION_COCKTAILS).whereEqualTo(Constants.KEY_STATUS,Constants.KEY_COCKTAIL_STATUS_APPROVED)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                Date date = document.getDate(Constants.KEY_DATE);
                                Cocktail a = new Cocktail(document.getId(),cocktailName,recipe,image,rating,creator,rating_count,tags,date);
                                cocktails.add(a);
                            }
                            ArrayList<Cocktail> topCocktails = new ArrayList<>();
                            cocktails.sort(ratingComparator);
                            for(int i=0;i<cocktails.size();i++){
                                if(i==10){
                                    break;
                                }
                                topCocktails.add(cocktails.get(i));
                            }
                            cocktails.sort(dateComparator);
                            adapter = new TopTenOfWeekAdapter(topCocktails,cocktailListener);
                            binding.topTenOfWeekRecyclerView.setAdapter(adapter);
                            binding.recyclerviewOthers.setAdapter(new CocktailsAdapter(cocktails,cocktailListener));
                        } else {
                            Log.d("FCM", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }




    private void listeners(){
        binding.getRoot().setOnClickListener(v->{
            InputMethodManager inm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),0);
        });
        binding.notificationButton.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(),NotificationsActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });
        binding.homeSearch.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(),SearchActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });

        binding.highball.setOnClickListener(v->glassTypes("highball"));
        binding.rocks.setOnClickListener(v->glassTypes("rocks"));
        binding.shot.setOnClickListener(v->glassTypes("shot"));
        binding.champagne.setOnClickListener(v->glassTypes("champagne"));
        binding.margarita.setOnClickListener(v->glassTypes("margarita"));
        binding.martini.setOnClickListener(v->glassTypes("martini"));
    }

}