package com.example.mua_mua_thu.maptest.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by S-O-S on 10/28/2017.
 */

public class PhotoAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    private NhaTro nhaTro;


    public PhotoAdapter(Context context, NhaTro nhaTro) {
        this.context = context;
        this.nhaTro = nhaTro;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return nhaTro.getHinhAnhList().size();
    }

    @Override
    // kiem tra xem Obj co phai la view khong
    public boolean isViewFromObject(View view, Object object) {
        // neu la view thi moi dua len viewPager;
        return object != null && object instanceof View && object == view;
    }

    @Override
    // tao ra view va du lieu len view dinh view len viewPager
    public Object instantiateItem(ViewGroup container, int position) {
        View view; // chinh la 1 item view trong viewPager;
        // Buoc 1 tao view  tuong ung create trong RCV
        view = inflater.inflate(R.layout.item_photo, container, false);
        ImageView imgPhoto = (ImageView) view.findViewById(R.id.img_photo);
        // Buoc 2 do du lieu len view
        if (nhaTro.getHinhAnhList().size() > 0) {
            StorageReference hinhAnh = FirebaseStorage.getInstance()
                    .getReference().child("hinhanhs").child(nhaTro.getHinhAnhList().get(position));
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(hinhAnh)
                    .into(imgPhoto);
        }

//        imgPhoto.setImageBitmap(nhaTro.getBitmapList().get(position));
        // dinh view vao ViewPager
        container.addView(view);
        return view;

    }

    @Override
    // xoa cai item khoi viewPager
    public void destroyItem(ViewGroup container, int position, Object object) {
        // object la item
        // kiem tra bang cau lenh tren isViewFromObject
        container.removeView((View) object);


    }
}
