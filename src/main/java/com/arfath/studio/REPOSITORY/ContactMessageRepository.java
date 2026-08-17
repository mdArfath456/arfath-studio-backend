package com.arfath.studio.REPOSITORY;


import org.springframework.data.jpa.repository.JpaRepository;

import com.arfath.studio.ENTITY.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
	
}
