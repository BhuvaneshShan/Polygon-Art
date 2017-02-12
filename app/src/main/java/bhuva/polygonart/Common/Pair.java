package bhuva.polygonart.Common;

/**
 * Created by bhuva on 5/27/2016.
 *
 * Class to store pairs of valus of any type
 * The first and second parameters passed can be accessed as object.a and object.b
 */
public class Pair<One, Two> {
    public One a;
    public Two b;

    public Pair(One one, Two two) {
        this.a = one;
        this.b = two;
    }
    public Pair(Pair<One, Two> p){
        this.a = p.getA();
        this.b = p.getB();
    }
    public One getA() { return a; }
    public Two getB() { return b; }

    @Override
    public int hashCode() { return a.hashCode() ^ b.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairo = (Pair) o;
        return this.a.equals(pairo.getA()) &&
                this.b.equals(pairo.getB());
    }

}
