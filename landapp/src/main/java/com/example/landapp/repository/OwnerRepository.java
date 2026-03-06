package com.example.landapp.repository;

import com.example.landapp.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/*
What is the purpose of a repository ?

This interface acts like a communicator between the database and the backend .
custom Sql queries can be inserted inside this repo .

by extending this preBuilt JpaRepository
you immediately get these methods for free without writing a single line of SQL

.save(entity) (Used for both Create and Update)
.findById(id)
.findAll()
.deleteById(id)
.count()
* */

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

}