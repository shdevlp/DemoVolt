package ru.volt.demovolt.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.volt.demovolt.AppConfig;;
import ru.volt.demovolt.db.DbHelper;
import ru.volt.demovolt.R;
import ru.volt.demovolt.adapters.MyAdapter;
import ru.volt.demovolt.events.MessageEvent;
import ru.volt.demovolt.interfaces.BtnCallBack;

public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {
    private ListView listView;
    private DbHelper dbHelper;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView)view.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        dbHelper = DbHelper.getInstance();

        Thread thread = new Thread() {
            @Override
            public void run() {
                AppConfig.setRecords(dbHelper.selectRecords());
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();

            AppConfig.alertDialog(getActivity(), getString(R.string.error),
                    ex.getLocalizedMessage(),
                    R.mipmap.ic_launcher, null, 0,
                    getString(R.string.exit), new BtnCallBack() {
                        @Override
                        public void onBtnClick() {
                            getActivity().finish();
                        }
                    }, null, null, null, null);
        }


        listView.setAdapter(new MyAdapter(AppConfig.getRecords()));

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppConfig.getEventBus().post(new MessageEvent(AppConfig.MES_GOTO_DETAIL_SCREEN, position));
    }
}
