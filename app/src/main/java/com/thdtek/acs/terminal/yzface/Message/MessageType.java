package com.thdtek.acs.terminal.yzface.Message;

public enum MessageType {
    UNKNOWN                 (0x000000, (byte) 0x00, (byte) 0x00, (byte) 0x00, "未知"),
    //预定义响应命令
    NODE_RES_OK                  (0x210100, (byte) 0x21, (byte) 0x01, (byte) 0x00, "应答OK"),
    NODE_RES_NG_PASSWORD         (0x210200, (byte) 0x21, (byte) 0x02, (byte) 0x00, "密码错误"),
    NODE_RES_NG_VERIFY           (0x210300, (byte) 0x21, (byte) 0x03, (byte) 0x00, "校验错"),
    NODE_RES_NG_IPSET            (0x210400, (byte) 0x21, (byte) 0x04, (byte) 0x00, "IP设置错误"),

    //十九、搜索设备
    HOST_REQ_SCAN_NODE           (0x01FE00, (byte) 0x01, (byte) 0xFE, (byte) 0x00, "搜索不同网络标识的设备"),
    NODE_RES_SCAN_NODE           (0x31FE00, (byte) 0x31, (byte) 0xFE, (byte) 0x00, "应答搜索不同网络标识的设备"),

    HOST_REQ_SET_NODE_TOKEN      (0x01FE01, (byte) 0x01, (byte) 0xFE, (byte) 0x01, "设置设备网络标识"),
    //NODE_RES_OK                  (0x210100, (byte) 0x21, (byte) 0x01, (byte) 0x00, "应答OK"),

    //十、获取设备版本号
    HOST_REQ_GET_NODE_VERSION      (0x010800, (byte) 0x01, (byte) 0x08, (byte) 0x00, "获取设备版本号"),
    NODE_RES_GET_NODE_VERSION      (0x310800, (byte) 0x31, (byte) 0x08, (byte) 0x00, "应答：传送版本号"),

    //第十一类 人员照片和记录照片
    HOST_REQ_PREPARE_WRITE_FILE      (0x0B0100, (byte) 0x0B, (byte) 0x01, (byte) 0x00, "准备写文件"),
    NODE_RES_PREPARE_WRITE_FILE      (0x3B0100, (byte) 0x3B, (byte) 0x01, (byte) 0x00, "应答：文件句柄"),

    HOST_REQ_WRITE_FILE              (0x0B0200, (byte) 0x0B, (byte) 0x02, (byte) 0x00, "写文件"),
    NODE_RES_WRITE_FILE_SAVE_OK      (0x3B0200, (byte) 0x3B, (byte) 0x02, (byte) 0x00, "应答：成功存储"),
    NODE_RES_WRITE_FILE_PREPARE_SAVE (0x3B0202, (byte) 0x3B, (byte) 0x02, (byte) 0x02, "应答：未启动准备状态"),

    HOST_REQ_WRITE_FILE_OK_CRC        (0x0B0300, (byte) 0x0B, (byte) 0x03, (byte) 0x00, "写入文件完毕,进行CRC32校验"),
    NODE_RES_WRITE_FILE_OK_CRC        (0x3B0300, (byte) 0x3B, (byte) 0x03, (byte) 0x00, "应答：CRC32校验"),

    HOST_REQ_QUERY_PERSON_PIC        (0x0B0400, (byte) 0x0B, (byte) 0x04, (byte) 0x00, "查询人员照片和指纹"),
    NODE_RES_QUERY_PERSON_PIC        (0x3B0400, (byte) 0x3B, (byte) 0x04, (byte) 0x00, "应答:查询人员照片和指纹"),

    HOST_REQ_GET_PERSON_PIC        (0x0B0500, (byte) 0x0B, (byte) 0x05, (byte) 0x00, "读取人员照片/记录照片/指纹"),
    NODE_RES_GET_PERSON_PIC        (0x3B0500, (byte) 0x3B, (byte) 0x05, (byte) 0x00, "应答:读取人员照片/记录照片/指纹"),

    //五、添加人员
    HOST_REQ_ADD_PERSON_INFO         (0x070400, (byte) 0x07, (byte) 0x04, (byte) 0x00, "添加人员"),
    //NODE_RES_OK                    (0x210100, (byte) 0x21, (byte) 0x01, (byte) 0x00, "应答OK"),
    NODE_RES_ADD_PERSON_INFO_NG      (0x3704FF, (byte) 0x37, (byte) 0x04, (byte) 0xFF, "应答：失败卡号"),

    //读取记录信息
    HOST_REQ_READ_RECORD_INFO         (0x080100, (byte) 0x08, (byte) 0x01, (byte) 0x00, "读取记录信息"),
    NODE_RES_READ_RECORD_INFO         (0x380100, (byte) 0x38, (byte) 0x01, (byte) 0x00, "应答：记录信息"),

    HOST_REQ_DELALL_RECORD_INFO         (0x080200, (byte) 0x08, (byte) 0x02, (byte) 0x00, "删除所有记录"),
    //NODE_RES_OK                    (0x210100, (byte) 0x21, (byte) 0x01, (byte) 0x00, "应答OK"),

    HOST_REQ_RESTLALL_RECORD_INFO         (0x080300, (byte) 0x08, (byte) 0x03, (byte) 0x00, "重置所有记录为新记录"),
    //NODE_RES_OK                    (0x210100, (byte) 0x21, (byte) 0x01, (byte) 0x00, "应答OK"),

    HOST_REQ_READ_NEW_RECORD_INFO         (0x080400, (byte) 0x08, (byte) 0x04, (byte) 0x00, "读取新记录"),
    NODE_RES_READ_NEW_RECORD_INFO         (0x380400, (byte) 0x38, (byte) 0x04, (byte) 0x00, "应答：新记录"),
    NODE_RES_READ_NEW_RECORD_INFO_END      (0x3804FF, (byte) 0x38, (byte) 0x04, (byte) 0xFF, "应答：新记录，传输结束"),

    NODE_RES_EVENT_MSG           (0x190100, (byte) 0x19, (byte) 0x01, (byte) 0x00, "事件消息");

    private int code;
    private byte category;
    private byte command;
    private byte parameter;
    private String desc;

    MessageType(int code, byte category, byte command, byte parameter, String desc) {
        this.code = code;
        this.category = category;
        this.command = command;
        this.parameter = parameter;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static MessageType getType(int code) {
        for (MessageType messageType : values()) {
            if (code == messageType.code) {
                return messageType;
            }
        }
        return UNKNOWN;
    }

    public byte getCategory() {
        return category;
    }

    public byte getCommand() {
        return command;
    }

    public byte getParameter() {
        return parameter;
    }
}