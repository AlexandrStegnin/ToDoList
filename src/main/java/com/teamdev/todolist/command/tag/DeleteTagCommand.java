package com.teamdev.todolist.command.tag;

import com.teamdev.todolist.command.Command;
import com.teamdev.todolist.entity.Tag;
import com.teamdev.todolist.service.TagService;

/**
 * @author Alexandr Stegnin
 */

public class DeleteTagCommand implements Command {

    private TagService tagService;
    private Tag tag;

    public DeleteTagCommand(TagService tagService, Tag tag) {
        this.tagService = tagService;
        this.tag = tag;
    }

    @Override
    public void execute() {
        tagService.delete(tag.getId());
    }
}
