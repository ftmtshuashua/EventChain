package demo;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/8 15:57
 * </pre>
 */
class Evt<P, R> {


    public <R1, T extends Evt<? super R, R1>> Chin<P, R1> chain(T event) {
        return null;
    }
}
