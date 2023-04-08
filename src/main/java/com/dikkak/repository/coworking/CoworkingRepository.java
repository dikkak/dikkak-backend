package com.dikkak.repository.coworking;

import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CoworkingRepository extends JpaRepository<Coworking, Long> {
    Optional<Coworking> findByProposalIdAndDesigner(Long proposalId, User Designer);
    @Query("select c from Coworking c " +
            "join fetch c.proposal " +
            "where c.id = :id")
    Optional<Coworking> findWithProposalById(Long id);
}

