package org.extensify.transform.xalan.extensions;


import org.apache.xalan.extensions.ExpressionContext;

public interface CallableExtensionFunction {

    Object call(ExpressionContext expressionContext, Object... args);

}
