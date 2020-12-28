package org.sheedon.advlibrary.model;

/**
 * 资源Model
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/7/21 8:24 AM
 */
public class SourceModel {

    private String[] imagePaths;
    private String[] videoPaths;
    private String webPath;

    private String imageParents = "/storage/emulated/0/DCIM";
    private String videoParents = "/storage/emulated/0/Movies";

    public static SourceModel build(String[] imagePaths, String[] videoPaths, String webPath,
                                    String imageParents, String videoParents) {
        SourceModel model = new SourceModel();
        model.imagePaths = imagePaths;
        model.videoPaths = videoPaths;
        model.webPath = webPath;
        model.imageParents = imageParents == null || imageParents.isEmpty() ? "/storage/emulated/0/DCIM" : imageParents;
        model.videoParents = videoParents == null || videoParents.isEmpty() ? "/storage/emulated/0/Movies" : videoParents;
        return model;
    }

    public String[] getImagePaths() {
        if(imagePaths == null)
            imagePaths = new String[0];
        return imagePaths;
    }

    public String[] getVideoPaths() {
        if(videoPaths == null)
            videoPaths = new String[0];
        return videoPaths;
    }

    public String getWebPath() {
        return webPath;
    }

    public String getImageParents() {
        return imageParents;
    }

    public String getVideoParents() {
        return videoParents;
    }

    public void setImagePaths(String[] imagePaths) {
        this.imagePaths = imagePaths;
    }

    public void setVideoPaths(String[] videoPaths) {
        this.videoPaths = videoPaths;
    }

    public void setWebPath(String webPath) {
        this.webPath = webPath;
    }

    public void setImageParents(String imageParents) {
        this.imageParents = imageParents;
    }

    public void setVideoParents(String videoParents) {
        this.videoParents = videoParents;
    }
}
