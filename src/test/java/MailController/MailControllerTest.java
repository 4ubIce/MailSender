package MailController;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

import static org.junit.Assert.*;

public class MailControllerTest {

    private static  MailController   mailController                 = new MailController(true);
    private static JTextPane         fromLine                       = mailController.getFromLine();
    private static JTextPane         toLine                         = mailController.getToLine();
    private static JTextPane         subjectLine                    = mailController.getSubjectLine();
    private static JTextPane         bodyArea                       = mailController.getBodyArea();
    private static JTextPane         footerArea                     = mailController.getFooterArea();

    private HTMLDocument             bodyStyledDocument             = (HTMLDocument )bodyArea.getStyledDocument();
    private StyledDocument           fromStyledDocument             = fromLine.getStyledDocument();
    private StyledDocument           toStyledDocument               = toLine.getStyledDocument();
    private StyledDocument           subjectStyledDocument          = subjectLine.getStyledDocument();
    private StyledDocument           footerStyledDocument           = footerArea.getStyledDocument();

    @BeforeClass
    public static void runOnceBeforeClass() {
        fromLine.setText("791790b969-917ec3@inbox.mailtrap.io");
        toLine.setText("791790b969-917ec3@inbox.mailtrap.io");
        subjectLine.setText("test");
        bodyArea.setText("asdasd asdasd, des: asdasd des? ewr < ewr1 ? ewr");
        footerArea.setText("footer text");
    }

    @Test
    public void setAttribute() {
    }

    @Test
    public void resetToDefault() {
    }

    @Test
    public void checkFields() {

    }

    @Test
    public void isUTF8Charset() {
    }

    @Test
    public void collectData() {
    }

    @Test
    public void getEmails() {
    }

    @Test
    public void checkFromLine() {
    }

    @Test
    public void checkToLine() {
        toLine.setText("791790b969-917ec3@inbox.mailtrap.io, asdasd <dasd@asd.das.da>");
        assertTrue(mailController.checkToLine());
        toLine.setText("791790b969-917ec3@inbox.mailtrap.io, asdasd dasd@asd.das.da>");
        assertFalse(mailController.checkToLine());
        toLine.setText("791790b969-917ec3@inbox.mailtrap.io, asdasd dasd@asd.das.da>");
        assertFalse(mailController.checkToLine());
        toLine.setText("asdad <asdasd@asd.das>, asdda, dsad@asda.das.das, sasd");
        System.out.println("MailController.checkToLine()....ok");
    }

    @Test
    public void isValidSubject() {

    }

    @Test
    public void checkSubject() {
    }

    @Test
    public void modifyBody() {
    }

    @Test
    public void addFooterToBody() {
    }
}