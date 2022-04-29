package org.example.app.repository;

import org.example.app.entity.Twit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TwitRepository extends JpaRepository<Twit, String> {

}
