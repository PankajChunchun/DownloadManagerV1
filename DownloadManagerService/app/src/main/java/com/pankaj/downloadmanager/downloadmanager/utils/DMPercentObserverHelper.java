package com.pankaj.downloadmanager.downloadmanager.utils;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.interfaces.DownloadableObjPercentCallback;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Observer/ Subscriber helper to track current downloading status.
 * <p/>
 * Created by Pankaj Kumar on 7/16/2017.
 * pankaj.arrah@gmail.com
 */
public class DMPercentObserverHelper {
    private ObservableEmitter percentageObservableEmitter;
    private Disposable downloadPercentDisposable;
    private final DownloadableObjPercentCallback callback;

    public DMPercentObserverHelper(DownloadableObjPercentCallback callback) {
        this.callback = callback;
        ObservableOnSubscribe observableOnSubscribe = new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                percentageObservableEmitter = e;
            }
        };

        final Observable observable = Observable.create(observableOnSubscribe);

        final Observer subscriber = getObserver();
        observable.subscribeWith(subscriber);
    }

    public ObservableEmitter getPercentageObservableEmitter() {
        return percentageObservableEmitter;
    }

    private Observer getObserver() {
        return new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                downloadPercentDisposable = d;
            }

            @Override
            public void onNext(Object value) {
                if (!(value instanceof DownloadableObject)) {
                    return;
                }
                callback.updateDownloadableObject((DownloadableObject) value);
            }

            @Override
            public void onError(Throwable e) {
                if (downloadPercentDisposable != null) {
                    downloadPercentDisposable.dispose();
                }
            }

            @Override
            public void onComplete() {
                if (downloadPercentDisposable != null) {
                    downloadPercentDisposable.dispose();
                }
            }
        };
    }

    public void performCleanUp() {
        if (downloadPercentDisposable != null) {
            downloadPercentDisposable.dispose();
        }
    }
}
