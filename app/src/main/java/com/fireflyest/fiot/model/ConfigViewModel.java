package com.fireflyest.fiot.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fireflyest.fiot.bean.Characteristic;
import com.fireflyest.fiot.bean.Service;
import com.fireflyest.fiot.service.BluetoothIntentService;

import java.util.ArrayList;
import java.util.List;

public class ConfigViewModel extends ViewModel {

    private final MutableLiveData<List<Service>> servicesData = new MutableLiveData<>();

    private final MutableLiveData<List<Characteristic>> characteristicsData = new MutableLiveData<>();

    public ConfigViewModel() {
    }

    public void initView(String address){
        List<BluetoothGattService> services = BluetoothIntentService.getService(address);
        if (services == null) {
            return;
        }
        List<Service> s = new ArrayList<>();
        for (BluetoothGattService service : services) {
            String uuidService = service.getUuid().toString();
            s.add(new Service(uuidService.substring(0, 8), uuidService, this.getServiceUsage(uuidService.substring(4, 8))));
        }
        servicesData.setValue(s);
        this.setCharacteristics(services.get(0));
    }

    public void setCharacteristics(BluetoothGattService service){
        List<Characteristic> c = new ArrayList<>();
        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
            StringBuilder characteristicBuilder = new StringBuilder();
            String uuidCharacteristic = characteristic.getUuid().toString();
            c.add(new Characteristic(uuidCharacteristic.substring(0, 8),
                            uuidCharacteristic,
                            this.getServiceUsage(uuidCharacteristic.substring(4, 8)),
                            this.getProprty(characteristic.getProperties())));
        }
        characteristicsData.setValue(c);
    }

    public MutableLiveData<List<Service>> getServicesData() {
        return servicesData;
    }

    public MutableLiveData<List<Characteristic>> getCharacteristicsData() {
        return characteristicsData;
    }

    /**
     * 获取特征可操作的
     * @param p Proprty
     * @return s
     */
    private String getProprty(int p) {
        StringBuilder builder = new StringBuilder();
        if ((p & 0x01) != 0) builder.append("广播,");
        if ((p & 0x02) != 0) builder.append("读取,");
        if ((p & 0x04) != 0) builder.append("无回应写入,");
        if ((p & 0x08) != 0) builder.append("写入,");
        if ((p & 0x10) != 0) builder.append("反馈信息,");
        if ((p & 0x20) != 0) builder.append("指示,");
        if ((p & 0x40) != 0) builder.append("带签名写入,");
        if ((p & 0x80) != 0) builder.append("扩展属性,");
        String string = builder.toString();
        return string.substring(0, string.length()-1);
    }

    /**
     * 获取服务标准用途
     * https://www.bluetooth.com/specifications/gatt/services/
     * @param name uuid有效低八位
     * @return 用途
     */
    private String getServiceUsage(String name){
        switch (name.toUpperCase()){
            case "1800": return "通用访问";
            case "1811": return "闹钟通知";
            case "1815": return "自动化输入输出";
            case "180F": return "电池数据";
            case "183B": return "二元传感器";
            case "1810": return "血压";
            case "181B": return "身体组成";
            case "181E": return "设备绑定管理";
            case "181F": return "动态血糖检测";
            case "1805": return "当前时间";
            case "1818": return "骑行能量";
            case "1816": return "循环速度和节奏";
            case "180A": return "设备信息";
            case "183C": return "应急配置";
            case "181A": return "环境传感";
            case "1826": return "健康设备";
            case "1801": return "通用属性";
            case "1808": return "葡萄糖";
            case "1809": return "温度计";
            case "180D": return "心率";
            case "1823": return "HTTP代理";
            case "1812": return "HID设备";
            case "1802": return "即时闹钟";
            case "1821": return "室内定位";
            case "183A": return "胰岛素给药";
            case "1820": return "互联网协议支持";
            case "1803": return "连接丢失";
            case "1819": return "定位及导航";
            case "1827": return "节点配置";
            case "1828": return "节点代理";
            case "1807": return "夏令时更改";
            case "1825": return "对象传输";
            case "180E": return "手机报警状态";
            case "1822": return "脉搏血氧计";
            case "1829": return "重连配置";
            case "1806": return "参照时间更新";
            case "1814": return "跑步速度和节奏";
            case "1813": return "扫描参数";
            case "1824": return "传输发现";
            case "1804": return "发送功率";
            case "181C": return "用户数据";
            case "181D": return "体重秤";
            default: return "自定义服务";
        }
    }

    /**
     * 获取特征标准用途
     * @param name 有效位
     * @return 用途
     */
    private String getCharacteristicUsage(String name){
        switch (name.toUpperCase()){
            case "2A7E": return "有氧心律下限";
            case "2A84": return "有氧心率上限";
            case "2A7F": return "有氧运动阈值";
            case "2A80": return "年龄";
            case "2A5A": return "集合";
            case "2A43": return "报警类别ID";
            case "2A42": return "报警类别ID位掩码";
            case "2A06": return "报警等级";
            case "2A44": return "报警通知控制点";
            case "2A3F": return "报警状态";
            case "2AB3": return "海拔";
            case "2A81": return "无氧心率下限";
            case "2A82": return "无氧心率上限";
            case "2A83": return "无氧运动阈值";
            case "2A58": return "模拟";
            case "2A59": return "模拟输出";
            case "2A73": return "视风向";
            case "2A72": return "视风速";
            case "2A01": return "外观";
            case "2AA3": return "气压趋势";
            case "2A19": return "电池电量";
            case "2A1B": return "电池电量状态";
            case "2A1A": return "电池电量状态";
            case "2A49": return "血压功能";
            case "2A35": return "血压测量";
            case "2A9B": return "身体组成特征";
            case "2A9C": return "身体组成测量";
            case "2A38": return "人体感应器位置";
            case "2AA4": return "绑定管理控制点";
            case "2AA5": return "绑定管理功能";
            case "2A22": return "启动键盘输入报告";
            case "2A32": return "启动键盘输出报告";
            case "2A33": return "启动鼠标输入报告";
            case "2B2B": return "BSS控制点";
            case "2B2C": return "BSS回应";
            case "2AA8": return "CGM功能";
            case "2AA7": return "CGM测量";
            case "0x2AAB": return "CGM会话运行时间";
            case "0x2AAA": return "CGM会话开始时间";
            case "0x2AAC": return "CGM特定操作控制点";
            case "2AA9": return "CGM状态";
            case "0x2ACE": return "交叉训练员数据";
            case "2A5C": return "CSC功能";
            case "2A5B": return "CSC测量";
            case "2A2B": return "当前时间";
            case "2A66": return "骑行能量控制点";
            case "2A65": return "骑行能量功能";
            case "2A63": return "骑行能量测量";
            case "2A64": return "骑行能量矢量";
            case "2A99": return "数据库更改增量";
            case "2A85": return "出生日期";
            case "2A86": return "阈值评估日期";
            case "2A08": return "日期时间";
            case "0x2AED": return "UTC时间";
            case "2A0A": return "日期时间天";
            case "2A09": return "星期几";
            case "2A7D": return "描述符值已更改";
            case "2A7B": return "露点温度";
            case "2A56": return "数字";
            case "2A57": return "数字输出";
            case "2A0D": return "日光节约时间（夏令时）偏移";
            case "2A6C": return "海拔";
            case "2A87": return "电子邮件地址";
            case "2B2D": return "突发事件ID";
            case "2B2E": return "突发事件内容";
            case "2A0B": return "具体时间100";
            case "2A0C": return "具体时间256";
            case "2A88": return "脂肪燃烧心率下限";
            case "2A89": return "脂肪燃烧心率上限";
            case "2A26": return "固件修订字符";
            case "2A8A": return "名字";
            case "2AD9": return "健身设备控制点";
            case "0x2ACC": return "健身设备功能";
            case "0x2ADA": return "健身设备状态";
            case "2A8B": return "五区心率限制";
            case "2AB2": return "楼层号";
            case "2AA6": return "中央地址解析";
            case "2A00": return "设备名称";
            case "2A04": return "外围设备首选连接参数";
            case "2A02": return "周边隐私标志";
            case "2A03": return "重新连接地址";
            case "2A05": return "服务已更改";
            case "2A8C": return "性别";
            case "2A51": return "葡萄糖功能";
            case "2A18": return "血糖测量";
            case "2A34": return "葡萄糖测量环境";
            case "2A74": return "阵风系数";
            case "2A27": return "硬件修订字符";
            case "2A39": return "心率控制点";
            case "2A8D": return "最大心率";
            case "2A37": return "心率测量";
            case "2A7A": return "热度指数";
            case "2A8E": return "高度";
            case "2A4C": return "HID控制点";
            case "2A4A": return "HID信息";
            case "2A8F": return "臀围";
            case "0x2ABA": return "HTTP控制点";
            case "2AB9": return "HTTP实体主体";
            case "2AB7": return "HTTP头";
            case "2AB8": return "HTTP状态码";
            case "0x2ABB": return "HTTPS安全性";
            case "2A6F": return "湿度";
            case "2B22": return "IDD通告状态";
            case "2B25": return "IDD命令控制点";
            case "2B26": return "IDD命令数据";
            case "2B23": return "IDD功能";
            case "2B28": return "IDD历史数据";
            case "2B27": return "IDD记录访问控制点";
            case "2B21": return "IDD状态";
            case "2B20": return "IDD状态已更改";
            case "2B24": return "IDD状态读取器控制点";
            case "2A2A": return "IEEE 11073-20601法规认证数据列表";
            case "2AD2": return "室内自行车数据";
            case "0x2AAD": return "室内定位配置";
            case "2A36": return "中间的气囊压力";
            case "2A1E": return "中间的温度";
            case "2A77": return "辐照度";
            case "2AA2": return "语言";
            case "2A90": return "姓";
            case "0x2AAE": return "纬度";
            case "2A6B": return "LN控制点";
            case "2A6A": return "LN功能";
            case "2AB1": return "当地东部坐标";
            case "2AB0": return "当地北部坐标";
            case "2A0F": return "当地时间信息";
            case "2A67": return "位置和速度特征";
            case "2AB5": return "地点名称";
            case "0x2AAF": return "经度";
            case "2A2C": return "磁偏角";
            case "2AA0": return "磁通密度– 2D";
            case "2AA1": return "磁通密度– 3D";
            case "2A29": return "制造商名称字符";
            case "2A91": return "推荐最大心率";
            case "2A21": return "测量间隔";
            case "2A24": return "型号字符";
            case "2A68": return "导航";
            case "2A3E": return "网络可用性";
            case "2A46": return "新警报";
            case "2AC5": return "对象动作控制点";
            case "2AC8": return "对象已更改";
            case "2AC1": return "对象首先创建";
            case "2AC3": return "对象ID";
            case "2AC2": return "上次修改的对象";
            case "2AC6": return "对象列表控制点";
            case "2AC7": return "对象列表过滤器";
            case "0x2ABE": return "对象名称";
            case "2AC4": return "对象属性";
            case "2AC0": return "对象大小";
            case "0x2ABF": return "对象类型";
            case "0x2ABD": return "OTS功能";
            case "2A5F": return "PLX连续测量特性";
            case "2A60": return "PLX功能";
            case "2A5E": return "PLX抽查检查";
            case "2A50": return "即插即用ID";
            case "2A75": return "花粉浓度";
            case "2A2F": return "位置2D";
            case "2A30": return "位置3D";
            case "2A69": return "位置质量";
            case "2A6D": return "压力";
            case "2A4E": return "协议模式";
            case "2A62": return "脉搏血氧饱和度控制点";
            case "2A78": return "雨量";
            case "2B1D": return "RC功能";
            case "2B1E": return "RC设置";
            case "2B1F": return "重新连接配置控制点";
            case "2A52": return "记录访问控制点";
            case "2A14": return "参考时间信息";
            case "2B37": return "注册用户特征";
            case "2A3A": return "可移动的";
            case "2A4D": return "报告";
            case "2A4B": return "报告地图";
            case "2AC9": return "仅可解析的私有地址";
            case "2A92": return "静息心率";
            case "2A40": return "铃声控制点";
            case "2A41": return "铃声设置";
            case "2AD1": return "桨手数据";
            case "2A54": return "RSC功能";
            case "2A53": return "RSC测量";
            case "2A55": return "SC控制点";
            case "2A4F": return "扫描间隔窗口";
            case "2A31": return "扫描刷新";
            case "2A3C": return "科学温度（摄氏度）";
            case "2A10": return "次要时区";
            case "2A5D": return "传感器位置";
            case "2A25": return "序列号字符";
            case "2A3B": return "所需服务";
            case "2A28": return "软件修订版字符";
            case "2A93": return "有氧阈值和无氧阈值的运动类型";
            case "2AD0": return "攀登楼梯数";
            case "0x2ACF": return "攀登者步数";
            case "2A3D": return "字符串";
            case "2AD7": return "支持的心率范围";
            case "2AD5": return "支持的倾斜范围";
            case "2A47": return "支持的新警报类别";
            case "2AD8": return "支持的功率范围";
            case "2AD6": return "支持的电阻水平范围";
            case "2AD4": return "支持的速度范围";
            case "2A48": return "支持的未读警报类别";
            case "2A23": return "系统编号";
            case "0x2ABC": return "TDS控制点";
            case "2A6E": return "温度";
            case "2A1F": return "温度摄氏";
            case "2A20": return "温度华氏度";
            case "2A1C": return "温度测量";
            case "2A1D": return "温度类型";
            case "2A94": return "三区心率限制";
            case "2A12": return "时间精度";
            case "2A15": return "时间广播";
            case "2A13": return "时间来源";
            case "2A16": return "时间更新控制点";
            case "2A17": return "时间更新状态";
            case "2A11": return "夏令时";
            case "2A0E": return "时区";
            case "2AD3": return "训练状况";
            case "0x2ACD": return "跑步机数据";
            case "2A71": return "真风向";
            case "2A70": return "真风速";
            case "2A95": return "两区心率限制";
            case "2A07": return "发射功率等级";
            case "2AB4": return "不确定";
            case "2A45": return "未读警报状态";
            case "2AB6": return "URI链接";
            case "2A9F": return "用户控制点";
            case "2A9A": return "用户索引";
            case "2A76": return "紫外线指数";
            case "2A96": return "最大摄氧量";
            case "2A97": return "腰围";
            case "2A98": return "重量";
            case "2A9D": return "体重测量";
            case "2A9E": return "体重秤功能";
            case "2A79": return "风寒系数";
            default: return "自定义功能";
        }
    }

}
