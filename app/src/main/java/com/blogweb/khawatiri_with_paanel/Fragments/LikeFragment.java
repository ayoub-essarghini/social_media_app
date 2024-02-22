package com.blogweb.khawatiri_with_paanel.Fragments;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogweb.khawatiri_with_paanel.Adapters.FavAdapter;
import com.blogweb.khawatiri_with_paanel.Databases.favDB;
import com.blogweb.khawatiri_with_paanel.R;

import java.util.ArrayList;


public class LikeFragment extends Fragment {
    RecyclerView recyclerView;
    FavAdapter customAdapter;
    private ArrayList<String> quotes_fav;
    favDB myDB;
    RelativeLayout layout_empty;

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      view= inflater.inflate(R.layout.fragment_like, container, false);

        myDB = new favDB(getContext());
        layout_empty = view.findViewById(R.id.empty_fav);
        quotes_fav = new ArrayList<>();
        recyclerView = view.findViewById(R.id.res_Fav);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        customAdapter = new FavAdapter(getContext(), quotes_fav, true);
        storeDataInArrays();

        recyclerView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();




      return view;
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            layout_empty.setVisibility(View.VISIBLE);

        } else {
            while (cursor.moveToNext()) {

                quotes_fav.add(new String(cursor.getString(1)));

            }

            myDB.close();


        }

    }

    @Override
    public void onResume() {
        super.onResume();
        customAdapter.notifyDataSetChanged();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            customAdapter.notifyDataSetChanged();
        }
        super.onHiddenChanged(hidden);
    }
}