package ru.volt.demovolt;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import ru.volt.demovolt.interfaces.BtnCallBack;

/**
 * Created by dave on 07.05.16.
 */
public class AppConfig {
    public static final int DELAY = 55;

    public static final String PREF_FILENAME = "prefs";
    public static final String PREF_RECIVED_STATE = "recivedState";
    public static final int MES_GOTO_MAIN_SCREEN = 0;
    public static final int MES_GOTO_DETAIL_SCREEN = 1;

    private static Context context = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static EventBus eventBus;
    private static RecordType[] records = null;

    public AppConfig(Context ctx) {
        context = ctx;
        this.sharedPreferences = context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    public static EventBus getEventBus() {
        if (eventBus == null) {
            synchronized (AppConfig.class) {
                if (eventBus == null) {
                    eventBus = new EventBus();
                }
            }
        }
        return eventBus;
    }

    public static void setRecords(RecordType[] rec) {
        records = rec;
    }

    public static RecordType[] getRecords() {
        return records;
    }

    public void setDataRecived(boolean recived) {
        editor.putBoolean(PREF_RECIVED_STATE, recived);
        editor.commit();
    }

    public boolean getDataRecived() {
        return sharedPreferences.getBoolean(PREF_RECIVED_STATE, false);
    }

    public static void setContext(Context ctx) {
        context = ctx;
    }

    public static Context getContext() {
        return context;
    }

    public static void alertDialog(final Activity activity,
                                   String title, String message, int icon, View view, final int btnTextSize,
                                   final String neuBtnText, final BtnCallBack neuBtnCallBack,
                                   final String negBtnText, final BtnCallBack negBtnCallBack,
                                   final String posBtnText, final BtnCallBack posBtnCallBack) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        if (title != null) {
            builder.setTitle(title);
        }
        if (message != null) {
            builder.setMessage(message);
        }
        if (icon != 0) {
            builder.setIcon(icon);
        }
        if (view != null) {
            builder.setView(view);
        }
        builder.setCancelable(false);

        if (neuBtnText != null) {
            builder.setNeutralButton(neuBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (neuBtnCallBack != null) {
                        neuBtnCallBack.onBtnClick();
                    }
                    dialog.cancel();
                }
            });
        }
        if (negBtnText != null) {
            builder.setNegativeButton(negBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (negBtnCallBack != null) {
                        negBtnCallBack.onBtnClick();
                    }
                    dialog.cancel();
                }
            });
        }
        if (posBtnText != null) {
            builder.setPositiveButton(posBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (posBtnCallBack != null) {
                        posBtnCallBack.onBtnClick();
                    }
                    dialog.cancel();
                }
            });
        }

        final AlertDialog alert = builder.create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                if (neuBtnText != null) {
                    alert.getButton(DialogInterface.BUTTON_NEUTRAL)
                            .setTextColor(activity.getResources().getColor(R.color.colorBlack));
                    if (btnTextSize != 0) {
                        alert.getButton(DialogInterface.BUTTON_NEUTRAL).setTextSize(btnTextSize);
                    }
                }
                if (negBtnText != null) {
                    alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                            .setTextColor(activity.getResources().getColor(R.color.colorBlack));
                    if (btnTextSize != 0) {
                        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(btnTextSize);
                    }
                }
                if (posBtnText != null) {
                    alert.getButton(DialogInterface.BUTTON_POSITIVE)
                            .setTextColor(activity.getResources().getColor(R.color.colorBlack));
                    if (btnTextSize != 0) {
                        alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(btnTextSize);
                    }
                }
            }
        });
        alert.show();
    }
}
