package com.thdtek.acs.terminal.util;

import android.os.Environment;

import com.thdtek.acs.terminal.R;
import com.thdtek.acs.terminal.base.MyApplication;

import java.io.File;
import java.util.EventListener;

/**
 * Time:2018/6/20
 * User:lizhen
 * Description:
 */

public class Const {

    //系统语言
    //zh_TW
    //zh_HK
    //zh_CN
    //en_SG
    public static final String LANGUAGE_EN = "en";
    public static final String LANGUAGE_ZH_CN = "zh_CN";
    public static final String LANGUAGE_ZH_TW = "zh_TW";
    public static final String LANGUAGE_ZH_HK = "zh_HK";
    //设备加密的key
    public static final String DEVICE_AES_KEY = "device_aes_key";

    //今天是否已经重启
    public static final String TODAY_OF_YEAR = "today_of_year";

    //连接状态
    public static final String ACTION_CONNECT = "action_connect";
    public static final String CONNECT_STATE = "connect_state";

    //SDK选择
    public static String SDK = Const.SDK_YUN_TIAN_LI_FEI;
    public static String DEVICE_MODE = Const.SDK_YUN_TIAN_LI_FEI;
    public static final String SDK_FACE = "sdk_face";
    public static final String SDK_HONG_RUAN = "sdk_hong_ruan";
    public static final String SDK_YUN_TIAN_LI_FEI = "sdk_yun_tian_li_fei";

    public static final String SDK_YUN_TIAN_LI_FEI_KEY = "ki5BRKCnV8SPPIaa";
    public static final String SDK_YUN_TIAN_LI_FEI_SECRET = "inhTDI5QhOp1aFciZX9K27oJG7iT9G";

    public static final int SDK_YUN_TIAN_LI_FEI_INIT_TIME = 30000;
    //sdk 初始化 云天励飞
    public static final int SDK_INIT_START_YUN_TIAN_LI_FEI = 0;
    //sdk 初始化 虹软
    public static final int SDK_INIT_START_HONG_RUAN = 6;
    //sdk初始化结束
    public static final int SDK_INIT_END = -1;


    //文件路径
//    public static final String DIR_LOG = MyApplication.getContext().getFilesDir() + "/log";
//    public static final String DIR_IMAGE_EMPLOYEE = MyApplication.getContext().getFilesDir() + "/imagePerson";
    //    public static final String DIR_IMAGE_RECORD = MyApplication.getContext().getFilesDir() + "/imageRecord";
//    public static final String DIR_IMAGE_TEMP = MyApplication.getContext().getFilesDir() + "/temp";
//    public static final String DIR_APK = MyApplication.getContext().getFilesDir() + "/apk";
//    public static final String DIR_ID_IMAGE = MyApplication.getContext().getFilesDir() + "/idImage";

    public static final String DIR_IMAGE_EMPLOYEE = Environment.getExternalStorageDirectory() + "/imagePerson";
    public static final String DIR_IMAGE_RECORD = Environment.getExternalStorageDirectory() + "/imageRecord";
    public static final String DIR_LOG = Environment.getExternalStorageDirectory() + "/log";
    public static final String DIR_IMAGE_TEMP = Environment.getExternalStorageDirectory() + "/temp";
    public static final String DIR_APK = Environment.getExternalStorageDirectory() + "/apk";
    public static final String DIR_ID_IMAGE = Environment.getExternalStorageDirectory() + "/idImage";
    public static final String DIR_VIDEO = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video";
    public static final String DIR_TEMP_SERVER_PHOTO = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tempServerPhoto";
    public static final String LICENSE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "license";
    public static final String MODEL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "model";
    public static final String DIR_LICENSE = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "license";

    //记录apk安装后的启动次数
    public static final String START_COUNT = "START_COUNT";


    //http模式心跳url对应key
    public static final String URL_FOR_HTTP_HEARTBEAT = "URL_FOR_HTTP_HEARTBEAT";
    //http模式心跳周期对应key
    public static final String PERIOD_FOR_HTTP_HEARTBEAT = "PERIOD_FOR_HTTP_HEARTBEAT";
    //http模式自动上传流水url对应key
    public static final String URL_FOR_HTTP_AUTO_UPLOAD_RECORD = "URL_FOR_HTTP_AUTO_UPLOAD_RECORD";


    public static final int FACE_TWO_EYE_ALIVE = 0;
    public static final int FACE_NOT_ALIVE = 1;
    public static final int FACE_ONE_EYE_ALIVE = 2;

    public static final int FACE_INIT_CODE_SUCCESS = 0;
    public static final int FACE_INIT_CODE_FAIL = 2;
    public static final int FACE_INIT_GET_SIGN_TOKEN_FAIL = 1;

    public static final String CAMERA_LOG_IMAGE_TYPE = ".webp";
    public static final String IMAGE_TYPE_DEFAULT_JPG = ".jpg";
    public static final String IMAGE_TYPE_DEFAULT_PNG = ".png";

    public static final int WINDOW_SIZE_WIDTH_1920 = 1920;
    public static final int WINDOW_SIZE_HEIGHT_1080 = 1080;

    public static final int SLEEP_TIME = 3000;

    public static final int CAMERA_PREVIEW_WIDTH = 640;
    public static final int CAMERA_PREVIEW_HEIGHT = 480;

    public static final float CAMERA_MAX_AREA = (float) CAMERA_PREVIEW_WIDTH * CAMERA_PREVIEW_HEIGHT;
    public static final float CAMERA_MAX_WIDTH = (float) CAMERA_PREVIEW_WIDTH ;
    public static final int CAMERA_BITMAP_WIDTH = 640;
    public static final int CAMERA_BITMAP_HEIGHT = 480;
    public static final int CAMERA_ORIENTATION_90 = 90;
    public static final int CAMERA_ORIENTATION_180 = 480;
    public static final int CAMERA_ORIENTATION_270 = 270;

    public static final String DEVICE_REBOOT = "DEVICE_REBOOT";

    public static final String ERROR_EMPLOYEE_CARD_NUMBER = "00000000";

    public static final String USB_KEY_FILE_NAME = "key.txt";

    public static final String NETWORK_COMMAND_GET_ETH0 = "getprop | grep eth0";

    public static final String NETWORK_IP_ADDRESS = "ipaddress";
    public static final String NETWORK_GATEWAY = "gateway";
    public static final String NETWORK_DNS = "dns1";
    public static final String NETWORK_TYPE = "init.svc.";
    public static final String NETWORK_TYPE_DHCP = "running";
    public static final String NETWORK_TYPE_STATIC = "stopped";

    public static final String DATABASE_LAST_ACTION_ID = "database_last_action_id";
    public static final String DATABASE_LAST_ACTION_TIME = "database_last_action_time";

    public static final long HANDLER_DELAY_TIME_200 = 200;
    public static final long HANDLER_DELAY_TIME_300 = 300;
    public static final long HANDLER_DELAY_TIME_500 = 500;
    public static final long HANDLER_DELAY_TIME_1000 = 1000;
    public static final long HANDLER_DELAY_TIME_2000 = 2000;
    public static final long HANDLER_DELAY_TIME_3000 = 3000;
    public static final long HANDLER_DELAY_TIME_5000 = 5000;
    public static final long HANDLER_DELAY_TIME_10000 = 10000;
    public static final long HANDLER_DELAY_TIME_15000 = 15000;
    public static final long HANDLER_DELAY_TIME_20000 = 20000;
    public static final long HANDLER_DELAY_TIME_30000 = 30000;
    public static final long HANDLER_DELAY_TIME_40000 = 40000;
    public static final long HANDLER_DELAY_TIME_50000 = 50000;
    public static final long HANDLER_DELAY_TIME_60000 = 60000;
    public static final long HANDLER_DELAY_TIME_90000 = 90000;
    public static final long HANDLER_DELAY_TIME_120000 = 120000;
    public static final long HANDLER_DELAY_TIME_600000 = 600000;
    public static final long HANDLER_DELAY_TIME_40_MIN = 40 * 60 * 60 * 1000;

    public static final int CAMERA_REPEAT_MAX_COUNT = 15;

    //云天励飞算法属性

    public static final int IFACEREC_GENDER = 0;
    public static final int IFACEREC_AGE = 1;
    public static final int IFACEREC_POSE_PITCH = 2;
    public static final int IFACEREC_QUALITY_MASK = 9;
    public static final int IFACEREC_HAT_MASK = 7;
    public static final int IFACEREC_GLASSES_MASK = 6;
    public static final int IFACEREC_LIVE_MASK = 0x80;


    //socket Time
    public static final int SOCKET_WAIT_TIME = 10 * 60 * 1000;

    public static final String PERSON_OFFICIAL_IMAGE_SAVE_SUCCESS = "official_success";

    //关闭补光灯和背光灯
    public static final int HANDLER_MAIN_CLOSE_BACK_AND_FILL = 10;
    //关闭继电器
    public static final int HANDLER_MAIN_CLOSE_RELAY = 2;
    //正在匹配
    public static final int HANDLER_MAIN_PAIRING = 3;
    //匹配成功
    public static final int HANDLER_MAIN_PAIR_SUCCESS = 4;
    //匹配失败
    public static final int HANDLER_MAIN_PAIR_FAIL = 5;
    //没有权限
    public static final int HANDLER_MAIN_NO_AUTHORITY = 9;
    //此次匹配结束
    public static final int HANDLER_MAIN_THIS_PAIR_OVER = 7;
    //开机查询未上传通过记录
    public static final int HANDLER_MAIN_UPLOAD_ACCESS_RECORD = 8;
    //查询没有特征值的人
    public static final int HANDLER_MAIN_CHECK_PERSON_NO_FACE_FEATURE = 11;
    //百度授权
    public static final int HANDLER_BAIDU = 0;
    //更新apk
    public static final int HANDLER_UPDATE_APK = 12;
    //停止语音播放
    public static final int HANDLER_STOP_SPEAK = 13;
    //重置语音播放次数
    public static final int HANDLER_RESET_SPEAK_COUNT = 15;
    //重置相同的人的id
    public static final int HANDLER_RESET_AUTHORITY_ID = 16;
    //下载人员开始
    public static final int HANDLER_DOWN_LOAD_PERSON_START = 17;
    //下载人员结束
    public static final int HANDLER_DOWN_LOAD_PERSON_END = 18;
    //下载人员view消失
    public static final int HANDLER_DOWN_LOAD_PERSON_FINISH = 19;
    //开启看门狗
    public static final int HANDLER_WATCH_DOG = 20;
    public static final int HANDLER_RESET = 21;

    public static final int HANDLER_STOP_READ_IC_NUMBER = 22;
    //下载video
    public static final int HANDLER_DOWN_LOAD_VIDEO = 23;
    //检查摄像头是否挂了
    public static final int HANDLER_CHECK_CAMERA = 24;
    //检查cpu温度,判断是否需要打开风扇
    public static final int HANDLER_HANDLE_FEN = 27;
    //初始化摄像头
    public static final int HANDLER_INIT_SDK_FIRST = 30;
    //初始化sdk
    public static final int HANDLER_INIT_SDK = 31;
    //检测是否已经安装了看门狗
    public static final int HANDLER_CHECK_ACS_WATCH = 32;
    //打开看门狗
    public static final int HANDLER_OPEN_ACS_WATCH_DOG = 33;
    //初始化摄像头
//    public static final int HANDLER_INIT_CAMERA = 34;
    //判断CPU温度打开风扇
    public static final int HANDLER_HANDLE_FENG_SHAN = 36;


    //权限id insert
    public static final long TYPE_INSERT = 0;
    //权限id update
    public static final long TYPE_UPDATE = 1;

    //apk下载起始位置
    public static final String DOWN_LOAD_APK_FILE_START_BYTE = "down_load_apk_file_start_byte";
    //apk下载结束位置
    public static final String DOWN_LOAD_APK_FILE_END_BYTE = "down_load_apk_file_end_byte";
    //apk需要下载的versionCode
    public static final String DOWN_LOAD_APK_FILE_VERSION_CODE = "down_load_apk_file_version_code";
    //apk下载的url
    public static final String DOWN_LOAD_APK_URL = "down_load_apk_url";
    //apk下载的路径
    public static final String DOWN_LOAD_APK_PATH = "down_load_apk_path";
    //apk下载temp路径
    public static final String DOWN_LOAD_APK_TEMP_PATH = "down_load_apk_temp_path";
    //立即升级
    public static final String DOWN_LOAD_APK_UPDATE_NOW = "down_load_apk_update_now";

    //天气
    public static final String WEATHER = "weather";
    //天气类型
    public static final String WEATHER_TYPE = "weather_type";
    //天气温度
    public static final String WEATHER_CODE = "weather_code";
    //在线
    public static final String DEVICE_ON_LINE = "device_on_line";
    //摄氏度
    public static final String weather_Celsius = "℃";

    //登陆成功
    public static final int VIEW_STATUS_ON_LINE = 0;
    //登录失败
    public static final int VIEW_STATUS_OFF_LINE = 1;
    //未激活
    public static final int VIEW_STATUS_NOT_ALIVE = 2;


    //首页的标题
    public static final String MAIN_TITLE = "main_title";
    public static final String MAIN_TITLE_DEFAULT = MyApplication.getContext().getString(R.string.title);

    //默认的权限id
    public static final long DEFAULT_AUTHORITY_ID = -1L;
    public static final long DEFAULT_CONITUE_AUTHORITY_ID = -2L;

    //打开关闭特征值更新
    public static boolean UPDATE_FACE_FEATURE = true;

    //人脸开门
    public static final int OPEN_DOOR_TYPE_FACE = 0;
    //人脸 || IC卡开门
    public static final int OPEN_DOOR_TYPE_I_C = 2;
    //人脸+身份证
    public static final int OPEN_DOOR_TYPE_FACE_ID = 3;
    //人脸+IC卡
    public static final int OPEN_DOOR_TYPE_FACE_IC = 4;
    //访客,没有打开
    public static final int OPEN_DOOR_TYPE_GUEST_NOT_OPEN = 0;
    //人脸+身份证+访客,未登记
    public static final int OPEN_DOOR_TYPE_GUEST_ID_FACE_UN_REGISTER = 1;
    //人脸+身份证+访客,登记
    public static final int OPEN_DOOR_TYPE_GUEST_ID_FACE_REGISTER = 2;
    //员工
    public static final int PERSON_TYPE_EMPLOYEE = 0;
    //登记访客
    public static final int PERSON_TYPE_GUEST = 1;
    //访客id
    public static final long PERSON_TYPE_GUEST_DEFAULT_AUTHORITY_ID = 2147483647L + 2;
    //当前是身份证录入
    public static boolean PERSON_TYPE_CAMERA_PHOTO = false;

    //准备匹配
    public static final int FACE_PAIR_READY = 100;
    //找到人脸
    public static final int FACE_PAIR_FIND_FACE = 101;
    //正在匹配
    public static final int FACE_PAIR_ING = 102;
    //匹配成功
    public static final int FACE_PAIR_SUCCESS = 103;
    //匹配失败
    public static final int FACE_PAIR_FAIL = 104;
    //没有权限
    public static final int FACE_PAIR_FAIL_CONTINUE = 105;
    //本次匹配结束
    public static final int FACE_PAIR_FINISH = 106;
    //数据库中没有人
    public static final int FACE_PAIR_DATABASE_NO_PEOPLE = 107;
    //IC卡匹配错误
    public static final int FACE_PAIR_IC_FAIL = 108;
    //同一个人
    public static final int FACE_PAIR_SAME_PEOPLE = 109;
    //不是同一个人
    public static final int FACE_PAIR_NOT_SAME_PEOPLE = 110;
    //非活体
    public static final int FACE_PAIR_NOT_ALIVE = 111;
    public static final int FACE_PAIR_NOT_ALIVE_FINISH = 112;
    //人脸失败重复次数
    public static final int FACE_PAIR_FAIL_REPEAT_COUNT = 3;

    //维根26
    public static final int WG_26 = 26;
    //维根34
    public static final int WG_34 = 34;
    //继电器
    public static final int WG_0 = 0;
    //维根26+继电器
    public static final int WG_26_0 = 260;
    //维根34+继电器
    public static final int WG_34_0 = 340;
    public static final int WG_66 = 66;
    public static final int WG_66_0 = 660;

    //图像缩放比例
    public static float CAMERA_SCALE_NUMBER = 1;
    //图像最左边
    public static float CAMERA_MIN_LEFT = 0;
    //最右边的图像
    public static float CAMERA_MAX_RIGHT = 0;
    //没有图像的次数
    public static int MAX_NO_FACE_COUNT = 10;

    //下载received
    public static final String DOWN_RECEIVE = "down_receive";
    //当前下载状态
    public static final String DOWN_LOAD_CURRENT_STATUS = "down_load_current_status";
    //下载开始
    public static final String DOWN_LOAD_START = "down_load_start";
    //下载中
    public static final String DOWN_LOAD_ING = "down_load_ing";
    //下载结束
    public static final String DOWN_LOAD_END = "down_load_end";
    //文件大小
    public static final String DOWN_LOAD_FILE_LENGTH = "down_load_file_length";
    //当前进度
    public static final String DOWN_LOAD_CURRENT_LENGTH = "down_load_current_length";
    //新的Video path
    public static final String DOWN_LOAD_NEW_VIDEO_PATH = "down_load_new_video_path";
    //临时视频第一帧
    public static final String DOWN_LOAD_FIRST_IMAGE = "down_load_first_image";
    //上一次视频下载的 号码_开始位置_结束位置_url_file
    public static final String DOWN_LOAD_NUMBER_START_END_URL_FILE = "down_load_number_start_end_url_file";
    //视频的url
    public static final String DOWN_LOAD_VIDEO_URLS = "down_load_video_urls";
    //下载视频状态
    public static final String DOWN_LOAD_VIDEO_RECEIVER = "down_load_video_receiver";
    public static final String DOWN_LOAD_VIDEO_STATUE = "down_load_video_statue";
    public static final String DOWN_LOAD_VIDEO_START = "down_load_video_start";
    public static final String DOWN_LOAD_VIDEO_END = "down_load_video_end";
    public static final String DOWN_LOAD_VIDEO_FINISH = "down_load_video_finish";
    public static final String DOWN_LOAD_AD_IMAGE = "down_load_ad_image";
    public static final String DOWN_LOAD_AD_MESSAGE = "DOWN_LOAD_AD_MESSAGE";
    public static final String DOWN_LOAD_SP_AD_MESSAGE = "DOWN_LOAD_SP_AD_MESSAGE";

    public static final int VIDEO_MIN_SIZE = 1024;
    public static final int VIDEO_MAX_FAIL_COUNT = 10;

    //http模式 表示检测的图片可用
    public static final String HTTP_CHECK_PHOTO_IS_VALID = "HTTP_CHECK_PHOTO_IS_VALID";
    //http模式 表示检测的图片是重复的
    public static final String HTTP_CHECK_PHOTO_IS_EXIST = "HTTP_CHECK_PHOTO_IS_EXIST";
    //http模式 表示检测的图片尺寸错误
    public static final String HTTP_CHECK_PHOTO_SIZE_ERROR = "HTTP_CHECK_PHOTO_SIZE_ERROR";
    //http模式 表示检测的图提取特征值失败
    public static final String HTTP_CHECK_PHOTO_FEATURE_ERROR = "HTTP_CHECK_PHOTO_FEATURE_ERROR";
    //http模式 表示检测的图片找不到人脸
    public static final String HTTP_CHECK_PHOTO_FACE_NOTFOUND = "HTTP_CHECK_PHOTO_SIZE_ERROR";
    //http模式 拍照前提示语
    public static final String HTTP_PHOTO_TIPS_BEFORE = "HTTP_PHOTO_TIPS_BEFORE";
    //http模式 拍照后提示语
    public static final String HTTP_PHOTO_TIPS_AFTER = "HTTP_PHOTO_TIPS_AFTER";


    //线程比对参数
    public static final int THREAD_PAIR_INDEX_ONE = 0;
    public static final int THREAD_PAIR_INDEX_TWO = 1;
    public static final int THREAD_PAIR_INDEX_THREE = 2;
    //线程比对类型,1:学习照,2:正装照
    public static final int THREAD_PAIR_TYPE_LEARN = 1;
    public static final int THREAD_PAIR_TYPE_OFFICIAL = 2;
    //英泽最后一次拉取流水的时间
    public static final String YING_ZE_LIST_RECORD_LAST_TIME = "YING_ZE_LIST_RECORD_LAST_TIME";
    //人脸识别
    public static final String PAIR_TYPE_DEFAULT = "-1";
    public static final String PAIR_TYPE_FACE = "0";
    public static final String PAIR_TYPE_IC = "1";
    public static final String PAIR_TYPE_ID = "2";

    //人脸比对发生异常
    public static final int FACE_PAIR_ERROR_CODE_EXCEPTION = 0;
    //获取特征值失败
    public static final int FACE_PAIR_ERROR_CODE_FACE_FEATURE_FAIL = 1;
    //未登记
    public static final int FACE_PAIR_ERROR_CODE_NOT_LOGIN = 2;
    //没有权限
    public static final int FACE_PAIR_ERROR_CODE_NOT_AUTHORITY = 4;
    //获取身份证图片失败
    public static final int FACE_PAIR_ERROR_CODE_NOT_ID_IMAGE = 6;
    //获取人脸失败
    public static final int FACE_PAIR_ERROR_CODE_FACE_RECT = 7;
    //没有开启访客模式
    public static final int FACE_PAIR_ERROR_CODE_NOT_GUEST_MODE = 8;
    //访客模式要刷身份证
    public static final int FACE_PAIR_ERROR_CODE_PAIR_ID_CARD = 9;
    public static float test = 1.2f;

    //好工e--轮询时间间隔，单位秒
    public static String haogonge_transInterval = "haogonge_transInterval";
    //好工e--是否需要上传考勤照片，0不上传，1上传
    public static String haogonge_uploadAttPic = "haogonge_uploadAttPic";
    //好工e--通信秘钥
    public static String haogonge_encryptKey = "haogonge_encryptKey";
    //好工e--验证方式
    public static String haogonge_verifyType = "haogonge_verifyType";
    //好工e--设备掉线后，人员对比通过后判断开闸与否，可选择open、close,默认open
    public static String haogonge_offlineAction = "haogonge_offlineAction";

    //本地存储人员信息表最大数量
    public static final int PERSON_MAX_COUNT = 10000;

    //app启动时sdk初始化完成广播
    public static final String APP_SDK_INIT_COMPLETE = "APP_SDK_INIT_COMPLETE";

    //好工e配置项
    public static final String haogonge_ip = "haogonge_ip";
    public static final String haogonge_sn = "haogonge_sn";
    public static final String haogonge_url = "haogonge_url";
    public static final String haogonge_model = "haogonge_model";
    public static final String haogonge_code = "haogonge_code";
    public static final String haogonge_mac = "haogonge_mac";
    public static final String haogonge_interface_version = "haogonge_interface_version";
    public static final String haogonge_card_id = "haogonge_card_id";
    public static final String haogonge_compare_threshold = "haogonge_compare_threshold";
    public static final String haogonge_huoti = "haogonge_huoti";
    //好工e配置初始化默认值----在第一次启动初始化，随后的启动都不需要
    public static final String haogonge_init_default = "haogonge_init_default";

    //是否启用aes加密  true-使用aes加密   false-不使用aes加密
    //==========================================================
    //========若改为false，因秘钥保存使用网络aes加密，所以历史版本都会出bug，切记=================
    //========若改为false，因秘钥保存使用网络aes加密，所以历史版本都会出bug，切记=================
    //========若改为false，因秘钥保存使用网络aes加密，所以历史版本都会出bug，切记=================
    //========若改为false，因秘钥保存使用网络aes加密，所以历史版本都会出bug，切记=================
    //========若改为false，因秘钥保存使用网络aes加密，所以历史版本都会出bug，切记=================
    //========若改为false，因秘钥保存使用网络aes加密，所以历史版本都会出bug，切记=================
    //==========================================================
    public static final boolean IS_OPEN_DYNAMIC_AESKEY = true;
    public static boolean DEVICE_CONNECT_INTERNET = true;

    //

}

