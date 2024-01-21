package com.teamaloha.internshipprocessmanagement.dao;

import com.teamaloha.internshipprocessmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDao extends JpaRepository<User, Integer> {
    User findById(int id);
    User findByMail(String mail);
    boolean existsByMail(String mail);
    User save(User user);

    @Query("SELECT a.mail FROM User a where a.id IN :ids")
    List<String> findMailByUserId(
            @Param("ids") List<Integer> ids
    );
}
