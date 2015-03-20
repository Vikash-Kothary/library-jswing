package PRA;

import jSwing.jFrame;
import jSwing.jPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class EmailPopup extends jFrame {

	private String body;

	boolean status = false;
	ArrayList<String> studentEmails = new ArrayList<String>();

	private String subject;

	public EmailPopup(final StudentList mainList) throws FileNotFoundException,
			IOException {
		super("Email");
		setSize(1000, 600);

		final jPanel wholeThing = addContainer();
		wholeThing.setLayout(new GridLayout(1, 1));
		final jPanel makeEmailContainer = wholeThing
				.addPanel("makeEmailContainer");
		final jPanel previewEmailContainer = new jPanel();
		final EmailSettings emailSettings = new EmailSettings();
		makeEmailContainer.setLayout(new BorderLayout());
		jPanel students = makeEmailContainer.addPanel("students",
				BorderLayout.WEST);
		students.setLayout(new BorderLayout());
		jPanel buttons = students.addPanel("buttons", BorderLayout.NORTH);
		buttons.setLayout(new FlowLayout());
		buttons.addButton("Select All");
		buttons.addButton("Select None");

		students.addScrollPanel("scrollPanel");
		students.getScrollPanel("scrollPanel").getPanel()
				.setLayout(new GridLayout(mainList.size(), 1));
		students.getScrollPanel("scrollPanel").setSize(200, 200);
		for (int i = 0; i < mainList.size(); i++) {
			students.getScrollPanel("scrollPanel").getPanel()
					.addCheckBox(mainList.getStudent(i).getStudentName());
		}

		jPanel emailComponents = makeEmailContainer.addPanel("emailComponents",
				BorderLayout.CENTER);
		emailComponents.setLayout(new BorderLayout());
		emailComponents.addTextField(null, "Header", BorderLayout.NORTH);
		emailComponents.addTextArea(null, "Footer");
		jPanel bottom = emailComponents.addPanel("bottom", BorderLayout.SOUTH);
		bottom.setLayout(new BorderLayout());
		bottom.addButton("Next", BorderLayout.EAST);
		final jPanel scrollPanel = getFrameContainer()
				.getPanel("makeEmailContainer").getPanel("students")
				.getScrollPanel("scrollPanel").getPanel();
		buttons.getButton("Select All").addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < mainList.size(); i++) {
					scrollPanel.getCheckBox(
							mainList.getStudent(i).getStudentName())
							.setSelected(true);
				}
			}
		});
		buttons.getButton("Select None").addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						for (int i = 0; i < mainList.size(); i++) {
							scrollPanel.getCheckBox(
									mainList.getStudent(i).getStudentName())
									.setSelected(false);

						}
					}
				});
		previewEmailContainer.setLayout(new BorderLayout());
		jPanel emailPreview = previewEmailContainer.addPanel("EmailPreview",
				BorderLayout.CENTER);
		jPanel previewButtons = previewEmailContainer.addPanel(
				"PreviewButtons", BorderLayout.SOUTH);
		bottom.getButton("Next").addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				subject = getFrameContainer().getPanel("makeEmailContainer")
						.getPanel("emailComponents").getTextField("Header")
						.getText();

				for (int i = 0; i < mainList.size(); i++) {
					if (scrollPanel.getCheckBox(
							mainList.getStudent(i).getStudentName())
							.isSelected()) {

						studentEmails.add(mainList.getStudent(i)
								.getEmailAddress());
					}
				}

				body = getFrameContainer().getPanel("makeEmailContainer")
						.getPanel("emailComponents").getTextArea("Footer")
						.getTextArea().getText();
				previewEmailContainer.getPanel("PreviewButtons")
						.addProgressBar(1, studentEmails.size(), "progressBar");
				wholeThing.remove(makeEmailContainer);
				wholeThing.add(previewEmailContainer);
				wholeThing.revalidate();
				wholeThing.repaint();
			}
		});
		previewButtons.addButton("Previous");
		previewButtons.addButton("Send");

		previewButtons.getButton("Previous").addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						wholeThing.remove(previewEmailContainer);
						wholeThing.add(makeEmailContainer);
						wholeThing.revalidate();
						wholeThing.repaint();
					}
				});
		// jPanel panel = previewEmailContainer.getPanel("PreviewButtons");
		// final JProgressBar pb = panel.getProgressBar("progressBar");
		previewButtons.getButton("Send").addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						jPanel passPanel = new jPanel();
						passPanel.addLabel("Password: ");
						passPanel.addPasswordField("passwordField");
						final int option = JOptionPane.showOptionDialog(null,
								passPanel, "Enter Password",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE, null, null, "OK");
						if (option == 0) {
							final char[] password = passPanel.getPasswordField(
									"passwordField").getPassword();
							Properties props = new Properties();

							props.put("mail.smtp.auth", EmailSettings.getAuth());
							props.put("mail.smtp.starttls.enable",
									EmailSettings.getSSL());
							props.put("mail.smtp.host", EmailSettings.getHost());
							props.put("mail.smtp.port", EmailSettings.getPort());

							Session session = Session.getInstance(props,
									new javax.mail.Authenticator() {
										@Override
										protected PasswordAuthentication getPasswordAuthentication() {
											return new PasswordAuthentication(
													EmailSettings.getUserName(),
													new String("passworddvt"));
										}
									});
							for (int i = 0; i < studentEmails.size(); i++) {
								MimeMessage message = new MimeMessage(session);
								try {

									message.setFrom(new InternetAddress(
											EmailSettings.getUserName()));
									message.setSubject(subject);
									message.setText(body);
									message.addRecipient(
											Message.RecipientType.TO,
											new InternetAddress(studentEmails
													.get(i)));
									Transport.send(message);
									previewEmailContainer
											.getPanel("PreviewButtons")
											.getProgressBar("progressBar")
											.setValue(i);
								} catch (MessagingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}

							System.out.println("Done");
							exitFrame();

						}

					}
				});

		setVisible(true);

	}

}