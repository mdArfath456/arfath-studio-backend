package com.arfath.studio.SERVICE;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.arfath.studio.DTO.ContactRequest;
import com.arfath.studio.ENTITY.ContactMessage;
import com.arfath.studio.REPOSITORY.ContactMessageRepository;

@Service
public class EmailService {

	private final ContactMessageRepository repository;
	private final WebClient webClient;
	private final String inboxAddress;
	private final String fromEmail;
	private final String fromName;

	public EmailService(
			ContactMessageRepository repository,
			@Value("${brevo.api-key}") String apiKey,
			@Value("${studio.contact.inbox}") String inboxAddress,
			@Value("${brevo.from-email}") String fromEmail,
			@Value("${brevo.from-name}") String fromName) {

		this.repository = repository;
		this.inboxAddress = inboxAddress;
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.webClient = WebClient.builder()
				.baseUrl("https://api.brevo.com/v3")
				.defaultHeader("api-key", apiKey)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
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

		Map<String, Object> payload = Map.of(
				"sender", Map.of("name", fromName, "email", fromEmail),
				"to", List.of(Map.of("email", inboxAddress)),
				"replyTo", Map.of("email", req.getEmail(), "name", req.getName()),
				"subject", "New project inquiry from " + req.getName(),
				"htmlContent", html);

		try {
			webClient.post()
					.uri("/smtp/email")
					.bodyValue(payload)
					.retrieve()
					.toBodilessEntity()
					.block();

			System.out.println("Email sent successfully via Brevo for inquiry from " + req.getName());

		} catch (Exception e) {
			throw new RuntimeException("Failed to send email through Brevo", e);
		}
	}
}