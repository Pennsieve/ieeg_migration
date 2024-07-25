/**
 * From http://stackoverflow.com/questions/5303539/didnt-java-once-have-a-pair-class 
 */

package edu.upenn.cis.db.mefview.shared;

public interface IPair<First, Second> extends Comparable<IPair<First,Second>> {
    public First getFirst();

    public Second getSecond();
}