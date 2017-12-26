package com.p2p.core.update;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.p2p.core.utils.MyUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManager {
    public static final int HANDLE_MSG_DOWNING = 17;
    public static final int HANDLE_MSG_DOWN_FAULT = 19;
    public static final int HANDLE_MSG_DOWN_SUCCESS = 18;
    private static final String UPDATE_URL = "http://www.gwelltimes.com/upg/android/";
    private static UpdateManager manager = null;
    private int download_state;
    private boolean isDowning = false;
    private String version_server;

    private UpdateManager() {
    }

    public static synchronized UpdateManager getInstance() {
        UpdateManager updateManager;
        synchronized (UpdateManager.class) {
            if (manager == null) {
                synchronized (UpdateManager.class) {
                    manager = new UpdateManager();
                }
            }
            updateManager = manager;
        }
        return updateManager;
    }

    public boolean getIsDowning() {
        return this.isDowning;
    }

    public void cancelDown() {
        this.isDowning = false;
    }

    public boolean checkUpdate() {
        try {
            String version = MyUtils.getVersion();
            String[] version_parse = version.split("\\.");
            String url = UPDATE_URL + version_parse[0] + "/" + version_parse[1] + "/latestversion.asp";
            System.out.print("当前版本URL" + url);
            Log.e("nowURL", url);
            this.version_server = version;
            URL update_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) update_url.openConnection();
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            byte[] buffer = new byte[128];
            while (bis.read(buffer, 0, buffer.length) != -1) {
                this.version_server = new String(buffer);
            }
            bis.close();
            connection.disconnect();
            System.out.print("服务器版本URL" + update_url);
            Log.e("servURL", "update_url");
            String[] vaersion_server_parse = this.version_server.split("\\.");
            if (Integer.parseInt((version_parse[2] + version_parse[3]).trim()) < Integer.parseInt((vaersion_server_parse[2] + vaersion_server_parse[3]).trim())) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUpdateDescription() {
        Exception e;
        Throwable th;
        String description = "";
        BufferedInputStream bufferedInputStream = null;
        try {
            String[] version_parse = MyUtils.getVersion().split("\\.");
            HttpURLConnection connection = (HttpURLConnection) new URL(UPDATE_URL + version_parse[0] + "/" + version_parse[1] + "/des_html.asp").openConnection();
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            try {
                StringBuffer desBuffer = new StringBuffer();
                byte[] buffer = new byte[1024];
                while (bis.read(buffer, 0, buffer.length) != -1) {
                    desBuffer.append(new String(buffer, "utf-8"));
                }
                bis.close();
                connection.disconnect();
                description = desBuffer.toString();
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        bufferedInputStream = bis;
                    }
                }
                bufferedInputStream = bis;
            } catch (Exception e3) {
                e = e3;
                bufferedInputStream = bis;
                try {
                    e.printStackTrace();
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e22) {
                            e22.printStackTrace();
                        }
                    }
                    return description.trim();
                } catch (Throwable th2) {
                    th = th2;
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedInputStream = bis;
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            e.printStackTrace();
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            return description.trim();
        }
        return description.trim();
    }

    public String getUpdateDescription_en() {
        Exception e;
        Throwable th;
        String description = "";
        BufferedInputStream bufferedInputStream = null;
        try {
            String[] version_parse = MyUtils.getVersion().split("\\.");
            HttpURLConnection connection = (HttpURLConnection) new URL(UPDATE_URL + version_parse[0] + "/" + version_parse[1] + "/des_html_en.asp").openConnection();
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            try {
                StringBuffer desBuffer = new StringBuffer();
                byte[] buffer = new byte[1024];
                while (bis.read(buffer, 0, buffer.length) != -1) {
                    desBuffer.append(new String(buffer, "utf-8"));
                }
                bis.close();
                connection.disconnect();
                description = desBuffer.toString();
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                        bufferedInputStream = bis;
                    }
                }
                bufferedInputStream = bis;
            } catch (Exception e3) {
                e = e3;
                bufferedInputStream = bis;
                try {
                    e.printStackTrace();
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e22) {
                            e22.printStackTrace();
                        }
                    }
                    return description.trim();
                } catch (Throwable th2) {
                    th = th2;
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                bufferedInputStream = bis;
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            e.printStackTrace();
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            return description.trim();
        }
        return description.trim();
    }

    public void downloadApk(Handler handler, String filePath, String fileName) {
        Message msg;
        boolean isSuccess = true;
        int progress = 0;
        try {
            if (Environment.getExternalStorageState().equals("mounted")) {
                String savePath = Environment.getExternalStorageDirectory() + "/" + filePath;
                File dirfile = new File(savePath);
                if (!dirfile.exists()) {
                    dirfile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(savePath + "/" + fileName)));
                String[] version_server_parse = this.version_server.split("\\.");
                String[] version_parse = MyUtils.getVersion().split("\\.");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://www.gwelltimes.com/upg/android//" + version_parse[0] + "/" + version_parse[1] + "/" + this.version_server.trim() + ".apk").openConnection();
                BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
                int fileLength = connection.getContentLength();
                int downLength = 0;
                byte[] buffer = new byte[1024];
                this.isDowning = true;
                while (true) {
                    int n = bis.read(buffer, 0, buffer.length);
                    if (n == -1) {
                        break;
                    } else if (!this.isDowning) {
                        break;
                    } else {
                        bos.write(buffer, 0, n);
                        downLength += n;
                        progress = (int) ((((float) downLength) / ((float) fileLength)) * 100.0f);
                        msg = new Message();
                        msg.what = 17;
                        msg.arg1 = progress;
                        handler.sendMessage(msg);
                    }
                }
                isSuccess = false;
                bis.close();
                bos.close();
                this.isDowning = false;
                connection.disconnect();
            }
        } catch (Exception e) {
            this.isDowning = false;
            isSuccess = false;
            e.printStackTrace();
        }
        msg = new Message();
        msg.arg1 = progress;
        if (isSuccess) {
            msg.what = 18;
        } else {
            msg.what = 19;
        }
        handler.sendMessage(msg);
    }
}
