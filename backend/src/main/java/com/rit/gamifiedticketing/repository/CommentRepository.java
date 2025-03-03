package com.rit.gamifiedticketing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rit.gamifiedticketing.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketId(Long ticketId);
}

