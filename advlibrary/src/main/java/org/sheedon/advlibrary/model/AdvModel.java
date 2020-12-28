package org.sheedon.advlibrary.model;

/**
 * 广告model
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/10 10:07
 */
public class AdvModel {

    // 图片路径
    private String imagePath;
    // 视频路径
    private String videoPath;
    // h5路径
    private String webUrl;
    private String account;
    private String password;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebInfo(String webUrl,String account,String password) {
        this.webUrl = webUrl;
        this.account = account;
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

}
