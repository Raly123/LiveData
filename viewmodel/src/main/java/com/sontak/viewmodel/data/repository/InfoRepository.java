package com.sontak.viewmodel.data.repository;

import android.arch.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sontak.viewmodel.data.database.InfoDao;
import com.sontak.viewmodel.data.database.NewsBean;
import com.sontak.viewmodel.model.InfoBean;
import com.sontak.viewmodel.data.network.NewsApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * @package: com.sontak.livedata.data.repository
 * @author: Lei
 * @date: 2018-01-01 22:28
 * @version: V1.0.0
 * @description:
 */
public class InfoRepository {

    private InfoDao mInfoDao;

    private NewsApi mNewsApi;

    @Inject
    public InfoRepository(@Named("InfoDao") InfoDao infoDao, NewsApi newsApi) {
        mInfoDao = infoDao;
        mNewsApi = newsApi;
    }

    public LiveData<List<NewsBean>> loadNewsInfo() {
        updateInfo();
        return mInfoDao.getAll();
    }

    private void updateInfo() {
        mNewsApi.getNewsInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Subscriber<InfoBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(InfoBean infoBean) {
                        //不能在主线程使用
                        Gson gson = new Gson();
                        String temp = gson.toJson(infoBean.getNewslist());
                        List<NewsBean> list = gson.fromJson(temp, new TypeToken<List<NewsBean>>() {
                        }.getType());
                        mInfoDao.insertAll(list);
                    }
                });
    }
}
