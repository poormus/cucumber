package com.example.architecturecompsqlbased;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ListAdapter<Note,NoteAdapter.NoteHolder> implements Filterable {
List<Note> mNote;
List<Note> mFilteredNote;
private String lastFilter = "";
int Id;

    //for interface
    private OnItemClickListener listener;

    // to add animation and calculate the position of the list...
    public NoteAdapter() {
        super(DIFF_CALLBACK);
        mNote=new ArrayList<>();
        mFilteredNote=new ArrayList<>();
    }

    protected List<Note> getFilteredResults(String constraint){
     List<Note> results=new ArrayList<>();
     for(Note note: mNote){
         if(note.getTitle().toLowerCase().contains(constraint)){
             results.add(note);
         }
     }
     return results;
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                mFilteredNote.clear();
                lastFilter=constraint.toString();
                if (constraint.length() == 0) {
                    mFilteredNote.addAll(mNote);
                }else{
                    mFilteredNote=getFilteredResults(constraint.toString().toLowerCase());
                }
                FilterResults results = new FilterResults();
                results.values=mFilteredNote;
                results.count=mFilteredNote.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
              mFilteredNote= (List<Note>) results.values;
              notifyDataSetChanged();
            }
        };
    }
    public void setNote(List<Note> mNote){
        this.mNote=mNote;
        getFilter().filter(lastFilter);

    }



    public static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK=new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId()==newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return  oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getPriority() == newItem.getPriority();
        }
    };


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {

        if(mFilteredNote!=null) {

            Note currentNote = mFilteredNote.get(position);
            Id=currentNote.getId();

            holder.setIsRecyclable(false);
            holder.textViewTitle.setText(currentNote.getTitle());
            holder.textViewDescription.setText(currentNote.getDescription());
            holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
            holder.itemView.setTag(Id);
        }
    }


    @Override
    public int getItemCount() {
        return mFilteredNote!=null?mFilteredNote.size():0;
    }

    public Note getNoteAt(int position) {

        String title=mFilteredNote.get(position).getTitle();
        for (int i = 0; i <mNote.size() ; i++) {
            if (title.equals(mNote.get(i).getTitle())) {
                position = i;
                break;
            }
        }
        return getItem(position);
    }






    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPriority;

        public NoteHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int positionId = (int) itemView.getTag();
                    int position=getAdapterPosition();
                    if (listener != null) {
                        String title=mFilteredNote.get(position).getTitle();
                        for (int i = 0; i <mNote.size() ; i++) {
                            if(title.equals(mNote.get(i).getTitle())){
                                position=i;
                                break;
                            }
                        }
                    }
                    if(position != RecyclerView.NO_POSITION){
                        assert listener != null;
                        listener.OnItemClick(getItem(position),positionId,position,itemView);

                    }
                }
            });
        }
    }


    //interface for clicking

    public interface OnItemClickListener {
        void OnItemClick(Note note,int id,int position,View view);
    }
   //pass the one above
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;

    }

}
