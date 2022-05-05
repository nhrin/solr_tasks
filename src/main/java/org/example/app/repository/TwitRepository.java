package org.example.app.repository;

import org.example.app.entity.Twit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwitRepository extends JpaRepository<Twit, String> {

}
