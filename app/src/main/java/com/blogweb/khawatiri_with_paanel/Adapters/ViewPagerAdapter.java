package com.blogweb.khawatiri_with_paanel.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.blogweb.khawatiri_with_paanel.R;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    private  int images[] = {R.drawable.img_share,R.drawable.img_copy,R.drawable.img_fav};
    private String titles[] = {"مشاركة","نسخ","المفضلة"};
    private String descs[] = {
            "يمكنك مشاركة الخواطر مع أصدقائك عبر تطبيقات التواصل الاجتماعي مباشرة",
            "يمكنك نسخ الخواطر والاحتفاظ بها بالمسودة الخاصة بك",
            "يمكنك اضافة الخواطر التي تعجبك الى قائمة المفضلة الخاصة بك بالتطبيق"};
    @Override
    public int getCount() {
        return titles.length;
    }



    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
     inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

     View v = inflater.inflate(R.layout.view_pager,container,false);

        ImageView imageView = v.findViewById(R.id.img_view_pager);
        TextView txtTitle = v.findViewById(R.id.txt_title_v_pager);
        TextView txtdesc = v.findViewById(R.id.txt_desc_v_pager);
        imageView.setImageResource(images[position]);
        txtTitle.setText(titles[position]);
        txtdesc.setText(descs[position]);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
