package com.example.backend.category;

import com.example.backend.comment.Comment;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    public String name;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private Category parentCategory;
    @OneToMany(mappedBy = "parentCategory", orphanRemoval = true)
    @JsonManagedReference
    private List<Category> subCategories = new ArrayList<>();
}
