package com.jwkj.global;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import cn.com.streamax.miotp.p2p.jni.GWellUserInfo;
import cn.com.streamax.miotp.p2p.jni.P2pJni;
import com.jwkj.data.Contact;
import com.jwkj.data.DataManager;
import com.jwkj.entity.CmdCameraAdd;
import com.jwkj.entity.CmdCameraDelete;
import com.jwkj.entity.CmdCameraDelete.CmdParamBean;
import com.jwkj.entity.CmdCameraUpdate;
import com.jwkj.entity.LocalDevice;
import com.jwkj.global.Constants.Action;
import com.jwkj.net.CMD;
import com.jwkj.net.HKHttpClient;
import com.jwkj.net.HKHttpClient.HKCallback;
import com.jwkj.utils.Utils;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PValue;
import com.p2p.shake.ShakeManager;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import okhttp3.Call;
import okhttp3.Response;

public class FList {
    private static int MESG_GET_DEV_COVER = BZip2Constants.BASEBLOCKSIZE;
    private static List<Contact> lists = null;
    private static List<LocalDevice> localdevices = new ArrayList();
    private static volatile FList manager = null;
    private static HashMap<String, Contact> maps = new HashMap();
    private static List<LocalDevice> tempLocalDevices = new ArrayList();
    public int hikam_sdk_register_state = -1;
    private Handler mHandler = new Handler(new C05616());

    class C05594 extends Thread {
        C05594() {
        }

        public void run() {
            Log.i("Register", "P2pJni.P2pClientSdkRegister start");
            FList.getInstance().hikam_sdk_register_state = 1;
            int regResult = P2pJni.P2pClientSdkRegister(1, new GWellUserInfo());
            Log.i("Register", "P2pJni.P2pClientSdkRegister finish, result = " + regResult);
            if (regResult == 0) {
                FList.getInstance().hikam_sdk_register_state = 0;
            } else {
                FList.getInstance().hikam_sdk_register_state = -1;
            }
        }
    }

    class C05605 extends Thread {
        C05605() {
        }

        public void run() {
            int i = 0;
            while (true) {
                FList.manager;
                if (i < FList.lists.size()) {
                    FList.manager;
                    Contact contact = (Contact) FList.lists.get(i);
                    if (contact.contactType == 5 || contact.contactType == 7 || contact.contactType == 2) {
                        P2PHandler.getInstance().getDefenceStates(contact.contactModel, contact.contactId, contact.contactPassword);
                    }
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    class C05616 implements Callback {
        C05616() {
        }

        public boolean handleMessage(Message msg) {
            LocalDevice localDevice;
            switch (msg.what) {
                case 17:
                    FList.localdevices.clear();
                    for (LocalDevice localDevice2 : FList.tempLocalDevices) {
                        FList.localdevices.add(localDevice2);
                    }
                    Intent i = new Intent();
                    i.setAction(Action.LOCAL_DEVICE_SEARCH_END);
                    MyApp.app.sendBroadcast(i);
                    break;
                case 18:
                    Bundle bundle = msg.getData();
                    String id = bundle.getString("id");
                    String name = bundle.getString(HttpPostBodyUtil.NAME);
                    int flag = bundle.getInt("flag", 1);
                    int type = bundle.getInt("type", 7);
                    InetAddress address = (InetAddress) bundle.getSerializable("address");
                    localDevice2 = new LocalDevice();
                    localDevice2.setContactId(id);
                    localDevice2.setFlag(flag);
                    localDevice2.setType(type);
                    localDevice2.setAddress(address);
                    String model = bundle.getString("model");
                    if (P2PValue.HikamDeviceModelList.contains(model)) {
                        localDevice2.setContactModel(model);
                    }
                    if (!FList.tempLocalDevices.contains(localDevice2)) {
                        FList.tempLocalDevices.add(localDevice2);
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    class C11251 implements HKCallback {
        C11251() {
        }

        public void onFailure(Call call, IOException e) {
        }

        public void onResponse(Call call, Response response) {
        }
    }

    class C11262 implements HKCallback {
        C11262() {
        }

        public void onFailure(Call call, IOException e) {
        }

        public void onResponse(Call call, Response response) {
        }
    }

    class C11273 implements HKCallback {
        C11273() {
        }

        public void onFailure(Call call, IOException e) {
        }

        public void onResponse(Call call, Response response) {
        }
    }

    public FList() {
        if (lists != null) {
            lists.clear();
        }
        if (localdevices != null) {
            localdevices.clear();
        }
        synchronized (FList.class) {
            manager = this;
        }
        lists = DataManager.findContactByActiveUser(MyApp.app, NpcCommon.mThreeNum);
        maps.clear();
        synchronized (lists) {
            for (Contact contact : lists) {
                maps.put(contact.contactId, contact);
            }
        }
    }

    public static FList getInstance() {
        return manager;
    }

    public List<Contact> list() {
        return lists;
    }

    public HashMap<String, Contact> map() {
        return maps;
    }

    public Contact get(int position) {
        if (position >= lists.size()) {
            return null;
        }
        return (Contact) lists.get(position);
    }

    public int getType(String threeNum) {
        Contact contact = (Contact) maps.get(threeNum);
        if (contact == null) {
            return 0;
        }
        return contact.contactType;
    }

    public void setType(String threeNum, int type) {
        Contact contact = (Contact) maps.get(threeNum);
        if (contact != null) {
            contact.contactType = type;
            DataManager.updateContact(MyApp.app, contact);
        }
    }

    public int getState(String threeNum) {
        Contact contact = (Contact) maps.get(threeNum);
        if (contact == null) {
            return 0;
        }
        return contact.onLineState;
    }

    public void setState(String threeNum, int state) {
        Contact contact = (Contact) maps.get(threeNum);
        if (contact != null) {
            contact.onLineState = state;
        }
    }

    public void setDefenceState(String threeNum, int state) {
        Contact contact = (Contact) maps.get(threeNum);
        if (contact != null) {
            contact.defenceState = state;
        }
    }

    public void setIsClickGetDefenceState(String threeNum, boolean bool) {
        Contact contact = (Contact) maps.get(threeNum);
        if (contact != null) {
            contact.isClickGetDefenceState = bool;
        }
    }

    public int size() {
        return lists.size();
    }

    public void sort() {
        synchronized (lists) {
            Collections.sort(lists);
        }
    }

    public void delete(Contact contact, int position, Handler handler) {
        maps.remove(contact.contactId);
        lists.remove(position);
        DataManager.deleteContactByActiveUserAndContactId(MyApp.app, NpcCommon.mThreeNum, contact.contactId);
        handler.sendEmptyMessage(0);
        if (P2PValue.HikamDeviceModelList.contains(contact.contactModel)) {
            P2pJni.P2pClientSdkClosePeer(contact.contactId);
        }
        Intent refreshNearlyTell = new Intent();
        refreshNearlyTell.setAction(Action.ACTION_REFRESH_NEARLY_TELL);
        MyApp.app.sendBroadcast(refreshNearlyTell);
        CmdCameraDelete cmdCameraDelete = new CmdCameraDelete();
        cmdCameraDelete.setMagic_number(CMD.MAGIC_NUMBER);
        cmdCameraDelete.setMessage_id((int) (System.currentTimeMillis() / 1000));
        cmdCameraDelete.setDate_time(CMD.getDateTime());
        cmdCameraDelete.setMessage_cmd(CMD.CMD_CAMERA_DELETE);
        CmdParamBean bean = new CmdParamBean();
        bean.setCameraID(Utils.showShortDevID(contact.contactId));
        bean.setDeviceToken(CMD.getToken());
        cmdCameraDelete.setCmd_param(bean);
        HKHttpClient.getInstance().asyncPost(cmdCameraDelete, CMD.URL, new C11251());
    }

    public void insert(Contact contact) {
        Log.e("flist", "insert");
        DataManager.insertContact(MyApp.app, contact);
        lists.add(contact);
        maps.put(contact.contactId, contact);
        String[] contactIds = new String[]{contact.contactId};
        String[] contactModels = new String[]{contact.contactModel};
        String[] password = new String[]{contact.contactPassword};
        CmdCameraAdd cmdCameraAdd = new CmdCameraAdd();
        cmdCameraAdd.setMagic_number(CMD.MAGIC_NUMBER);
        cmdCameraAdd.setMessage_id((int) (System.currentTimeMillis() / 1000));
        cmdCameraAdd.setDate_time(CMD.getDateTime());
        cmdCameraAdd.setMessage_cmd(CMD.CMD_CAMERA_ADD);
        CmdCameraAdd.CmdParamBean bean = new CmdCameraAdd.CmdParamBean();
        bean.setDeviceType(CMD.PHONE_TYPE);
        bean.setDeviceLang(CMD.getLanguage(MyApp.app));
        bean.setDeviceZoneOffset(CMD.getTimeZoneOffset());
        bean.setDeviceDstOffset(CMD.getDST());
        bean.setDeviceToken(CMD.getToken());
        bean.setCameraID(Utils.showShortDevID(contact.contactId));
        bean.setCameraName(contact.contactName);
        cmdCameraAdd.setCmd_param(bean);
        HKHttpClient.getInstance().asyncPost(cmdCameraAdd, CMD.URL, new C11262());
        P2PHandler.getInstance().getFriendStatus(contactModels, contactIds, password, NpcCommon.mThreeNum, AccountPersist.getInstance().getActiveAccountInfo(MyApp.app).three_number2);
    }

    public void update(Contact contact) {
        int i = 0;
        synchronized (lists) {
            for (Contact u : lists) {
                if (u.contactId.equals(contact.contactId)) {
                    lists.set(i, contact);
                    break;
                }
                i++;
            }
        }
        CmdCameraUpdate cmdCameraUpdate = new CmdCameraUpdate();
        cmdCameraUpdate.setMagic_number(CMD.MAGIC_NUMBER);
        cmdCameraUpdate.setMessage_id((int) (System.currentTimeMillis() / 1000));
        cmdCameraUpdate.setDate_time(CMD.getDateTime());
        cmdCameraUpdate.setMessage_cmd(CMD.CMD_CAMERA_UPDATE);
        CmdCameraUpdate.CmdParamBean bean = new CmdCameraUpdate.CmdParamBean();
        bean.setDeviceToken(CMD.getToken());
        bean.setCameraID(Utils.showShortDevID(contact.contactId));
        bean.setCameraName(contact.contactName);
        cmdCameraUpdate.setCmd_param(bean);
        HKHttpClient.getInstance().asyncPost(cmdCameraUpdate, CMD.URL, new C11273());
        maps.put(contact.contactId, contact);
        DataManager.updateContact(MyApp.app, contact);
    }

    public Contact isContact(String contactId) {
        return (Contact) maps.get(contactId);
    }

    public synchronized void disconnectFriend() {
        FList flist = getInstance();
        if (flist.size() <= 0) {
            Intent friends = new Intent();
            friends.setAction(Action.GET_FRIENDS_STATE);
            MyApp.app.sendBroadcast(friends);
        } else {
            String[] contactModels = new String[flist.size()];
            String[] contactIds = new String[flist.size()];
            List<Contact> list = flist.list();
            int i = 0;
            synchronized (lists) {
                for (Contact contact : list) {
                    contactModels[i] = contact.contactModel;
                    contactIds[i] = contact.contactId;
                    i++;
                    Log.e("flist", contact.contactId + "_" + contact.contactModel);
                }
            }
            P2PHandler.getInstance().disconnectFriend(contactModels, contactIds);
        }
    }

    public synchronized void updateOnlineState() {
        if (getInstance().hikam_sdk_register_state == -1) {
            new C05594().start();
        }
        FList flist = getInstance();
        if (flist.size() <= 0) {
            Intent friends = new Intent();
            friends.setAction(Action.GET_FRIENDS_STATE);
            MyApp.app.sendBroadcast(friends);
        } else {
            String[] contactModels = new String[flist.size()];
            String[] contactIds = new String[flist.size()];
            String[] password = new String[flist.size()];
            List<Contact> list = flist.list();
            int i = 0;
            synchronized (lists) {
                for (Contact contact : list) {
                    contactModels[i] = contact.contactModel;
                    contactIds[i] = contact.contactId;
                    password[i] = contact.contactPassword;
                    i++;
                }
            }
            P2PHandler.getInstance().getFriendStatus(contactModels, contactIds, password, NpcCommon.mThreeNum, AccountPersist.getInstance().getActiveAccountInfo(MyApp.app).three_number2);
        }
    }

    public synchronized void updataCameraCover() {
        FList flist = getInstance();
        if (flist.size() > 0) {
            if (MESG_GET_DEV_COVER >= BZip2Constants.BASEBLOCKSIZE) {
                MESG_GET_DEV_COVER = 99000;
            }
            List<Contact> list = flist.list();
            synchronized (lists) {
                for (Contact contact : list) {
                    if (P2PValue.HikamDeviceModelList.contains(contact.contactModel)) {
                        String path = Environment.getExternalStorageDirectory().getPath() + "/screenshot/tempHead/" + NpcCommon.mThreeNum;
                        File file = new File(path);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        if (P2pJni.P2pClientSdkGetSessionStatus(contact.contactId) == 3) {
                            P2pJni.P2PClientSdkGetDevCover(NpcCommon.mThreeNum, contact.contactId, contact.contactPassword, path, MESG_GET_DEV_COVER);
                        }
                    }
                }
            }
            MESG_GET_DEV_COVER++;
        }
    }

    public void getDefenceState() {
        new C05605().start();
    }

    public synchronized void searchLocalDevice() {
        ShakeManager.getInstance().setSearchTime(5000);
        ShakeManager.getInstance().setHandler(this.mHandler);
        if (ShakeManager.getInstance().shaking()) {
            tempLocalDevices.clear();
        }
    }

    public void updateLocalDeviceWithLocalFriends() {
        List<LocalDevice> removeList = new ArrayList();
        for (LocalDevice localDevice : localdevices) {
            if (manager.isContact(localDevice.getContactId()) != null) {
                removeList.add(localDevice);
            }
        }
        for (LocalDevice localDevice2 : removeList) {
            localdevices.remove(localDevice2);
        }
    }

    public List<LocalDevice> getLocalDevices() {
        return localdevices;
    }

    public List<LocalDevice> getUnsetPasswordLocalDevices() {
        List<LocalDevice> datas = new ArrayList();
        for (LocalDevice device : localdevices) {
            if (device.flag == 0 && isContact(device.contactId) == null) {
                datas.add(device);
            }
        }
        return datas;
    }

    public List<LocalDevice> getSetPasswordLocalDevices() {
        List<LocalDevice> datas = new ArrayList();
        for (LocalDevice device : localdevices) {
            if (device.flag == 1 && isContact(device.contactId) == null) {
                datas.add(device);
            }
        }
        return datas;
    }

    public LocalDevice isContactUnSetPassword(String contactId) {
        if (isContact(contactId) == null) {
            return null;
        }
        for (LocalDevice device : localdevices) {
            if (device.contactId.equals(contactId)) {
                if (device.flag != 0) {
                    return null;
                }
                return device;
            }
        }
        return null;
    }

    public void updateLocalDeviceFlag(String contactId, int flag) {
        for (LocalDevice device : localdevices) {
            if (device.contactId.equals(contactId)) {
                device.flag = flag;
                return;
            }
        }
    }
}
