package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Collections;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.SCORE_WEIGHT;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.SIMILARITY_WEIGHT;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getRecipeObject;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getRecipesWithIngredient;
import static java.lang.Math.min;

/**
 * This class deals with finding recipes without creating a graph.
 * Has methods like findRecipesWithIngredients that finds the top
 * most similar recipes, based on the ingredients in the user's
 * inventory and how they rated the ingredients/recipes.
 */
public final class RecipeFinder {

  private RecipeFinder() {
  }

  /**
   * finds the top most similar recipes, based on the ingredients in the user's
   * inventory and how they rated the ingredients/recipes.
   * @param numRecipesToReturn the number of recipes that will be return to the frontend
   * @param curUser the current user (used to find their rated recipes)
   * @return arraylist of recipe names that correspond to the top recipes
   */
  public static ArrayList<String> findRecipesWithIngredients(int numRecipesToReturn, User curUser) {
    HashSet<String> ingredientList = curUser.getIngredients();
    //if no ingredients in user's fridge, no recipes can be made
    if (ingredientList == null || ingredientList.size() == 0) {
      return new ArrayList<String>();
    }
    LinkedHashMap<String, Integer> recipeMap = new LinkedHashMap<>();
    /* for every ingredient find recipes that have that ingredient
    count maintains a number representing how many ingredients in
     ingredientList (the user's inventory) are in the given recipe.
     For e.g. if count for "lasagna" is 2, then two of the ingredients
     in the lasagna recipe are in the user's inventory. */
    for (String ingredient : ingredientList) {
      //gets all recipes with ingredient
      String recipesString = getRecipesWithIngredient(ingredient);
      if (recipesString == null) {
        continue;
      }
      String[] rec = recipesString.trim().split("\\s*,\\s*");
      for (String recipe : rec) {
        // get the number of occurences of the specified recipe
        Integer count = recipeMap.get(recipe);
        // if the map contains no mapping for the recipe,
        // map the recipe with a value of 1
        if (count == null) {
          recipeMap.put(recipe, 1);
        } else {  // else increment the found value by 1
          recipeMap.put(recipe, count + 1);
        }
      }
    }
    //create hashmap where keys are count and value is arraylist of recipes with that count
    Map<Integer, ArrayList<String>> invertedHashMap = new HashMap<>();
    for (Map.Entry<String, Integer> entry : recipeMap.entrySet()) {
      ArrayList<String> recipe = invertedHashMap.get(entry.getValue());
      if (recipe == null) {
        ArrayList<String> recipeList = new ArrayList<>();
        recipeList.add(entry.getKey());
        invertedHashMap.put(entry.getValue(), recipeList);
      } else {
        recipe.add(entry.getKey());
        invertedHashMap.put(entry.getValue(), recipe);
      }
    }

    //create a new treemap with recipes with the most number of ingredients in the user's inventory.
    TreeMap<Integer, ArrayList<String>> top = treeMapOfTop(invertedHashMap, numRecipesToReturn);
    //creates a new treemap, where the top (numRecipesToReturn*2) are sorted based on the user's
    //rating preferences
    TreeMap<Double, ArrayList<String>> ratedMap = factorInRatings(top, curUser);
    //converts to arraylist of size numRecipesToReturn of top recipes
    return topSortedRecipes(ratedMap, numRecipesToReturn);
  }

  /**
   * Converts sorted map of recipes, to arraylist of top numRecipesToReturn recipes.
   * @param ratedMap each recipe with their score as the key
   * @param numRecipesToReturn is the size of the arraylist of recipes to return
   * @return arraylist of the top recipes that most match the user's inventory + ratings
   */
  private static ArrayList<String> topSortedRecipes(TreeMap<Double, ArrayList<String>> ratedMap,
                                                    int numRecipesToReturn) {
    ArrayList<String> topSortedRecipes = new ArrayList<>();
    int numToAdd = numRecipesToReturn;
    for (double key : ratedMap.keySet()) {
      ArrayList<String> rep = ratedMap.get(key);

      topSortedRecipes.addAll(rep.subList(0, min(numToAdd, rep.size())));
      numToAdd = numToAdd - rep.size();
      if (numToAdd < 0) {
        return topSortedRecipes;
      }
    }
    return topSortedRecipes;
  }

  /**
   * Given a map, returns a small map containing numToReturn recipes.
   * @param allMap from which we will create the smaller map.
   * @param numToReturn is the number of recipes the smaller map should contain.
   * @return smaller map containing numToReturn*2 recipes
   */
  private static TreeMap<Integer, ArrayList<String>> treeMapOfTop(Map<Integer,
          ArrayList<String>> allMap, int numToReturn) {
    int numToAdd = numToReturn * 2;
    TreeMap<Integer, ArrayList<String>> top = new TreeMap<>();
    for (int key = allMap.keySet().size(); key > 0; key--) {
      ArrayList<String> rep = allMap.get(key);
      rep = new ArrayList<>(rep.subList(0, min(numToAdd, rep.size())));
      top.put(key, rep);
      numToAdd = numToAdd - rep.size();
      if (numToAdd < 0) {
        return top;
      }
    }
    return top;
  }

  /**
   * Creates a metric that takes into account number of ingredients in user's inventory that
   * overlap with the recipe, as well as what the user rated that recipe/the ingredients
   * it uses. Then sorts the map based on this metric.
   * @param map from which we will sort the contents
   * @param user used to find what the user has rated the recipe/ingredients
   * @return sorted map based on metric
   */
  private static TreeMap<Double, ArrayList<String>> factorInRatings(Map<Integer,
          ArrayList<String>> map, User user) {
    TreeMap<Double, ArrayList<String>> mapWithRatings = new TreeMap<>(Collections.reverseOrder());
    //iterates through every count in the map
    for (int count = map.keySet().size(); count > 0; count--) {
      ArrayList<String> rep = map.get(count);
      //iterate through every recipe
      for (String recipe : rep) {
        Recipe recipeObj = getRecipeObject(recipe, user);
        int numIngredients = recipeObj.getIngredients().size();
        if (recipeObj == null) {
          continue;
        }
        //calculate a weighted sum to decide the match
        Double metric = count / numIngredients * SCORE_WEIGHT + SIMILARITY_WEIGHT
                * recipeObj.getValue();
        ArrayList<String> newRating;
        //add this rating to a hashmap, that sorts based on metric
        if (mapWithRatings.get(metric) == null) {
          newRating = new ArrayList<>();
          newRating.add(recipe);
          mapWithRatings.put(metric, newRating);
        } else {
          newRating = mapWithRatings.get(metric);
          newRating.add(recipe);
          mapWithRatings.put(metric, newRating);
        }
      }
    }
    return mapWithRatings;
  }
}
