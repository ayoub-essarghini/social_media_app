package com.blogweb.khawatiri_with_paanel.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.blogweb.khawatiri_with_paanel.Models.Category;
import com.blogweb.khawatiri_with_paanel.R;

import java.util.List;

public class CategoriesAdaper extends BaseAdapter {
    private Context context;
    private List<Category> categories_list;

    public CategoriesAdaper(Context context, List<Category> categories) {
        this.context = context;
        this.categories_list = categories;

    }

    @Override
    public int getCount() {
        return categories_list != null ? categories_list.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.category_layout,viewGroup,false);
        TextView id = view1.findViewById(R.id.categ_id);
        CardView card = view1.findViewById(R.id.spin_item);
        TextView category_name = view1.findViewById(R.id.categ_name);

        id.setText(String.valueOf(categories_list.get(i).getId()));
        category_name.setText(categories_list.get(i).getCategory_name());

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+ categories_list.get(i).getId(), Toast.LENGTH_SHORT).show();
            }
        });



        return view1;
    }
}
