package org.extensify.transform.xalan.extensions.annotated;

import org.apache.xalan.extensions.ExpressionContext;
import org.extensify.transform.xalan.extensions.CallableExtensionFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodCallableExtensionFunction<T> implements CallableExtensionFunction {

    private Method method = null;
    private T targetInstance = null;

    public MethodCallableExtensionFunction(Method method, T targetInstance) {
        this.method = method;
        this.targetInstance = targetInstance;
    }

    public Object call(ExpressionContext expressionContext, Object... args) {
        Object result = null;

        /*
         * Create a new argument list with the "expressionContext" variable as the
         * first argument.
         */
        List<Object> arguments = new ArrayList<Object>(args.length + 1);
        arguments.addAll(Arrays.asList((args)));
        arguments.add(0, expressionContext);

        try {
            result = method.invoke(targetInstance, arguments.toArray());
        } catch (IllegalAccessException e) {
            // TODO: handle exception
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return result;
    }
}
