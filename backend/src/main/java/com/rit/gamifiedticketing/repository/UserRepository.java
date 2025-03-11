package com.rit.gamifiedticketing.repository;

import com.rit.gamifiedticketing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findTop30ByRoleOrderByPointsDesc(String role);

    @Query("SELECT u.points FROM User u WHERE u.username = :username")
    int findUserPoints(@Param("username") String username);

    @Query("SELECT COUNT(u) + 1 FROM User u WHERE u.points > (SELECT points FROM User WHERE username = :username)")
    int getUserRank(@Param("username") String username);

    @Modifying
    @Query("UPDATE User u SET u.points = 0")
    void resetAllUserPoints();

}
