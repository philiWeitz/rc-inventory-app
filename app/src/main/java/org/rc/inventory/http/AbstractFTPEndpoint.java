package org.rc.inventory.http;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.rc.inventory.App;
import org.rc.inventory.R;

import java.io.IOException;

public abstract class AbstractFTPEndpoint {
    
    private static final int CONNECTION_TIMEOUT = 5 * 1000;


    protected FTPClient connectToFTPServer(String serverUrl, int fileType) throws IOException {

        final String userName = App.getInstance().getString(R.string.ftp_username);
        final String password = App.getInstance().getString(R.string.ftp_password);

        FTPClient ftp = new FTPClient();
        ftp.setConnectTimeout(CONNECTION_TIMEOUT);
        ftp.connect(serverUrl);
        ftp.setKeepAlive(true);
        ftp.login(userName, password);
        ftp.setFileType(fileType);
        ftp.enterLocalPassiveMode();

        return ftp;
    }
    
}
