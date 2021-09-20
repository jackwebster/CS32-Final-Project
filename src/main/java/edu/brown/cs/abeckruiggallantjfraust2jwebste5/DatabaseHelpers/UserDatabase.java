package edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public final class UserDatabase {
  private static Connection conn;

  private UserDatabase() { }

  /**
   * @return return UserDatabase connection (same as Database class
   * connection)
   */
  public static Connection getUserConn() {
    return conn;
  }

  /**
   * Sets connection to null.
   */
  public static void closeUserConn() {
    conn = null;
  }

  /**
   * Given connection (established in the Database class). Initializes
   * it in this class.
   * @param con to initialize
   */
  public static void initializeConn(Connection con) {
    conn = con;
  }

  /**
   * Used in preprocess, creates a user database.
   * @throws SQLException
   */
  public static void createUserDatabase() {
    try {
      PreparedStatement prep = conn.prepareStatement("DROP TABLE IF EXISTS users");
      prep.execute();
      prep = conn.prepareStatement(
              "CREATE TABLE IF NOT EXISTS users("
                      + "name TEXT, "
                      + "email TEXT PRIMARY KEY,"
                      + "ratedRecipes TEXT,"
                      + "ratedIngredients TEXT,"
                      + "inventory);");
      prep.execute();
      prep.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Adds the given user to database.
   * @param name name of user to add
   * @param email email of user to add
   */
  public static void addUserToDatabase(String name, String email) {
    try {
      if (conn != null) {
        PreparedStatement prep;
        prep = conn.prepareStatement(
                "INSERT INTO users VALUES (?,?,?,?,?);");
        prep.setString(1, name);
        prep.setString(2, email);
        prep.setString(3, "");
        prep.setString(4, "");
        prep.setString(5, "");
        prep.executeUpdate();
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Gets the name of a given user.
   * @param email email to query for name for
   * @return name corresponding to email of given user
   */
  public static String getName(String email) throws SQLException {
    if (conn != null) {
      PreparedStatement prep;
      prep = conn.prepareStatement(
              "SELECT name FROM users WHERE email = ?;");
      prep.setString(1, email);
      ResultSet rs = prep.executeQuery();
      String name = "";
      while (rs.next()) {
        name = rs.getString(1);
      }
      rs.close();
      prep.close();
      return name;
    }
    return "";
  }

  /**
   * Adds an ingredient to user's inventory.
   * @param user username
   * @param ingredient to add
   */
  public static void addUserIngredient(String user, String ingredient) {
    try {
      String currentInventory = getUserInventory(user);
      //this logic check if inventory already contains given ingredient
      String lastValue = "";
      String firstValue = "";
      if (currentInventory.lastIndexOf(",") != -1) {
        // if ingredient is at end
        lastValue = currentInventory.substring(currentInventory.lastIndexOf(",") + 1);
      }
      if (currentInventory.indexOf(",") != -1) {
        // if ingredient is at beginning
        firstValue = currentInventory.substring(0, currentInventory.indexOf(","));
      } else if (currentInventory.equals(ingredient)) {
        return;
      }
      if (!(currentInventory.contains("," + ingredient + ","))
              && !lastValue.equals(ingredient)
              && !firstValue.equals(ingredient)) {
        if (currentInventory.length() != 0) {
          currentInventory = currentInventory + ("," + ingredient);
        } else {
          currentInventory = ingredient;
        }
        /* currentInventory now represents what we want our inventory to be
        replaced with */
        if (conn != null) {
          PreparedStatement prep;
          prep = conn.prepareStatement(
                  "UPDATE users SET inventory = ? WHERE email = ?;");
          prep.setString(1, currentInventory);
          prep.setString(2, user);
          prep.execute();
          prep.close();
        }
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Removes an ingredient from the user's inventory if it exists.
   * @param user from which we will remove the inventory
   * @param ingredient to remove from the inventory
   */
  public static void removeUserIngredient(String user, String ingredient) {
    try {
      String currentInventory = getUserInventory(user);
      //if ingredient is not in inventory
      if (!currentInventory.contains(ingredient)) {
        return;
      }
      // if ingredient is in middle of inventory
      if (!currentInventory.contains(",")) {
        currentInventory = "";
      } else if (currentInventory.contains("," + ingredient + ",")) {
        currentInventory = currentInventory.replace("," + ingredient + ",", ",");
      } else {
        // ingredient is last ingredient in inventory
        String lastValue = currentInventory.substring(currentInventory.lastIndexOf(",") + 1);
        // ingredient is first ingredient in inventory
        String firstValue = currentInventory.substring(0, currentInventory.indexOf(","));
        if (lastValue.equals(ingredient)) {
          currentInventory = currentInventory.substring(0, currentInventory.lastIndexOf(","));
        } else if (firstValue.equals(ingredient)) {
          currentInventory = currentInventory.substring(currentInventory.indexOf(",") + 1);
        }
      }
      //now currentInventory = what we want user's inventory to be
      if (conn != null) {
        PreparedStatement prep;
        prep = conn.prepareStatement(
                "UPDATE users SET inventory = ? WHERE email = ?;");
        prep.setString(1, currentInventory);
        prep.setString(2, user);
        prep.execute();
        prep.close();
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Adds an ingredient rating to a user's list of rated ingredients.
   * @param user to add the ingredient rating to
   * @param ingredient to rate
   * @param rating to give to ingredient
   */
  public static void addUserIngredientRating(User user,
                                             String ingredient, Double rating) {
    try {
      String currentRatings = getUserIngredientRatings(user.getName());

      //if ingredient is already rated, we want to replace the rating
      if (currentRatings.contains(ingredient)) {

        //if ingredient is already in rating, and it is anywhere but the first item
        currentRatings = currentRatings.replace("," + ingredient + ":"
                + user.getIngredientRatings().get(ingredient), "," + ingredient + ":" + rating);

        //if ingredient is first item
        if (currentRatings.contains(",")) {
          String firstEntry = currentRatings.substring(0, currentRatings.indexOf(","));
          String firstIngredient = firstEntry.substring(0, firstEntry.indexOf(":"));
          if (firstIngredient.equals(ingredient)) {
            //remove first entry and add new first entry on
            currentRatings = ingredient + ":" + rating + ","
                    + currentRatings.substring(currentRatings.indexOf(",") + 1);
          }
        } else if (currentRatings.contains(ingredient)
                && currentRatings.substring(0, currentRatings.indexOf(":")).equals(ingredient)) {
          currentRatings = ingredient + ":" + rating;
        }
      } else {
        if (currentRatings.length() == 0) {
          currentRatings = ingredient + ":" + rating;
        } else {
          currentRatings = currentRatings + "," + ingredient + ":" + rating;
        }
      }
      if (conn != null) {
        PreparedStatement prep;
        prep = conn.prepareStatement(
                "UPDATE users SET ratedIngredients = ? WHERE email = ?;");
        prep.setString(1, currentRatings);
        prep.setString(2, user.getName());
        prep.execute();
        prep.close();
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Gets the user rated ingredients.
   * @param user to get ingredients from
   * @return comma separated list in form of ingredient1:rating1,ingredient2:rating2
   */
  public static String getUserIngredientRatings(String user) {
    try {
      if (conn != null) {
        PreparedStatement prep;
        prep = conn.prepareStatement(
                "SELECT ratedIngredients FROM users WHERE email = ?;");
        prep.setString(1, user);
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
          String ratings = rs.getString(1);
          return ratings;
        }
      }
    } catch (Exception e) {
      System.out.println("SQL ERROR");
    }
    return "";
  }

  /**
   * Adds a recipe rating to a user's list of rated recipes.
   * @param user to add the rating to
   * @param recipe to rate
   * @param rating rating to rate recipe with
   */
  public static void addUserRecipeRating(User user,
                                         String recipe, Double rating) {
    try {
      recipe = recipe.toLowerCase();
      String currentRatings = getUserRecipeRatings(user.getName());

      //if ingredient is already rated, we want to replace the rating
      if (currentRatings.contains(recipe)) {

        //if ingredient is already in rating, and it is anywhere but the first item
        currentRatings = currentRatings.replace("," + recipe + ":"
                + user.getRecipeRatings().get(recipe), "," + recipe + ":" + rating);

        //if ingredient is first item
        if (currentRatings.contains(",")) {
          String firstEntry = currentRatings.substring(0, currentRatings.indexOf(","));
          String firstIngredient = firstEntry.substring(0, firstEntry.indexOf(":"));
          if (firstIngredient.equals(recipe)) {
            //remove first entry and add new first entry on
            currentRatings = recipe + ":" + rating + ","
                    + currentRatings.substring(currentRatings.indexOf(",") + 1);
          }
        } else if (currentRatings.contains(recipe) //if no commas, means only one element
                && currentRatings.substring(0, currentRatings.indexOf(":")).equals(recipe)) {
          currentRatings = recipe + ":" + rating;
        }

      } else {
        if (currentRatings.length() == 0) {
          currentRatings = recipe + ":" + rating;
        } else {
          currentRatings = currentRatings + "," + recipe + ":" + rating;
        }
      }
      if (conn != null) {
        PreparedStatement prep;
        prep = conn.prepareStatement(
                "UPDATE users SET ratedRecipes = ? WHERE email = ?;");
        prep.setString(1, currentRatings);
        prep.setString(2, user.getName());
        prep.execute();
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Gets the user rated recipes.
   * @param user from which to get recipes from
   * @return comma separated list in form of recipe1:rating1,recipe2:rating2
   */
  public static String getUserRecipeRatings(String user) {
    try {
      if (conn != null) {
        PreparedStatement prep;
        prep = conn.prepareStatement(
                "SELECT ratedRecipes FROM users WHERE email = ?;");
        prep.setString(1, user);
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
          String ratings = rs.getString(1);
          return ratings;
        }
      }
    } catch (Exception e) {
      System.out.println("SQL ERROR");
    }
    return "";
  }

  /**
   * Gets user's inventory (everything in their fridge).
   * @param user to get inventory from
   * @return comma separated list of ingredeints
   */
  public static String getUserInventory(String user) {
    try {
      if (conn != null) {
        PreparedStatement prep;
        prep = conn.prepareStatement(
                "SELECT inventory FROM users WHERE email = ?;");
        prep.setString(1, user);
        ResultSet rs = prep.executeQuery();
        String inventory = "";
        while (rs.next()) {
          inventory = rs.getString(1);
        }
        rs.close();
        prep.close();
        return inventory;
      }
      return "";
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return "";
    }
  }

  /**
   * Deletes a user by email from database.
   * @param email used to query user
   * @throws SQLException thrown if user doesn't exit/connection issue.
   */
  public static void deleteUser(String email) throws SQLException {
    if (conn != null) {
      PreparedStatement prep;
      prep = conn.prepareStatement(
              "DELETE FROM users WHERE email = ?;");
      prep.setString(1, email);
      prep.execute();
      prep.close();
    }
  }

  /**
   * Calls getUserRecipeRatings, and converts to a map.
   * @param username to query on
   * @return map where each recipe name is a string, and each double
   * is a rating
   */
  public static HashMap<String, Double> userRecipeRatingsToMapHelper(String username) {
    String result = getUserRecipeRatings(username);
    HashMap<String, Double> ratingMap = new HashMap<>();
    String[] recipes = result.trim().split("\\s*,\\s*");
    for (String rec : recipes) {
      String[] tuple = rec.split(":");
      if (tuple.length != 2) {
        continue;
      }
      ratingMap.put(tuple[0], Double.parseDouble(tuple[1]));
    }
    return ratingMap;
  }

  /**
   * Calls getUserIngredientRatings, and converts to a map.
   * @param username to query on
   * @return map where each ingredient name is a string, and each double
   * is a rating
   */
  public static HashMap<String, Double> userIngredientRatingsToMapHelper(String username) {
    try {
      String result = getUserIngredientRatings(username);
      HashMap<String, Double> ratingMap = new HashMap<>();
      String[] ingredients = result.trim().split("\\s*,\\s*");
      for (String ing : ingredients) {
        String[] tuple = ing.split(":");
        if (tuple.length != 2) {
          continue;
        }
        ratingMap.put(tuple[0], Double.parseDouble(tuple[1]));
      }
      return ratingMap;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

}
