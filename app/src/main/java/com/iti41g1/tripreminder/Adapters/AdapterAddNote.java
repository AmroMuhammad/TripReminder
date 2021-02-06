package com.iti41g1.tripreminder.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iti41g1.tripreminder.R;
import com.iti41g1.tripreminder.controller.Fragments.FragmentAddNotes;

import java.util.ArrayList;
import java.util.List;

public class AdapterAddNote extends RecyclerView.Adapter<AdapterAddNote.viewHolder> {
    @NonNull

    List<String> notesList;
    Context context;

    public AdapterAddNote(List<String> notesList, Context context) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);
        EditText editText = view.findViewById(R.id.editTxtNote);
      //  editText.setText("");
        Log.i(FragmentAddNotes.TAG, "onCreateViewHolder: " + notesList.toString());
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAddNote.viewHolder holder, int position) {
           String note=notesList.get(position);
            holder.getEditText().setText(note);
        //  holder.note.setText("");
        Log.i(FragmentAddNotes.TAG, "onBindViewHolder: " + notesList.get(position));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public List<String> getNotesList() {
        return notesList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        EditText note;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            note = itemView.findViewById(R.id.editTxtNote);
            note.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    notesList.set(getAdapterPosition(), s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }

        public EditText getEditText() {
            return note;
        }

    }
}
