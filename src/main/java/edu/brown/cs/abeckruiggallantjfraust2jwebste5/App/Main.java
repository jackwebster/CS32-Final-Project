package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import com.google.common.collect.ImmutableMap;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.Autocorrect.Autocorrector;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.json.JSONException;
import org.json.JSONObject;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;
import com.google.gson.Gson;
import freemarker.template.Configuration;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.DEFAULT_RATING;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.ConstantHyperparameters.NUM_RECOMMENDATIONS;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.JsonFormatter.ratingMapToJson;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.addUserToDatabase;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.deleteUser;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.getName;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.getRecipeObject;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.getUserIngredientRatings;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.getUserInventory;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.getUserRecipeRatings;

/**
 * The Main class of our project. This is where execution begins.
 */

public final class Main {
  private static Autocorrector ac;
  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private final String[] args;
  private RecipeApp recipeApp;

  private Main(String[] args) {
    this.args = args;
    this.recipeApp = new RecipeApp();
  }

  /**
   * This method runs the program, creating a new REPL
   * to take in user input.
   */
  private void run() {
    ac = new Autocorrector("data/ingredients.txt", true, true, 1);
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("Message: Unable use %s for template loading.%n",
              templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());
    FreeMarkerEngine freeMarker = createEngine();
    // Setup Spark Route
    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }
      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }
      return "OK";
    });

    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    Spark.exception(Exception.class, new ExceptionPrinter());

    /* all routes (names are indicative of their function) */
    //handlers related to ingredients
    Spark.post("/enter-ingredient", new AddIngredientHandler());
    Spark.post("/rate-ingredient", new RateIngredientHandler());
    Spark.post("/delete-ingredient", new DeleteIngredientHandler());
    //handlers related to recipes
    Spark.post("/rate-recipe", new RateRecipeHandler());
    //main pages
    Spark.post("/find-suggestions", new FindRecipeSuggestionsHandler()); //suggested recipe page
    Spark.post("/recipe", new FindSimilarRecipesHandler()); //recipe page (includes similar recipes)
    Spark.post("/profile", new GetProfileInfo()); //profile page
    Spark.post("/inventory", new GetUserInventory()); //called on fridge load
    //user account handlres
    Spark.post("/newUser", new CreateNewUserHandler());
    Spark.post("/newUserSignup", new CreateNewUserHandlerSignup());
    Spark.post("/name", new GetName());
    Spark.post("/delete-user", new DeleteUser());
    //for autocorrect
    Spark.post("/autocorrect", new AutocorrectHandler());
    Spark.post("/valid-ingredient", new ValidIngredient());
  }

  /**
   * Display an Message page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   * Called when ingredient is being added to user's inventory.
   */
  private class AddIngredientHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String ingredientName = data.getString("ingredient");
      recipeApp.getCurUser().addIngredient(ingredientName);
      Map<String, Object> variables = ImmutableMap.of("rating",
              recipeApp.getCurUser().getIngredientRatings().get(ingredientName));
      return GSON.toJson(variables);
    }
  }

  /**
   * Called when ingredient is being rated.
   */
  private class RateIngredientHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String ingredientName = data.getString("ingredient");
      Double ingredientRating = data.getDouble("rating");
      recipeApp.getCurUser().addIngredientRating(ingredientName, ingredientRating);
      return "";
    }
  }

  /**
   * Called when ingredient is being deleted from a user's inventory.
   */
  private class DeleteIngredientHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String ingredientName = data.getString("ingredient");
      recipeApp.getCurUser().removeIngredient(ingredientName);
      return "";
    }
  }

  /**
   * Handles finding initial recipe suggestions.
   */
  private class FindRecipeSuggestionsHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      //finds recipe suggestions (based on user's inventory)
      ArrayList<Recipe> recipeSuggestions = recipeApp.getCurUser().cook();
      Map<String, ImmutableMap<String, String>> toReturn = new HashMap<>();
      //if there were no suggestions
      if (recipeSuggestions.size() == 0) {
        toReturn.put("error", ImmutableMap.copyOf(new HashMap<>()));
      } else {
        //find the number to return
        int numToReturn = Math.min(NUM_RECOMMENDATIONS, recipeSuggestions.size());
        //for each suggestion, converts recipe to a small map (to be converted to json)
        for (int i = 0; i < numToReturn; i++) {
          toReturn.put("suggestion-" + i, recipeSuggestions.get(i).toSmallMap());
        }
      }
      Map<String, Object> variables = ImmutableMap.copyOf(toReturn);
      String json = GSON.toJson(variables);
      return json;
    }
  }

  /**
   * Handles finding similar recipes once a recipe is selected.
   */
  private class FindSimilarRecipesHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      //get queried recipe
      String currentRecipeName = data.getString("recipe");
      Recipe curRecipe = getRecipeObject(currentRecipeName, recipeApp.getCurUser());

      //get queried recipe rating
      String email = data.getString("user");
      String ratings = getUserRecipeRatings(email);

      //finds similar recipes
      ArrayList<Map<String, String>> similarRecipes = recipeApp.getCurUser()
              .findSimilarRecipes(currentRecipeName);

      //converts similar recipes to json
      Map<String, Object> variables = ImmutableMap.of("recipe",
              curRecipe.toBigMap(), "similar1", similarRecipes.get(0),
              "similar2", similarRecipes.get(1), "similar3", similarRecipes.get(2),
              "rating", ratings);
      String json = GSON.toJson(variables);
      return json;
    }
  }

  /**
   * Handles rating recipes.
   */
  private class RateRecipeHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String recipeName = data.getString("recipe").toLowerCase();
      Double recipeRating = data.getDouble("rating");
      recipeApp.getCurUser().addRecipeRating(recipeName, recipeRating);
      return "";
    }
  }

  /**
   * Handles creating a new user object and updating the currentUser object.
   */
  private class CreateNewUserHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String username = data.getString("name");
      User newUser;
      //get inventory
      String inventoryString = getUserInventory(username);
      //splitting to create hashset for user ingredients
      if (inventoryString.length() > 0) {
        String[] inventoryArr = inventoryString.split(",");
        HashSet<String> ingredients = new HashSet<>(Arrays.asList(inventoryArr));
        //get rating
        String ratingString = getUserIngredientRatings(username);
        newUser = new User(username, ingredients);
        //splitting to create hashset for user ingredients
        if (ratingString.length() > 0) {
          String[] ratingArr = ratingString.split(",");
          for (String review : ratingArr) {
            String[] rating = review.split(":");
            newUser.addIngredientRating(rating[0], Double.parseDouble(rating[1]));
          }
        }
      } else {
        newUser = new User(username, null);
      }
      recipeApp.setCurUser(newUser);
      return "success";
    }
  }

  /**
   * Front end handler for creating new user off of signup.
   */
  private class CreateNewUserHandlerSignup implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String name = data.getString("name");
      String email = data.getString("email");
      addUserToDatabase(name, email);
      HashSet<String> ingredients = new HashSet<>();
      User newUser = new User(email, ingredients);
      recipeApp.setCurUser(newUser);
      return "";
    }
  }

  /**
   * Front end handler for returning user inventory when Fridge loads.
   */
  private class GetUserInventory implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      Map<String, Object> map = new HashMap<>();
      String username = data.getString("name");
      String inventory = getUserInventory(username);
      //splitting to create hashset for user ingredients
      if (inventory.length() > 0) {
        HashSet<String> ingredients = new HashSet<>(Arrays.asList(inventory.split(",")));
        //get ratings for ingredients
        User user = recipeApp.getCurUser();
        HashMap<String, Double> preRated = user.getIngredientRatings();
        HashMap<String, String> ingRatings = new HashMap<>();
        //gets rating for each ingredient, and if its not in database, sets default rating
        for (String ingredient : ingredients) {
          boolean rated = preRated.containsKey(ingredient);
          if (rated) {
            ingRatings.put(ingredient, Double.toString(preRated.get(ingredient)));
          } else {
            ingRatings.put(ingredient, Double.toString(DEFAULT_RATING));
          }
          map = ImmutableMap.of("inventory", ingRatings);
        }
      } else {
        map = ImmutableMap.of("inventory", "");
      }
      return GSON.toJson(map);
    }
  }

  /**
   * Gets current users name.
   */
  private class GetName implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String email = data.getString("name");
      String name = getName(email);
      Map<String, String> map = ImmutableMap.of("name", name);
      return GSON.toJson(map);
    }
  }

  /**
   * Gets the profile info for a user. Includes rated ingredients + recipes.
   */
  private class GetProfileInfo implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String email = data.getString("name");
      try {
        String name = getName(email);
        String recipes = getUserRecipeRatings(email);
        String ingredients = getUserIngredientRatings(email);
        Map<String, String> recipeRating = new HashMap<>();
        Map<String, String> ingredientRating = new HashMap<>();
        //check if no rated recipes
        if (recipes.length() == 0) {
          recipeRating.put("error", "");
        } else {
          recipeRating = ratingMapToJson(recipes);
        }
        //check if no rated ingredients
        if (ingredients.length() == 0) {
          ingredientRating.put("error", "");
        } else {
          ingredientRating = ratingMapToJson(ingredients);
        }

        Map<String, Object> map = ImmutableMap.of("name", name, "recipes",
                recipeRating, "ingredients", ingredientRating);
        return GSON.toJson(map);
      } catch (SQLException e) {
        System.err.println("ERROR: Error connecting to database");
        return "error";
      }
    }
  }

  /**
   * Deletes a user from the database.
   */
  private class DeleteUser implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      JSONObject data = new JSONObject(request.body());
      String email = data.getString("name");
      try {
        deleteUser(email);
        return "success";
      } catch (SQLException e) {
        System.err.println("ERROR: Error connecting to database");
        return "error";
      }
    }
  }

  /** Handles requests for autocorrect on an input.
   *  @return GSON which contains the result of autocorrect.suggest()
   */
  private static class AutocorrectHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      //Get JSONObject from req and use it to get the value of the input you want to
      // generate suggestions for
      try {
        JSONObject obj = new JSONObject(req.body());
        String input = obj.getString("text");
        //use the global autocorrect instance to get the suggestions
        Set<String> response = ac.suggest(input);
        //create an immutable map using the suggestions
        Map<String, Set<String>> suggestions = ImmutableMap.of("results", response);
        //return a Json of the suggestions (HINT: use the GSON.Json())
        return GSON.toJson(suggestions);
      } catch (JSONException e) {
        System.err.println("Error parsing JSON Object" + e);
        return null;
      }
    }
  }

  /** Handles requests to tell if input is valid ingredient.
   *  @return GSON which contains the result of autocorrect.suggest()
   */
  private static class ValidIngredient implements Route {
    @Override
    public Object handle(Request req, Response res) {
      try {
        JSONObject obj = new JSONObject(req.body());
        String input = obj.getString("ingredient");
        //check if input is in the autocorrect word list (which is the ingredients)
        if (ac.getWordList().contains(input)) {
          Map<String, Boolean> validity = ImmutableMap.of("result", true);
          return GSON.toJson(validity);
        } else {
          Map<String, Boolean> validity = ImmutableMap.of("result", false);
          return GSON.toJson(validity);
        }
      } catch (JSONException e) {
        System.err.println("Error parsing JSON Object" + e);
        return null;
      }
    }
  }
}
