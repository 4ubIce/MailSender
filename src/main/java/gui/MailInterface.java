package gui;

import javax.swing.*;
import java.awt.*;


public class MailInterface extends JFrame {
    private static JFrame mainFrame = new JFrame("Send mail");
    private static JTextPane fromLine = new JTextPane();
    private static JTextPane toLine = new JTextPane();
    private static JTextPane subjectLine = new JTextPane();
    private static JTextPane bodyArea = new JTextPane();
    private static JTextPane footerArea = new JTextPane();
    private static JButton sendButton;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int FRAME_MIN_WIDTH = 640;
    private static final int FRAME_MIN_HEIGHT = 480;


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

    public JButton getSendButton() {
        return sendButton;
    }

    public MailInterface() {

        final GraphicsDevice gD = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        final int screenCenterX = gD.getDisplayMode().getWidth()/2;
        final int screenCenterY = gD.getDisplayMode().getHeight()/2;

        final int frameWidth  = Math.max(FRAME_MIN_WIDTH, screenCenterX);
        final int frameHeight = Math.max(FRAME_MIN_HEIGHT, screenCenterY);

        final int frameOffsetLeft = screenCenterX - frameWidth/2;
        final int frameOffsetTop = screenCenterY - frameHeight/2;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mainFrame.setSize(new Dimension(frameWidth, frameHeight));
        mainFrame.setLocation(frameOffsetLeft, frameOffsetTop);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        mainFrame.setPreferredSize(new Dimension(FRAME_WIDTH * 2, FRAME_HEIGHT * 2));
        mainFrame.setResizable(true);
        JPanel bgPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints= new GridBagConstraints();
        mainFrame.add(bgPanel);

        sendButton = new JButton("Send button");
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 0.001;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.NONE;
        bgPanel.add(sendButton, constraints);


        JLabel fromLabel = new JLabel("From: ");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.01;
        constraints.weighty = 0.02;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        bgPanel.add(fromLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.99;
        constraints.weighty = 0.02;
        constraints.fill = GridBagConstraints.BOTH;
        bgPanel.add(fromLine, constraints);

        JLabel toLabel = new JLabel("To: ");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.weightx = 0.01;
        constraints.weighty = 0.02;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        bgPanel.add(toLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.weightx = 0.99;
        constraints.weighty = 0.02;
        constraints.fill = GridBagConstraints.BOTH;
        bgPanel.add(toLine, constraints);

        JLabel subjectLabel = new JLabel("Subject: ");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridheight = 1;
        constraints.weightx = 0.01;
        constraints.weighty = 0.02;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        bgPanel.add(subjectLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridheight = 1;
        constraints.weightx = 0.99;
        constraints.weighty = 0.02;
        constraints.fill = GridBagConstraints.BOTH;
        bgPanel.add(subjectLine, constraints);

        JLabel bodyLabel = new JLabel("Body: ");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridheight = 1;
        constraints.weightx = 0.01;
        constraints.weighty = 0.9;

        constraints.fill = GridBagConstraints.HORIZONTAL;
        bgPanel.add(bodyLabel, constraints);

        bodyArea.setContentType("text/html");
        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridheight = 1;
        constraints.weightx = 0.99;
        constraints.weighty = 0.9;
        constraints.fill = GridBagConstraints.BOTH;
        bgPanel.add(bodyArea, constraints);

        JLabel footerLabel = new JLabel("Footer: ");
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridheight = 1;
        constraints.weightx = 0.01;
        constraints.weighty = 0.3;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        bgPanel.add(footerLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 5;
        constraints.gridheight = 1;
        constraints.weightx = 0.99;
        constraints.weighty = 0.3;
        constraints.fill = GridBagConstraints.BOTH;
        bgPanel.add(footerArea, constraints);

        fromLine.setBorder(null);
        toLine.setBorder(null);
        subjectLine.setBorder(null);
        bodyArea.setBorder(null);
        footerArea.setBorder(null);
    }

    public void start(){
        mainFrame.setVisible(true);
    }
}
