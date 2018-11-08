package com.thdtek.acs.terminal.socket.core;


import android.nfc.Tag;

import com.google.protobuf.ByteString;
import com.thdtek.acs.terminal.Msg;
import com.thdtek.acs.terminal.base.ThreadPool;
import com.thdtek.acs.terminal.server.AppHttpServer;
import com.thdtek.acs.terminal.socket.command.CommandPool2;
import com.thdtek.acs.terminal.util.AESUtils;
import com.thdtek.acs.terminal.util.LogUtils;

public class SendMsgHelper {

    private static final String TAG = SendMsgHelper.class.getSimpleName();
    //发送数据超时时间 毫秒
    private final Long SEND_TIMEOUT_DEFAULT_MS = 10 * 1000L;
    //默认重新请求次数
    private final int SEND_NEED_REPEAT_NUM = 0;
    //标明本类的一个实例是否已经执行发送消息操作  默认没有执行
    private boolean executed = false;

    /**
     * @param message
     * @param seq
     * @param timeOutMs
     * @param needRepeatNum
     * @param callback
     */
    private void send(final Msg.Message message, final int seq, final Long timeOutMs, final Integer needRepeatNum, final RequestCallback callback) {

        if (executed) {

            throw new RuntimeException("此类的实例只能执行一次 request或者response操作");
        } else {
            executed = true;

//            ThreadPool.getThread().execute(new Runnable() {
//                @Override
//                public void run() {
                    try {
                        byte[] enc_re = AESUtils.enc(message.toByteArray(), AES.getEncKey(seq).getBytes());

                        //使用proto的size字段来标明每次消息整体长度
                        //1.size需要放在第一个位置设置(赋值，随便赋一个值占位置)
                        //2.最终在此处再次赋值给size真确的值
                        Msg.Package.Builder builder = Msg.Package.newBuilder()
                                .setSize(1)/**站位*/
                                .setSeq(seq)
                                .setData(ByteString.copyFrom(enc_re));

                        builder.setSize(builder.build().toByteArray().length);

                        Msg.Package pkg = builder.build();


                        if (message.hasHeartBeatReq()) {
                            //心跳数据
                            ConnectHandler.getConnectCore().sendHeart(pkg);
                        } else {
                            //非心跳数据
                            ConnectHandler.getConnectCore().send(pkg);
                        }


                        //如果是发起请求 则保存请求相关信息
                        if (seq % 2 == 1) {
                            RepeatBean repeatBean = new RepeatBean(needRepeatNum, 0);
                            CommandPool2.getInstance().put(
                                    (long) seq,
                                    pkg,
                                    callback,
                                    timeOutMs,
                                    repeatBean,
                                    Object.class);
                        }
                    } catch (Exception e) {
                        LogUtils.e(TAG, "SendMsgHelper error = " + e.getMessage());
                    }


//                    LogUtils.i(TAG, "send:" + pkg.toString());
//                }
//            });
        }

    }


    /**
     * 发起请求
     *
     * @param message
     * @param callback
     */
    public void request(Msg.Message message, RequestCallback callback) {
        request(message, SEND_TIMEOUT_DEFAULT_MS, SEND_NEED_REPEAT_NUM, callback);
    }

    /**
     * 发起请求
     *
     * @param message
     * @param needRepeatNum
     * @param callback
     */
    public void request_custom_repeat(Msg.Message message, int needRepeatNum, RequestCallback callback) {
        request(message, SEND_TIMEOUT_DEFAULT_MS, needRepeatNum, callback);
    }

    /**
     * 发起请求
     * <p>
     * 设置超时时间无效
     *
     * @param message
     * @param timeOutMs
     * @param callback
     */
    @Deprecated
    public void request_custom_timeout(Msg.Message message, long timeOutMs, RequestCallback callback) {
        request(message, timeOutMs, SEND_NEED_REPEAT_NUM, callback);
    }

    /**
     * 发起请求
     * <p>
     * 设置超时时间无效
     *
     * @param message
     * @param timeOutMs
     * @param needRepeatNum
     * @param callback
     */
    @Deprecated
    public void request(Msg.Message message, long timeOutMs, int needRepeatNum, RequestCallback callback) {

        send(message, RequestSeq.getRequestKey(message), timeOutMs, needRepeatNum, callback);
    }

    /**
     * 发起请求--此方法不推荐使用，如需使用则每次使用都需要对pkg的message加密，然后计算pkg长度，最后重新组装pkg
     *
     * @param pkg        已经加密和计算消息包长
     * @param seq
     * @param timeOutMs
     * @param repeatBean
     * @param callback
     */
    @Deprecated
    public void request(Msg.Package pkg, int seq, long timeOutMs, RepeatBean repeatBean, RequestCallback callback) {
        executed = true;

        //pkg必须是已经加密已经计算包长
        ConnectHandler.getConnectCore().send(pkg);

        //如果是发起请求 则保存请求相关信息
        if (seq % 2 == 1) {
            CommandPool2.getInstance().put(
                    (long) seq,
                    pkg,
                    callback,
                    timeOutMs,
                    repeatBean,
                    Object.class);
        }
    }

    /**
     * 响应服务端
     *
     * @param message
     * @param seq
     */
    public void response(Msg.Message message, int seq) {
        send(message, seq, null, null, null);

    }


}
