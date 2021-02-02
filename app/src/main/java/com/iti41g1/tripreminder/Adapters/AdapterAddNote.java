package com.iti41g1.tripreminder.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iti41g1.tripreminder.R;

import java.util.ArrayList;

import com.iti41g1.tripreminder.Models.NoteModel;

public class AdapterAddNote extends RecyclerView.Adapter<AdapterAddNote.viewHolder> {
    @NonNull

    ArrayList<NoteModel> notesList;
    Context context;
    public AdapterAddNote(ArrayList<NoteModel> notesList, Context context){
        this.context=context;
        this.notesList=notesList;
    }
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_row, parent, false);
        EditText editText = view.findViewById(R.id.editTxtNote);
        editText.setText("");
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAddNote.viewHolder holder, int position) {
        if (notesList.get(position).getNote().length() > 0)
            holder.note.setText(notesList.get(position).getNote());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
    public ArrayList<NoteModel> getNotesList(){
        return notesList;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        EditText note;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            note=itemView.findViewById(R.id.editTxtNote);
            note.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    notesList.set(getAdapterPosition(),new NoteModel(s.toString(),
                            notesList.get(getAdapterPosition()).getChecked()));
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
        public EditText getEditText() {
            return note ;
        }

    }
}
