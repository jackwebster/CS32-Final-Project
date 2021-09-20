package edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.User;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public final class Database {
  private Database() {
  }

  private static Connection conn;

  public static Connection getConn() {
    return conn;
  }

  public static void closeConn() throws SQLException {
    if (conn != null) {
      conn.close();
      conn = null;
    }
  }
  /**
   * Initializes connection to database.
   *
   * @param filename From which to retrieve data
   * @throws ClassNotFoundException thrown if org.sqlite.JDBC is not found
   * @throws SQLException           when querying and encounters an unexptected error
   */
  public static void initialize(String filename) throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");
    String urlToDB = "jdbc:sqlite:" + filename;
    conn = DriverManager.getConnection(urlToDB);
    // these two lines tell the database to enforce foreign
    // keys during operations
    Statement stat = conn.createStatement();
    stat.executeUpdate("PRAGMA foreign_keys=ON;");
  }


  /**
   * Used in preprocess, creates an ingredients database.
   * @throws SQLException
   */
  public static void createIngredientDatabase() throws SQLException {
    try {
      PreparedStatement prep = conn.prepareStatement("DROP TABLE IF EXISTS ingredientMap");
      prep.execute();
      prep = conn.prepareStatement(
              "CREATE TABLE IF NOT EXISTS ingredientMap("
                      + "ingredient TEXT PRIMARY KEY, "
                      + "recipes TEXT);");
      prep.execute();
      prep.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Used in preprocess, creates a recipes database.
   * @throws SQLException
   */
  public static void createRecipeDatabase() throws SQLException {
    try {
      PreparedStatement prep = conn.prepareStatement("DROP TABLE IF EXISTS recipes");
      prep.execute();
      prep = conn.prepareStatement(
              "CREATE TABLE IF NOT EXISTS recipes("
                      + "title TEXT PRIMARY KEY, "
                      + "description TEXT, "
                      + "ingredients TEXT, "
                      + "ingredients_detailed TEXT, "
                      + "chef TEXT, "
                      + "instructions TEXT, "
                      + "cooktime TEXT, "
                      + "preptime TEXT, "
                      + "photo TEXT, "
                      + "serves TEXT, "
                      + "url TEXT);");
      prep.execute();
      prep.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Used in preprocess, adds an ingredients to the ingredient map database.
   * @param ingredient to add to ingredientMap database.
   * @param recipes with the above ingredient in them.
   */
  public static void addIngredient(String ingredient, String recipes) {
    try {
      if (conn != null) {
        PreparedStatement prep = conn.prepareStatement(
                "INSERT INTO ingredientMap "
                        + "VALUES (?, ?)");
        prep.setString(1, ingredient);
        prep.setString(2, recipes);
        prep.execute();
        prep.close();
      }
    } catch (Exception e) {
      if (e.getMessage().contains("constraint violation")) {
        return;
      } else {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Used in preprocess. Adds a given recipe to the recipe database.
   * @param params
   * @throws SQLException
   */
  public static void addToRecipeDatabase(ArrayList<String> params)
          throws SQLException {
    try {
      if (conn != null) {
        PreparedStatement prep = conn.prepareStatement(
                "INSERT INTO recipes "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        for (int i = 0; i < params.size(); i++) {
          prep.setString(i + 1, params.get(i));
        }
        prep.executeUpdate();
        prep.close();
      }
    } catch (Exception e) {
      if (e.getMessage().contains("constraint violation")) {
        return;
      } else {
        System.out.println(e.getMessage());
      }
    }
  }

  /**
   * Gets all recipes with a given ingredient.
   * @param ingredient to query on to find what recipes it is contained in.
   * @return comma separated list of recipes.
   */
  public static String getRecipesWithIngredient(String ingredient) {
    try {
      PreparedStatement prep = conn.prepareStatement("SELECT recipes FROM ingredientMap "
              + "WHERE ingredient IS ?");
      prep.setString(1, ingredient);
      ResultSet rs = prep.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      String recipes = rs.getString(1);
      rs.close();
      prep.close();
      return recipes;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  /**
   * Gets ingedients for a given recipe.
   * @param recipeName to query on
   * @return comma separated list of recipes
   */
  public static String getIngredientForRecipe(String recipeName) {
    try {
      PreparedStatement prep = conn.prepareStatement("SELECT ingredients FROM recipes "
              + "WHERE title IS ?");
      prep.setString(1, recipeName);
      ResultSet rs = prep.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      String ingredients = rs.getString(1);
      return ingredients;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

  /**
   * Converts a recipe name to a recipe object given what the user's rating of it is.
   * @param recipeName to query on
   * @param user used to find the user's rating of recipe
   * @return recipe object
   */
  public static Recipe getRecipeObject(String recipeName, User user) {
    try {
      PreparedStatement prep = conn.prepareStatement("SELECT * FROM recipes "
              + "WHERE title IS ?");
      prep.setString(1, recipeName);
      ResultSet rs = prep.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      final int numParams = 12;
      ArrayList<String> params = new ArrayList<>() {
        {
          for (int i = 1; i < numParams; i++) {
            if (rs.getString(i) != null) {
              add(rs.getString(i));
            } else {
              add("");
            }
          }
        }
      };
      rs.close();
      prep.close();
      return new Recipe(params, user);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }
}
