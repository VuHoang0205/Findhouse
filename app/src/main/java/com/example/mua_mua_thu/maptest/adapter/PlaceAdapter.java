package com.example.mua_mua_thu.maptest.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.model.BinhLuan;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.view.ChiTietNhaTroActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    private Context context;
    private List<NhaTro> nhaTroList;
    private LayoutInflater inflater;

    public PlaceAdapter(Context context, List<NhaTro> nhaTroList) {
        this.context = context;
        this.nhaTroList = nhaTroList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_places, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        showNhaTro(holder, position);
    }

    @Override
    public int getItemCount() {
        return nhaTroList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenChuTro, txtDiaChi, txtNoiDung1, txtNoiDung2,
                txtTieuDe1, txtTieuDe2, txtDiem1, txtDiem2, txtTongBL, txtTongAnhBL, txtDiemTB,
                txtDienTich, txtGiaPhong, txtKhoangCach, txtTenDiaChi, txtNgayDang;
        Button btnDatPhong;
        ImageView imgNhaTro;
        CircleImageView civBluan1, civBluan2;
        LinearLayout llmContainer1, llmContainer2;
        CardView cardViewPlace;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTenChuTro = itemView.findViewById(R.id.txt_ten_chu_tro_place);
            btnDatPhong = itemView.findViewById(R.id.btn_book_room);
            imgNhaTro = itemView.findViewById(R.id.img_avatar_nha_tro);
            txtDiaChi = itemView.findViewById(R.id.txt_dia_chi_place);
            txtTieuDe1 = itemView.findViewById(R.id.txt_tieu_de_bl_1);
            txtTieuDe2 = itemView.findViewById(R.id.txt_tieu_de_bl_2);
            txtNoiDung1 = itemView.findViewById(R.id.txt_noi_dung_bl_1);
            txtNoiDung2 = itemView.findViewById(R.id.txt_noi_dung_bl_2);
            civBluan1 = itemView.findViewById(R.id.civ_binh_luan_1);
            civBluan2 = itemView.findViewById(R.id.civ_binh_luan_2);
            llmContainer1 = itemView.findViewById(R.id.lnl_container_1);
            llmContainer2 = itemView.findViewById(R.id.lnl_container_2);
            txtDiem1 = itemView.findViewById(R.id.txt_diem_1);
            txtDiem2 = itemView.findViewById(R.id.txt_diem_2);
            txtTongBL = itemView.findViewById(R.id.txt_tong_bl);
            txtTongAnhBL = itemView.findViewById(R.id.txt_tong_hinh_anh_bl);
            txtDiemTB = itemView.findViewById(R.id.txt_diem_tb);
            txtDienTich = itemView.findViewById(R.id.txt_dien_tich);
            txtGiaPhong = itemView.findViewById(R.id.txt_gia_phong);
            txtTenDiaChi = itemView.findViewById(R.id.txt_dia_chi_place);
            txtKhoangCach = itemView.findViewById(R.id.txt_khoang_cach);
            cardViewPlace = itemView.findViewById(R.id.cardview_place);
            txtNgayDang = itemView.findViewById(R.id.txt_gio_dang_place);

        }
    }

    private void getHinhBinhLuan(CircleImageView circleImageView, String linkHinh) {
        StorageReference hinhAnhBL = FirebaseStorage.getInstance().getReference().child("thanhviens")
                .child(linkHinh);
        Glide.with(context).using(new FirebaseImageLoader())
                .load(hinhAnhBL)
                .into(circleImageView);

    }

    public void showNhaTro(final ViewHolder holder, int position) {
        final NhaTro
                nhaTro = nhaTroList.get(position);
        holder.txtTenChuTro.setText(nhaTro.getTenChuTro());
        holder.txtGiaPhong.setText("Tiền phòng:  " + nhaTro.getGiaPhong() + "đ");
        holder.txtDienTich.setText("Diện tích:  " + nhaTro.getDienTich());
        holder.txtTenDiaChi.setText("Địa chỉ:  " + nhaTro.getDiaChi().getTenDiaChi());
        holder.txtKhoangCach.setText(String.format("%.1f", nhaTro.getDiaChi().getKhoangCach()) + " km");
        holder.txtNgayDang.setText(nhaTro.getNgayDang());
        if (nhaTro.isDatPhong()) {
            holder.btnDatPhong.setVisibility(View.VISIBLE);
            holder.btnDatPhong.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {
                    String number = nhaTro.getSoDT();
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:" + number));
                    context.startActivity(phoneIntent);
                }
            });
        }
        if (nhaTro.getHinhAnhList().size() > 0) {
            StorageReference hinhAnh = FirebaseStorage.getInstance()
                    .getReference().child("hinhanhs").child(nhaTro.getHinhAnhList().get(0));
            Log.d("Place", "onBindViewHolder: " + hinhAnh + " ");
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(hinhAnh)
                    .into(holder.imgNhaTro);
        }
        if (nhaTro.getBinhLuanList().size() > 0) {
            BinhLuan binhLuan1 = nhaTro.getBinhLuanList().get(0);
            holder.txtTieuDe1.setText(binhLuan1.getThanhVien().getHoTen());
            holder.txtNoiDung1.setText(binhLuan1.getNoidung());
            holder.txtDiem1.setText(binhLuan1.getChamdiem() + "");
            //Todo get image bl
            String url = binhLuan1.getThanhVien().getHinhAnh();
            int s = url.indexOf(".");
            String nameHinh = url.substring(0, s);
            getHinhBinhLuan(holder.civBluan1,nameHinh);
            if (nhaTro.getBinhLuanList().size() > 2) {
                BinhLuan binhLuan2 = nhaTro.getBinhLuanList().get(1);
                holder.txtTieuDe2.setText(binhLuan2.getThanhVien().getHoTen());
                holder.txtNoiDung2.setText(binhLuan2.getNoidung());
                holder.txtDiem2.setText(binhLuan2.getChamdiem() + "");
                String url2 = binhLuan2.getThanhVien().getHinhAnh();
                int s2 = url.indexOf(".");
                String nameHinh2 = url2.substring(0, s2);
                getHinhBinhLuan(holder.civBluan2,nameHinh2);
            } else {
                holder.llmContainer2.setVisibility(View.GONE);
            }
            holder.txtTongBL.setText(nhaTro.getBinhLuanList().size() + "");
            int tongHinhBL = 0;
            double diemTB = 0, tongDiem = 0;
            for (BinhLuan binhLuan : nhaTro.getBinhLuanList()) {
                tongHinhBL += binhLuan.getImgBinhLuans().size();
                tongDiem += binhLuan.getChamdiem();
            }
            diemTB = tongDiem / nhaTro.getBinhLuanList().size();
            holder.txtDiemTB.setText(String.format("%.1f", diemTB));
            if (tongHinhBL > 0) {
                holder.txtTongAnhBL.setText(tongHinhBL + " ");
            }
        } else {
            holder.llmContainer1.setVisibility(View.GONE);
            holder.llmContainer2.setVisibility(View.GONE);
        }
        holder.cardViewPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(Contants.COMMENTS, MODE_PRIVATE);
                String idHouse = nhaTro.getMaNhaTro();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("id_house", idHouse);
                editor.putString(Contants.NHA_TRO_ID, nhaTro.getIdUser());
                editor.commit();
                Intent intent = new Intent(context, ChiTietNhaTroActivity.class);
                intent.putExtra(Contants.NHA_TRO, nhaTro);
                intent.putExtra(Contants.KEY_LIST, 0);
                Log.d("vu", "onClick: " + nhaTro.getHinhAnhList());
                context.startActivity(intent);
            }
        });
    }
}
