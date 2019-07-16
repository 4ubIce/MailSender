package MailController;

import gui.MailInterface;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;


public class MailController {

    private MailInterface            mailInterface                  = new MailInterface();
    private SimpleAttributeSet       attributesRed                  = new SimpleAttributeSet();
    private SimpleAttributeSet       attributesWhite                = new SimpleAttributeSet();

    private JTextPane                fromLine                       = mailInterface.getFromLine();
    private JTextPane                toLine                         = mailInterface.getToLine();
    private JTextPane                subjectLine                    = mailInterface.getSubjectLine();
    private JTextPane                bodyArea                       = mailInterface.getBodyArea();
    private JTextPane                footerArea                     = mailInterface.getFooterArea();

    private HTMLDocument             bodyStyledDocument             = (HTMLDocument )bodyArea.getStyledDocument();
    private StyledDocument           fromStyledDocument             = fromLine.getStyledDocument();
    private StyledDocument           toStyledDocument               = toLine.getStyledDocument();
    private StyledDocument           subjectStyledDocument          = subjectLine.getStyledDocument();
    private StyledDocument           footerStyledDocument           = footerArea.getStyledDocument();


    public MailController() {
        mailInterface.start();
        mailInterface.getSendButton().addActionListener(new SendButtonListener(this));
        this.attributesRed.addAttribute(StyleConstants.Background, Color.RED);
        this.attributesWhite.addAttribute(StyleConstants.Background, Color.WHITE);
    }

    public MailController(boolean whithoutGUI) {
        //this is constructor for tests only
    }

    public JTextPane getFromLine() {
        return fromLine;
    }

    public JTextPane getToLine() {
        return toLine;
    }

    public JTextPane getSubjectLine() {
        return subjectLine;
    }

    public JTextPane getBodyArea() {
        return bodyArea;
    }

    public JTextPane getFooterArea() {
        return footerArea;
    }

    public void setAttribute(StyledDocument document, int offset,
                             int length, SimpleAttributeSet attributeSet) {
        document.setCharacterAttributes(offset,
                length,
                attributeSet, false);
    }

    public void resetToDefault() {
        fromLine.setBorder(null);
        toLine.setBorder(null);
        subjectLine.setBorder(null);
        bodyArea.setBorder(null);
        footerArea.setBorder(null);
        setAttribute(bodyStyledDocument, 0, bodyStyledDocument.getLength(), attributesWhite);
        setAttribute(fromStyledDocument, 0, fromStyledDocument.getLength(), attributesWhite);
        setAttribute(toStyledDocument, 0, toStyledDocument.getLength(), attributesWhite);
        setAttribute(subjectStyledDocument, 0, subjectStyledDocument.getLength(), attributesWhite);
        setAttribute(footerStyledDocument, 0, footerStyledDocument.getLength(), attributesWhite);
    }

    public boolean checkFields() {
        boolean result = true;
        if (!checkFromLine()) {
            result = false;
        }
        if (!checkToLine()) {
            result = false;
        }
        if (!checkSubject()) {
            result = false;
        }
        if (!modifyBody()) {
            result = false;
        }
        if (!addFooterToBody()) {
            result = false;
        }
        return result;
    }

    public boolean isUTF8Charset(String s) {

        boolean result = false;
        CharsetDecoder csUTF8 = Charset.forName("UTF-8").newDecoder();

        try {
            csUTF8.decode(ByteBuffer.wrap(s.getBytes()));
            result = true;
        } catch (CharacterCodingException e) { }
        return result;
    }

    public HashMap<String, String> collectData() {

        HashMap<String, String> dataContainer = new HashMap<>();

        dataContainer.put("from", fromLine.getText());
        dataContainer.put("to", toLine.getText());
        dataContainer.put("subject", subjectLine.getText());
        dataContainer.put("body", bodyArea.getText());
        dataContainer.put("footer", footerArea.getText());
        return dataContainer;
    }

    public LinkedHashMap<String, int[]> getEmails(String s) {

        LinkedHashMap<String, int[]> result = new LinkedHashMap<>();

        String  adress,
                regEx = "((((?<Name>[\\w\\\\.-]+\\s)(?<NameBegin><)?)?"+
                        "(?<Adress>[\\w\\\\.-]+@[\\w]+[\\\\.][\\w]+([\\\\.][\\w]+)?)"+
                        "(?<NameEnd>>)?)(,\\s)?)+?";

        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(s);

        while (matcher.find()) {
            int[] array = new int[3];
            array[0] = 1;                               //флаг валидности адреса email
            array[1] = matcher.start();
            array[2] = matcher.end();
            adress = matcher.group("Adress");
            try {
                if ((matcher.group("Name") != null)
                        && ((matcher.group("NameBegin") == null)
                        || (matcher.group("NameEnd") == null))) {

                    array[0] = 0;
                }
                if (!isUTF8Charset(adress)) {
                    array[0] = 0;
                }
            } catch (IllegalStateException e) {
                array[0] = 0;
            }
            result.putIfAbsent(adress, array);
        }
        return result;
    }

    public boolean checkEmailString(StyledDocument document, LinkedHashMap<String,
                                    int[]> emails) {
        boolean result = true;
        int isValidEmail, startPos = 0, endPos = document.getLength();

        Set<Map.Entry<String, int[]>> set = emails.entrySet();
        for (Map.Entry<String, int[]> s : set) {
            if (startPos != s.getValue()[1]) {
                setAttribute(document, startPos,
                        s.getValue()[1] - startPos, attributesRed);
                result = false;
            }
            startPos = s.getValue()[2];
            isValidEmail = s.getValue()[0];
            if (isValidEmail == 0) {
                setAttribute(document, s.getValue()[1],
                        s.getValue()[2] - s.getValue()[1], attributesRed);
                result = false;
            }
        }
        if (startPos != endPos) {
            setAttribute(document, startPos,
                    endPos - startPos, attributesRed);
            result = false;
        }
        return !result;
    }

    public boolean checkFromLine() {

        boolean result;
        LinkedHashMap<String, int[]> emails = getEmails(fromLine.getText());

        result = isUTF8Charset(fromLine.getText());
        if (emails.isEmpty()) {
            fromLine.setBorder(BorderFactory.createLineBorder(Color.red));
            result = false;
        } else if (emails.size() > 1) {
            setAttribute(fromStyledDocument, 0,
                        fromStyledDocument.getLength(), attributesRed);
            result = false;
        } else {
            if (checkEmailString(fromStyledDocument, emails)) {
                result = false;
            }
        }
        return result;
    }

    public boolean checkToLine() {
        boolean result;
        LinkedHashMap<String, int[]> emails = getEmails(toLine.getText());

        result = isUTF8Charset(toLine.getText());
        if (emails.isEmpty()) {
            toLine.setBorder(BorderFactory.createLineBorder(Color.red));
            result = false;
        } else {
            if (checkEmailString(toStyledDocument, emails)) {
                result = false;
            }
        }
        return result;
    }

    public boolean isValidSubject(String s) {
        boolean result;
        String regEx = "[а-яёіўa-záéíñóúü\\s\\d\\p{Punct}]+";

        result = isUTF8Charset(s);
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(s);
        if (result) {
            result = matcher.matches();
        }
        return result;
    }

    public boolean checkSubject() {
        boolean result = true;
        if (!isValidSubject(subjectLine.getText())) {
            subjectLine.setBorder(BorderFactory.createLineBorder(Color.red));
            result = false;
        }
        return result;
    }

    public boolean modifyBody() {
        boolean result;
        HashMap<String, Integer> hashMap = new HashMap<>();
        String regEx = "((<p.*>\\R\\s*)(<strong>)?(?<InnerStr>.+)(</strong>)?(\\R\\s*</p>))+?";
        String innerRegEx = "(?<Word>(<strong>)?\\p{L}+(</strong>)?)|(?<Date>\\d+[-_/~\\d\\s:\\\\.]+\\d)";
        String outerStr, innerStr, str, wordStr, dateStr;
        Pattern innerPattern;
        Matcher innerMatcher;

        result = isUTF8Charset(bodyArea.getText());
        try {
            if (bodyArea.getText(0, bodyStyledDocument.getLength()).length() == 0) {
                return result;
            }
            outerStr = unescapeHtml4(bodyArea.getText());
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(outerStr);
            while (matcher.find()) {
                str = matcher.group("InnerStr");
                innerPattern = Pattern.compile(innerRegEx);
                innerMatcher = innerPattern.matcher(str);
                while (innerMatcher.find()) {
                    innerStr = innerMatcher.group();
                    wordStr = innerMatcher.group("Word");
                    dateStr = innerMatcher.group("Date");
                    if (wordStr != null) {
                        if (hashMap.putIfAbsent(wordStr.trim(), 0) != null) {
                            if (hashMap.get(wordStr.trim()) == 0) {
                                outerStr = outerStr.replaceAll("\\b" + wordStr + "\\b", "<strong>" + wordStr + "</strong>");
                                outerStr = outerStr.replaceFirst("<strong>" + innerStr + "</strong>", wordStr);
                                hashMap.put(wordStr.trim(), 1);
                            }
                        }
                    } else if (dateStr != null) {
                        outerStr = outerStr.replaceAll(dateStr, dateSelection(dateStr));
                    }
                }
            }
            bodyArea.setText(outerStr);
        } catch (BadLocationException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public boolean addFooterToBody() {
        boolean result = true;

        if (footerArea.getText().length() == 0) {
            return result;
        }
        try {
            bodyStyledDocument.insertBeforeEnd(bodyStyledDocument.getElement(bodyStyledDocument.getDefaultRootElement(),
                    StyleConstants.NameAttribute, HTML.Tag.BODY),
                    "<p><font color=\"gray\">" + footerArea.getText() + "</font></p>");
        } catch (BadLocationException| IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public String dateSelection(String inString) {

        String datePattern = "", tag = "";
        String regEx = "(1999|200[0-2])" +                        //year
                "(?<separator1>[-/])([0][1-9]|[1][012])" +        //months
                "(?<separator2>[-/])([012][1-9]|[3][01])" +       //days
                "(?<separator3>[\\s_~])([0-1]\\d|2[0-4])" +       //hours
                ":([0-5]\\d|60)" +                                //minutes
                "(?<Seconds>:([0-5]\\d|60))?" +                   //seconds
                "(?<Milliseconds>.(\\d{3}))?";                    //milliseconds
        String str, separator1, separator2, separator3;

        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(inString);


        while (matcher.find()) {
            str = matcher.group();
            try {
                separator1 = matcher.group("separator1");
                separator2 = matcher.group("separator2");
                separator3 = matcher.group("separator3");
                if ((separator1.equals("-")) && (separator2.equals("-"))
                        && (separator3.equals(" "))) {
                     tag = "i";
                } else if ((separator1.equals("-")) && (separator2.equals("-"))
                        && (separator3.equals("_"))) {
                    tag = "u";
                } else if ((separator1.equals("/")) && (separator2.equals("/"))
                        && (separator3.equals("~") && matcher.group("Milliseconds") == null)) {
                    tag = "s";
                }
                if (!tag.isEmpty()) {
                    datePattern = "yyyy" + separator1 + "MM" + separator2 + "dd" + separator3 + "HH:mm";
                    if (matcher.group("Seconds") != null) {
                        datePattern = datePattern + ":ss";
                    }
                    if (matcher.group("Milliseconds") != null) {
                        datePattern = datePattern + ".SSS";
                    }
                }
                DateFormat formatter = new SimpleDateFormat(datePattern);
                formatter.setLenient(false);
                formatter.parse(str);
            } catch (ParseException e) {
                return inString;
            }
            inString = inString.replaceAll(str, "<" + tag + ">" + str + "</" + tag + ">");
        }
        return inString;
    }
}
