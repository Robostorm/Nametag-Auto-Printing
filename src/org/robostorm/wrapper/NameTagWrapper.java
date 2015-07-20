package org.robostorm.wrapper;

import org.robostorm.model.NameTag;

import java.util.List;

public class NameTagWrapper {
    List<NameTag> nameTags;
    boolean[] deleted;

    public NameTagWrapper(){}

    public NameTagWrapper(List<NameTag> nameTags, boolean[] deleted) {
        this.nameTags = nameTags;
        this.deleted = deleted;
    }

    public NameTagWrapper(List<NameTag> nameTags) {
        this.nameTags = nameTags;
    }

    public List<NameTag> getNameTags() {
        return nameTags;
    }

    public void setNameTags(List<NameTag> nameTags) {
        this.nameTags = nameTags;
    }

    public boolean[] getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean[] deleted) {
        this.deleted = deleted;
    }
}
