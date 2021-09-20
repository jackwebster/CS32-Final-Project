package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;


import static org.junit.Assert.assertTrue;
import edu.brown.cs.abeckruiggallantjfraust2jwebste5.Autocorrect.Autocorrector;
import java.util.Set;
import org.junit.Test;

/**
 * Testing for autocorrect functionality and edge cases
 */
public class AutocorrectTest {

  @Test
  public void emptyInputTest() {
    Autocorrector ac = new Autocorrector("data/ingredients.txt", true, true, 1);
    Set<String> results = ac.suggest("");
    assertTrue(results.size() == 5);
  }

  @Test
  public void basicInputTest() {
    Autocorrector ac = new Autocorrector("data/ingredients.txt", true, true, 1);
    Set<String> results = ac.suggest("broc");
    assertTrue(results.contains("broccoli"));
    assertTrue(!results.contains("banana"));
  }

  @Test
  public void twoWordTest() {
    Autocorrector ac = new Autocorrector("data/ingredients.txt", true, true, 1);
    Set<String> results = ac.suggest("lemon ");
    assertTrue(results.contains("lemon curd"));
    assertTrue(results.contains("lemon juice"));
  }
}