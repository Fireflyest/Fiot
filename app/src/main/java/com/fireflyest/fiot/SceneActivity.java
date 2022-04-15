package com.fireflyest.fiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.fireflyest.fiot.adapter.SentenceItemAdapter;
import com.fireflyest.fiot.bean.Home;
import com.fireflyest.fiot.databinding.ActivitySceneBinding;
import com.fireflyest.fiot.model.SceneViewModel;
import com.fireflyest.fiot.net.SentencesHttpRunnable;
import com.fireflyest.fiot.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class SceneActivity extends AppCompatActivity {

    private static final String TAG = "SceneActivity";

    private ActivitySceneBinding binding;
    private SceneViewModel model;

    private Home home;

    private final List<Integer> drawableList = new ArrayList<>();
    private final List<String> conditionList = new ArrayList<String>(){
        {
            add("手动切换");
            add("语音助手");
            add("定时");
            add("环境变化");
        }
    };

    private int selectIcon = 0;

    private ArrayAdapter<String> deviceArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scene);

        model = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(SceneViewModel.class);

        this.initView();
    }

    private void initView() {
        StatusBarUtil.StatusBarLightMode(this);

        binding.sceneToolbar.setTitle("");
        setSupportActionBar(binding.sceneToolbar);

        // 切换图标
        drawableList.add(R.drawable.ic_scene_outdoor);
        drawableList.add(R.drawable.ic_scene_gohome);
        drawableList.add(R.drawable.ic_scene_game);
        drawableList.add(R.drawable.ic_scene_movie);
        drawableList.add(R.drawable.ic_scene_sleep);
        drawableList.add(R.drawable.ic_scene_study);
        drawableList.add(R.drawable.ic_scene_icon1);
        drawableList.add(R.drawable.ic_scene_icon2);
        drawableList.add(R.drawable.ic_scene_icon3);
        drawableList.add(R.drawable.ic_scene_icon4);
        drawableList.add(R.drawable.ic_scene_icon5);
        drawableList.add(R.drawable.ic_scene_icon6);
        drawableList.add(R.drawable.ic_scene_icon7);
        binding.sceneIconSwitch.setFactory(() -> {
            // makeView返回的是当前需要显示的ImageView控件，用于填充进ImageSwitcher中。
            return new ImageView(SceneActivity.this);
        });
        binding.sceneIconSwitch.setImageResource(drawableList.get(selectIcon));
        binding.sceneNameEdit.setText("场景1");
        binding.sceneIconPre.setOnClickListener(v -> {
            if(selectIcon != 0){
                selectIcon--;
                binding.sceneIconSwitch.setInAnimation(this, R.anim.icon_slide_left_in);
                binding.sceneIconSwitch.setOutAnimation(this, R.anim.icon_slide_left_out);
                // 切换
                Log.d(TAG, String.format("切换上一张图标：%s", selectIcon));
                binding.sceneIconSwitch.setImageResource(drawableList.get(selectIcon));
                this.setSceneName();
            }
        });
        binding.sceneIconNext.setOnClickListener(v -> {
            if(selectIcon < drawableList.size()-1) {
                selectIcon++;
                binding.sceneIconSwitch.setInAnimation(this, R.anim.icon_slide_right_in);
                binding.sceneIconSwitch.setOutAnimation(this, R.anim.icon_slide_right_out);
                // 切换
                Log.d(TAG, String.format("切换下一张图标：%s", selectIcon));
                binding.sceneIconSwitch.setImageResource(drawableList.get(selectIcon));
                this.setSceneName();
            }
        });

        // 场景条件
        deviceArrayAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, conditionList);
        deviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.sceneConditionSpinner.setAdapter(deviceArrayAdapter);

        // 指令列表
        home = getIntent().getParcelableExtra(BaseActivity.EXTRA_HOME);
        SentenceItemAdapter sentenceItemAdapter = new SentenceItemAdapter(this, model.getSentenceList());
        binding.sceneSentenceList.setAdapter(sentenceItemAdapter);
        model.getSentenceData().observe(this, sentence -> {
            Log.d(TAG, String.format("name=%s", sentence.getName()));
            sentenceItemAdapter.addItem(sentence);
        });
        if (home != null) {
            new Thread(new SentencesHttpRunnable(home.getId(), model.getSentenceData())).start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) back();
        return true;
    }

    @Override
    public void onBackPressed() {
        this.back();
        super.onBackPressed();
    }

    private void setSceneName(){
        switch (selectIcon){
            case 0:
                binding.sceneNameEdit.setText("外出");
                break;
            case 1:
                binding.sceneNameEdit.setText("回家");
                break;
            case 2:
                binding.sceneNameEdit.setText("游戏");
                break;
            case 3:
                binding.sceneNameEdit.setText("电影");
                break;
            case 4:
                binding.sceneNameEdit.setText("睡觉");
                break;
            case 5:
                binding.sceneNameEdit.setText("学习");
                break;
            default:
                binding.sceneNameEdit.setText("自定义");
                break;
        }
    }

    private void back(){
        Intent intent = new Intent();
//        if (flag && control != null && device != null) {
//            intent.putExtra(MqttIntentService.EXTRA_DATA, binding.commandDataEdit.getText().toString());
//            intent.putExtra(BaseActivity.EXTRA_DEVICE, device);
//            intent.putExtra(BaseActivity.EXTRA_NAME, control+device.getNickname());
//            this.setResult(RESULT_OK, intent);
//        }else {
//            this.setResult(RESULT_CANCELED);
//        }
        this.finishAfterTransition();
    }

}