package com.arfath.studio.SERVICE;

import com.arfath.studio.DTO.ContactRequest;
import com.arfath.studio.ENTITY.ContactMessage;
import com.arfath.studio.REPOSITORY.ContactMessageRepository;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final ContactMessageRepository repository;
	private final Resend resend;

	@Value("${studio.contact.inbox}")
	private String inboxAddress;

	public EmailService(ContactMessageRepository repository, @Value("${RESEND_API_KEY}") String apiKey) {

		this.repository = repository;
		this.resend = new Resend(apiKey);
	}

	public void handleContactSubmission(ContactRequest req) {

		ContactMessage entity = new ContactMessage();

		entity.setName(req.getName());
		entity.setPhone(req.getPhone());
		entity.setEmail(req.getEmail());
		entity.setMessage(req.getMessage());

		repository.save(entity);

		String html = """
				<h2>New Project Inquiry</h2>

				<p><strong>Name:</strong> %s</p>
				<p><strong>Phone:</strong> %s</p>
				<p><strong>Email:</strong> %s</p>

				<h3>Project Brief</h3>
				<p>%s</p>
				""".formatted(req.getName(), req.getPhone(), req.getEmail(), req.getMessage());

		CreateEmailOptions emailRequest = CreateEmailOptions.builder().from("Portfolio <onboarding@resend.dev>")
				.to(inboxAddress).replyTo(req.getEmail()).subject("New project inquiry from " + req.getName())
				.html(html).build();

		try {
			CreateEmailResponse response = resend.emails().send(emailRequest);

			System.out.println("Email sent successfully. ID: " + response.getId());

		} catch (ResendException e) {
			throw new RuntimeException("Failed to send email through Resend", e);
		}
	}
}