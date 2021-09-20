package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

import edu.brown.cs.abeckruiggallantjfraust2jwebste5.DataPreprocess.NumIngredientsComparator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;
/**
 * Tests comparator that sorts recipes by number of ingredients they have
 */
public class NumIngredientsComparatorTest {
  NumIngredientsComparator comp;
  @Before
  public void setUp() {
    HashMap<String, Integer> numIngredientsMap = new HashMap<>();
    numIngredientsMap.put("tomato soup", 10);
    numIngredientsMap.put("lasagna", 20);
    numIngredientsMap.put("ice cream", 3);
    numIngredientsMap.put("steak", 1);
    comp = new NumIngredientsComparator(numIngredientsMap);
  }

  /**
   * Base case: Tests basic sorting using comparator
   */
  @Test
  public void testComparatorBasic() {
    ArrayList<String> recipes = new ArrayList<>();
    recipes.add("tomato soup");
    recipes.add("lasagna");
    recipes.add("ice cream");
    recipes.add("steak");
    recipes.sort(comp);
    assertTrue(recipes.get(0).equals("steak"));
    assertTrue(recipes.get(1).equals("ice cream"));
    assertTrue(recipes.get(2).equals("tomato soup"));
    assertTrue(recipes.get(3).equals("lasagna"));
  }

  /**
   * Edge case: Tests if all ingredients are not in ingredientMap
   */
  @Test
  public void testAllNotInIngredientMap() {
    ArrayList<String> recipes = new ArrayList<>();
    recipes.add("blueberry muffin");
    recipes.add("pasta");
    recipes.add("salad");
    recipes.add("green juice");
    recipes.sort(comp);
    assertTrue(recipes.get(0).equals("blueberry muffin"));
    assertTrue(recipes.get(1).equals("pasta"));
    assertTrue(recipes.get(2).equals("salad"));
    assertTrue(recipes.get(3).equals("green juice"));
  }

  /**
   * Edge case: Tests if some ingredients are not in ingredientMap
   */
  @Test
  public void testSomeNotInIngredientMap() {
    ArrayList<String> recipes = new ArrayList<>();
    recipes.add("blueberry muffin");
    recipes.add("pasta");
    recipes.add("tomato soup");
    recipes.add("steak");
    recipes.add("salad");
    recipes.add("green juice");
    recipes.sort(comp);
    assertTrue(recipes.get(0).equals("blueberry muffin"));
    assertTrue(recipes.get(1).equals("pasta"));
    assertTrue(recipes.get(2).equals("steak"));
    assertTrue(recipes.get(3).equals("tomato soup"));
    assertTrue(recipes.get(4).equals("salad"));
    assertTrue(recipes.get(5).equals("green juice"));
  }
}
