package com.bolenum.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolenum.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
