package com.brian.dmgnt.models;

public class Selection {

    private String selectionId, selection;

    public Selection(String selectionId, String selection) {
        this.selectionId = selectionId;
        this.selection = selection;
    }

    public String getSelectionId() {
        return selectionId;
    }

    public String getSelection() {
        return selection;
    }
}
