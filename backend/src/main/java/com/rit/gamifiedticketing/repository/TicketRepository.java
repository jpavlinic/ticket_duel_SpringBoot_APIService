package com.rit.gamifiedticketing.repository;

import com.rit.gamifiedticketing.entity.Ticket;
import com.rit.gamifiedticketing.entity.Ticket.TicketStatus;
import com.rit.gamifiedticketing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreatedBy(User createdBy);

    List<Ticket> findByAssignedTo(User assignedTo);

    List<Ticket> findByStatusAndAssignedToIsNull(TicketStatus status);

    Optional<Ticket> findByIdAndCreatedBy_Username(Long id, String username);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo.username = :username AND t.status = 'COMPLETED'")
    int countCompletedTickets(@Param("username") String username);
}
