package com.rupesh.user.repository;

import com.rupesh.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT U FROM User U WHERE lower(U.email) = lower(:key) OR U.username = :key")
    Optional<User> findBy(@Param("key") String key);

    @Query("SELECT U FROM User U WHERE U.username = :username")
    Optional<User> findByUsername(String username);

    @Query("SELECT user FROM User user WHERE lower(user.lastName) LIKE lower(concat('%', :query, '%')) " +
            "OR lower(user.firstName) LIKE lower(concat('%', :query, '%'))")
    Page<User> search(Pageable pageable, String query);

}
