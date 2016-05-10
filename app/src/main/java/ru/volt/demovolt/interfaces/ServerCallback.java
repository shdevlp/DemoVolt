package ru.volt.demovolt.interfaces;

import ru.volt.demovolt.RecordType;

/**
 * Created by dave on 06.05.16.
 */
public interface ServerCallback {
    public void onSuccess(final RecordType[] result);
    public void onError(final String message);
}
