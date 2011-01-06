package org.extensify.transform.xalan.extensions;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodCallableExtensionElement implements CallableExtensionElement {

    private Method method = null;
    private Object targetInstance = null;

    public MethodCallableExtensionElement(Method method, Object targetInstance) {
        this.method = method;
        this.targetInstance = targetInstance;
    }

    public Object call(ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet) {
        Object result = null;

        try {
            result = method.invoke(targetInstance, elemTemplateElement, transformer, stylesheet);
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
