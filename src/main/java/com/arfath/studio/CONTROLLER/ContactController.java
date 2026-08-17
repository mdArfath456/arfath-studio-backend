package com.arfath.studio.CONTROLLER;

import com.arfath.studio.DTO.ContactRequest;
import com.arfath.studio.SERVICE.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

	private final EmailService emailService;

	public ContactController(EmailService emailService) {
		this.emailService = emailService;
	}

	@PostMapping("/send")
	public ResponseEntity<?> sendMessage(@Valid @RequestBody ContactRequest req) {
		emailService.handleContactSubmission(req);
		return ResponseEntity.ok().body("Message sent");
	}
}