package base;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by heaven7 on 2018/2/19.
 */
public class MethodTest {

    @Test
    public void methodTest() throws Exception {
        Method method = A.class.getMethod("test", String.class);
        String[] paramaterName = MethodUtil.getAllParamaterName(method);
        assertArrayEquals(paramaterName, new String[]{"name"});

        final Method method2 = A.class.getMethod("test2", String.class, int.class, int.class);
        String[] names = MethodUtil.getAllParamaterName(method2);
        System.out.println(Arrays.toString(names));

        final Method method3 = A.class.getMethod("test3", int.class, int.class);
        names = MethodUtil.getAllParamaterName(method3);
        System.out.println(Arrays.toString(names));
    }
}
