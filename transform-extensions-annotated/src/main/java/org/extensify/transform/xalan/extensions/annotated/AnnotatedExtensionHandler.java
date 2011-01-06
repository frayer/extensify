package org.extensify.transform.xalan.extensions.annotated;

import org.extensify.transform.xalan.extensions.BasicExtensionHandler;
import org.extensify.transform.xalan.extensions.*;
import org.extensify.transform.xalan.extensions.CallableExtensionFunction;
import org.extensify.transform.xalan.extensions.MethodCallableExtensionElement;
import org.extensify.transform.xalan.extensions.CallableExtensionElement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotatedExtensionHandler<T> extends BasicExtensionHandler {

    private T annotatedHandler;
    private Class<?> annotatedHandlerClass = null;
    private List<String> availableFunctions = null;
    private List<String> availableElements = null;

    public AnnotatedExtensionHandler(T annotatedHandler) {
        super(null, "javaclass");

        this.annotatedHandler = annotatedHandler;
        this.annotatedHandlerClass = annotatedHandler.getClass();

        if (annotatedHandlerClass.isAnnotationPresent(XSLTExtension.class)) {
            XSLTExtension xsltExtension = annotatedHandlerClass.getAnnotation(XSLTExtension.class);
            this.m_namespaceUri = xsltExtension.namespace();
        }

        availableFunctions = new ArrayList<String>();
        availableElements = new ArrayList<String>();

        Method[] methods = annotatedHandlerClass.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(XSLTExtensionFunction.class)) {
                XSLTExtensionFunction extensionFunction = method.getAnnotation(XSLTExtensionFunction.class);
                if (extensionFunction.value() != null) {
                    availableFunctions.add(extensionFunction.value());
                }
            } else if (method.isAnnotationPresent(XSLTExtensionElement.class)) {
                XSLTExtensionElement extensionElement = method.getAnnotation(XSLTExtensionElement.class);
                if (extensionElement.value() != null) {
                    availableElements.add(extensionElement.value());
                }
            }
        }
    }

    @Override
    public boolean isFunctionAvailable(String functionName) {
        return availableFunctions.contains(functionName);
    }

    @Override
    public boolean isElementAvailable(String elementName) {
        return availableElements.contains(elementName);
    }

    public Method findExtensionElement(String elementName) {
        Method result = null;
        Method[] methods = annotatedHandlerClass.getMethods();
        for (Method method : methods) {
            XSLTExtensionElement extensionElement = method.getAnnotation(XSLTExtensionElement.class);
            if ((extensionElement != null) && extensionElement.value().equals(elementName)) {
                result = method;
            }
        }
        return result;
    }

    public Method findExtensionFunction(String functionName) {
        Method result = null;
        Method[] methods = annotatedHandlerClass.getMethods();
        for (Method method : methods) {
            XSLTExtensionFunction extensionFunction = method.getAnnotation(XSLTExtensionFunction.class);
            if ((extensionFunction != null) && extensionFunction.value().equals(functionName)) {
                result = method;
            }
        }
        return result;
    }

    @Override
    protected CallableExtensionElement getCallableExtensionElement(String elementName) {
        Method method = findExtensionElement(elementName);
        return new MethodCallableExtensionElement(method, annotatedHandler);
    }

    @Override
    protected CallableExtensionFunction getCallableExtensionFunction(final String functionName) {
        Method method = findExtensionFunction(functionName);
        return new MethodCallableExtensionFunction<T>(method, annotatedHandler);
    }
}
