package com.iti41g1.tripreminder.Models;

public class NoteModel {
    private String note;
    private Boolean isChecked;

    public NoteModel(String note, Boolean isChecked) {
        this.note = note;
        this.isChecked = isChecked;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public String getNote() {

        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
