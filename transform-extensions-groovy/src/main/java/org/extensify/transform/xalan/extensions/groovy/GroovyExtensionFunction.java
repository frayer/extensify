package org.extensify.transform.xalan.extensions.groovy;

import groovy.lang.Closure;
import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.extensions.MethodResolver;
import org.extensify.transform.xalan.extensions.CallableExtensionFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;

public class GroovyExtensionFunction implements CallableExtensionFunction {

    public static final Logger logger = LoggerFactory.getLogger(GroovyExtensionFunction.class);

    private Closure closure = null;

    public GroovyExtensionFunction(Closure closure) {
        this.closure = closure;
    }

    public Object call(ExpressionContext expressionContext, Object... args) {
        Class[] closureParamTypes = closure.getParameterTypes();
        Class[] paramTypes = null;

        /*
         * Build a Class array representing types which will be passed to the
         * Closure. Later this is used to convert Xalan types such as XString
         * and XNodeSet found in the "org.apache.xpath.objects"
         * package to equivalent Java types or DOM types which would be found
         * in the "org.w3c.dom" packages.
         *
         * If the first Closure parameter type is ExpressionContext, then the convention is to pass
         * this variable in to the closure as the first argument in addition to all the other
         * arguments passed to the XSL function.
         */
        if (closureParamTypes[0] == ExpressionContext.class) {
            paramTypes = new Class[args.length + 1];
            paramTypes[0] = ExpressionContext.class;
            System.arraycopy(closureParamTypes, 1, paramTypes, 1, args.length);
        } else {
            paramTypes = new Class[args.length];
            System.arraycopy(closureParamTypes, 0, paramTypes, 0, args.length);
        }

        Object[][] convertedParams = new Object[1][];

        try {
            /*
             * Call the Xalan MethodResolver class to resolve Xalan specific
             * types into their Java API equivalents.
             */
            MethodResolver.convertParams(args, convertedParams, paramTypes, expressionContext);
        } catch (TransformerException e) {
            String message = "An error occurred when trying converting XSLT parameters to their Java API equivalents.";
            logger.error(message, e);
            throw new InvalidClosureParametersException(message, e);
        }

        /*
         * The second dimension of the convertedParams two-dimensional array
         * contains the parameters passed to the extension function after
         * being coerced into their new types by the MethodResolver.convertParams()
         * call.
         */
        return closure.call(convertedParams[0]);
    }

}
