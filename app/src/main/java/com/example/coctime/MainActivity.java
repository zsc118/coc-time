package com.example.coctime;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<Item> list;
    Adapter adapter;
    Item it;
    byte apprentice, assistant, bellTower;
    int pos;
    static final String SET_FILE_NAME = "settings.ser";
    static final String SET_FILE_DIR = "CocTimer";
    static final int STORAGE_PERMISSION_CODE = 4, NOTIFICATION_PERMISSION_CODE = 3;

    @RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        NotificationReceiver.createNotificationChannel(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);

        /*try {
            FileInputStream fis = openFileInput(SET_FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<Item>) ois.readObject();
            apprentice = ois.readByte();
            assistant = ois.readByte();
            bellTower = ois.readByte();
            ois.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            list = new ArrayList<>();
            bellTower = assistant = apprentice = 0;
        }*/
        checkAndRequestStoragePermission();
        load();

        while (!list.isEmpty() && list.get(0).time.isBefore(LocalDateTime.now(ZoneId.systemDefault())))
            list.removeFirst();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        registerForContextMenu(lv = findViewById(R.id.lv));
        lv.setAdapter(adapter = new Adapter(this, list));

        findViewById(R.id.iv_set).setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            MenuInflater inflater = menu.getMenuInflater();
            inflater.inflate(R.menu.settings_menu, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_set) return set();
                if (id == R.id.menu_add) return add();
                if (id == R.id.menu_bell_tower) return bellTower();
                if (id == R.id.menu_building_potion) return buildingPotion();
                if (id == R.id.menu_lab_potion) return labPotion();
                if (id == R.id.menu_bellTower_potion) return bellTowerPotion();
                if (id == R.id.menu_save) return save();
                return false;
            });
            menu.show();
        });
    }

    void checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager())
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "通知权限被拒绝，部分功能可能无法使用", Toast.LENGTH_SHORT).show();
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "存储权限已授予", Toast.LENGTH_SHORT).show();
                load();
            } else
                Toast.makeText(this, "存储权限被拒绝，数据无法保存和加载", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.list_item_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        assert info != null;
        int id = item.getItemId();
        if (id == R.id.menu_add) return add();
        pos = info.position;
        if (id == R.id.menu_edt) return edit(pos);
        if (id == R.id.menu_del) return del(pos);
        if (id == R.id.menu_apprentice) return applyApprentice(pos);
        if (id == R.id.menu_assistant) return applyAssistant(pos);
        return super.onContextItemSelected(item);
    }

    boolean edit(int pos) {
        Intent intent = new Intent(this, ItemEditActivity.class);
        if (pos < 0 || pos >= list.size()) return false;
        it = list.get(pos);
        intent.putExtra("account", it.account);
        intent.putExtra("isEdit", true);
        intent.putExtra("project", it.project);
        intent.putExtra("type", it.type);
        startActivityForResult(intent, 1);
        return true;
    }

    boolean add() {
        startActivityForResult(new Intent(this, ItemEditActivity.class), 1);
        return true;
    }

    boolean bellTower() {
        byte account = getAccount();
        if (account == -1) return false;
        for (Item item : list)
            if (item.type == Item.TYPE_NIGHT && item.account == account)
                item.time = accelerate(item.time, bellTower, (byte) 10);
        adapter.notifyDataSetChanged();
        return true;
    }

    boolean bellTowerPotion() {
        byte account = getAccount();
        if (account == -1) return false;
        for (Item item : list)
            if (item.type == Item.TYPE_NIGHT && item.account == account)
                item.time = accelerate(item.time, (byte) 30, (byte) 10);
        adapter.notifyDataSetChanged();
        return true;
    }

    boolean applyApprentice(int pos) {
        Item it = list.get(pos);
        it.time = accelerate(it.time, (byte) 60, apprentice);
        adapter.notifyDataSetChanged();
        return true;
    }

    boolean applyAssistant(int pos) {
        Item it = list.get(pos);
        it.time = accelerate(it.time, (byte) 60, assistant);
        adapter.notifyDataSetChanged();
        return true;
    }

    boolean set() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("building", apprentice);
        intent.putExtra("lab", assistant);
        intent.putExtra("bellTower", bellTower);
        startActivityForResult(intent, 2);
        return true;
    }

    boolean del(int pos) {
        list.remove(pos);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null)
            if (requestCode == 1)
                editItemRes(data);
            else {
                apprentice = data.getByteExtra("building", (byte) 0);
                assistant = data.getByteExtra("lab", (byte) 0);
                bellTower = data.getByteExtra("bellTower", (byte) 0);
            }
    }

    void editItemRes(Intent data) {
        LocalDateTime t = Item.str2date(data.getStringExtra("time"));
        if (it != null) {
            if (t != null) {
                it.time = t;
                int i = pos - 1;
                if (pos > 0 && it.compareTo(list.get(i)) < 0) {
                    list.set(pos, list.get(i));
                    while (i-- > 0 && it.compareTo(list.get(i)) < 0) list.set(i + 1, list.get(i));
                    list.set(i + 1, it);
                } else {
                    int n = list.size() - 1;
                    if (pos < n && it.compareTo(list.get(i = pos + 1)) > 0) {
                        list.set(pos, list.get(i));
                        while (i++ < n && it.compareTo(list.get(i)) > 0)
                            list.set(i - 1, list.get(i));
                        list.set(i - 1, it);
                    }
                }
            }
            it.account = data.getBooleanExtra("account", true) ? Item.ACC_DELTA : Item.ACC_EPSILON;
            it.project = data.getStringExtra("project");
            it.type = data.getByteExtra("type", Item.TYPE_HOME_BUILDING);
            setNotificationAlarm(it);
            it = null;
        } else {
            if (t == null) {
                Toast.makeText(this, "时间格式错误！", Toast.LENGTH_SHORT).show();
                return;
            }
            Item it = new Item(data.getBooleanExtra("account", true) ? Item.ACC_DELTA : Item.ACC_EPSILON, data.getStringExtra("project"), t, data.getByteExtra("type", Item.TYPE_HOME_BUILDING));
            int i = list.size();
            list.add(it);
            while (i-- > 0) {
                Item k = list.get(i);
                if (k.compareTo(it) <= 0) break;
                list.set(i + 1, k);
            }
            list.set(i + 1, it);
            setNotificationAlarm(it);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 使用加速器
     *
     * @param t   加速前计划完成时间
     * @param len 加速多长时间 (分钟)
     * @param mul 加速器倍率
     * @return 加速后计划完成时间
     */
    static LocalDateTime accelerate(LocalDateTime t, byte len, byte mul) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        if (t == null || t.isBefore(now)) return t;
        int m = (int) ChronoUnit.MINUTES.between(now, t), n = len * mul;
        return m < n ? now.plusMinutes((long) ((double) m / mul)) : t.minusMinutes(n - len);
    }

    boolean checkStoragePermission() {
        // return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            return Environment.isExternalStorageManager();
        else
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    boolean save() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
            return false;
        }
        File dir = new File(Environment.getExternalStorageDirectory(), SET_FILE_DIR);
        if (!dir.exists())
            if (!dir.mkdirs()) {
                Toast.makeText(this, "创建存储目录失败", Toast.LENGTH_SHORT).show();
                return false;
            }
        File file = new File(dir, SET_FILE_NAME);
        //File file = new File(Environment.getExternalStorageDirectory() + File.separator + SET_FILE_DIR, SET_FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(list);
            oos.writeByte(apprentice);
            oos.writeByte(assistant);
            oos.writeByte(bellTower);
            Toast.makeText(this, "数据保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "数据保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void load() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
            list = new ArrayList<>();
            apprentice = assistant = bellTower = 0;
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + SET_FILE_DIR, SET_FILE_NAME);
        if (!file.exists()) {
            list = new ArrayList<>();
            apprentice = assistant = bellTower = 0;
            return;
        }
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            list = (ArrayList<Item>) ois.readObject();
            apprentice = ois.readByte();
            assistant = ois.readByte();
            bellTower = ois.readByte();
            Toast.makeText(this, "数据加载成功", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "文件未找到: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "文件读写错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "类未找到错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();
    }

    /*void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入错误").setMessage(message).setPositiveButton("确定", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

    boolean buildingPotion() {
        byte account = getAccount();
        if (account == -1) return false;
        for (Item it : list)
            if (it.type == Item.TYPE_HOME_BUILDING && it.account == account)
                it.time = accelerate(it.time, (byte) 60, (byte) 10);
        adapter.notifyDataSetChanged();
        return true;
    }

    boolean labPotion() {
        byte account = getAccount();
        if (account == -1) return false;
        for (Item it : list)
            if (it.type == Item.TYPE_HOME_LAB && it.account == account)
                it.time = accelerate(it.time, (byte) 60, (byte) 24);
        adapter.notifyDataSetChanged();
        return true;
    }

    byte getAccount() {
        final byte[] r = new byte[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择账号");
        builder.setItems(Item.ACCOUNT_NAME, (dialog, which) -> {
            r[0] = (byte) which;
            dialog.dismiss();
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            r[0] = -1;
            dialog.dismiss();
        });
        builder.create().show();
        return r[0];
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        if (list.isEmpty()) return;
        Item it = list.get(0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("content", it.getText());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long triggerTime = it.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }*/

    void setNotificationAlarm(Item it) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("content", it.getText());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        long triggerTime = it.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}