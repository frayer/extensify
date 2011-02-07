package org.extensify.transform.xalan.extensions.groovy;

import groovy.lang.Closure;
import org.apache.xalan.extensions.ExpressionContext;
import org.extensify.transform.xalan.extensions.CallableExtensionFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroovyExtensionFunction implements CallableExtensionFunction {

    private Closure closure = null;

    public GroovyExtensionFunction(Closure closure) {
        this.closure = closure;
    }

    public Object call(ExpressionContext expressionContext, Object... args) {
        Class[] paramTypes = closure.getParameterTypes();
        List<Object> arguments = new ArrayList<Object>(closure.getMaximumNumberOfParameters());;
        arguments.addAll(Arrays.asList(args));

        /*
         * If the first Closure parameter type is ExpressionContext, then the convention is to pass
         * this variable in to the closure as the first argument in addition to all the other
         * arguments passed to the XSL function.
         */
        if (paramTypes[0] == ExpressionContext.class) {
            arguments.add(0, expressionContext);
        }

        return closure.call(arguments.toArray());
    }

}
