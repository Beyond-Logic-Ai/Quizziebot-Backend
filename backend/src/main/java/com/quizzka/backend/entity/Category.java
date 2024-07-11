package com.quizzka.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "categories")
public class Category {
    @Id
    private String id;
    private String name;
    @JsonIgnore
    private Date createdAt;
    @JsonIgnore
    private Date updatedAt;
}
