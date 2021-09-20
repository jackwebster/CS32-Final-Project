package edu.brown.cs.abeckruiggallantjfraust2jwebste5.DataPreprocess;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.createIngredientDatabase;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.createRecipeDatabase;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.createUserDatabase;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.initialize;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.addToRecipeDatabase;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.addIngredient;

/**
 * This is a class that is used only in preprocessing the data.
 * It takes in the Json located in the data folders of the BBC
 * recipes, and converts it to a SQL database of recipes, as well
 * as an ingredient to recipe map (each ingredient mapped to the
 * recipes it is used in).
 *
 * NOTE: This class was not junit tested, as we ran it once, and can see and
 * verify the results ourselves.
 */
public final class JsonToSql {
  private JsonToSql() { }
  public static void parseJson() throws FileNotFoundException, JSONException {
    //used to sort the inputs, as we want the ingredients to be sorted from the least
    //number of ingredients to most
    HashMap<String, Integer> numIngredientsMap = new HashMap<>();
    //maps each ingredient to a set of recipes
    ListMultimap<String, Object> ingredientSet = ArrayListMultimap.create();
    try {
      initialize("data/newdb.sqlite3");
      //SQL creates each of these databases
      createRecipeDatabase();
      createIngredientDatabase();
      createUserDatabase();
      JSONParser parser = new JSONParser();
      BufferedReader bufferedReader = new BufferedReader(new FileReader("data/recipes2.json"));
      String line;
      //read the JSON line by line
      while ((line = bufferedReader.readLine()) != null) {
        JSONObject jsonObject = (JSONObject) parser.parse(line);
        ArrayList<String> parameters = new ArrayList<>();

        //parsing json fields to send to sql database
        String title = jsonObject.get("title").toString().trim().replace(",", "");
        title = title.toLowerCase();
        parameters.add(title);
        parameters.add(jsonObject.get("description").toString());
        parameters.add(jsonObject.get("ingredients").toString());
        parameters.add(jsonObject.get("chef").toString());
        parameters.add(jsonObject.get("instructions").toString());
        parameters.add(jsonObject.get("cooking_time_minutes").toString());
        parameters.add(jsonObject.get("preparation_time_minutes").toString());
        parameters.add(jsonObject.get("photo_url") == null ? null
                : jsonObject.get("photo_url").toString());
        parameters.add(jsonObject.get("serves").toString());
        parameters.add(jsonObject.get("url").toString());
        JSONArray ingredientList = (JSONArray) jsonObject.get("instructions_detailed");

        String ingredients = "";
        //cycle through ingredient list
        for (int i = 0; i < ingredientList.size(); i++) {
          JSONObject ingredientObj = (JSONObject) ingredientList.get(i);
          String ingredient = ingredientObj.get("ingredient") == null ? null
                  : ingredientObj.get("ingredient").toString().toLowerCase();
          //if the ingredientMap doesn't already have this recipe, add it
          if (!ingredientSet.containsEntry(ingredient, title)) {
            ingredientSet.put(ingredient, title);
            ingredients += ingredient + ",";
          }
        }
        //keeps a running list of all the ingredients in this recipe
        if (ingredients.length() > 0) {
          ingredients = ingredients.substring(0, ingredients.length() - 1);
        } else {
          ingredients = "";
        }
        parameters.add(2, ingredients);
        //add to recipe database
        addToRecipeDatabase(parameters);
        numIngredientsMap.put(title, ingredientList.size());
      }
      //add to ingredientMap database
      Comparator<String> newComp = new NumIngredientsComparator(numIngredientsMap);
      for (String ingredient: ingredientSet.keySet()) {
        String recipeList = ingredientSet.get(ingredient).toString();
        recipeList = recipeList.strip();
        recipeList = recipeList.substring(1, recipeList.length() - 1);
        // sort recipe list by number of ingredients (lowest number of ingredients first)
        String[] recArr = recipeList.trim().split("\\s*,\\s*");
        ArrayList<String> recArrList = new ArrayList<>(Arrays.asList(recArr));
        recArrList.sort(newComp);
        recipeList = recArrList.toString().replace("[", "")
                .replace("]", "");
        addIngredient(ingredient, recipeList);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
