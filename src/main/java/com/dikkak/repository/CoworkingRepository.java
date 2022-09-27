package com.dikkak.repository;

import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoworkingRepository extends JpaRepository<Coworking, Long> {
    Optional<Coworking> findByProposalIdAndDesigner(Long proposalId, User Designer);
}

