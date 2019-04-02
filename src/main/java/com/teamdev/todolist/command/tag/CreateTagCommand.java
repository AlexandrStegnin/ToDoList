package com.teamdev.todolist.command.tag;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.service.TagService;

/**
 * @author Alexandr Stegnin
 */

public class CreateTagCommand implements Command {

    private TagService tagService;
    private Tag tag;

    public CreateTagCommand(TagService tagService, Tag tag) {
        this.tagService = tagService;
        this.tag = tag;
    }

    @Override
    public void execute() {
        tagService.create(tag);
    }
}
