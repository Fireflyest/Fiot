package com.fireflyest.fiot.util;

import com.fireflyest.fiot.data.DeviceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandUtils {

    private static final Map<String, String> commandDataMap = new HashMap<String, String>(){
        {
            put("打开", "on");
            put("暂停", "pause");
            put("重启", "reload");
            put("关闭", "off");

            put("增加亮度", "add");
            put("减少亮度", "down");

            put("查看", "read");
        }
    };

    private CommandUtils(){
    }

    public static List<String> getCommands(long type){
        List<String> commands = new ArrayList<>();
        commands.add("打开");
        commands.add("暂停");
        commands.add("重启");
        commands.add("关闭");
        if((type & DeviceType.ILLUMINATION_LIGHT) != 0){
            commands.add("增加亮度");
            commands.add("减少亮度");
        }
        if((type & DeviceType.ENVIRONMENT) != 0){
            commands.add("查看");
        }
        return commands;
    }

    public static String getCommandData(String command){
        if (!commandDataMap.containsKey(command)) return "";
        return commandDataMap.get(command);
    }


}
