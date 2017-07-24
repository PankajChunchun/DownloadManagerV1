package com.pankaj.downloadmanager;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadStatus;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;

import java.util.ArrayList;

/**
 * Local storage for downloadable url list.
 * Instead-of reading url form local, an UI interface can be
 * implemented to get url from user.
 * <p>
 * Created by Pankaj Kumar on 7/16/2017.
 * pankaj.arrah@gmail.com
 */
public final class DownloadObjStore {

    public static ArrayList<DownloadableObject> getDummyList() {
        ArrayList<DownloadableObject> result = new ArrayList<DownloadableObject>();
        result.add(new DownloadableObject(0, -1, DownloadStatus.NEW, "https://upload.wikimedia.org/wikipedia/commons/2/2c/A_new_map_of_Great_Britain_according_to_the_newest_and_most_exact_observations_%288342715024%29.jpg", 0, 0));
        result.add(new DownloadableObject(1, -1, DownloadStatus.NEW, "http://sherly.mobile9.com/download/media/550/cutehatsun_y2FkUEgM.jpg", 0, 0));
        result.add(new DownloadableObject(2, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(3, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(4, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(5, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(6, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(7, -1, DownloadStatus.NEW, "http://sherly.mobile9.com/download/media/550/cutehatsun_y2FkUEgM.jpg", 0, 0));
        result.add(new DownloadableObject(8, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(9, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 10, 0));
        result.add(new DownloadableObject(10, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 20, 0));
        result.add(new DownloadableObject(11, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 30, 0));
        result.add(new DownloadableObject(12, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(13, -1, DownloadStatus.NEW, "http://sherly.mobile9.com/download/media/550/cutehatsun_y2FkUEgM.jpg", 0, 0));
        result.add(new DownloadableObject(14, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(15, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 50, 0));
        result.add(new DownloadableObject(16, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(17, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 10, 0));
        result.add(new DownloadableObject(18, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(19, -1, DownloadStatus.NEW, "http://sherly.mobile9.com/download/media/550/cutehatsun_y2FkUEgM.jpg", 0, 0));
        result.add(new DownloadableObject(20, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        result.add(new DownloadableObject(21, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 7, 0));
        result.add(new DownloadableObject(22, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 87, 0));
        result.add(new DownloadableObject(23, -1, DownloadStatus.NEW, "http://www.coolandroidwallpapers.com/wp-content/uploads/Cute/Cute%20Android%20Wallpapers%20HD%2073.jpg", 0, 0));
        return result;
    }
}
