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
import com.iti41g1.tripreminder.database.Trip;

import java.util.ArrayList;

public class AdapterViewNote extends RecyclerView.Adapter<AdapterViewNote.viewHolder> {

    Context context;
    ArrayList<String> notes;
    public AdapterViewNote(ArrayList<String> notes,Context context){

        this.context=context;
        this.notes=notes;
    }
    @NonNull
    @Override
    public AdapterViewNote.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);
        Log.i(FragmentAddNotes.TAG, "onCreateViewHolder: " + notes.toString());
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewNote.viewHolder holder, int position) {

        holder.note.setText(notes.get(position));
        Log.i(FragmentAddNotes.TAG, "onBindViewHolder: " + notes.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        EditText note;
        public viewHolder(@NonNull View itemView) {
                super(itemView);
                note = itemView.findViewById(R.id.editTxtNote);
                note.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        notes.set(getAdapterPosition(), s.toString());
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }
        public EditText getNote() {
            return note;
        }


    }
}
