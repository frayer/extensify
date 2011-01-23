package org.extensify.matchers;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.extensify.xmlunit.IgnoreBlankTextValuesDifferenceListener;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.xml.sax.SAXException;

import java.io.IOException;

public class XMLStringMatcher extends TypeSafeMatcher<String> {

    private String testXML;
    private Diff diff;
    private DifferenceListener differenceListener;

    public XMLStringMatcher(String testXML) {
        this.testXML = testXML;
    }

    @Override
    protected boolean matchesSafely(String controlXML) {
        try {
            diff = new Diff(controlXML, testXML);
            if (differenceListener != null) {
                diff.overrideDifferenceListener(differenceListener);
            }

            return diff.similar();
        } catch (SAXException e) {
            // TODO: Implement exception handler.
            e.printStackTrace();
        } catch (IOException e) {
            // TODO: Implement exception handler.
            e.printStackTrace();
        }

        return false;
    }

    public void describeTo(Description description) {
        description.appendText(diff.toString());
    }

    @Factory
    public static Matcher<String> equalToXMLString(String controlXML) {
        XMLStringMatcher xmlStringMatcher = new XMLStringMatcher(controlXML);
        return xmlStringMatcher;
    }

    @Factory
    public static Matcher<String> equalToXMLStringIgnoringWhitespace(String testXML) {
        XMLStringMatcher xmlStringMatcher = new XMLStringMatcher(testXML);
        xmlStringMatcher.setDifferenceListener(new IgnoreBlankTextValuesDifferenceListener());
        return xmlStringMatcher;
    }

    public void setDifferenceListener(DifferenceListener differenceListener) {
        this.differenceListener = differenceListener;
    }

}
