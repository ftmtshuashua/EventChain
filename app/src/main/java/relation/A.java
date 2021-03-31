package relation;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/3/31 15:41
 * </pre>
 */
public class A {

    public <T extends A> T get() {
        return (T) this;
    }

}
