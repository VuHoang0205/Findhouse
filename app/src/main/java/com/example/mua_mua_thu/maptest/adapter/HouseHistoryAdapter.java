package com.example.mua_mua_thu.maptest.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.dialog.DeleteDialog;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.view.EditHouseActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class HouseHistoryAdapter extends RecyclerView.Adapter<HouseHistoryAdapter.ViewHolder> {
    private Context context;
    private List<NhaTro> nhaTroList;
    private LayoutInflater inflater;

    public HouseHistoryAdapter(Context context, List<NhaTro> nhaTroList) {
        this.context = context;
        this.nhaTroList = nhaTroList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_house_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NhaTro nhaTro = nhaTroList.get(position);
        holder.txtTenNhaTro.setText(nhaTro.getTenChuTro());
        holder.txtNgayDang.setText(nhaTro.getNgayDang());
        holder.txtDiaChi.setText("Địa chỉ: " + nhaTro.getDiaChi().getTenDiaChi());
        holder.txtDienTich.setText("Diện tích: " + nhaTro.getDienTich());
        holder.txtTienPhong.setText("Tiền phòng: " + nhaTro.getGiaPhong() + "đ");
        if (nhaTro.getHinhAnhList().size() > 0) {
            StorageReference hinhAnh = FirebaseStorage.getInstance()
                    .getReference().child("hinhanhs").child(nhaTro.getHinhAnhList().get(0));
            Log.d("Place", "onBindViewHolder: " + hinhAnh + " ");
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(hinhAnh)
                    .into(holder.imgNhaTro);
        }
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDialog deleteDialog = new DeleteDialog(context, nhaTro);
                deleteDialog.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditHouseActivity.class);
                SharedPreferences sharedPreferences = context.getSharedPreferences(Contants.COMMENTS,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(nhaTro.getIdUser(),Contants.NHA_TRO_ID);
                editor.apply();
                intent.putExtra("house", nhaTro);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return nhaTroList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgNhaTro, ivDelete;
        private TextView txtTenNhaTro, txtNgayDang, txtDiaChi, txtDienTich, txtTienPhong;


        public ViewHolder(View itemView) {
            super(itemView);
            imgNhaTro = itemView.findViewById(R.id.txt_img_avatar_history);
            txtTenNhaTro = itemView.findViewById(R.id.txt_ten_chu_tro_history);
            txtNgayDang = itemView.findViewById(R.id.txt_gio_dang_history);
            txtDiaChi = itemView.findViewById(R.id.txt_dia_chi_history);
            txtDienTich = itemView.findViewById(R.id.txt_dien_tich_history);
            txtTienPhong = itemView.findViewById(R.id.txt_gia_phong_history);
            ivDelete = itemView.findViewById(R.id.iv_delete);


        }
    }


}
