package edu.brown.cs.abeckruiggallantjfraust2jwebste5.App;

public final class ConstantHyperparameters {
  private ConstantHyperparameters() {
  }
  public static final int TOTAL_RATING = 5;
  public static final double DEFAULT_RATING = 2.5;
  public static final int NUM_RECOMMENDATIONS = 10;
  public static final double SCORE_WEIGHT = 0.3;
  public static final double SIMILARITY_WEIGHT = 1 - SCORE_WEIGHT;
}
