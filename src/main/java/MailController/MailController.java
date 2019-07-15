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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        if (checkToLine()) {
            result = false;
        }
        if (checkSubject()) {
            result = false;
        }
        if (modifyBody()) {
            result = false;
        }
        if (addFooterToBody()) {
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
        String regEx = "((<strong>)?\\p{L}+(</strong>)?)";
//        String regEx = "(<body>\\R\\s+)(?<MyStr>(((<strong>)?\\p{ASCII}+)" +
//                "(</strong>)?)+)(\\R\\s+</body>)";
        String innerStr, str;

        result = isUTF8Charset(bodyArea.getText());
        try {
            if (bodyArea.getText(0, bodyStyledDocument.getLength()).length() == 0) {
                return result;
            }
            bodyArea.setText(bodyArea.getText(0, bodyStyledDocument.getLength())); //убираем лишние html тэги (<p>, </p>)

            innerStr = bodyArea.getText(0, bodyStyledDocument.getLength());

            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(innerStr);
            while (matcher.find()) {
                str = matcher.group();
                if (hashMap.putIfAbsent(str.trim() , 0) != null) {
                    if (hashMap.get(str.trim()) == 0) {
                        innerStr = innerStr.replaceAll("\\b"+str+"\\b", "<strong>" + str + "</strong>");
                        innerStr = innerStr.replaceFirst("<strong>" + str + "</strong>", str);
                        bodyArea.setText(innerStr);
                        hashMap.put(str.trim(), 1);
                    }
                }
            }
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

}
