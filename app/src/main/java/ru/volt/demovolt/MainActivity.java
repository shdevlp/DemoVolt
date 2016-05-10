package ru.volt.demovolt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import org.greenrobot.eventbus.Subscribe;

import ru.volt.demovolt.db.DbHelper;
import ru.volt.demovolt.events.MessageEvent;
import ru.volt.demovolt.fragments.DetailFragment;
import ru.volt.demovolt.fragments.MainFragment;
import ru.volt.demovolt.interfaces.BtnCallBack;
import ru.volt.demovolt.net.RefRequest;
import ru.volt.demovolt.interfaces.ServerCallback;

public class MainActivity extends Activity {
    private final static String TAG = "MainActivity:";

    private MainFragment fragmentMain;
    private DetailFragment fragmentDetail;
    private DbHelper dbHelper;
    private AppConfig appConfig;
    private ProgressDialog dialog;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        appConfig = new AppConfig(getApplicationContext());

        AppConfig.getEventBus().register(this);

        fragmentMain = new MainFragment();
        fragmentDetail = new DetailFragment();

         //Получение данных с сервера если базы не существует или небыло загрузок ранее
        if (DbHelper.checkDatabase() == false || appConfig.getDataRecived() == false) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.load_data));
            dialog.show();

            dbHelper = DbHelper.getInstance();

            RefRequest request = RefRequest.getInstance(this);
            request.posts(new ServerCallback() {
                @Override
                public void onSuccess(final RecordType[] result) {
                    appConfig.setDataRecived(true);

                    final int size = result.length;

                    //"Долгая" операция добавления данных - работа в паралельном потоке
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            for (int i = 0; i < size; i++) {
                                RecordType record = result[i];
                                dbHelper.insertIntoRecord(record);
                            }
                        }
                    };
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        dialog.dismiss();

                        AppConfig.alertDialog(activity, getString(R.string.error), ex.getLocalizedMessage(),
                                R.mipmap.ic_launcher, null, 0,
                                getString(R.string.exit), new BtnCallBack() {
                                    @Override
                                    public void onBtnClick() {
                                        finish();
                                    }
                                }, null, null, null, null);

                    }

                    dialog.dismiss();

                    getFragmentManager().beginTransaction().add(R.id.fragment_id,
                            fragmentMain).commit();
                }

                @Override
                public void onError(String message) {
                    dialog.dismiss();

                    AppConfig.alertDialog(activity, getString(R.string.error), message,
                            R.mipmap.ic_launcher, null, 0,
                            getString(R.string.exit), new BtnCallBack() {
                                @Override
                                public void onBtnClick() {
                                    finish();
                                }
                            }, null, null, null, null);
                }
            });
        } else {
            getFragmentManager().beginTransaction().add(R.id.fragment_id,
                    fragmentMain).commit();
        }

    }


    /**
     * Переключение фрагментов по событию
     * @param event
     */
    @Subscribe
    public void onEvent(MessageEvent event) {
        switch (event.getMessage()){
            case AppConfig.MES_GOTO_MAIN_SCREEN:
                getFragmentManager().beginTransaction().replace(R.id.fragment_id,
                        fragmentMain).commit();
                break;
            case AppConfig.MES_GOTO_DETAIL_SCREEN: {
                final int position = event.getPosition();
                fragmentDetail.setPostition(position);
                getFragmentManager().beginTransaction().replace(R.id.fragment_id,
                        fragmentDetail).commit();
                }
                break;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }
}
