package com.mehmetbeken.proje.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mehmetbeken.proje.R;
import com.mehmetbeken.proje.recyclerview.recycleview_adapter;

import java.util.ArrayList;

public class listefragment extends Fragment {

    ArrayList<String> nameArray;
    ArrayList<Integer> idArray;
    recycleview_adapter adapter;





    public listefragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listefragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
        nameArray=new ArrayList<String>();
        idArray=new ArrayList<Integer>();

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter=new recycleview_adapter(idArray,nameArray);
        recyclerView.setAdapter(adapter);










        getData();
    }
    public void getData(){

        try {
            SQLiteDatabase database=getActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE,null);

            Cursor cursor=database.rawQuery("SELECT*FROM arts",null);

            int nameIx=cursor.getColumnIndex("artname");
            int idIx=cursor.getColumnIndex("id");

            while (cursor.moveToNext()){

                nameArray.add(cursor.getString(nameIx));
                idArray.add(cursor.getInt(idIx));


                adapter.notifyDataSetChanged();
            }

            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }










    }
}