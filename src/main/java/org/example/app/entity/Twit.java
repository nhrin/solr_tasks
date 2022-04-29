package org.example.app.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "twits")
@Data
public class Twit {

    public Twit() {
        this.publicationDate = Timestamp.from(Instant.now());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "content")
    private String content;

    @Column(name = "publication_date")
    private Timestamp publicationDate;
}
