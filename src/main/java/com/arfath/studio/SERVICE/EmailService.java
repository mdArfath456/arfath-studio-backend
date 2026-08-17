package com.arfath.studio.SERVICE;

import com.arfath.studio.DTO.ContactRequest;
import com.arfath.studio.ENTITY.ContactMessage;
import com.arfath.studio.REPOSITORY.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender mailSender;
	private final ContactMessageRepository repository;

	@Value("${studio.contact.inbox}")
	private String inboxAddress;

	public EmailService(JavaMailSender mailSender, ContactMessageRepository repository) {
		this.mailSender = mailSender;
		this.repository = repository;
	}

	public void handleContactSubmission(ContactRequest req) {
		ContactMessage entity = new ContactMessage();
		entity.setName(req.getName());
		entity.setPhone(req.getPhone());
		entity.setEmail(req.getEmail());
		entity.setMessage(req.getMessage());
		repository.save(entity);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(inboxAddress);
		message.setReplyTo(req.getEmail());
		message.setSubject("New project inquiry from " + req.getName());
		message.setText("Name: " + req.getName() + "\n" + "Phone: " + req.getPhone() + "\n" + "Email: " + req.getEmail()
				+ "\n\n" + "Message:\n" + req.getMessage());
		mailSender.send(message);
	}
}