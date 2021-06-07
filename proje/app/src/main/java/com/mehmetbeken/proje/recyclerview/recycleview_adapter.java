package com.mehmetbeken.proje.recyclerview;

import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.mehmetbeken.proje.R;
import com.mehmetbeken.proje.fragment.listefragmentDirections;

import java.util.ArrayList;

public class recycleview_adapter extends RecyclerView.Adapter<recycleview_adapter.ArtHolder>   {

    private ArrayList<Integer> idList;
    private ArrayList<String> nameList;

    public recycleview_adapter(ArrayList<Integer> idList, ArrayList<String> nameList) {
        this.idList = idList;
        this.nameList = nameList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.recyclerview_row,parent,false);

        return new ArtHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, int position) {
    holder.test.setText(nameList.get(position));


    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
           listefragmentDirections.ActionListefragmentToKayitfragment action=listefragmentDirections.actionListefragmentToKayitfragment("old");
           action.setArtId(idList.get(position));
           action.setInfo("old");
           Navigation.findNavController(view).navigate(action);
        }
    });


    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    class ArtHolder extends RecyclerView.ViewHolder{

        TextView test;

        public ArtHolder(@NonNull View itemView) {
            super(itemView);

            test=itemView.findViewById(R.id.rowTextView);




        }
    }
}
