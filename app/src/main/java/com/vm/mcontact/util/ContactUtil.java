package com.vm.mcontact.util;

import android.content.Context;

import com.vm.mcontact.model.Contact;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by VanManh on 10-Dec-17.
 */

public class ContactUtil {

    public static final String vfile = "mContact.txt";

    public static boolean exportContacts(Context mContext) {
        try {
            List<Contact> list = ContactHelper.getAllContacts(mContext.getContentResolver());
            if (list.size() <= 0) {
                return false;
            }
            FileOutputStream fos = mContext.openFileOutput(vfile, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(fos, true);
            for (Contact c : list) {
                String rs = c.getName() + "\t" + c.getPhoneNum();
                writer.println(rs);
            }
            writer.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean importContact(Context context) {
        try {
            InputStream is = context.openFileInput(vfile);
            if (is != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;
                ContactHelper.deleteAllContacts(context);
                while ((line = reader.readLine()) != null) {
                    StringTokenizer tk = new StringTokenizer(line, "\t");
                    String name = tk.nextToken();
                    String phoneNum = tk.nextToken();
                    ContactHelper.insertContact(context.getContentResolver(), name, phoneNum);
                }
                reader.close();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
