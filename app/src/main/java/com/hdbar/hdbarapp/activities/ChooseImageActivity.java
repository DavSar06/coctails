package com.hdbar.hdbarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.hdbar.hdbarapp.adapters.ImagesAdapter;
import com.hdbar.hdbarapp.databinding.ActivityChooseImageBinding;
import com.hdbar.hdbarapp.models.ProfileImage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ChooseImageActivity extends AppCompatActivity {

    private ActivityChooseImageBinding binding;
    private HashMap<String, List<ProfileImage>> images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        images = new HashMap<>();
        List<ProfileImage> temp = new LinkedList<>();
        for(int i=0;i<100;i++){
            if(i!=0 && i%10==0){
                images.put("Category "+i/10,temp);
                temp = new LinkedList<>();
            }else{
                temp.add(new ProfileImage("image"+i+".jpeg","BlaBla"));
            }
        }

        binding.imageListRecyclerView.setAdapter(new ImagesAdapter(images));
        binding.imageListRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.INVISIBLE);

        setListeners();
    }

    public void setListeners(){
        binding.imageBack.setOnClickListener(v->finish());
    }
}