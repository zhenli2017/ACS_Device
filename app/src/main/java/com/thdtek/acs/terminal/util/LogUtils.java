package com.thdtek.acs.terminal.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.mindpipe.android.logging.log4j.LogCatAppender;

public class LogUtils {
    //log4j输出日志级别
    private static final Level CURRENT_LEVEL = Level.DEBUG;
    //log4j生成的log日志文件最大的大小
    private static final int LOG_MAX_FILE_SIZE = 1024 * 1024 * 50;
    //msg的最大长度
    private static final int LOG_MAX_BACK_UP_SIZE = 1;
    //log4j超过最大大小后继续生成文件的个数
    private static final int LOG_MSG_MAX_LENGTH = 1000;
    public static final String FORMAT_TIME = "yyyy_MM_dd";
    private static LogBean mLogBean;

    /**
     * app启动时必须调用次方法初始化
     *
     * @param logDir
     */
    public static void init(String logDir, @NonNull List<String> tagNameFilter) {
        System.out.println(" ==== init logDir -> " + logDir);
        System.out.println(" ==== init tagNameFilter -> " + tagNameFilter);

        mLogBean = new LogBean();
        mLogBean.setLogDir(logDir);
        //获取当天结束时间的毫秒值 23:59:59秒的毫秒值
        mLogBean.setDayEndTime(getDayEndTime());
        mLogBean.setTagFilterList(tagNameFilter);
        mLogBean.setNeedSaveToLog(true);

        mLogBean.setCurrentLevel(CURRENT_LEVEL);
        mLogBean.setLogMaxBackUPSize(LOG_MAX_BACK_UP_SIZE);
        mLogBean.setLogMaxFileSize(LOG_MAX_FILE_SIZE);

        //log是否正在上传
        mLogBean.setLogUploading(false);
        mLogBean.setFormatType(FORMAT_TIME);

        initLogConfigurator(mLogBean);
    }

    public static void d(String tag, @NonNull String msg) {
        if (msg.length() > LOG_MSG_MAX_LENGTH) {
            msg = msg.substring(0, LOG_MSG_MAX_LENGTH);
        }
        Logger logger = getLogger(tag, mLogBean);
        if (mLogBean.isNeedSaveToLog()) {
            logger.debug(msg);
        } else {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, @NonNull String msg) {
        if (msg.length() > LOG_MSG_MAX_LENGTH) {
            msg = msg.substring(0, LOG_MSG_MAX_LENGTH);
        }
        Logger logger = getLogger(tag, mLogBean);
        if (mLogBean.isNeedSaveToLog()) {
            logger.info(msg);
        } else {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, @NonNull String msg) {
        if (msg.length() > LOG_MSG_MAX_LENGTH) {
            msg = msg.substring(0, LOG_MSG_MAX_LENGTH);
        }
        Logger logger = getLogger(tag, mLogBean);
        if (mLogBean.isNeedSaveToLog()) {
            logger.warn(msg);
        } else {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, @NonNull String msg) {
        if (msg.length() >LOG_MSG_MAX_LENGTH) {
            msg = msg.substring(0, LOG_MSG_MAX_LENGTH);
        }
        Logger logger = getLogger(tag, mLogBean);
        if (mLogBean.isNeedSaveToLog()) {
            logger.error(msg);
        } else {
            Log.e(tag, msg);
        }
    }

    /**
     * 是否需要保存到本地文件中
     * 在上传日志的时候设置为false , 不能一边写一遍读
     *
     * @param needToSave
     */
    public static void setNeedToSave(boolean needToSave) {
        mLogBean.setNeedSaveToLog(needToSave);
    }

//    public static void setLogUploading(boolean uploading, Context context) {
//        mLogBean.setLogUploading(uploading);
//        if (uploading) {
//            mLogBean.setLogDir(Environment.getExternalStorageDirectory() + "/skypos/log");
//            resetLogConfigurator(mLogBean);
//        } else {
//            mLogBean.setLogDir(context.getFilesDir() + "/log");
//            resetLogConfigurator(mLogBean);
//        }
//
//    }

    /**
     * 保存最近[logLen]天日志
     */
    private static int logLen = 5;

    public static void delLog(String log_dir) {
        long now = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        try {
            File file = new File(log_dir);
            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        //                            1989_06_10.log
                        if (files[i].getName().matches("\\d{4}_\\d{2}_\\d{2}.log")) {
                            String timeStr = files[i].getName().replaceFirst(".log", "");
                            long time = sdf.parse(timeStr).getTime();
                            if ((now < time) || ((now - time) / 1000 / 60 / 60 / 24 > (logLen - 1))) {
                                files[i].delete();
                            }
                        } else {
                            files[i].delete();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delLogAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                delLog(mLogBean.getLogDir());
            }
        }).start();
    }

    public static File[] getAllLogs() {
        File file = new File(mLogBean.getLogDir());
        if (file != null && file.exists()) {
            return file.listFiles();
        }
        return null;
    }

    /**
     * 重新配置log4j
     */
    public static void resetLogConfigurator(LogBean logBean) {
        if (TextUtils.isEmpty(logBean.getLogDir()) || logBean.getTagFilterList() == null) {

            System.out.println(" ==== resetLogConfigurator return");
            System.out.println(" ==== log_dir " + logBean.getLogDir());
            System.out.println(" ==== tagNameFilterList " + logBean.getTagFilterList());
            return;
        }
        initLogConfigurator(logBean);
    }

    /**
     * Log4j建议只使用FATAL ,ERROR ,WARN ,INFO ,DEBUG这五个级别。
     */
    private static void initLogConfigurator(LogBean logBean) {
        System.out.println(" ==== 初始化log文件");
        final CustomLogConfigurator logConfigurator = new CustomLogConfigurator();
        File dirFile = new File(logBean.getLogDir());
        boolean mkdirs = false;
        if (!dirFile.exists()) {
            mkdirs = dirFile.mkdirs();
        }
        System.out.println(" ==== 创建文件夹log -> " + mkdirs);
        //设置文件名
        String fileName = "";
//        if (logBean.isLogUploading()) {
//            //当前log正在上传,创建一个临时的文件 yyyy_MM_dd_temp.log
//            String tempFileName = "temp_" + new SimpleDateFormat(logBean.getFormatType(), Locale.ENGLISH).format(logBean.getDayEndTime()) + ".log";
//            fileName = logBean.getLogDir() + File.separator + tempFileName;
//        } else {
        //当前log没有正在上传,使用正常的上传方式
        String tempFileName = new SimpleDateFormat(logBean.getFormatType(), Locale.ENGLISH).format(logBean.getDayEndTime()) + ".log";
        fileName = logBean.getLogDir() + File.separator + tempFileName;
//        }

        File file = new File(fileName);
        System.out.println(" ==== logFile -> " + file);
        if (!file.exists()) {
            try {
                boolean newFile = file.createNewFile();
                System.out.println(" ==== logFile 创建新的文件 -> " + newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logBean.setLogFile(file);
        logConfigurator.setFileName(fileName);
        //设置root日志输出级别 默认为DEBUG
        logConfigurator.setRootLevel(logBean.getCurrentLevel());
        // 设置日志输出级别
        logConfigurator.setLevel("org.apache", logBean.getCurrentLevel());
        //设置 输出到日志文件的文字格式 默认 %d %-5p [%c{2}]-[%L] %m%n
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        //设置输出到控制台的文字格式 默认%m%n
        logConfigurator.setLogCatPattern("%m%n");
        //设置总文件大小
        logConfigurator.setMaxFileSize(logBean.getLogMaxFileSize());
        //设置最大产生的文件个数
        logConfigurator.setMaxBackupSize(logBean.getLogMaxBackUPSize());
        //设置所有消息是否被立刻输出 默认为true,false 不输出
        logConfigurator.setImmediateFlush(true);
        //是否本地控制台打印输出 默认为true ，false不输出
        logConfigurator.setUseLogCatAppender(true);
        //设置是否启用文件附加,默认为true。false为覆盖文件
        logConfigurator.setUseFileAppender(true);
        //设置是否重置配置文件，默认为true       重要->设置为false会出事
        logConfigurator.setResetConfiguration(true);
        //是否显示内部初始化日志,默认为false
        logConfigurator.setInternalDebugging(false);
        //增加日志过滤
        logConfigurator.addTagFilterName(logBean.getTagFilterList());
        logConfigurator.configure();
    }

    private static Logger getLogger(String tag, LogBean logBean) {
        long timeMillis = System.currentTimeMillis();
        if (timeMillis > logBean.getDayEndTime()) {
            //时间增加一天
            long time = timeMillis + 60 * 60 * 24 * 1000;
            logBean.setDayEndTime(time);
            //dayEndTime = dayEndTime + 60 * 1000;
            //时间大于本天 23:59:59 ,此时认为是下一天
            resetLogConfigurator(logBean);
            System.out.println(" ==== 时间过了一天,重新生成logFile");
        }
        if (!logBean.checkLogFile()) {
            System.out.println("Log File 不存在,重新成成logFile");
            resetLogConfigurator(logBean);
        }
        return Logger.getLogger(tag);
    }

    /**
     * 获取当天结束时间
     * 23 : 59 : 59
     */
    private static long getDayEndTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTimeInMillis();
    }

    private static class LogBean {
        private String logDir;
        private long dayEndTime;
        private File logFile;
        private List<String> tagFilterList;
        private boolean needSaveToLog;
        private Level currentLevel;
        private long logMaxFileSize;
        private int logMaxBackUPSize;
        private boolean logUploading;
        private String formatType;

        private boolean checkLogFile() {
            if (getLogFile() == null || !getLogFile().exists()) {
                return false;
            } else {
                return true;
            }
        }

        public String getFormatType() {
            return formatType;
        }

        public void setFormatType(String formatType) {
            this.formatType = formatType;
        }

        public boolean isLogUploading() {
            return logUploading;
        }

        public void setLogUploading(boolean logUploading) {
            this.logUploading = logUploading;
        }

        public Level getCurrentLevel() {
            return currentLevel;
        }

        public void setCurrentLevel(Level currentLevel) {
            this.currentLevel = currentLevel;
        }

        public long getLogMaxFileSize() {
            return logMaxFileSize;
        }

        public void setLogMaxFileSize(long logMaxFileSize) {
            this.logMaxFileSize = logMaxFileSize;
        }

        public int getLogMaxBackUPSize() {
            return logMaxBackUPSize;
        }

        public void setLogMaxBackUPSize(int logMaxBackUPSize) {
            this.logMaxBackUPSize = logMaxBackUPSize;
        }

        public boolean isNeedSaveToLog() {
            return needSaveToLog;
        }

        public void setNeedSaveToLog(boolean needSaveToLog) {
            this.needSaveToLog = needSaveToLog;
        }

        public List<String> getTagFilterList() {
            return tagFilterList;
        }

        public void setTagFilterList(List<String> tagFilterList) {
            this.tagFilterList = tagFilterList;
        }

        public File getLogFile() {
            return logFile;
        }

        public void setLogFile(File logFile) {
            this.logFile = logFile;
        }

        public String getLogDir() {
            return logDir;
        }

        public void setLogDir(String logDir) {
            this.logDir = logDir;
        }

        public long getDayEndTime() {
            return dayEndTime;
        }

        public void setDayEndTime(long dayEndTime) {
            this.dayEndTime = dayEndTime;
        }
    }

    public static class CustomLogConfigurator {

        private List<String> mList;

        private Level rootLevel;
        private String filePattern;
        private String logCatPattern;
        private String fileName;
        private int maxBackupSize;
        private long maxFileSize;
        private boolean immediateFlush;
        private boolean useLogCatAppender;
        private boolean useFileAppender;
        private boolean resetConfiguration;
        private boolean internalDebugging;


        public CustomLogConfigurator() {
            this.rootLevel = Level.DEBUG;
            this.filePattern = "%d - [%p::%c::%C] - %m%n";
            this.logCatPattern = "%m%n";
            this.fileName = "android-log4j.log";
            this.maxBackupSize = 5;
            this.maxFileSize = 524288L;
            this.immediateFlush = true;
            this.useLogCatAppender = true;
            this.useFileAppender = true;
            this.resetConfiguration = true;
            this.internalDebugging = false;
        }

        public CustomLogConfigurator(String fileName) {
            this.rootLevel = Level.DEBUG;
            this.filePattern = "%d - [%p::%c::%C] - %m%n";
            this.logCatPattern = "%m%n";
            this.fileName = "android-log4j.log";
            this.maxBackupSize = 5;
            this.maxFileSize = 524288L;
            this.immediateFlush = true;
            this.useLogCatAppender = true;
            this.useFileAppender = true;
            this.resetConfiguration = true;
            this.internalDebugging = false;
            this.setFileName(fileName);
        }

        public CustomLogConfigurator(String fileName, Level rootLevel) {
            this(fileName);
            this.setRootLevel(rootLevel);
        }

        public CustomLogConfigurator(String fileName, Level rootLevel, String filePattern) {
            this(fileName);
            this.setRootLevel(rootLevel);
            this.setFilePattern(filePattern);
        }

        public CustomLogConfigurator(String fileName, int maxBackupSize, long maxFileSize, String filePattern, Level rootLevel) {
            this(fileName, rootLevel, filePattern);
            this.setMaxBackupSize(maxBackupSize);
            this.setMaxFileSize(maxFileSize);
        }

        public void configure() {
            Logger root = Logger.getRootLogger();
            if (this.isResetConfiguration()) {
                LogManager.getLoggerRepository().resetConfiguration();
            }

            LogLog.setInternalDebugging(this.isInternalDebugging());
            if (this.isUseFileAppender()) {
                this.configureFileAppender();
            }

            if (this.isUseLogCatAppender()) {
                this.configureLogCatAppender();
            }
            root.setLevel(this.getRootLevel());
        }

        public void setLevel(String loggerName, Level level) {
            Logger.getLogger(loggerName).setLevel(level);
        }

        private void configureFileAppender() {
            Logger root = Logger.getRootLogger();
            PatternLayout fileLayout = new PatternLayout(this.getFilePattern());

            RollingFileAppender rollingFileAppender;
            try {
                rollingFileAppender = new RollingFileAppender(fileLayout, this.getFileName());
            } catch (IOException var5) {
                throw new RuntimeException("Exception configuring log system", var5);
            }
            for (int i = 0; i < mList.size(); i++) {
                final int finalI = i;
                rollingFileAppender.addFilter(new Filter() {
                    @Override
                    public int decide(LoggingEvent loggingEvent) {
                        return TextUtils.equals(loggingEvent.getLoggerName(), mList.get(finalI)) ? -1 : 0;
                    }
                });
            }
            rollingFileAppender.setMaxBackupIndex(this.getMaxBackupSize());
            rollingFileAppender.setMaximumFileSize(this.getMaxFileSize());
            rollingFileAppender.setImmediateFlush(this.isImmediateFlush());
            root.addAppender(rollingFileAppender);
        }


        private void configureLogCatAppender() {
            Logger root = Logger.getRootLogger();
            PatternLayout logCatLayout = new PatternLayout(this.getLogCatPattern());
            LogCatAppender logCatAppender = new LogCatAppender(logCatLayout);
            root.addAppender(logCatAppender);
        }

        public Level getRootLevel() {
            return this.rootLevel;
        }

        public void setRootLevel(Level level) {
            this.rootLevel = level;
        }

        public String getFilePattern() {
            return this.filePattern;
        }

        public void setFilePattern(String filePattern) {
            this.filePattern = filePattern;
        }

        public String getLogCatPattern() {
            return this.logCatPattern;
        }

        public void setLogCatPattern(String logCatPattern) {
            this.logCatPattern = logCatPattern;
        }

        public String getFileName() {
            return this.fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getMaxBackupSize() {
            return this.maxBackupSize;
        }

        public void setMaxBackupSize(int maxBackupSize) {
            this.maxBackupSize = maxBackupSize;
        }

        public long getMaxFileSize() {
            return this.maxFileSize;
        }

        public void setMaxFileSize(long maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public boolean isImmediateFlush() {
            return this.immediateFlush;
        }

        public void setImmediateFlush(boolean immediateFlush) {
            this.immediateFlush = immediateFlush;
        }

        public boolean isUseFileAppender() {
            return this.useFileAppender;
        }

        public void setUseFileAppender(boolean useFileAppender) {
            this.useFileAppender = useFileAppender;
        }

        public boolean isUseLogCatAppender() {
            return this.useLogCatAppender;
        }

        public void setUseLogCatAppender(boolean useLogCatAppender) {
            this.useLogCatAppender = useLogCatAppender;
        }

        public void setResetConfiguration(boolean resetConfiguration) {
            this.resetConfiguration = resetConfiguration;
        }

        public boolean isResetConfiguration() {
            return this.resetConfiguration;
        }

        public void setInternalDebugging(boolean internalDebugging) {
            this.internalDebugging = internalDebugging;
        }

        public boolean isInternalDebugging() {
            return this.internalDebugging;
        }


        /**
         * 添加过滤的 TAG 名称
         *
         * @param tagName
         */
        public void addTagFilterName(String tagName) {
            if (mList == null) {
                mList = new ArrayList<>();
            }
            mList.add(tagName);
        }

        public void addTagFilterName(@NonNull List<String> tagNameList) {
            mList = new ArrayList<>();
            mList.addAll(tagNameList);
        }

        /**
         * 清空过滤 TAG 名称的集合
         */
        public void clearTagFilter() {
            if (mList != null) {
                mList.clear();
            }
        }


    }

}
