import com.acap.ec.Event;

import org.w3c.dom.Text;

import event.PrintEvent;
import event.TypeEvent;
import event.乘法;
import event.减法;
import event.加法;

/**
 * <pre>
 * Tip:
 *
 *
 * Created by ACap on 2021/3/30 10:22
 * </pre>
 */
public class DemoMain {

    public static final void main(String[] args) {
        //构建事件链条
        new PrintEvent<Integer>(">> 计算 (x+5+5)*2-3")    //打印
                .chain(new 加法(5))    //执行加法运算，等同于 1+5
                .chain(new 乘法(2))  // ((1+5)+5)*2
                .chain(new 减法(3))   // ((1+5)+5)*2-3
                .print("结果")//结果 = 21
                .start(1); //入参方法，这里的计算的原始值传 1

        /*  ---------------- 控制台打印信息 ----------------
            >> 执行数学计算
            结果 = 21
         */
        System.out.println("--------------------------------------------------------------");

//        ApplyEvent<Integer, Integer> 结果 =
//                Event.create(new PrintEvent<Integer>(">> 计算 (x+1)*2+(x+1)*3")).apply(p -> p.get(0))
////                        .chain(new ThreadEvent<>())
//                        .chain(new 加法(1))
//                        .merge(new 乘法(2), new 乘法(3),new TypeEvent<Integer,Integer>())
//                        .apply(p -> (int)p.get(0) + (int)p.get(1))
////                        .addOnChainListener(new OnChainLogListener<>(""))
//                        .print("结果");
//
//        结果.start(1); // 结果=10
//        System.out.println("--------------------------------------------------------------");
//        结果.start(2); // 结果=15


        Event.create(new PrintEvent<Integer>(">> 计算 (x+1)*2+(x+1)*3"))
                .apply(p -> p[0])
                .merge(new 乘法(2), new 乘法(3), new TypeEvent<Integer, TypeEvent>())
                .apply(P -> (Integer) P[0])
                .oneOf(new 乘法(2), new 乘法(3), new TypeEvent<Integer, Text>())

                .start();


    }


}
