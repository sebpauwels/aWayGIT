/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seb.debugAndroid;

import android.content.ContextWrapper;
import android.widget.Toast;

/**
 *
 * @author spauwels1
 */
public class debugAndroid
{
    public void alerte(String msg, ContextWrapper context) //getBaseContext()
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
