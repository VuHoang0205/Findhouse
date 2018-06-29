package com.example.mua_mua_thu.maptest.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mua_mua_thu.maptest.R;

import java.util.List;

public class AnhNhaTroAdapter extends RecyclerView.Adapter<AnhNhaTroAdapter.ViewHolder> {
    private Context context;
    private List<Bitmap> bitmapList;
    private LayoutInflater inflater;

    public AnhNhaTroAdapter(Context context, List<Bitmap> bitmapList) {
        this.context = context;
        this.bitmapList = bitmapList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public AnhNhaTroAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_anh_nha_tro, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AnhNhaTroAdapter.ViewHolder holder, int position) {
        Bitmap bitmap = bitmapList.get(position);
        holder.imgNhaTro.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNhaTro;

        public ViewHolder(View itemView) {
            super(itemView);
            imgNhaTro = itemView.findViewById(R.id.img_avatar_nha_tro_custom);
        }
    }
}
