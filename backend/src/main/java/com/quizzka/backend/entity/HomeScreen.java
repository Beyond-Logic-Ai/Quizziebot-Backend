package com.quizzka.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor

@Document(collection = "homeScreen")
public class HomeScreen {
    @Id
    private String id;
    private List<Topic> topics;
    private List<Tab> tabs;

}
