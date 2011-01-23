package org.extensify.xmlunit;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.NodeDetail;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IgnoreBlankTextValuesDifferenceListenerTest {

    private IgnoreBlankTextValuesDifferenceListener listener = null;
    private Difference difference;
    private NodeDetail controlNodeDetail;
    private NodeDetail testNodeDetail;
    private Node controlNode;
    private Node testNode;

    @Before
    public void setUp() {
        listener = new IgnoreBlankTextValuesDifferenceListener();

        difference = mock(Difference.class);
        controlNodeDetail = mock(NodeDetail.class);
        testNodeDetail = mock(NodeDetail.class);
        controlNode = mock(Node.class);
        testNode = mock(Node.class);

        when(controlNodeDetail.getNode()).thenReturn(controlNode);
        when(testNodeDetail.getNode()).thenReturn(testNode);

        when(difference.getControlNodeDetail()).thenReturn(controlNodeDetail);
        when(difference.getTestNodeDetail()).thenReturn(testNodeDetail);
    }

    @Test
    public void testAcceptsNonTextNodeDifferences() {
        when(controlNode.getNodeType()).thenReturn(Node.ELEMENT_NODE);
        when(testNode.getNodeType()).thenReturn(Node.ELEMENT_NODE);

        int differenceFound = listener.differenceFound(difference);
        assertEquals("Expecting RETURN_ACCEPT_DIFFERENCE.", differenceFound, DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
    }

    @Test
    public void testBlankTextNodesAreConsideredSimilar_newline() {
        when(controlNode.getNodeType()).thenReturn(Node.TEXT_NODE);
        when(testNode.getNodeType()).thenReturn(Node.TEXT_NODE);

        when(controlNode.getTextContent()).thenReturn("\n \n \n");
        when(testNode.getTextContent()).thenReturn("    \n    \n    \n    \n");

        int differenceFound = listener.differenceFound(difference);
        assertEquals("Expecting RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR.", differenceFound, DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
    }

    @Test
    public void testBlankTextNodesAreConsideredSimilar_tabs() {
        when(controlNode.getNodeType()).thenReturn(Node.TEXT_NODE);
        when(testNode.getNodeType()).thenReturn(Node.TEXT_NODE);

        when(controlNode.getTextContent()).thenReturn(" \t \t \t \t \t");
        when(testNode.getTextContent()).thenReturn("    \t    \t    \t    \t    \t    \t    \t");

        int differenceFound = listener.differenceFound(difference);
        assertEquals("Expecting RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR.", differenceFound, DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
    }

    @Test
    public void testBlankTextNodesAreConsideredSimilar_mix() {
        when(controlNode.getNodeType()).thenReturn(Node.TEXT_NODE);
        when(testNode.getNodeType()).thenReturn(Node.TEXT_NODE);

        when(controlNode.getTextContent()).thenReturn(" \n \t \n \t");
        when(testNode.getTextContent()).thenReturn("    \n \t    \n \t    \n \t    \n \t");

        int differenceFound = listener.differenceFound(difference);
        assertEquals("Expecting RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR.", differenceFound, DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
    }

}
