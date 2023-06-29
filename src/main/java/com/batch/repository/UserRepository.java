package com.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batch.model.UserOutput;

public interface UserRepository extends JpaRepository<UserOutput,Long> {

}
