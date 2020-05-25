package MailController;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import java.util.HashMap;
import java.util.LinkedHashMap;


import static org.junit.Assert.*;

public class MailControllerTest {

    private static  MailController   mailController                 = new MailController(true);
    private static JTextPane         fromLine                       = mailController.getFromLine();
    private static JTextPane         toLine                         = mailController.getToLine();
    private static JTextPane         subjectLine                    = mailController.getSubjectLine();
    private static JTextPane         bodyArea                       = mailController.getBodyArea();
    private static JTextPane         footerArea                     = mailController.getFooterArea();

    private HTMLDocument             bodyStyledDocument             = (HTMLDocument )bodyArea.getStyledDocument();
    private StyledDocument           toStyledDocument               = toLine.getStyledDocument();

    private String                   errorEmailsString              = "asdad, dasdasd@asdda.das, fasgdsgfa," +
                                                                      " assdaf <asdf@asfd.fdsa.fds> fsaffd";
    private int                      startError1Email               = 7;
    private int                      endError1Email                 = 26;
    private int                      startError2Email               = 37;
    private int                      endError2Email                 = 64;

    private int                      startValid1Email               = 0;
    private int                      endValid1Email                 = 19;
    private int                      startValid2Email               = 19;
    private int                      endValid2Email                 = 46;

    private String                   validEmailsString              = "dasdasd@asdda.das," +
                                                                      " assdaf <asdf@asfd.fdsa.fds>";
    private String                   emailAddress1                  = "dasdasd@asdda.das";
    private String                   emailAddress2                  = "asdf@asfd.fdsa.fds";

    private static String            defaultToString                = "791790b969-917ec3@inbox.mailtrap.io";
    private static String            defaultFromString              = "791790b969-917ec3@inbox.mailtrap.io";
    private static String            defaultSubjectString           = "test";
    private static String            defaultBodyString              = "<html>\n" +
                                                                      "  <head>\n" +
                                                                      "    \n" +
                                                                      "  </head>\n" +
                                                                      "  <body>\n" +
                                                                      "    <p style=\"margin-top: 0\">\n" +
                                                                      "      asdasd asdasd, des: asdasd des? ewr ewr1 ? ewr\n" +
                                                                      "    </p>\n" +
                                                                      "  </body>\n" +
                                                                      "</html>\n";
    private String                   expectedBodyString             = "<html>\n" +
                                                                      "  <head>\n" +
                                                                      "    \n" +
                                                                      "  </head>\n" +
                                                                      "  <body>\n" +
                                                                      "    <p style=\"margin-top: 0\">\n" +
                                                                      "      asdasd <strong>asdasd</strong>, des: <strong>asdasd</strong> <strong>des</strong>? \n" +
                                                                      "      ewr ewr1 ? <strong>ewr</strong>\n" +
                                                                      "    </p>\n" +
                                                                      "  </body>\n" +
                                                                      "</html>\n";

    private static String            defaultFooterString            = "footer text";
    private String                   expectedFooterString           = "\nfooter text";

    private String                   dateString1                    = "2002-11-28 21:19";
    private String                   expectedDateString1            = "<i>2002-11-28 21:19</i>";
    private String                   dateString2                    = "2001-11-28_21:19:54.621";
    private String                   expectedDateString2            = "<u>2001-11-28_21:19:54.621</u>";
    private String                   dateString3                    = "2002/11/28~21:19:27";
    private String                   expectedDateString3            = "<s>2002/11/28~21:19:27</s>";

    @BeforeClass
    public static void runOnceBeforeClass() {
        fromLine.setText(defaultFromString);
        toLine.setText(defaultToString);
        subjectLine.setText(defaultSubjectString);
        bodyArea.setText(defaultBodyString);
        footerArea.setText(defaultFooterString);
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

        HashMap<String, String> dataContainer = mailController.collectData();
        assertEquals(defaultFromString, dataContainer.get("from"));
        assertEquals(defaultToString, dataContainer.get("to"));
        assertEquals(defaultSubjectString, dataContainer.get("subject"));
        assertEquals(defaultBodyString, dataContainer.get("body"));
        assertEquals(defaultFooterString, dataContainer.get("footer"));
        System.out.println("MailController.collectData().......ok");
    }

    @Test
    public void getEmails() {

        LinkedHashMap<String, int[]> emails = mailController.getEmails(errorEmailsString);

        assertTrue(emails.containsKey(emailAddress1));
        assertEquals(1, emails.get(emailAddress1)[0]);
        assertEquals(startError1Email, emails.get(emailAddress1)[1]);
        assertEquals(endError1Email, emails.get(emailAddress1)[2]);
        assertTrue(emails.containsKey(emailAddress2));
        assertEquals(1, emails.get(emailAddress2)[0]);
        assertEquals(startError2Email, emails.get(emailAddress2)[1]);
        assertEquals(endError2Email, emails.get(emailAddress2)[2]);
        System.out.println("MailController.getEmails().........ok");
    }

    @Test
    public void checkEmailString() {

        LinkedHashMap<String, int[]> emails = new LinkedHashMap<>();
        int[] array = new int[3];

        toLine.setText(errorEmailsString);
        array[0] = 1;
        array[1] = startError1Email;
        array[2] = endError1Email;
        emails.put(emailAddress1, array);
        array = new int[3];
        array[0] = 1;
        array[1] = startError2Email;
        array[2] = endError2Email;
        emails.put(emailAddress2, array);
        assertTrue(mailController.checkEmailString(toStyledDocument, emails));

        emails.clear();
        array = new int[3];
        toLine.setText(validEmailsString);
        array[0] = 1;
        array[1] = startValid1Email;
        array[2] = endValid1Email;
        emails.put(emailAddress1, array);
        array = new int[3];
        array[0] = 1;
        array[1] = startValid2Email;
        array[2] = endValid2Email;
        emails.put(emailAddress2, array);
        assertFalse(mailController.checkEmailString(toStyledDocument, emails));
        System.out.println("MailController.checkEmailString()..ok");
        toLine.setText(defaultToString);
    }

    @Test
    public void checkFromLine() {
        fromLine.setText("791790b969-917ec3@inbox.mailtrap.io");
        assertTrue(mailController.checkFromLine());
        fromLine.setText("791790b969-917ec3@inbox.mailtrap.io, asdasd <dasd@asd.das.da>");
        assertFalse(mailController.checkFromLine());
        fromLine.setText("dasda 791790b969-917ec3@inbox.mailtrap.io");
        assertFalse(mailController.checkFromLine());
        fromLine.setText(defaultFromString);
        System.out.println("MailController.checkFromLine().....ok");
    }

    @Test
    public void checkToLine() {
        toLine.setText("791790b969-917ec3@inbox.mailtrap.io, asdasd <dasd@asd.das.da>");
        assertTrue(mailController.checkToLine());
        toLine.setText("791790b969-917ec3@inbox.mailtrap.io, asdasd dasd@asd.das.da>");
        assertFalse(mailController.checkToLine());
        toLine.setText("791790b969-917ec3@inbox.mailtrap.io, asdasd dasd@asd.das.da>");
        assertFalse(mailController.checkToLine());
        toLine.setText(defaultToString);
        System.out.println("MailController.checkToLine().......ok");
    }

    @Test
    public void isValidSubject() {
        assertTrue(mailController.isValidSubject(defaultSubjectString));
        System.out.println("MailController.isValidSubject()....ok");
    }

//    @Test
//    public void checkSubject() {
//        subjectLine.setText("фывфыв 123 dasd, záéíñóúü? а-яёіў");
//        assertTrue(mailController.checkSubject());
//        subjectLine.setText("žš фывфыв das 312");
//        assertTrue(mailController.checkSubject());
//        subjectLine.setText("äf, äffä dsasdв выфыв 312441");
//        assertTrue(mailController.checkSubject());
//        subjectLine.setText(defaultSubjectString);
//        System.out.println("MailController.checkSubject()......ok");
//    }

    @Test
    public void modifyBody() {
        assertTrue(mailController.modifyBody());
        assertEquals(expectedBodyString, bodyArea.getText());
        bodyArea.setText(defaultBodyString);
        System.out.println("MailController.modifyBody()........ok");
    }

    @Test
    public void addFooterToBody() {

        expectedFooterString = "\nfooter text";

        bodyArea.setText("");
        assertTrue(mailController.addFooterToBody());
        try {
            assertTrue(bodyArea.getText(0, bodyStyledDocument.getLength()).equals(expectedFooterString));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        bodyArea.setText(defaultBodyString);
        System.out.println("MailController.addFooterToBody()...ok");
    }

    @Test
    public void dateSelection() {

        assertEquals(expectedDateString1, mailController.dateSelection(dateString1));
        assertEquals(expectedDateString2, mailController.dateSelection(dateString2));
        assertEquals(expectedDateString3, mailController.dateSelection(dateString3));
        System.out.println("MailController.dateSelection().....ok");
    }
}