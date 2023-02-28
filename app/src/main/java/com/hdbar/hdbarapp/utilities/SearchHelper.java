package com.hdbar.hdbarapp.utilities;

import com.hdbar.hdbarapp.models.Cocktail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchHelper {
    public static List<Cocktail> searchInCocktails(String s, List<Cocktail> cocktails){
        String a[] = s.split(" ");
        List<String> chars = Arrays.asList(a);
        List<Cocktail> finalList = new ArrayList<>();
        for(int i=0;i<cocktails.size();i++){
            Cocktail temp = cocktails.get(i);
            if(temp.recipe.contains(s)){
                finalList.add(temp);
            }else if(chars.contains(temp.name)){
                finalList.add(temp);
            }else {
                for(int j = 0;j<chars.size();j++){
                    if(temp.tags.contains(chars.get(j))){
                        finalList.add(temp);
                        break;
                    }
                }
            }
        }
        return finalList;
    }
}
