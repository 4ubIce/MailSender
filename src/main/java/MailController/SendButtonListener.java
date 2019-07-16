package MailController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SendButtonListener implements ActionListener {

    private MailController      mailController;

    public SendButtonListener(MailController mailController) {
        this.mailController = mailController;
    }

    public void actionPerformed(ActionEvent e) {
        mailController.resetToDefault();
        if (mailController.checkFields()) {
            sendEmail();
        }
    }

    private void sendEmail() {
        EmailSender emailSender = new EmailSender();
        emailSender.sendEmail(mailController.collectData());
    }
}
