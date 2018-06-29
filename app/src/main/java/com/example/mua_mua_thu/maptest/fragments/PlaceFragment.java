package com.example.mua_mua_thu.maptest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mua_mua_thu.maptest.adapter.PlaceAdapter;
import com.example.mua_mua_thu.maptest.define.SearchHouseInterface;
import com.example.mua_mua_thu.maptest.dialog.DialogSearchHouse;
import com.example.mua_mua_thu.maptest.model.BinhLuan;
import com.example.mua_mua_thu.maptest.model.NhaTro;
import com.example.mua_mua_thu.maptest.R;
import com.example.mua_mua_thu.maptest.define.PlaceInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PlaceFragment extends Fragment implements View.OnClickListener, SearchHouseInterface {
    private View rootView;
    private NhaTro nhaTro;
    private List<NhaTro> nhaTroList;
    private RecyclerView recyclerViewPlace;
    private PlaceAdapter placeAdapter;
    private LinearLayoutManager llm;
    private ProgressBar progressPlace;
    private SharedPreferences sharedPreferences;
    private NestedScrollView nestedScrollViewPlace;
    public static final String TAG = "kiem_tra";
    private Location currentLocation;
    private int itemDaco = 20;
    double latitude, longitude;
    private Button btnNearMy, btnReview, btnSearchByPrice, btnSreach;
    private boolean isSearch;
    private List<NhaTro> nhaTroListLocation, nhaTroListbyRating, nhaTroListPrice;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place, container, false);
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();
        getListNhaTro();
        registerlisner();


    }

    private void registerlisner() {
        btnNearMy.setOnClickListener(this);
        btnReview.setOnClickListener(this);
        btnSearchByPrice.setOnClickListener(this);
        btnSreach.setOnClickListener(this);
    }

    private void initializeComponents() {
        btnNearMy = rootView.findViewById(R.id.btn_near_my);
        btnReview = rootView.findViewById(R.id.btn_review);
        btnSearchByPrice = rootView.findViewById(R.id.btn_search_by_price);
        btnSreach = rootView.findViewById(R.id.btn_sreach);
        sharedPreferences = getContext().getSharedPreferences("toado", Context.MODE_PRIVATE);
        currentLocation = new Location("");
        latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0"));
        longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0"));
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);
        nestedScrollViewPlace = rootView.findViewById(R.id.netedScrollView_place);
        progressPlace = rootView.findViewById(R.id.progess_place);
        nhaTro = new NhaTro();
        recyclerViewPlace = rootView.findViewById(R.id.rcv_place_fragment);
        llm = new LinearLayoutManager(getContext());
        recyclerViewPlace.setLayoutManager(llm);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getListNhaTro() {
        nhaTroList = new ArrayList<>();
        final PlaceInterface placeInterface = new PlaceInterface() {
            @Override
            public void getListNhaTro(NhaTro nhaTro) {
                if (nhaTro.isDatPhong()) {
                    nhaTroList.add(nhaTro);
                    Collections.sort(nhaTroList, new Comparator<NhaTro>() {
                        Date date1;
                        Date date2;

                        @Override
                        public int compare(NhaTro o1, NhaTro o2) {
                            String stringDateO1 = o1.getNgayDang();
                            String stringDateO2 = o2.getNgayDang();
                            try {
                                date1 = new SimpleDateFormat("HH:mm 'at' dd.MM.yyyy").parse(stringDateO1);
                                date2 = new SimpleDateFormat("HH:mm 'at' dd.MM.yyyy").parse(stringDateO2);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            o1.setDate(date1);
                            o2.setDate(date2);
                            Log.d(TAG, " : " + o1.getDate());
                            return date2.compareTo(date1);
                        }
                    });
                    setAdapter(placeAdapter, nhaTroList);
                    progressPlace.setVisibility(View.GONE);
                    Log.d(TAG, "getListNhaTro: " + nhaTroList);
                }
            }
        };
        nestedScrollViewPlace.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY >= v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight()) {
                        itemDaco += 20;
                        nhaTro.getListNhaTro(placeInterface, currentLocation, itemDaco + 20, itemDaco - 20);
                    }
                }
            }
        });

        nhaTro.getListNhaTro(placeInterface, currentLocation, itemDaco, 0);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_near_my:
                searchNearMy();
                break;
            case R.id.btn_review:
                searchByRating();
                break;
            case R.id.btn_search_by_price:
                searchByPrice();
                break;
            case R.id.btn_sreach:
                searchInRequest();
                break;
            default:
                break;
        }
    }


    private void searchByPrice() {
        if (!isSearch) {
            List<NhaTro> nhaTros = new ArrayList<>();
            nhaTros.addAll(nhaTroList);
            Collections.sort(nhaTros, new Comparator<NhaTro>() {
                @Override
                public int compare(NhaTro o1, NhaTro o2) {
                    return o1.getGiaPhong() > o2.getGiaPhong() ? 1 : -1;
                }
            });
            setAdapter(placeAdapter, nhaTros);
            Toast.makeText(getContext(), "Tìm kiếm theo giá của Nhà Trọ", Toast.LENGTH_SHORT).show();
        } else {
            Collections.sort(nhaTroListPrice, new Comparator<NhaTro>() {
                @Override
                public int compare(NhaTro o1, NhaTro o2) {
                    return o1.getGiaPhong() > o2.getGiaPhong() ? 1 : -1;
                }
            });
            setAdapter(placeAdapter, nhaTroListPrice);
            Toast.makeText(getContext(), "Tìm kiếm theo giá của Nhà Trọ", Toast.LENGTH_SHORT).show();
        }
        isSearch = false;
    }

    private void searchByRating() {
        if (!isSearch) {
            final List<NhaTro> nhaTros = new ArrayList<>();
            nhaTros.addAll(nhaTroList);
            Collections.sort(nhaTros, new Comparator<NhaTro>() {
                @Override
                public int compare(NhaTro o1, NhaTro o2) {
                    double diem1 = 0;
                    double diem2 = 0;
                    double tongDiem1;
                    double tongDiem2;
                    for (int i = 0; i < o1.getBinhLuanList().size(); i++) {
                        diem1 += o1.getBinhLuanList().get(i).getChamdiem();
                    }
                    for (int i = 0; i < o2.getBinhLuanList().size(); i++) {
                        diem2 += o2.getBinhLuanList().get(i).getChamdiem();
                    }
                    tongDiem1 = diem1 / o1.getBinhLuanList().size();
                    tongDiem2 = diem2 / o2.getBinhLuanList().size();
                    return tongDiem1 > tongDiem2 ? -1 : 1;
                }
            });

            setAdapter(placeAdapter, nhaTros);
            Toast.makeText(getContext(), "Tìm kiếm theo điểm của Nhà Trọ", Toast.LENGTH_SHORT).show();
        } else {
            Collections.sort(nhaTroListbyRating, new Comparator<NhaTro>() {
                @Override
                public int compare(NhaTro o1, NhaTro o2) {
                    double diem1 = 0;
                    double diem2 = 0;
                    double tongDiem1;
                    double tongDiem2;
                    for (int i = 0; i < o1.getBinhLuanList().size(); i++) {
                        diem1 += o1.getBinhLuanList().get(i).getChamdiem();
                    }
                    for (int i = 0; i < o2.getBinhLuanList().size(); i++) {
                        diem2 += o2.getBinhLuanList().get(i).getChamdiem();
                    }
                    tongDiem1 = diem1 / o1.getBinhLuanList().size();
                    tongDiem2 = diem2 / o2.getBinhLuanList().size();
                    return tongDiem1 > tongDiem2 ? -1 : 1;
                }
            });

            setAdapter(placeAdapter, nhaTroListbyRating);
            Toast.makeText(getContext(), "Tìm kiếm theo điểm của Nhà Trọ", Toast.LENGTH_SHORT).show();
        }
        isSearch = false;
    }

    private void searchNearMy() {
        if (!isSearch) {
            List<NhaTro> nhaTroList1 = new ArrayList<>();
            nhaTroList1.addAll(nhaTroList);

            Collections.sort(nhaTroList1, new Comparator<NhaTro>() {
                @Override
                public int compare(NhaTro o1, NhaTro o2) {
                    return o1.getDiaChi().getKhoangCach() > o2.getDiaChi().getKhoangCach() ? 1 : -1;
                }
            });
            setAdapter(placeAdapter, nhaTroList1);
            Toast.makeText(getContext(), "Tìm kiếm theo vị trí của Nhà Trọ", Toast.LENGTH_SHORT).show();
        } else {
            Collections.sort(nhaTroListPrice, new Comparator<NhaTro>() {
                @Override
                public int compare(NhaTro o1, NhaTro o2) {
                    return o1.getDiaChi().getKhoangCach() > o2.getDiaChi().getKhoangCach() ? 1 : -1;
                }
            });
            setAdapter(placeAdapter, nhaTroListPrice);
            Toast.makeText(getContext(), "Tìm kiếm theo vị trí của Nhà Trọ", Toast.LENGTH_SHORT).show();
        }
        isSearch = false;
    }

    private void setAdapter(PlaceAdapter placeAdapter, List<NhaTro> nhaTroList) {
        placeAdapter = new PlaceAdapter(getContext(), nhaTroList);
        recyclerViewPlace.setAdapter(placeAdapter);
        placeAdapter.notifyDataSetChanged();
    }

    private void searchInRequest() {
        DialogSearchHouse dialogSearchHouse = new DialogSearchHouse(getContext(), this);
        dialogSearchHouse.show();
    }

    @Override
    public void searchHouse(long distanceMin, long distanceMax, long moneyMin, long moneyMax, String khuVuc) {
        List<NhaTro> listTemp = new ArrayList<>();
        isSearch = true;
        nhaTroListbyRating = new ArrayList<>();
        nhaTroListLocation = new ArrayList<>();
        nhaTroListPrice = new ArrayList<>();
        List<NhaTro> nhatroSearchList = new ArrayList<>();
        Toast.makeText(getContext(), "Danh sách tìm kiếm nhà trọ theo yêu cầu", Toast.LENGTH_SHORT).show();
        long khoangCachRequest = distanceMax - distanceMin;
        long moneyRequest = moneyMax - moneyMin;
        for (int i = 0; i < nhaTroList.size(); i++) {
            NhaTro nhaTro = nhaTroList.get(i);
            double khoangCach = nhaTro.getDiaChi().getKhoangCach() * 1000;
            double money = nhaTro.getGiaPhong();
            if (khoangCachRequest == 0) {
                if (moneyRequest == 0) {
                    listTemp.add(nhaTro);
                } else {
                    if (moneyMin <= money && money <= moneyMax) {
                        listTemp.add(nhaTro);
                    }
                }
            } else {
                if (moneyRequest == 0) {
                    if (distanceMin <= khoangCach && khoangCach <= distanceMax) {
                        listTemp.add(nhaTro);
                    }
                } else if (distanceMin <= khoangCach && khoangCach <= distanceMax
                        && moneyMin <= money && money <= moneyMax) {
                    listTemp.add(nhaTro);

                }
            }
        }
        if (!khuVuc.isEmpty()) {
            String request = khuVuc.trim().toUpperCase();
            for (int i = 0; i < listTemp.size(); i++) {
                String temp = listTemp.get(i).getDiaChi().getTenDiaChi();
                String tenDiaChi = temp.replace(",", " ").trim().toUpperCase();
                String fullDiaChi = listTemp.get(i).getDiaChi().getFullTenDiaChi().replace(",", " ").trim().toUpperCase();
                boolean check = tenDiaChi.contains(request);
                boolean check2 = fullDiaChi.contains(request);
                if (check || check2) {
                    nhatroSearchList.add(listTemp.get(i));
                }
            }
        } else {
            nhatroSearchList.addAll(listTemp);
        }
        nhaTroListbyRating.addAll(nhatroSearchList);
        nhaTroListLocation.addAll(nhatroSearchList);
        nhaTroListPrice.addAll(nhatroSearchList);
        setAdapter(placeAdapter, nhatroSearchList);
    }
}
