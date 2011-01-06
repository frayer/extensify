package org.extensify.transform.xalan.extensions;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.transformer.TransformerImpl;

public interface CallableExtensionElement {

    Object call(ElemTemplateElement elemTemplateElement, TransformerImpl transformer, Stylesheet stylesheet);

}
