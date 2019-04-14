package com.teamdev.todolist.repository;

import com.teamdev.todolist.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    long count();

}
