package MailController;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.HashMap;
import java.util.Properties;

public class EmailSender {

    private Properties prop = new Properties();


    private void setProperties() {
        prop.setProperty("mail.smtp.auth", "true");
        prop.setProperty("mail.smtp.starttls.enable", "true");
        prop.setProperty("mail.smtp.host", "smtp.mailtrap.io");
        prop.setProperty("mail.smtp.port", "25");
        prop.setProperty("mail.smtp.ssl.trust", "smtp.mailtrap.io");
    }

    public void sendEmail(HashMap dataContainer) {

        InternetAddress fromAddress;
        Address[] toAddress;

        setProperties();
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("697d73121cf7a7", "8b908e9c5c67e2");
            }
        });

        try {
            fromAddress = new InternetAddress(dataContainer.get("from").toString());
            toAddress = InternetAddress.parse(dataContainer.get("to").toString());
            fromAddress.validate();

            Message message = new MimeMessage(session);
            message.setFrom(fromAddress);

            message.setRecipients(
                    Message.RecipientType.TO, toAddress);
            message.setSubject(dataContainer.get("subject").toString());

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(dataContainer.get("body").toString(), "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException e) {
            System.out.println("Some error with sending email");
            e.printStackTrace();
        }
    }
}
