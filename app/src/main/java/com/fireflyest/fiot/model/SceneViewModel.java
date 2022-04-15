package com.fireflyest.fiot.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Device;
import com.fireflyest.fiot.bean.Sentence;

import java.util.ArrayList;
import java.util.List;

public class SceneViewModel extends ViewModel {

    private final MutableLiveData<Sentence> sentenceData = new MutableLiveData<>();

    private final List<Sentence> sentenceList = new ArrayList<>();

    public MutableLiveData<Sentence> getSentenceData() {
        return sentenceData;
    }

    public List<Sentence> getSentenceList() {
        return sentenceList;
    }
}
