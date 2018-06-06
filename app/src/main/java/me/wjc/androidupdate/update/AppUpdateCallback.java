package me.wjc.androidupdate.update;


/**
 * @author:: wangjianchi
 * @time: 2018/6/5  14:03.
 * @description:
 */
public interface AppUpdateCallback {
    //需要更新的回调
    void needUpdate(UpdateBean updateBean);

    void noUpdate();

    /**
     * 下载成功
     */
    void onDownloadSuccess();

    /**
     * @param progress
     * 下载进度
     */
    void onDownloading(int progress);

    /**
     * 下载失败
     */
    void onDownloadFailed();
}
