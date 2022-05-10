package org.example.app.entity;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

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
    @Field("id")
    private Integer id;

    @Column(name = "user_name")
    @Field("user_name")
    private String userName;

    @Column(name = "content")
    @Field("content")
    private String content;

    @Column(name = "publication_date")
    @Field("publication_date")
    private Date publicationDate;
}
