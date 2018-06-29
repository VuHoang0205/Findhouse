package com.example.mua_mua_thu.maptest.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.contants.Contants;
import com.example.mua_mua_thu.maptest.define.DeleteBinhluanInterface;
import com.example.mua_mua_thu.maptest.model.BinhLuan;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.model.BinhLuan;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class BinhLuanAdapter extends RecyclerView.Adapter<BinhLuanAdapter.ViewHolder> {
    private Context context;
    private List<BinhLuan> binhLuanList;
    private LayoutInflater inflater;
    private SharedPreferences sharedPreferences;
    private DeleteBinhluanInterface deleteBinhluanInterface;

    public BinhLuanAdapter(Context context, List<BinhLuan> binhLuanList, DeleteBinhluanInterface deleteBinhluanInterface) {
        this.context = context;
        this.binhLuanList = binhLuanList;
        this.deleteBinhluanInterface = deleteBinhluanInterface;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_custom_binh_luan, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final BinhLuan binhLuan = binhLuanList.get(position);
        sharedPreferences = context.getSharedPreferences(Contants.USERS, MODE_PRIVATE);
        final String idUser = sharedPreferences.getString(Contants.ID_USER, "0");
        // TODO xem lại load ảnh bình luận có thẻ lỗi
        String url = binhLuan.getThanhVien().getHinhAnh();
        int s = url.indexOf(".");
        String nameHinh = url.substring(0, s);
        Log.d("", "onBindViewHolder: " + url);
        if (binhLuan.getThanhVien() != null) {
            holder.txtTieuDe.setText(binhLuan.getThanhVien().getHoTen());
            holder.txtNoiDung.setText(binhLuan.getNoidung());
            holder.txtDiem.setText(String.valueOf(binhLuan.getChamdiem()));
            setHinhBinhLuan(holder.imgAnhBinhLuan, nameHinh);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                sharedPreferences = context.getSharedPreferences(Contants.COMMENTS, MODE_PRIVATE);
                String idHouse = sharedPreferences.getString("id_house", "");
                String id = sharedPreferences.getString(Contants.NHA_TRO_ID, "");

                if (idUser.equals(binhLuan.getMauser()) || id.equals(idUser)) {

                    Log.d("", "onLongClick: " + idHouse);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("binhluans").child(idHouse).child(binhLuan.getCommentId());
                    databaseReference.removeValue();
                    binhLuanList.remove(position);
                    notifyDataSetChanged();
                    deleteBinhluanInterface.deleteInterface();
                    Toast.makeText(context, "Bạn đã xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Bình luận này không phải của bạn", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        int soBL = binhLuanList.size();
        if (soBL > 0 && soBL > 5) {
            return 5;
        } else {
            return binhLuanList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgAnhBinhLuan;
        private TextView txtTieuDe, txtNoiDung, txtDiem;
        private RecyclerView recyclerViewHinhBL;

        public ViewHolder(View itemView) {
            super(itemView);
            imgAnhBinhLuan = itemView.findViewById(R.id.civ_binh_luan_1_custom);
            txtDiem = itemView.findViewById(R.id.txt_diem_1_custom);
            txtNoiDung = itemView.findViewById(R.id.txt_noi_dung_bl_1_custom);
            txtTieuDe = itemView.findViewById(R.id.txt_tieu_de_bl_1_custom);
//
        }
    }

    public void setHinhBinhLuan(CircleImageView circleImageView, String linkHinh) {
        StorageReference hinhAnhBL = FirebaseStorage.getInstance().getReference().child("thanhviens")
                .child(linkHinh);
        Glide.with(context).using(new FirebaseImageLoader())
                .load(hinhAnhBL)
                .into(circleImageView);

    }
}
