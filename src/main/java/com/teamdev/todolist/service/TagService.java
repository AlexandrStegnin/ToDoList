package com.teamdev.todolist.service;

import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag create(Tag tag) {
        return tagRepository.save(tag);
    }

    public Tag update(Tag tag) {
        return create(tag);
    }

    public void delete(Tag tag) {
        delete(tag.getId());
    }

    public void delete(Long id) {
        tagRepository.deleteById(id);
    }

    public Long count() {
        return tagRepository.count();
    }

}
