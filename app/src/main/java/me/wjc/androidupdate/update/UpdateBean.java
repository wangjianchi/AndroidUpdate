package me.wjc.androidupdate.update;

/**
 * @author:: wangjianchi
 * @time: 2018/6/6  15:14.
 * @description:
 */
public class UpdateBean {

    /**
     * version : 12
     * description : testobj
     * url : http://192.168.3.26:3000/test.png
     * size : 12M
     */

    private String version;
    private String description;
    private String url;
    private String size;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
