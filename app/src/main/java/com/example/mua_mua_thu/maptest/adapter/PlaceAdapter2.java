package com.example.mua_mua_thu.maptest.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.define.ILoadmore;
import com.example.mua_mua_thu.maptest.model.BinhLuan;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0, VIEW_TYPE_LOADING = 1;
    ILoadmore loadMore;
    boolean isLoading;
    Context context;
    List<NhaTro> nhaTroList;
    int visibleThreshold = 5;
    int lastVisibleItem, totalItemCount;

    public PlaceAdapter2(Context context, List<NhaTro> nhaTroList, RecyclerView recyclerView) {
        this.context = context;
        this.nhaTroList = nhaTroList;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount(); // Lấy tổng số lượng item đang có
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition(); // Lấy vị trí của item cuối cùng
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) // Nếu không phải trạng thái loading và tổng số lượng item bé hơn hoặc bằng vị trí item cuối + số lượng item tối đa hiển thị
                {
                    if (loadMore != null)
                        loadMore.onLoadMore(); // Gọi callback interface Loadmore
                    isLoading = true;
                }

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return nhaTroList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;// So sánh nếu item được get tại vị trí này là null thì view đó là loading view , ngược lại là item
    }

    public void setLoadMore(ILoadmore loadMore) {
        this.loadMore = loadMore;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_places, parent, false);
            final RecyclerView.ViewHolder holder = new ItemViewHolder(view);
            return holder;
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_loading, parent, false);

            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            showNhaTro(viewHolder, position);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return nhaTroList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenChuTro, txtDiaChi, txtNoiDung1, txtNoiDung2,
                txtTieuDe1, txtTieuDe2, txtDiem1, txtDiem2, txtTongBL, txtTongAnhBL, txtDiemTB,
                txtDienTich, txtGiaPhong, txtKhoangCach, txtTenDiaChi, txtNgayDang;
        Button btnDatPhong;
        ImageView imgNhaTro;
        CircleImageView civBluan1, civBluan2;
        LinearLayout llmContainer1, llmContainer2;
        CardView cardViewPlace;

        public ItemViewHolder(View itemView) {
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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
    private void showNhaTro(ItemViewHolder holder, int position) {

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
    }
    private void getHinhBinhLuan(CircleImageView circleImageView, String linkHinh) {
        StorageReference hinhAnhBL = FirebaseStorage.getInstance().getReference().child("thanhviens")
                .child(linkHinh);
        Glide.with(context).using(new FirebaseImageLoader())
                .load(hinhAnhBL)
                .into(circleImageView);
    }
}
