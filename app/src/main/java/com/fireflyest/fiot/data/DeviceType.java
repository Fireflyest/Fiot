package com.fireflyest.fiot.data;

public class DeviceType {

    // 已连接，临时保存
    public static final int NON = 0x00;

    // 普通蓝牙设备，未配置远程设置
    public static final int LOCAL = 0x01;

    // 远程控制设备，未配置设备类型
    public static final int REMOTE = 0x10;

    // 环境设备(温湿度计)
    public static final int ENVIRONMENT = 3;

    // 安防设备(监控、门锁)
    public static final int SECURITY = 4;

    // 音视设备(电视、音响)
    public static final int AUDIO = 5;

    // 灯光设备(灯、窗帘)
    public static final int ILLUMINATION = 6;

    // 电力(插座)
    public static final int ELECTRICITY = 7;

}
