package com.teamdev.todolist.controller;

import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.teamdev.todolist.configuration.support.Constants.*;

/**
 * @author Alexandr Stegnin
 */

@RestController
@RequestMapping(value = API + API_TAGS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = API + API_TAGS, description = "Operations with tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /**
     * Создать тэг
     *
     * @param tag - тэг в формате json
     * @return - Tag
     */
    @ApiOperation(value = "Create tag", response = Tag.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @PostMapping
    public Tag create(@RequestBody Tag tag) {
        return tagService.create(tag);
    }

    /**
     * Найти тэг по id
     *
     * @param tagId - id тэга
     * @return - Tag
     */
    @ApiOperation(value = "Get tag by id", response = Tag.class)
    @GetMapping(value = API_TAGS_TAG_ID)
    public Tag findById(@PathVariable(API_TAG_ID) Long tagId) {
        return tagService.findOne(tagId);
    }

    /**
     * Достать все тэги
     *
     * @return - список тэгов
     */
    @ApiOperation(value = "View a list of available tags", response = Tag.class, responseContainer = "List")
    @GetMapping
    public List<Tag> findAllTags() {
        return tagService.findAll();
    }

    /**
     * Изменить тэг
     *
     * @param tag - данные тэга для изменения в формате json
     * @return - Tag
     */
    @ApiOperation(value = "Update tag", response = Tag.class)
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Tag update(@RequestBody Tag tag) {
        return tagService.update(tag);
    }

    /**
     * Удалить тэг по id
     *
     * @param tagId - id тэга
     */
    @ApiOperation(value = "Delete tag by id")
    @DeleteMapping(value = API_TAGS_TAG_ID)
    public void remove(@PathVariable(API_TAG_ID) Long tagId) {
        tagService.delete(tagId);
    }

}
