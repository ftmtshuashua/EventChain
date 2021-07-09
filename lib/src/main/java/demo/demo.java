package demo;

import com.acap.ec.listener.OnEventNextListener;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/7/8 15:58
 * </pre>
 */
public class demo {

    public static void main(String[] args) {


        Evt<String, Integer> A1 = new Evt();
        Evt<Integer, String> A2 = new Evt();
        Evt<Integer, Object> A3 = new Evt();
        Evt<Object, Integer> A4 = new Evt();


        A1.chain(A2.chain(A1).chain(A2).chain(A1))
                .addOnEventListener(new OnEventNextListener<String, Integer>() {
                    @Override
                    public void onNext(Integer result) {

                    }
                })
                .merge(A2, A2, A3)
                .addOnEventListener(new OnEventNextListener<String, Object[]>() {
                    @Override
                    public void onNext(Object[] result) {

                    }
                })
                .apply(it -> it[0])
                .chain(A4)
                .chain(A2)
                .start();


    }

}
