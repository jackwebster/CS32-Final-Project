package edu.brown.cs.abeckruiggallantjfraust2jwebste5.Autocorrect;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Class to create autocorrect suggestions given input.
 *
 */
public class Autocorrector {

  private Trie trie;
  private boolean prefix;
  private boolean whitespace;
  private List<String> wordList;
  private int led;

  public Autocorrector(String files, boolean prefixIn, boolean whitespaceIn, int ledIn) {
    trie = new Trie();
    trie.insertAll(parseCorpus(files));
    wordList = parseCorpus(files);
    prefix = prefixIn;
    whitespace = whitespaceIn;
    led = ledIn;
  }

  /**
   * Takes in a string with file names and reads and parses the corpus.
   *
   * @param files: String with filenames separated by commas.
   * @return List of strings parsed from files.
   */
  private static List<String> parseCorpus(String files) {
    List<String> fileNames = new ArrayList<String>(Arrays.asList(files.split(",")));
    List<String> words = new ArrayList<String>();
    for (String file : fileNames) {
      Scanner in = null;
      try {
        in = new Scanner(new File(file));
      } catch (FileNotFoundException e) {
        // If file does not exist, go to next file.
        continue;
      }
      while (in.hasNextLine()) {
        String nextLine = in.nextLine();
        words.add(nextLine);
      }
      in.close();
    }
    return words;
  }

  /**
   * Given phrase input by user, gives autocorrect suggestions.
   * @param phrase String input by user
   * @return Set of strings representing suggestions
   */
  public Set<String> suggest(String phrase) {
    // Regex to make input consistent.
    String query = phrase.toLowerCase().replaceAll("[^a-z ]", " ").trim().replaceAll(" +", " ");
    List<String> words = new ArrayList<String>(Arrays.asList(query.split(" ")));

    // Search trie for each type of flag set and add resulting words.
    Set<String> trieOutput = new TreeSet<String>();
    if (words.size() > 0) {
      String acWord = words.get(words.size() - 1);
      if (prefix) {
        trieOutput.addAll(trie.findAllWithPrefix(acWord, acWord));
      }
      if (whitespace) {
        trieOutput.addAll(trie.whiteSpace(acWord));
      }
      if (led > 0) {
        trieOutput.addAll(trie.findLedWithinRoot(acWord, led));
      }
    }
    List<String> trieOutputAsList = new ArrayList<String>(trieOutput);
    Set<String> suggestions = new TreeSet<String>();
    for (int i = 0; i < Math.min(5, trieOutputAsList.size()); i++) {
      suggestions.add(trieOutputAsList.get(i));
    }
    return suggestions;
  }

  public List<String> getWordList() {
    return wordList;
  }
}

