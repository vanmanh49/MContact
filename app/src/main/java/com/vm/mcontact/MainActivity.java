package com.vm.mcontact;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vm.mcontact.adapter.RCAdapter;
import com.vm.mcontact.adapter.RCItemClickListener;
import com.vm.mcontact.model.Contact;
import com.vm.mcontact.util.ContactHelper;
import com.vm.mcontact.util.ContactUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private List<Contact> contacts;
    private RCAdapter adapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        init();
        initRC();
    }

    private void init() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddContactDialog();
            }
        });
    }

    private void showAddContactDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_add__edit_contact, null);
        final TextView tvName = v.findViewById(R.id.tv_name);
        final TextView tvPhoneNum = v.findViewById(R.id.tv_phone_num);
        new AlertDialog.Builder(this)
                .setTitle("Thêm danh bạ")
                .setView(v)
                .setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = tvName.getText().toString();
                        String phoneNum = tvPhoneNum.getText().toString();
                        //them contact vao danh ba cua may
                        Contact c = new Contact(name, phoneNum);
                        if (c.checkValidate()) {
                            if (!c.checkContact(contacts)) {
                                if (ContactHelper.insertContact(getContentResolver(), name, phoneNum)) {
                                    contacts.add(c);
                                    sortContact(contacts);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getApplicationContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Danh bạ đã tồn tại!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(R.string.context_header);
        menu.add(0, 0, 0, "Sửa");
        menu.add(0, 1, 1, "Xóa");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getGroupId() == 0) {
            switch (item.getItemId()) {
                case 0:
                    //sua
                    showEditDialog(contacts.get(position));
                    return true;
                case 1:
                    //xoa
                    showDeleteConfirmDialog(contacts.get(position));
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    private void showEditDialog(final Contact c) {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_add__edit_contact, null);
        final TextView tvName = v.findViewById(R.id.tv_name);
        final TextView tvPhoneNum = v.findViewById(R.id.tv_phone_num);
        tvName.setText(c.getName());
        tvPhoneNum.setText(c.getPhoneNum());
        new AlertDialog.Builder(this)
                .setTitle("Sửa số liên hệ")
                .setView(v)
                .setPositiveButton("Sửa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = tvName.getText().toString();
                        String phoneNum = tvPhoneNum.getText().toString();
                        //sua contact trong may
                        ContactHelper.deleteContact(getContentResolver(), c.getPhoneNum());
                        if (ContactHelper.insertContact(getContentResolver(), name, phoneNum)) {
                            contacts.remove(c);
                            contacts.add(new Contact(name, phoneNum));
                            sortContact(contacts);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Sửa thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sửa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showDeleteConfirmDialog(final Contact c) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá số liên hệ")
                .setMessage("Bạn có muốn xóa " + c.getName() + " không?")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //xoa contact trong may
                        if (ContactHelper.deleteContact(getContentResolver(), c.getPhoneNum())) {
                            contacts.remove(c);
                            sortContact(contacts);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void initRC() {
        contacts = ContactHelper.getAllContacts(getContentResolver());
        sortContact(contacts);
        adapter = new RCAdapter(contacts);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);
        adapter.setOnRCItemClickListener(new RCItemClickListener() {
            @Override
            public void itemClickListener(View v, int pos) {
                openContextMenu(v);
                position = pos;
            }
        });
    }

    private void sortContact(List<Contact> contacts) {
        if (contacts.size() > 0) {
            Collections.sort(contacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_export:
                if (ContactUtil.exportContacts(this)) {
                    Toast.makeText(this, "Export thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Export thất bại!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.item_import:
                showConfirmImport();
                return true;
            case R.id.item_exit:
                showConfirmExit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConfirmImport() {
        new AlertDialog.Builder(this)
                .setTitle("Import danh bạ")
                .setMessage("Việc này sẽ ghi đè lên danh bạ hiện tại của bạn!\nBạn có muốn import danh bạ không?")
                .setPositiveButton("Import", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContactUtil.importContact(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "Import thành công!", Toast.LENGTH_SHORT).show();
                            recreate();
                        } else {
                            Toast.makeText(MainActivity.this, "Import thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showConfirmExit() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát ứng dụng")
                .setMessage("Bạn có muốn thoát không?")
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
