package ru.volt.demovolt.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import ru.volt.demovolt.AppConfig;
import ru.volt.demovolt.R;
import ru.volt.demovolt.RecordType;
import ru.volt.demovolt.db.DbHelper;
import ru.volt.demovolt.events.MessageEvent;
import ru.volt.demovolt.interfaces.BtnCallBack;

public class DetailFragment extends Fragment {
    private ImageView backBtn;
    private TextView tvUserId;
    private TextView tvRecordId;
    private TextView tvTitle;
    private TextView tvBody;
    private ImageView ivStar;
    private Button btnFavSetOn;
    private Button btnFavSetOff;

    private DbHelper dbHelper;

    private int postition;

    public DetailFragment() {
    }


    public void setPostition(int pos) {
        this.postition = pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        dbHelper = DbHelper.getInstance();

        btnFavSetOn = (Button)view.findViewById(R.id.btnFavSetOn);
        btnFavSetOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConfig.getRecords() != null) {
                    RecordType[] records = AppConfig.getRecords();
                    RecordType record = records[postition];
                    dbHelper.setFavoritesFlag(record, true);
                    ivStar.setImageResource(R.drawable.star_on);
                }
            }
        });

        btnFavSetOff = (Button)view.findViewById(R.id.btnFavSetOff);
        btnFavSetOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConfig.alertDialog(getActivity(), getString(R.string.information), getString(R.string.question),
                        R.mipmap.ic_launcher, null, 0, getString(R.string.yes), new BtnCallBack() {
                    @Override
                    public void onBtnClick() {
                        if (AppConfig.getRecords() != null) {
                            RecordType[] records = AppConfig.getRecords();
                            RecordType record = records[postition];
                            dbHelper.setFavoritesFlag(record, false);
                            ivStar.setImageResource(R.drawable.star_off);
                        }
                    }
                }, getString(R.string.no), null, null, null);
            }
        });

        backBtn = (ImageView)view.findViewById(R.id.ivBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConfig.getEventBus().post(new MessageEvent(AppConfig.MES_GOTO_MAIN_SCREEN, -1));
            }
        });

        tvUserId = (TextView)view.findViewById(R.id.tvUserId);
        tvRecordId = (TextView)view.findViewById(R.id.tvRecordId);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);
        tvBody = (TextView)view.findViewById(R.id.tvBody);
        ivStar = (ImageView)view.findViewById(R.id.ivStar);

        if (AppConfig.getRecords() != null) {
            RecordType[] records = AppConfig.getRecords();
            RecordType record = records[postition];
            tvUserId.setText(String.valueOf(record.getUserId()));
            tvRecordId.setText(String.valueOf(record.getRecordId()));
            tvTitle.setText(record.getTitle());
            tvBody.setText(record.getBody());

            if (record.getFavorites()) {
                ivStar.setImageResource(R.drawable.star_on);
            } else {
                ivStar.setImageResource(R.drawable.star_off);
            }
        }

        return view;
    }
}
