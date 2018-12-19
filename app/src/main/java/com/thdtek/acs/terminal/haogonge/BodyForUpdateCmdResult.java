package com.thdtek.acs.terminal.haogonge;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okio.Buffer;

/**
 * 好工e http请求body
 */
public class BodyForUpdateCmdResult {

    private static final String TAG = BodyForUpdateCmdResult.class.getSimpleName();
    private boolean executed_str = false;
    private boolean executed_req = false;
    private List<BodyItem> lst = new ArrayList<>();


    public BodyForUpdateCmdResult add(BodyItem item){
        lst.add(item);
        return this;
    }


    /**
     * 专用于好工e云平台http请求的body
     * @return
     */
    public String getBodyString(){

        if(executed_str){
            throw new RuntimeException("BodyForUpdateCmdResult.getBodyString()只能执行一次！");
        }

        if(lst.size() == 0){
            return null;
        }

        Buffer buffer = new Buffer();
        for (int i = 0; i < lst.size(); i++) {
            BodyItem item = lst.get(i);
            String key = String.valueOf(item.getId());

            String val = "";
            if(item.getCode() == 0){
                val = "0";
            }else{
                val = item.getCode()+ "&msg=" + item.getMsg();
            }


            if (i > 0) buffer.writeByte('&');
            buffer.writeUtf8(key);
            buffer.writeByte('=');
            buffer.writeUtf8(val);
            buffer.writeUtf8("\n");
        }


        executed_str = true;

        String bodyStr = new String(buffer.readByteArray());

        return bodyStr;

    }

    /**
     * 鉴于好工e云平台http请求的body有自定义格式，所以不适合使用此body
     * @return
     */
    public FormBody getBody(){
        if(executed_req){
            throw new RuntimeException("BodyForUpdateCmdResult.getBody()只能执行一次！");
        }

        if(lst.size() == 0){
            return null;
        }

        //编写body用于请求
        FormBody body = null;
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (int i = 0; i < lst.size(); i++) {
                BodyItem item = lst.get(i);
                String key = String.valueOf(item.getId());
                String val = item.getCode()+ "&msg=" + item.getMsg();
                builder.addEncoded(
                        URLEncoder.encode(key, "utf-8"),
                        URLEncoder.encode(val, "utf-8"));
            }
            body = builder.build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        executed_req = true;

        return body;
    }
}
