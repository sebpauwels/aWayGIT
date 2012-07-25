/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.seb.testapp;

import android.content.Intent;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.seb.away.datas.User;
import com.seb.away.datas.db.SQLiteDB;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.seb.debugAndroid.debugAndroid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class DisplayLoginActivity extends Activity
{

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "app.db";
    private static final String TABLE = "USER";
    private static final String COL_ID = "USER_ID";
    private static final String COL_NAME = "USER_NAME";
    private static final String COL_PASS = "USER_PASS";
    private static final String COL_REMEMBER = "USER_REMEMBER";
    private SQLiteDatabase bdd;
    private String login;
    private String pass;
    private Boolean autoConnect;
    private String idUser = "-1";
    private Integer remember;
    private ProgressBar mProgressBar;
    private String[][] infoUser;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_display);
        ///
       // goNextStep();
        ///
        Log.i("aWAY_DEBUG", " _connection : processing");

        mProgressBar = (ProgressBar) findViewById(R.id.progressBarConnect);

        Intent thisIntent = getIntent();
        login = thisIntent.getExtras().getString("login");
        pass = thisIntent.getExtras().getString("password");
        autoConnect = thisIntent.getExtras().getBoolean("autoConnect");
        remember = thisIntent.getExtras().getInt("remember");

        Log.i("aWAY_DEBUG", " _connection : autoconnect[" + autoConnect.toString() + "]");

        asyncConnexion connexion = new asyncConnexion();
        connexion.execute();
    }

    private class asyncConnexion extends AsyncTask<Void, Integer, Void>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), R.string.asyncConnect_connect, Toast.LENGTH_SHORT).show(); //connexion serveur

        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);



            switch (values[0])
            {

                /* case 1:
                Toast.makeText(getApplicationContext(), R.string.asyncConnect_send, Toast.LENGTH_SHORT).show(); //envoie requete serveur
                break;*/

                /* case 2:
                Toast.makeText(getApplicationContext(), R.string.asyncConnect_retrieve, Toast.LENGTH_SHORT).show(); //attente réponse
                break;*/

                case 3:
                    Toast.makeText(getApplicationContext(), R.string.asyncConnect_profile, Toast.LENGTH_SHORT).show(); //création du profil
                    break;

                case 4:
                    Toast.makeText(getApplicationContext(), R.string.asyncConnect_fail, Toast.LENGTH_SHORT).show(); //erreur
                    break;

                case 5:
                    Toast.makeText(getApplicationContext(), R.string.asyncConnect_success, Toast.LENGTH_SHORT).show(); //succes
                    break;

                default:
                    break;
            }
            // Mise à jour de la ProgressBar
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            if (suscribeOK())
            {
                infoUser = getUserInfos();
                publishProgress(5);
                Log.i("aWAY_DEBUG", " _connection : informations retrieved");
                publishProgress(3);
                newUser(infoUser);
                Log.i("aWAY_DEBUG", " _connection : user created");
                Log.i("aWAY_DEBUG", " _connection : done");
            }
            else
            {
                Log.i("aWAY_DEBUG", " _connection : fail");
                publishProgress(4);
                finish();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            Log.i("aWAY_DEBUG", "ok, what's going next ?");
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                Log.i("aWAY_DEBUG", e.toString());
            }
            goNextStep();
            finish();
        }
    }

    private boolean suscribeOK()
    {
        HttpPost httppost = new HttpPost("http://www.peekaboo-web.net/aWAYsuscribe/testok1.php");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("email", login));
        nameValuePairs.add(new BasicNameValuePair("pass", pass));

        String s = "";
        try
        {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            s = reader.readLine();
            idUser = s;
        }
        catch (Exception e)
        {
            Log.d("aWAY_DEBUG", e.toString());
            return false;
        }
        if (s == null)
        {
            return false;
        }
        return (s.equals("ok"));
    }

    private void newUser(String infoCUser[][])
    {
        SQLiteDB myDB = new SQLiteDB(this, DB_NAME, null, DB_VERSION);

        User aUser = new User(login, pass, remember);
        myDB.dbSQLReqMaker(infoCUser);
        bdd = myDB.getWritableDatabase();

        try
        {
            bdd.delete(TABLE, "", null);
        }
        catch (Exception e)
        {
            Log.d("aWAY_DEBUG", e.toString());
        }

        bdd.create(null);

        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_NAME, aUser.getName());
        values.put(COL_PASS, aUser.getPass());
        values.put(COL_REMEMBER, aUser.getRemember());
        for (int i = 0; i < infoCUser.length / 2; i++)
        {
            values.put(infoCUser[i][0], infoCUser[i][1]);
        }

        //on insère l'objet dans la BDD via le ContentValues
        bdd.insert(TABLE, null, values);

        bdd.close();
    }

    private String[][] getUserInfos()
    {
        HttpPost httppost = new HttpPost("http://www.peekaboo-web.net/aWAYsuscribe/testok3.xml");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


        nameValuePairs.add(new BasicNameValuePair("idUser", "1"));

        String[][] ret = new String[0][0];
        try
        {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse resp = httpclient.execute(httppost);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(resp.getEntity().getContent());
            org.w3c.dom.Element racine = document.getDocumentElement();
            NodeList liste = racine.getChildNodes();
            int listeLength = liste.getLength();
            String arrProfile[][] = new String[listeLength][2];
            int cpt = 0;
            for (int i = 0; i < listeLength; i++)
            {
                String nodeName = liste.item(i).getNodeName();
                String nodeValue = liste.item(i).getTextContent();
                if (!nodeName.equals("#text"))
                {
                    arrProfile[cpt][0] = nodeName;
                    arrProfile[cpt][1] = nodeValue;
                    cpt++;
                }
            }
            ret = arrProfile;
        }
        catch (Exception e)
        {
            Log.d("aWAY_DEBUG", e.toString());
        }

        return ret;
    }

    private void goNextStep()
    {
        // On met en place le passage entre les deux activités sur ce Listener
        try
        {
            Intent intent = new Intent(DisplayLoginActivity.this,
                    com.seb.testapp.mainGUI.class);
            startActivity(intent);
        }
        catch (Exception e)
        {
            Log.d("aWAY_DEBUG", e.toString());
        }
    }

    private void _debugme(String msg)
    {
        new debugAndroid().alerte(msg, DisplayLoginActivity.this);
    }
}
