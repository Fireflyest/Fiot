package com.fireflyest.fiot.data;

public class DeviceType {

    // 蓝牙连接，临时保存
    public static final int NON = 0x0;

    // 本地蓝牙设备
    public static final int LOCAL = 0x1;

    // 远程控制设备
    public static final int REMOTE = 0x2;

    // 环境设备
    // (温湿度计)
    public static final int ENVIRONMENT = 0x4;
    // (空调、新风机、加湿器)
    public static final int ENVIRONMENT_CONTROL = 0x8;

    // 安防设备
    // (火警、震警、可燃气监控)
    public static final int SECURITY = 0x10;
    // (门锁)
    public static final int SECURITY_LOCK = 0x20;
    // (闭路监控)
    public static final int SECURITY_MONITOR = 0x40;
//    public static final int SECURITY_ = 0x80;

    // 音视设备(电视、音响)
    public static final int AUDIO = 0x100;
    // 语音助手
    public static final int AUDIO_ASSISTANT = 0x200;
//    public static final int AUDIO = 0x400;
//    public static final int AUDIO = 0x800;

    // 灯光设备
    // (亮度监控)
    public static final int ILLUMINATION = 0x1000;
    // (灯)
    public static final int ILLUMINATION_LIGHT = 0x2000;
    // (彩色灯关)
    public static final int ILLUMINATION_COLOR = 0x4000;
    // (窗帘)
    public static final int ILLUMINATION_WINDOW = 0x8000;

    // 电力(开关)
    public static final int ELECTRICITY = 0x10000;
    // (插座功率)
    public static final int ELECTRICITY_RATE = 0x20000;
//    public static final int ELECTRICITY = 0x40000;
//    public static final int ELECTRICITY = 0x80000;

}
