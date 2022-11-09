package com.dikkak.repository.coworking.message;

import com.dikkak.entity.coworking.CoworkingMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoworkingMessageRepository
        extends JpaRepository<CoworkingMessage, Long>, CoworkingMessageRepositoryCustom {
}
