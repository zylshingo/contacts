package com.jikexueyuan.contacts;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class InsertContactFragment extends DialogFragment {


    private EditText textName;
    private EditText textNumber;

    public InsertContactFragment() {
        // Required empty public constructor
    }

    //设置Fragment的对话框样式 并设置点击事件添加联系人
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_blank, null);
        textName = (EditText) view.findViewById(R.id.textName);
        textNumber = (EditText) view.findViewById(R.id.textNumber);
        builder.setView(view)
                .setNegativeButton(R.string.btnCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.btnOk, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(textName.getText().toString()) && !TextUtils.isEmpty(textNumber.getText().toString())) {
                            boolean flag = true;
                            char[] str = textNumber.getText().toString().toCharArray();
                            char[] nameChar = textName.getText().toString().toCharArray();
                            for (char aStr : str) {
                                if (aStr == ' ') {
                                    Toast.makeText(getActivity(), "电话号码不能填入空格!", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                }
                            }
                            for (char aNameChar : nameChar) {
                                if (aNameChar == ' ') {
                                    Toast.makeText(getActivity(), "联系人不能填入空格!", Toast.LENGTH_SHORT).show();
                                    flag = false;
                                }
                            }
                            if (flag) {
                                addContacts();
                                Map<String, Object> map = new HashMap<>();
                                map.put("name", textName.getText().toString());
                                map.put("number", textNumber.getText().toString());
                                MainActivity.lists.add(map);
                                MainActivity.Adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "添加联系人成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.toast, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return builder.create();

    }

    //添加联系人方法
    public void addContacts() {
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        long contactId = ContentUris.parseId(resolver.insert(uri, values));

        //添加姓名
        uri = Uri.parse("content://com.android.contacts/data");
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/name");
        values.put("data2", textName.getText().toString());
        resolver.insert(uri, values);

        //添加手机号码
        values.clear();
        values.put("raw_contact_id", contactId);
        values.put("mimetype", "vnd.android.cursor.item/phone_v2");
        values.put("data1", textNumber.getText().toString());
        resolver.insert(uri, values);


    }

}
