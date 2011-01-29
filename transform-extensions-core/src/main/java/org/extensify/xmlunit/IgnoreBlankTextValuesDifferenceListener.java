package org.extensify.xmlunit;

import org.apache.commons.lang.StringUtils;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.NodeDetail;
import org.w3c.dom.Node;

/**
 * An XMLUnit DifferenceListener which ignores Text Nodes which have blank values. Using this
 * DifferenceListener in your XMLUnit Diff test allows the following two XML examples to be
 * considered similar. Without this, the following two XML examples would be considered
 * different due to the whitespace before and after the <code>person</code>element.
 *
 * example 1:
 * <people>
 *      <person>buddy</person>
 * </people>
 *
 * example 2:
 * <people><person>buddy</person></people>
 */
public class IgnoreBlankTextValuesDifferenceListener implements DifferenceListener {

    public int differenceFound(Difference difference) {
        NodeDetail controlNodeDetail = difference.getControlNodeDetail();
        NodeDetail testNodeDetail = difference.getTestNodeDetail();
        Node controlNode = controlNodeDetail.getNode();
        Node testNode = testNodeDetail.getNode();

        if ((controlNode.getNodeType() == Node.TEXT_NODE) && (testNode.getNodeType() == Node.TEXT_NODE)) {
            String controlNodeText = controlNode.getTextContent();
            String testNodeText = testNode.getTextContent();

            if (StringUtils.isBlank(controlNodeText) && StringUtils.isBlank(testNodeText)) {
                return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
            }
        }

        return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
    }

    /**
     * Do nothing.
     *
     * @param node
     * @param node1
     */
    public void skippedComparison(Node node, Node node1) {
        // Do nothing
    }

}
