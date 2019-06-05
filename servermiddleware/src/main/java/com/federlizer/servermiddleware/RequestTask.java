package com.federlizer.servermiddleware;

import android.os.AsyncTask;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class RequestTask extends AsyncTask<Void, Void, Result> {
    private Consumer<Result> callback;
    private Callable<Result> request;

    public RequestTask(Callable<Result> request, Consumer<Result> callback) {
        this.request = request;
        this.callback = callback;
    }

    @Override
    protected Result doInBackground(Void... voids) {
        // TODO uuuuuh..... I have no idea why this required me to surround it in a try catch... dafuq
        try {
            return request.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Result result) {
        callback.accept(result);
    }
}
