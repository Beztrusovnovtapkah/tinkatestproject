package ru.alitryel.bfmetvennorath.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "maps")
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Size(min = 3, max = 50, message = "Name length between 3 and 30.")
    @NotBlank
    private String name;

    private Integer maxCount;

    private String description;

    private String pathToImage;

    public String getNameWithRank() {
        return name;
    }

}