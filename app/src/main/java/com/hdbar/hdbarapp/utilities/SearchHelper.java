package com.hdbar.hdbarapp.utilities;

import com.hdbar.hdbarapp.models.Cocktail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchHelper {
    public static List<Cocktail> searchInCocktails(String s, List<Cocktail> cocktails){
        String a[] = s.split(" ");
        List<String> charsNormal = Arrays.asList(a);
        List<String> chars = new ArrayList<>();

        for(int i=0;i< charsNormal.size();i++){
            chars.add(charsNormal.get(i).toLowerCase());
        }
        List<Cocktail> finalList = new ArrayList<>();
        for(int i=0;i<cocktails.size();i++){
            Cocktail temp = cocktails.get(i);
            boolean added = false;
            for(int j=0;j<chars.size();j++){
                for(int z = 0;z<temp.recipe.size();z++){
                    if(temp.recipe.get(z).toLowerCase().contains(chars.get(j))){
                        finalList.add(temp);
                        added = true;
                        break;
                    }
                }
                if(added){
                    break;
                }
                if(temp.name.toLowerCase().contains(chars.get(j))){
                    finalList.add(temp);
                    added = true;
                    break;
                }
                for(int z = 0;z<temp.tags.size();z++){
                    if(temp.tags.get(z).contains(chars.get(j))){
                        finalList.add(temp);
                        added = true;
                        break;
                    }
                }
                if(added){
                    break;
                }
            }
        }
        return finalList;
    }
}
