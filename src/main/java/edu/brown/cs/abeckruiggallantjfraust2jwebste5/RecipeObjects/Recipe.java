package edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects;

import com.google.common.collect.ImmutableMap;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.User;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.Graph.Vertex;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Models a recipe. Stores information about a given recipe
 * object.
 */
public class Recipe implements Vertex<Ingredient> {

  private HashSet<Ingredient> adjIngredients = new HashSet<>();
  private String title;
  private String description;
  private HashSet<String> ingredients;
  private String chef;
  private String instructions;
  private String cookingTime;
  private String prepTime;
  private String photourl;
  private String serves;
  private String url;
  private Double rating;
  //if the rating has been explicitly set
  private boolean ratingSet = false;
  private User owner;

  public Recipe(ArrayList<String> params, User user) {
    //used for checkstyle :(
    final int magicNum7 = 7;
    final int magicNum8 = 8;
    final int magicNum9 = 9;
    final int magicNum10 = 10;
    this.title = params.get(0);
    this.description = params.get(1);
    String[] ingredientArr = params.get(2).trim().split("\\s*,\\s*");
    this.ingredients =  new HashSet(Arrays.asList(ingredientArr));
    this.chef = params.get(4);
    this.instructions = params.get(5).replaceAll("[\\[\\]()\\//{}\"]",
            "").replaceAll("[\b,]", "").replaceAll("[.]", ". ");
    this.cookingTime = params.get(6);
    this.prepTime = params.get(magicNum7);
    this.photourl = params.get(magicNum8);
    this.serves = params.get(magicNum9);
    this.url = params.get(magicNum10);
    this.owner = user;
    //if rating is set use the saved rating
    if (user.getRecipeRatings().keySet().contains(title)) {
      ratingSet = true;
      this.rating = user.getRecipeRatings().get(title);
    } else { //otherwise calculate estimated rating based on adjacent vertices
      this.rating = getAdjacentRatings();
    }
  }

  /**
   * @return Gets an estimated rating of this recipe, based on the rating of adjacent ingredients
   */
  private double getAdjacentRatings() {
    String ingredientString = Database.getIngredientForRecipe(title);
    String[] ingredientArray = ingredientString.trim().split("\\s*,\\s*");
    int numIngredients = ingredientArray.length;
    double newSim = 0;
    for (String ingredientName : ingredientArray) {
      Ingredient ing = new Ingredient(ingredientName, this.owner);
      newSim += ing.getValue();
    }
    return newSim / (double) numIngredients;
  }

  /**
   * @return the name of this recipe
   */
  public String getName() {
    return title;
  }

  /**
   * Gets the adjacent ingredient vertices.
   * @param ingredientsAlreadyAdded that were already added to graph, so that
   *                                we can use the same object to reference the
   *                                adjacent vertices of this graph
   * @return HashSet of adjacent ingredients
   */
  @Override
  public HashSet<Ingredient> getAdjacentVertices(
          HashMap<String, Ingredient> ingredientsAlreadyAdded) {
    double newSim = 0;
    int numIngredients;
    if (adjIngredients.size() == 0) {
      String ingredientString = Database.getIngredientForRecipe(title);
      String[] ingredientArray = ingredientString.trim().split("\\s*,\\s*");
      numIngredients = ingredientArray.length;
      for (String ingredientName : ingredientArray) {
        Ingredient ing;
        //if ingredient object exist reference that object
        if (ingredientsAlreadyAdded.containsKey(ingredientName)) {
          ing = ingredientsAlreadyAdded.get(ingredientName);
        } else { //otherwise create a new ingredient object
          ing = new Ingredient(ingredientName, this.owner);
        }
        newSim += ing.getValue();
        adjIngredients.add(ing);
      }
      //if this rating has not been set use this to calculate new rating
      if (!ratingSet) {
        this.rating = newSim / (double) (numIngredients);
      }
    }

    HashSet<Ingredient> adjIngredientsToReturn = new HashSet<>();
    adjIngredientsToReturn.addAll(adjIngredients);
    return adjIngredientsToReturn;
  }

  /**
   * @param title set title for this recipe
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @return decription for this recipe
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description set description for this recipe
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return ingredients for this recipe
   */
  public HashSet<String> getIngredients() {
    return ingredients;
  }

  /**
   * @param ingredients set ingredients for this recipe
   */
  public void setIngredients(HashSet<String> ingredients) {
    this.ingredients = ingredients;
  }

  /**
   * @return chef for this recipe
   */
  public String getChef() {
    return chef;
  }

  /**
   * @param chef set check for this recipe
   */
  public void setChef(String chef) {
    this.chef = chef;
  }

  /**
   * @return instructions for this recipe
   */
  public String getInstructions() {
    return instructions;
  }

/**
 * @param instructions sets instructions for this recipe
 */
  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  /**
   * @return cooking time for this recipe
   */
  public String getCookingTime() {
    return cookingTime;
  }

  /**
   * @return prepTime for this recipe
   */
  public String getPrepTime() {
    return prepTime;
  }

  /**
   * @param prepTime sets prepTime for this recipe
   */
  public void setPrepTime(String prepTime) {
    this.prepTime = prepTime;
  }

  /**
   * @return photorul for this recipe
   */
  public String getPhotourl() {
    return photourl;
  }

  /**
   * @param photourl sets photourl for this recipe
   */
  public void setPhotourl(String photourl) {
    this.photourl = photourl;
  }

  /**
   * @return how many people this recipe serves
   */
  public String getServes() {
    return serves;
  }

  /**
   * @param serves sets serves for this recipe
   */
  public void setServes(String serves) {
    this.serves = serves;
  }

  /**
   * @return url for this recipe
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url sets url for this recipe
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return returns an immutable map with some of the information
   * for this recipe. Used to return to front end.
   */
  public ImmutableMap<String, String> toSmallMap() {
    Map<String, String> mutableMap = new HashMap<>();
    mutableMap.put("recipeName", this.title);
    if (this.photourl == null) {
      mutableMap.put("src", "https://demofree.sirv.com/nope-not-here.jpg");
    } else {
      mutableMap.put("src", this.photourl);
    }
    mutableMap.put("chef", this.chef);
    ImmutableMap<String, String> immutableMap = ImmutableMap.<String, String>builder()
            .putAll(mutableMap)
            .build();
    return immutableMap;
  }

  /**
   * @return returns an immutable map with more information
   * (than in the bigMap)
   * for this recipe. Used to return to front end.
   */
  public ImmutableMap<String, String> toBigMap() {
    Map<String, String> map = new HashMap<>() {{
        put("title", checkForNull(title));
        put("description", checkForNull(description));
        put("instructions", checkForNull(instructions));
        put("url", checkForNull(url));
        put("chefName", checkForNull(chef));
        put("rating", checkForNull(Double.toString(rating)));
      }
    };
    HashSet<String> recipeIngredients = ingredients;
    int ingredeintNum = 0;
    for (String ingredient : recipeIngredients) {
      map.put("ingredient" + ingredeintNum, ingredient);
      ingredeintNum++;
    }
    ImmutableMap<String, String> immutableMap = ImmutableMap.<String, String>builder()
            .putAll(map)
            .build();
    return immutableMap;
  }

  /**
   * @return rating for this recipe
   */
  @Override
  public Double getValue() {
    return this.rating;
  }

  /**
   * @param value which is the rating for this recipe
   */
  @Override
  public void setValue(double value) {
    this.rating = value;
    ratingSet = true;
  }

  private String checkForNull(String toCheck) {
    if (toCheck != null) {
      return toCheck;
    } else {
      return "";
    }
  }
}
