package com.fireflyest.fiot.data;

public class DeviceType {

    // 蓝牙连接，临时保存
    public static final int NON = 0x0000;

    // 本地蓝牙设备
    public static final int LOCAL = 0x0001;

    // 远程控制设备
    public static final int REMOTE = 0x0002;

    // 环境设备
    // (温湿度计)
    public static final int ENVIRONMENT = 0x0004;
    // (空调、新风机、加湿器)
    public static final int ENVIRONMENT_CONTROL = 0x0008;

    // 安防设备
    // (火警、震警、可燃气监控)
    public static final int SECURITY = 0x00010;
    // (门锁)
    public static final int SECURITY_LOCK = 0x00020;
    // (闭路监控)
    public static final int SECURITY_MONITOR = 0x00040;

    // 音视设备(电视、音响)
    public static final int AUDIO = 0x0080;
//    public static final int AUDIO = 0x0100;
//    public static final int AUDIO = 0x0200;

    // 灯光设备
    // (亮度监控)
    public static final int ILLUMINATION = 0x0400;
    // (灯)
    public static final int ILLUMINATION_LIGHT = 0x0800;
    // (窗帘)
    public static final int ILLUMINATION_WINDOW = 0x1000;

    // 电力(插座)
    public static final int ELECTRICITY = 0x2000;

}
