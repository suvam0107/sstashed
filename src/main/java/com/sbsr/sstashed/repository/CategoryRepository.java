package com.sbsr.sstashed.repository;

import com.sbsr.sstashed.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find category by name
    Optional<Category> findByName(String name);

    // Find all active categories
    List<Category> findByIsActiveTrue();

    // Check if category name exists
    Boolean existsByName(String name);
}