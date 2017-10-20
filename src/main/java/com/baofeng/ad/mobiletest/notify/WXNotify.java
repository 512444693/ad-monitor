package com.baofeng.ad.mobiletest.notify;

import com.google.gson.Gson;
import com.zm.frame.utils.HttpsClientUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static com.zm.frame.log.Log.log;

public class WXNotify implements Notify {

    private String corpID;
    private String secret;
    private String toparty;
    private int agentid;
    private int tryTimes;

    private String GET_TOKEN_URL =
            "https://qyapi.weixin.qq.com/cgi-bin/gettoken?CORPID=%s&CORPSECRET=%s";
    private static final String SEND_INFO_URL_FORMAT =
            "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";
    private static final String UPLOAD_IMAGE_MATERIAL_URL_FORMAT =
            "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=image";
    private String accessToken;
    private TextInfo textInfo;
    private ImageInfo imageInfo;

    public WXNotify(String corpID, String secret, String toparty, int agentid, int tryTimes) {
        this.corpID = corpID;
        this.secret = secret;
        this.GET_TOKEN_URL = String.format(GET_TOKEN_URL, this.corpID, this.secret);
        this.toparty = toparty;
        this.agentid = agentid;
        this.tryTimes = tryTimes;
    }

    //尝试多次获取access token
    private boolean updateAccessToken() {
        int tmpTimes = tryTimes;
        while(( -- tmpTimes) >= 0) {
            String respJson = HttpsClientUtils.get(GET_TOKEN_URL);
            if (!respJson.equals("")) {
                AccessTokenInfo accessTokenInfo = AccessTokenInfo.getFromJson(respJson);
                if (accessTokenInfo.getErrcode() != 0) {
                    log.error("获取access_token失败 : " +
                            accessTokenInfo.getErrcode() + ":" + accessTokenInfo.getErrmsg());
                } else {
                    this.accessToken = accessTokenInfo.getAccess_token();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void init() {
        if (!updateAccessToken()) {
            log.error("初始化微信access_token失败");
            System.exit(1);
        }
        textInfo = new TextInfo(toparty, agentid, "");
        imageInfo = new ImageInfo(toparty, agentid, "");
    }

    private boolean send(String json) {
        int tmpTimes = tryTimes;
        while((-- tmpTimes) >= 0) {
            String url = String.format(SEND_INFO_URL_FORMAT, accessToken);
            String respJson = HttpsClientUtils.post(url, json);
            ErrorInfo errorInfo = ErrorInfo.GetFromJson(respJson);
            if (errorInfo != null) {
                if (errorInfo.getErrcode() == 0) {
                    //log.info("发送微信消息成功 : ");
                    return true;
                } else if (errorInfo.getErrcode() == 42001) {
                    log.debug("access_token过期, 重新获取");
                    updateAccessToken();
                } else {
                    log.error("发送微信消息失败 : "
                            + errorInfo.getErrcode() + ":" + errorInfo.getErrmsg());
                }
            } else {
                log.error("发送微信没有收到回包");
            }
        }
        return false;
    }

    @Override
    public void send(NotifyData notifyData) {
        String content = notifyData.getTitle();
        textInfo.setContent(content);
        String jsonToSend = textInfo.toJson();
        if (this.send(jsonToSend)) {
            log.info("发送微信消息成功 : " + content);
        }

    }

    private void sendImage(String mediaId) {
        imageInfo.setMediaId(mediaId);
        if (this.send(imageInfo.toJson())) {
            log.info("发送微信图片成功");
        }
    }

    private String uploadImage() {
        int tmpTimes = tryTimes;
        while((-- tmpTimes) >= 0) {
            String url = String.format(UPLOAD_IMAGE_MATERIAL_URL_FORMAT, accessToken);
            CloseableHttpClient httpclient = null;
            try {
                httpclient = HttpClients.createDefault();
                HttpPost httpPost = new HttpPost(url);
                HttpEntity httpEntity = MultipartEntityBuilder
                        .create().addBinaryBody("media", new File("screen_shot.jpg")).build();
                httpPost.setEntity(httpEntity);

                CloseableHttpResponse response = null;
                try {
                    response = httpclient.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity resEntity = response.getEntity();
                        String content = getRespString(resEntity);
                        UploadImageInfo retInfo = UploadImageInfo.GetFromJson(content);
                        if (retInfo != null) {
                            if (retInfo.getErrcode() == 0) {
                                return retInfo.getMedia_id();
                            } else if (retInfo.getErrcode() == 42001) {
                                log.debug("access_token过期, 重新获取");
                                updateAccessToken();
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error(e);
                } finally {
                    if(response != null) {
                        try {
                            response.close();
                        } catch (IOException e) {
                            log.error(e);
                        }
                    }
                }
            } finally {
                if(httpclient != null) {
                    try {
                        httpclient.close();
                    } catch (IOException e) {
                        log.error(e);
                    }
                }
            }
        }
        return "";
    }

    private String getRespString(HttpEntity httpEntity) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
        StringBuffer sb = new StringBuffer();
        char[] buffer = new char[4096];
        int len;
        while((len = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, len);
        }
        reader.close();
        return sb.toString();
    }

    /**
     * 指定屏幕区域截图，返回截图的BufferedImage对象
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    private BufferedImage getScreenShot(int x, int y, int width, int height) {
        BufferedImage bfImage = null;
        try {
            Robot robot = new Robot();
            bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return bfImage;
    }

    /**
     * 指定屏幕区域截图，保存到指定目录
     * @param x
     * @param y
     * @param width
     * @param height
     * @param savePath - 文件保存路径
     * @param fileName - 文件保存名称
     * @param format - 文件格式
     */
    private void screenShotAsFile(int x, int y, int width, int height, String savePath, String fileName, String format) {
        try {
            Robot robot = new Robot();
            BufferedImage bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
            File path = new File(savePath);
            File file = new File(path, fileName+ "." + format);
            ImageIO.write(bfImage, format, file);
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendScreenShot() {
        screenShotAsFile(0, 0, 1440, 900, ".", "screen_shot", "jpg");
        String mediaId = uploadImage();
        if (!mediaId.equals("")) {
            sendImage(mediaId);
        }
    }
}

class AccessTokenInfo {
    private int errcode;
    private String errmsg;
    private String access_token;
    private int expires_in;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public static AccessTokenInfo getFromJson(String json) {
        return new Gson().fromJson(json, AccessTokenInfo.class);
    }

}

class TextInfo {
    private String toparty;
    private String msgtype = "text";
    private int agentid;
    private Text text;
    private int safe = 0;

    public TextInfo(String toparty, int agentid, String content) {
        this.toparty = toparty;
        this.agentid = agentid;
        this.text = new Text(content);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public void setContent(String content) {
        this.text.setContent(content);
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public int getAgentid() {
        return agentid;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public int getSafe() {
        return safe;
    }

    public void setSafe(int safe) {
        this.safe = safe;
    }

    class Text{
        private String content;

        public Text(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

class ImageInfo {
    private String toparty;
    private String msgtype = "image";
    private int agentid;
    private Image image;
    private int safe = 0;

    public ImageInfo(String toparty, int agentid, String media_id) {
        this.toparty = toparty;
        this.agentid = agentid;
        this.image = new Image(media_id);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public void setMediaId(String media_id) {
        this.image.setMedia_id(media_id);
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public int getAgentid() {
        return agentid;
    }

    public void setAgentid(int agentid) {
        this.agentid = agentid;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getSafe() {
        return safe;
    }

    public void setSafe(int safe) {
        this.safe = safe;
    }

    class Image{
        private String media_id;

        public Image(String media_id) {
            this.media_id = media_id;
        }

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }
    }
}

class ErrorInfo {

    public static ErrorInfo GetFromJson (String json) {
        return new Gson().fromJson(json, ErrorInfo.class);
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    private int errcode;
    private String errmsg;
}

class UploadImageInfo {

    public static UploadImageInfo GetFromJson (String json) {
        return new Gson().fromJson(json, UploadImageInfo.class);
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    private int errcode;
    private String errmsg;
    private String media_id;
}