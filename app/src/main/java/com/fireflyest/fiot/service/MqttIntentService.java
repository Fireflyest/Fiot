package com.fireflyest.fiot.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.fireflyest.fiot.BaseActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashSet;
import java.util.Set;

public class MqttIntentService extends IntentService {

    private static final String TAG = "MqttIntentService";

    private static MqttClient mqttClient;
    private static String topicMain;
    private static final Set<String> deviceStateSet = new HashSet<>();
    private static final MqttConnectOptions connectOptions = new MqttConnectOptions();
    private static final MemoryPersistence persistence = new MemoryPersistence();

    private static final int SEND_QOS = 1;
    private static final int SUBSCRIBE_QOS = 2;

    public static final String ACTION_CREATE = "com.fireflyest.fiot.service.action.CREATE";
    public static final String ACTION_CLOSE = "com.fireflyest.fiot.service.action.CLOSE";
    public static final String ACTION_SEND = "com.fireflyest.fiot.service.action.SEND";
    public static final String ACTION_CONNECT_LOST = "com.fireflyest.fiot.service.action.CONNECT_LOST";
    public static final String ACTION_DEVICE_ONLINE = "com.fireflyest.fiot.service.action.DEVICE_ONLINE";
    public static final String ACTION_SEND_SUCCESS = "com.fireflyest.fiot.service.action.SEND_SUCCESS";
    public static final String ACTION_SEND_FAIL = "com.fireflyest.fiot.service.action.SEND_FAIL";
    public static final String ACTION_RECEIVER = "com.fireflyest.fiot.service.action.RECEIVER";
    public static final String ACTION_SUBSCRIBE = "com.fireflyest.fiot.service.action.SUBSCRIBE";
    public static final String ACTION_UNSUBSCRIBE = "com.fireflyest.fiot.service.action.UNSUBSCRIBE";

    public static final String EXTRA_DATA = "com.fireflyest.fiot.service.extra.DATA";
    public static final String EXTRA_TOPIC = "com.fireflyest.fiot.service.extra.TOPIC";
    public static final String EXTRA_USERNAME = "com.fireflyest.fiot.service.extra.USERNAME";
    public static final String EXTRA_PASSWORD = "com.fireflyest.fiot.service.extra.PASSWORD";
    public static final String EXTRA_CLIENTID = "com.fireflyest.fiot.service.extra.CLIENTID";

    public MqttIntentService() {
        super("MqttService");

    }

    /**
     *
     */
    public final MqttCallback callback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            broadcastUpdate(ACTION_CONNECT_LOST);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            String data = new String(message.getPayload());
            Log.d(TAG, topic+"??????????????????" + data);
            String address = topic.split("/")[1];
            // ???????????????
            if(!deviceStateSet.contains(address)){
                Log.d(TAG, topic+"????????????");
                deviceStateSet.add(address);
                broadcastUpdate(ACTION_DEVICE_ONLINE, address);
            }
            // ????????????
            broadcastDataAction(ACTION_RECEIVER, address, data);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    /**
     * ???????????????
     * @param context ?????????
     * @param clientId H(id) ??????topic??????
     * @param username ?????????
     * @param password ??????
     */
    public static void createClient(Context context, String clientId, String username, String password) {
        Intent intent = new Intent(context, MqttIntentService.class);
        intent.setAction(ACTION_CREATE);
        intent.putExtra(EXTRA_CLIENTID, clientId);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PASSWORD, password);
        context.startService(intent);
    }

    public static void closeClient(Context context) {
        Intent intent = new Intent(context, MqttIntentService.class);
        intent.setAction(ACTION_CLOSE);
        context.startService(intent);
    }

    /**
     * ?????????topic???????????????????????????
     * @param context ?????????
     * @param topic ?????? ??????topic
     * @param data ??????
     */
    public static void send(Context context, String topic, String data) {
        Intent intent = new Intent(context, MqttIntentService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_TOPIC, topic);
        context.startService(intent);
    }

    /**
     * ??????
     * @param context ?????????
     * @param topic ??????topic
     */
    public static void subscribe(Context context, String topic) {
        Intent intent = new Intent(context, MqttIntentService.class);
        intent.setAction(ACTION_SUBSCRIBE);
        intent.putExtra(EXTRA_TOPIC, topic);
        context.startService(intent);
    }

    /**
     * ????????????
     * @param context ?????????
     * @param topic ??????topic
     */
    public static void unsubscribe(Context context, String topic) {
        Intent intent = new Intent(context, MqttIntentService.class);
        intent.setAction(ACTION_UNSUBSCRIBE);
        intent.putExtra(EXTRA_TOPIC, topic);
        context.startService(intent);
    }

    public  static boolean isConnected(String topic){
        return deviceStateSet.contains(topic);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND.equals(action)) {
                final String topic = intent.getStringExtra(EXTRA_TOPIC);
                final String data = intent.getStringExtra(EXTRA_DATA);
                if (data != null) {
                    handleActionSend(topic, data);
                }
            }else if (ACTION_CREATE.equals(action)) {
                final String clientId   = intent.getStringExtra(EXTRA_CLIENTID);
                final String username = intent.getStringExtra(EXTRA_USERNAME);
                final String password = intent.getStringExtra(EXTRA_PASSWORD);
                try {
                    if (clientId != null) {
                        handleActionCreate(clientId, username, password);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }else if (ACTION_SUBSCRIBE.equals(action)) {
                final String topic   = intent.getStringExtra(EXTRA_TOPIC);
                try {
                    handleActionSubscribe(topic);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }else if (ACTION_UNSUBSCRIBE.equals(action)) {
                final String topic   = intent.getStringExtra(EXTRA_TOPIC);
                try {
                    handleActionUnsubscribe(topic);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }else if (ACTION_CLOSE.equals(action)) {
                try {
                    handleActionClose();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleActionSend(String topic, String data) {
        String deviceTopic = String.format("%s/%s", topicMain, topic);
        // ????????????
        MqttMessage message = new MqttMessage(data.getBytes());
        // ???????????????????????????
        message.setQos(SEND_QOS);
        // ????????????
        try {
            Log.d(TAG, "???????????????" + deviceTopic);
            mqttClient.publish(deviceTopic, message);
            broadcastDataAction(ACTION_SEND_SUCCESS, deviceTopic, data);
        } catch (MqttException e) {
            broadcastDataAction(ACTION_SEND_FAIL, deviceTopic, data);
            e.printStackTrace();
        }
    }

    private void handleActionClose() throws MqttException {
        mqttClient.disconnect();
        mqttClient.close();
        persistence.close();
    }

    private void handleActionCreate(String clientId, String username, String password) throws MqttException {
        // ???????????????????????????
        if (clientId.equals(topicMain)) return;
        topicMain = clientId;
        // ???????????????
        String url = String.format("tcp://%s:1883", BaseActivity.DEBUG_URL);
        Log.d(TAG, clientId + " ??????????????? " + url);
        mqttClient = new MqttClient(url, clientId, persistence);

        // ?????????????????????????????????????????????
        connectOptions.setCleanSession(false);
        // ????????????????????????
        connectOptions.setUserName(username);
        connectOptions.setPassword(password.toCharArray());
        // ?????????????????? ????????????
        connectOptions.setConnectionTimeout(30);
        // ???????????????????????? ???????????? ??????????????????1.5*20????????????????????????????????????????????????????????????????????????????????????????????????????????????
        connectOptions.setKeepAliveInterval(60);
        // ????????????
        mqttClient.connect(connectOptions);

        // ????????????

        mqttClient.setCallback(callback);
    }


    private void handleActionSubscribe(String topic) throws MqttException {
        String deviceTopic = String.format("%s/%s", topicMain, topic);

        if (mqttClient == null) {
            return;
        }
        Log.d(TAG, "?????????"+deviceTopic);

        mqttClient.subscribe(deviceTopic, SUBSCRIBE_QOS);

        deviceStateSet.add(topic);
    }

    private void handleActionUnsubscribe(String topic) throws MqttException {
        String deviceTopic = String.format("%s/%s", topicMain, topic);

        if (mqttClient == null) {
            return;
        }

        mqttClient.unsubscribe(deviceTopic);
        deviceStateSet.remove(topic);
    }

    /**
     * ???????????????UI??????
     * @param action ??????
     */
    private void broadcastUpdate(String action){
        broadcastUpdate(action, "");
    }

    private void broadcastUpdate(String action, String topic){
        broadcastDataAction(action, topic, "");
    }

    private void broadcastDataAction(String action, String topic, String data){
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(EXTRA_TOPIC, topic);
        intent.putExtra(EXTRA_DATA, data);
        sendBroadcast(intent);
    }

}