/**
 * From http://stackoverflow.com/questions/5303539/didnt-java-once-have-a-pair-class 
 */

package edu.upenn.cis.db.mefview.shared;

import java.io.Serializable;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true)
public class Pair<First extends Comparable<First>, Second extends Comparable<Second>> 
		implements Serializable, JsonTyped, IPair<First,Second> {
    private First first;
    private Second second;

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public void setSecond(Second second) {
        this.second = second;
    }

    @Override
    public First getFirst() {
        return first;
    }

    @Override
    public Second getSecond() {
        return second;
    }

    public void set(First first, Second second) {
        setFirst(first);
        setSecond(second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        if (second != null ? !second.equals(pair.second) : pair.second != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

	@Override
	public int compareTo(IPair<First, Second> o) {
		if (getFirst().equals(o.getFirst()))
			return getSecond().compareTo(o.getSecond());
		else
			return getFirst().compareTo(o.getFirst());
	}
}
