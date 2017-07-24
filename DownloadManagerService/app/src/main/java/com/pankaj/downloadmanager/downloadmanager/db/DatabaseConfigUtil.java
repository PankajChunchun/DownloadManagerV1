package com.pankaj.downloadmanager.downloadmanager.db;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

/**
 * An utility which creates configurations for mapping fields, table name etc.
 * By this way ormlite can remove all annotation work from your application and
 * make DAO creation an extremely fast operation.
 *
 * Run this utility to create config file data.
 * @see <a href="http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Using-Table-Config-File">Using Table Config File</a>
 *
 * Created by Pankaj Kumar on 7/20/2017.
 * pankaj.arrah@gmail.com
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    public static void main(String[] args) throws SQLException, IOException {
        // Read config file from res/raw directory
        writeConfigFile("ormlite_config.txt");
    }
}