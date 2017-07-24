package com.pankaj.downloadmanager.downloadmanager.utils;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.interfaces.DownloadableObjCallback;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Subscriber helper for download requests.
 * <p/>
 * Created by Pankaj Kumar on 7/15/2017.
 * pankaj.arrah@gmail.com
 */
public class DMSubscriberHelper {
    private static final String TAG = DMSubscriberHelper.class.getSimpleName();
    private Subscription mSubscription;
    private FlowableEmitter mFlowableEmitter;
    private DownloadableObjCallback mDownloadableObjCallback;

    public DMSubscriberHelper(DownloadableObjCallback downloadableObjCallback) {
        mDownloadableObjCallback = downloadableObjCallback;
        FlowableOnSubscribe flowableOnSubscribe = new FlowableOnSubscribe() {
            @Override
            public void subscribe(FlowableEmitter e) throws Exception {
                mFlowableEmitter = e;
            }
        };

        final Flowable flowable = Flowable.create(flowableOnSubscribe, BackpressureStrategy.BUFFER);
        final Subscriber subscriber = getSubscriber();
        flowable.subscribeWith(subscriber);
    }

    public void requestNextDownloads(int number) {
        mSubscription.request(number);
    }

    public void emitNextItem(DownloadableObject toBeDownloaded) {
        mFlowableEmitter.onNext(toBeDownloaded);
    }

    private Subscriber getSubscriber() {
        final String TAG = DMSubscriberHelper.TAG + ">>" + Subscriber.class.getSimpleName();

        return new Subscriber() {
            @Override
            public void onSubscribe(Subscription s) {
                mSubscription = s;
                mSubscription.request(Constants.DownloadConfig.MAX_DOWNLOADS);
            }

            @Override
            public void onNext(Object o) {
                if (!(o instanceof DownloadableObject)) {
                    return;
                }
                DMLog.d(TAG, "Next item to download : " + ((DownloadableObject) o).getObjectUrl());
                mDownloadableObjCallback.onDownloadStarted((DownloadableObject) o);
            }

            @Override
            public void onError(Throwable t) {
                DMLog.d(TAG, "onError()");
            }

            @Override
            public void onComplete() {
                DMLog.d(TAG, "onComplete(). STOP SERVICE NOW");
            }
        };
    }

    public void performCleanUp() {
        if (mSubscription != null) {
            mSubscription.cancel();
        }
    }
}
