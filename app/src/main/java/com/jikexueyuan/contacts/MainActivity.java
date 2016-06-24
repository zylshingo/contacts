package com.jikexueyuan.contacts;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private ListView listView;
    public static SimpleAdapter Adapter;


    //定义访问Content Provider需要的字符串
    private String[] columns = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    private String[] personName;
    private String[] personNum;
    private Intent intent;
    public static List<Map<String, Object>> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showUI();

        listView.setOnItemClickListener(this);
        findViewById(R.id.btnAdd).setOnClickListener(this);
    }

    //显示ListView界面方法
    public void showUI() {
        listView = (ListView) findViewById(R.id.List_contacts);
        setLists();
        Adapter = new SimpleAdapter(MainActivity.this, lists, R.layout.list_item, new String[]{"name", "number"}, new int[]{R.id.txtName, R.id.txtNumber});
        listView.setAdapter(Adapter);

    }

    //为ListView设置点击事件，创建对话框，并为对话框设置点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final TextView textView = (TextView) view.findViewById(R.id.txtNumber);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.dialogTitle);
        builder.setItems(R.array.dialogItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + textView.getText().toString()));
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + textView.getText().toString()));
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("restart");
        showUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("resume");
        showUI();
    }

    //设置添加联系人按钮的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
                showFragDialog();
        }
    }

    //启动添加联系人对话框方法
    private void showFragDialog() {
        InsertContactFragment fragment = new InsertContactFragment();
        fragment.show(getSupportFragmentManager(), "dialog");
    }

    //刷新lists方法
    public void setLists() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            personName = new String[cursor.getCount()];
            personNum = new String[cursor.getCount()];
            for (int i = 0; i < personName.length; ++i) {
                if (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex(columns[0]);
                    int nameIndex = cursor.getColumnIndex(columns[1]);
                    int id = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    personName[i] = name;
                    Cursor phone = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, columns[3] + "=" + id, null, null);
                    if (phone != null) {
                        while (phone.moveToNext()) {
                            int phoneNumIndex = phone.getColumnIndex(columns[2]);
                            String phoneNum = phone.getString(phoneNumIndex);
                            personNum[i] = phoneNum;
                        }
                        phone.close();
                    }
                }
            }
            cursor.close();
        }
        if (personName != null) {
            lists = new ArrayList<>();
            for (int j = 0; j < personName.length; ++j) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", personName[j]);
                map.put("number", personNum[j]);
                lists.add(map);
            }
        }
    }
}
