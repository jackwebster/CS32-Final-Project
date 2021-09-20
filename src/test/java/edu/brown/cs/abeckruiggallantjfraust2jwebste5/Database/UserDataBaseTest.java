package edu.brown.cs.abeckruiggallantjfraust2jwebste5.Database;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.App.User;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.Database.*;
import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.sql.SQLException;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static edu.brown.cs.abeckruiggallantjfraust2jwebste5.DatabaseHelpers.UserDatabase.getUserIngredientRatings;
import static org.junit.Assert.*;

/**
 * MethodTesting of Ingredient and Recipe Classes
 */
public class UserDataBaseTest {
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
    addUserToDatabase("testUser", "test@gmail.com");
    testUser = new User("test@gmail.com", testIngredients);
  }

  /**
   * Clear variables for future tests.
   */
  @After
  public void tearDown() {
    testUser = null;
    try {
      deleteUser("test@gmail.com");
      closeConn();
      closeUserConn();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  public void addUserAndGetName() {
    UserDatabase.addUserToDatabase("testUser2", "test2@gmail.com");
    try {
      assertTrue(getName("test2@gmail.com").equals("testUser2"));
      deleteUser("test2@gmail.com");
    } catch (SQLException e) {
      assertTrue(1==2);
    }
  }

  @Test
  public void addSameEmailTwice() {
    try {
      UserDatabase.addUserToDatabase("testUser2", "test2@gmail.com");
      UserDatabase.addUserToDatabase("testUser2", "test3@gmail.com");
      assertTrue(getName("test3@gmail.com").equals("testUser2"));
      deleteUser("test2@gmail.com");
      deleteUser("test3@gmail.com");
    } catch (SQLException e) {
      assertTrue(1==2);
    }
  }

  @Test
  public void addOneUserIngredientGetInventory() {
    addUserIngredient("test@gmail.com", "tomato");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato"));
  }

  @Test
  public void addMultipleUserIngredientsGetInventory() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "plain flour");
    addUserIngredient("test@gmail.com", "olive oil");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato,plain flour,olive oil"));
  }

  @Test
  public void addSameIngredientTwiceLengthOne() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "tomato");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato"));
  }

  @Test
  public void addSameIngredientTwiceInMiddle() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "plain flour");
    addUserIngredient("test@gmail.com", "olive oil");
    addUserIngredient("test@gmail.com", "plain flour");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato,plain flour,olive oil"));
  }

  @Test
  public void addSameIngredientTwiceInFront() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "plain flour");
    addUserIngredient("test@gmail.com", "olive oil");
    addUserIngredient("test@gmail.com", "tomato");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato,plain flour,olive oil"));
  }

  @Test
  public void addSameIngredientTwiceInBack() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "plain flour");
    addUserIngredient("test@gmail.com", "olive oil");
    addUserIngredient("test@gmail.com", "olive oil");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato,plain flour,olive oil"));
  }

  @Test
  public void removeIngredientBasicMiddle() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "plain flour");
    addUserIngredient("test@gmail.com", "olive oil");
    removeUserIngredient("test@gmail.com", "plain flour");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato,olive oil"));
  }

  @Test
  public void removeIngredientBasicInFront() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "plain flour");
    addUserIngredient("test@gmail.com", "olive oil");
    removeUserIngredient("test@gmail.com", "tomato");
    assertTrue(getUserInventory("test@gmail.com").equals("plain flour,olive oil"));
  }

  @Test
  public void removeIngredientBasicInBack() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "plain flour");
    addUserIngredient("test@gmail.com", "olive oil");
    removeUserIngredient("test@gmail.com", "olive oil");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato,plain flour"));
  }

  @Test
  public void removeIngredientOnlyIngredient() {
    addUserIngredient("test@gmail.com", "tomato");
    removeUserIngredient("test@gmail.com", "tomato");
    assertTrue(getUserInventory("test@gmail.com").equals(""));
  }

  @Test
  public void removeIngredientNonExistent() {
    addUserIngredient("test@gmail.com", "tomato");
    addUserIngredient("test@gmail.com", "plain flour");
    addUserIngredient("test@gmail.com", "olive oil");
    removeUserIngredient("test@gmail.com", "apple");
    assertTrue(getUserInventory("test@gmail.com").equals("tomato,plain flour,olive oil"));
  }

  @Test
  public void testAddUserIngredientRating() {
    addUserIngredientRating(testUser, "tomato", 2.4);
    addUserIngredientRating(testUser, "olive oil", .4);
    addUserIngredientRating(testUser, "plain flour", 4.0);
    assertTrue(getUserIngredientRatings("test@gmail.com").equals("tomato:2.4,olive oil:0.4,plain flour:4.0"));
  }

  @Test
  public void replaceMiddleUserIngredientRating() {
    testUser.addIngredientRating("tomato", 2.4);
    testUser.addIngredientRating( "olive oil", .4);
    testUser.addIngredientRating( "plain flour", 4.0);
    testUser.addIngredientRating( "olive oil", 2.4);
    assertTrue(getUserIngredientRatings("test@gmail.com").equals("tomato:2.4,olive oil:2.4,plain flour:4.0"));
  }

  @Test
  public void replaceEndUserIngredientRating() {
    testUser.addIngredientRating("tomato", 2.4);
    testUser.addIngredientRating( "olive oil", .4);
    testUser.addIngredientRating( "plain flour", 4.0);
    testUser.addIngredientRating( "plain flour", 2.4);
    assertTrue(getUserIngredientRatings("test@gmail.com").equals("tomato:2.4,olive oil:0.4,plain flour:2.4"));
  }

  @Test
  public void replaceFirstUserIngredientRating() {
    addUserIngredientRating(testUser, "tomato", 2.4);
    addUserIngredientRating(testUser, "olive oil", .4);
    addUserIngredientRating(testUser, "plain flour", 4.0);
    addUserIngredientRating(testUser, "tomato", 2.3);
    assertTrue(getUserIngredientRatings("test@gmail.com").equals("tomato:2.3,olive oil:0.4,plain flour:4.0"));
  }

  @Test
  public void testAddUserRecipeRating() {
    addUserRecipeRating(testUser, "3d biscuits", 2.4);
    addUserRecipeRating(testUser, "ten-minute pizza", .4);
    addUserRecipeRating(testUser, "lemon curd ice cream", 4.0);
    assertTrue(getUserRecipeRatings("test@gmail.com").equals("3d biscuits:2.4,ten-minute pizza:0.4,lemon curd ice cream:4.0"));
  }

  @Test
  public void replaceMiddleUserRecipeRating() {
    testUser.addRecipeRating("3d biscuits", 2.4);
    testUser.addRecipeRating( "ten-minute pizza", .4);
    testUser.addRecipeRating( "lemon curd ice cream", 4.0);
    testUser.addRecipeRating( "ten-minute pizza", 2.4);
    assertTrue(getUserRecipeRatings("test@gmail.com").equals("3d biscuits:2.4,ten-minute pizza:2.4,lemon curd ice cream:4.0"));
  }

  @Test
  public void replaceEndUserRecipeRating() {
    testUser.addRecipeRating("3d biscuits", 2.4);
    testUser.addRecipeRating( "ten-minute pizza", .4);
    testUser.addRecipeRating( "lemon curd ice cream", 4.0);
    testUser.addRecipeRating( "lemon curd ice cream", 2.4);
    assertTrue(getUserRecipeRatings("test@gmail.com").equals("3d biscuits:2.4,ten-minute pizza:0.4,lemon curd ice cream:2.4"));
  }

  @Test
  public void replaceFirstUserRecipeRating() {
    addUserRecipeRating(testUser, "3d biscuits", 2.4);
    addUserRecipeRating(testUser, "ten-minute pizza", .4);
    addUserRecipeRating(testUser, "lemon curd ice cream", 4.0);
    addUserRecipeRating(testUser, "3d biscuits", 2.3);
    assertTrue(getUserRecipeRatings("test@gmail.com").equals("3d biscuits:2.3,ten-minute pizza:0.4,lemon curd ice cream:4.0"));
  }

  @Test
  public void testAddUserRecipeRatingMap() {
    HashMap<String, Double> ratings = new HashMap<>();
    ratings.put("3d biscuits", 2.4);
    ratings.put("ten-minute pizza", .4);
    ratings.put("lemon curd ice cream", 4.0);
    addUserRecipeRating(testUser, "3d biscuits", 2.4);
    addUserRecipeRating(testUser, "ten-minute pizza", .4);
    addUserRecipeRating(testUser, "lemon curd ice cream", 4.0);
    HashMap<String, Double> generatedRatings = userRecipeRatingsToMapHelper("test@gmail.com");
    assertTrue(generatedRatings.keySet().equals(ratings.keySet()));
    assertTrue(new ArrayList<>( ratings.values() ).equals(new ArrayList<>( generatedRatings.values() )) );
  }

  @Test
  public void testAddUserIngredientRatingMap() {
    HashMap<String, Double> ratings = new HashMap<>();
    ratings.put("tomato", 2.4);
    ratings.put("plain flour", .4);
    ratings.put("olive oil", 4.0);
    addUserIngredientRating(testUser, "tomato", 2.4);
    addUserIngredientRating(testUser, "plain flour", .4);
    addUserIngredientRating(testUser, "olive oil", 4.0);
    HashMap<String, Double> generatedRatings = userIngredientRatingsToMapHelper("test@gmail.com");
    assertTrue(generatedRatings.keySet().equals(ratings.keySet()));
    assertTrue(new ArrayList<>( ratings.values() ).equals(new ArrayList<>( generatedRatings.values() )) );
  }

  /**
   * Tests Methods of UserDatabase Class
   */
  @Test
  public void generalDatabaseTest() {
    String result = "";

    addUserIngredient("test@gmail.com", "milk");
    addUserIngredient("test@gmail.com", "flour");
    addUserIngredient("test@gmail.com", "egg");
    addUserIngredient("test@gmail.com", "lettuce");
    result = getUserInventory("test@gmail.com");


    assertTrue(result.equals("milk,flour,egg,lettuce"));

    HashMap<String, Double> recipeRatings;
    HashMap<String, Double> ingredientRatings;

    recipeRatings = userRecipeRatingsToMapHelper("test@gmail.com");
    ingredientRatings = userIngredientRatingsToMapHelper("test@gmail.com");

    assertTrue(recipeRatings.size() == 0);
    assertTrue(ingredientRatings.size() == 0);
  }
}