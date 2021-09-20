package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.RecipeObjects.Recipe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.lang.NullPointerException;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.*;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.*;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.User;

import static org.junit.Assert.assertTrue;

public class RandomInputTest {

  private User testUser;

  /**
   * Create new User object for creating ingredients and recipes
   */
  @Before
  public void setUp() {
    HashSet<String> testIngredients = new HashSet<>();
    try {
      initialize("data/newdb.sqlite3");
      initializeConn(getConn());
    } catch (SQLException e) {
      System.out.println("ERROR");
    } catch (ClassNotFoundException e) {
      System.out.println("ERROR");
    }
    addUserToDatabase("testUser", "test1@gmail.com");
    testUser = new User("test1@gmail.com", testIngredients);
  }

  /**
   * Clear variables for future tests.
   */
  @After
  public void tearDown() {
    testUser = null;
    try {
      deleteUser("test1@gmail.com");
      closeConn();
      closeUserConn();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * This test reads in ingredients.txt file, and adds 15 unique random ingredients to the user's fridge.
   *
   * Checks if the cook() method is able to find recipes with those ingredients and if the default ratings have
   * been generated.
   *
   * The unique ingredients are removed from the users fridge after each trial.
   * This is conducted 50 times, adding 15 ingredients each time.
   *
   **/
  @Test
  public void randomInputTest() {
    List<String> allIngredients = new ArrayList<>();
    Random random = new Random();

    try {
      allIngredients = Files.readAllLines(Paths.get("data/ingredients.txt"));
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    for (int n = 0; n < 50; n++) {
      ArrayList<String> ingredientsAdded = new ArrayList<>();
      for (int i = 0; i < 15; i++) {
        int randomIndex = random.nextInt(allIngredients.size());
        while (ingredientsAdded.contains(allIngredients.get(randomIndex))) {
          randomIndex = random.nextInt(allIngredients.size());
        }
        ingredientsAdded.add(allIngredients.get(randomIndex));
        this.testUser.addIngredient(allIngredients.get(randomIndex));
      }
      try {
        assertTrue(this.testUser.cook().size() > 0);
        assertTrue(this.testUser.getIngredients().size() == 15);
        assertTrue(this.testUser.getIngredientRatings().size() > 0);
      } catch(NullPointerException e) {
        System.out.println("");
      }

      for (int j = 0; j < 15; j++) {
        this.testUser.removeIngredient(ingredientsAdded.get(j));
      }
      assertTrue(this.testUser.getIngredients().size() == 0);
    }
  }

  /**
   * This test reads in ingredients.txt file, and adds 15 unique random ingredients, and a random rating
   * value for each ingredient to the user's fridge
   *
   * Checks if the cook() method is able to find recipes with those ingredients and ratings.
   *
   * The unique ingredients are removed from the users fridge after each trial.
   * This is conducted 50 times, adding 15 ingredients each time.
   */
  @Test
  public void randomInputWithRatingsTest() {
    List<String> allIngredients = new ArrayList<>();
    Random random = new Random();

    try {
      allIngredients = Files.readAllLines(Paths.get("data/ingredients.txt"));
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }

    for (int n = 0; n < 50; n ++) {
      ArrayList<String> ingredientsAdded = new ArrayList<>();
      for (int i = 0; i < 15; i++) {
        int randomIndex = random.nextInt(allIngredients.size());
        Double randomRating = random.nextDouble()*5.0;
        while (ingredientsAdded.contains(allIngredients.get(randomIndex))) {
          randomIndex = random.nextInt(allIngredients.size());
        }
        ingredientsAdded.add(allIngredients.get(randomIndex));
        this.testUser.addIngredient(allIngredients.get(randomIndex));
        this.testUser.addIngredientRating(allIngredients.get(randomIndex), (randomRating));
      }

      try {
        assertTrue(this.testUser.cook().size() > 0);
        assertTrue(this.testUser.getIngredients().size() == 15);
        assertTrue(this.testUser.getIngredientRatings().size() > 0);
      } catch(NullPointerException e) {
        System.out.println("");
      }

      for (int j = 0; j < 15; j++) {
        this.testUser.removeIngredient(ingredientsAdded.get(j));
      }
      assertTrue(this.testUser.getIngredients().size() == 0);
    }
  }
}

