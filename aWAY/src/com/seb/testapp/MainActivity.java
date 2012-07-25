package com.seb.testapp;

//new debugAndroid().alerte("test", MainActivity.this);
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.seb.debugAndroid.debugAndroid;
import com.seb.away.datas.db.SQLiteDB;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.util.Log;

public class MainActivity extends Activity
{
    
    
    Button buttonConnexion;
    Button buttonRegister;
    Boolean vueRecap = false;
    EditText textEmail;
    EditText textPass;
    LinearLayout layoutMain;
    SQLiteDatabase bdd;
    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "USER";
    private static final String COL_ID = "USER_ID";
    private static final String COL_NAME = "USER_NAME";
    private static final String COL_PASS = "USER_PASS";
    private static final String COL_REMEMBER = "USER_REMEMBER";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ecran_d_accueil);

        Log.i("aWAY_DEBUG", "***************** [App launched] *****************");

        buttonRegister = (Button) findViewById(R.acountcreation.createAcount);
        buttonRegister.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View arg0)
            {
                String url = "http://www.peekaboo-web.net/aWAYsuscribe/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri u = Uri.parse(url);
                i.setData(u);
                startActivity(i);
            }
        });

        /*************************************************************************
         * 
         * évènement clic sur le bouton de connexion
         * 
         *************************************************************************/
        buttonConnexion = (Button) findViewById(R.acountcreation.connect);
        buttonConnexion.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View actuelView)
            {
                goNextStep(false);
            }
        });


        /*************************************************************************
         * 
         * déclaration des écouteurs TextChanged validant les entrées
         * 
         *************************************************************************/
        TextWatcher verifSaisie = new TextWatcher()
        {

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                /*..*/
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
                String loginStr = getInputStringValue(R.acountcreation.email);
                String passStr = getInputStringValue(R.acountcreation.password);

                //On déclare le pattern que l’on doit suivre
                Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
                //On déclare un matcher, qui comparera le pattern avec la string passée en argument
                Matcher m = p.matcher(loginStr);

                vueRecap = true;

                // Si l’adresse mail saisie ne correspond au format d’une adresse mail
                if (m.matches() == false || passStr.length() < 8 || loginStr.equals(""))
                {
                    vueRecap = false;
                }

                if (vueRecap)
                {
                    buttonConnexion.setEnabled(true);
                }
            }

            public void afterTextChanged(Editable arg0)
            {
                /*..*/
            }
        };


        /*************************************************************************
         * 
         * affectation des écouteurs validant les saisies
         * aux éléments EditText
         * 
         *************************************************************************/
        textEmail = (EditText) findViewById(R.acountcreation.email);
        textEmail.addTextChangedListener(verifSaisie);

        textPass = (EditText) findViewById(R.acountcreation.password);
        textPass.addTextChangedListener(verifSaisie);


        if (autoConnect())
        {
            goNextStep(true);
        }
    }

    private boolean autoConnect()
    {
        boolean auto = false;
        SQLiteDB myDB = new SQLiteDB(this, DB_NAME, null, DB_VERSION);
        try
        {
            bdd = myDB.getWritableDatabase();
            //Récupère dans un Cursor les valeurs
            Cursor cur = bdd.query(TABLE, new String[]
                    {
                        COL_ID, COL_NAME, COL_PASS, COL_REMEMBER
                    }, null, null, null, null, null);


            cur.moveToFirst();
            while (cur.isAfterLast() == false)
            {
                if (cur.getString(3).equals("1"))
                {
                    String loginText = cur.getString(1);
                    String passText = cur.getString(2);
                    EditText login = (EditText) findViewById(R.acountcreation.email);
                    login.setText(loginText);
                    EditText pass = (EditText) findViewById(R.acountcreation.password);
                    pass.setText(passText);
                    CheckBox remember = (CheckBox) findViewById(R.id.checkboxSouvenir);
                    remember.setChecked(true);
                    auto = true;
                }
                cur.moveToNext();
            }

            //On ferme le cursor
            cur.close();

            bdd.close();
        }
        catch (Exception e)
        {
            Log.d("aWAY_DEBUG", e.toString());
            auto = false;
        }

        return auto;
    }

    private void goNextStep(Boolean autoConnect)
    {
        // On met en place le passage entre les deux activités sur ce Listener
        Intent intent = new Intent(MainActivity.this,
                DisplayLoginActivity.class);

        //On récupère les deux champs, puis le texte saisi
        String loginStr = getInputStringValue(R.acountcreation.email);
        String passStr = getInputStringValue(R.acountcreation.password);

        CheckBox checkbox = (CheckBox) findViewById(R.id.checkboxSouvenir);
        Integer remember = 0;
        if (checkbox.isChecked())
        {
            remember = 1;
        }

        //On rajoute les valeurs à l’Intent
        // en tant qu’extra a ce dernier.
        // Les extras sont différenciés par un “id” (string)
        intent.putExtra("login", loginStr);
        intent.putExtra("password", passStr);
        intent.putExtra("remember", remember);
        intent.putExtra("autoConnect", autoConnect);

        startActivity(intent);
        //finish();
    }

    private String getInputStringValue(int idElem)
    {
        EditText elem = (EditText) findViewById(idElem);
        String value = elem.getText().toString();

        return value;
    }

    private void _debugme(String msg)
    {
        new debugAndroid().alerte(msg, MainActivity.this);
    }
}
