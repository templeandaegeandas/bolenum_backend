package com.bolenum.repo.user;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bolenum.model.Error;

public interface ErrorRepository extends JpaRepository<Error, Long> {

}
