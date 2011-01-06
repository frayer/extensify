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
        /*
         * Create a new argument list with the "expressionContext" variable as the
         * first argument.
         */
        List<Object> arguments = new ArrayList<Object>(args.length + 1);
        arguments.addAll(Arrays.asList((args)));
        arguments.add(0, expressionContext);

        return closure.call(arguments);
    }

}