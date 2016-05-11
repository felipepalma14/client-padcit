package com.example.felipe.clientepadcit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.felipe.clientepadcit.conexaoAPI.HTTPDataHandler;
import com.example.felipe.clientepadcit.modelo.Carro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String URLServer = "http://padcit.pythonanywhere.com/carro/";
    private List<Carro> carros;
    private ArrayAdapter<Carro> adapter;
    ListView listView;
    Button btnReceber,btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnReceber = (Button)findViewById(R.id.btnReceber);
        btnCadastrar = (Button)findViewById(R.id.btnCadastrar);

        listView = (ListView)findViewById(R.id.listCarros);

        carros = new ArrayList<>();

        adapter = new ArrayAdapter<Carro>(MainActivity.this, android.R.layout.simple_list_item_1, carros);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(MainActivity.this,carros.get(position).getMarca(),Toast.LENGTH_LONG).show();
                Intent visualizar = new Intent(MainActivity.this, AtualizarCarro.class);
                Toast.makeText(MainActivity.this, "ID: " + carros.get(position).getId(), Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable("CARRO",carros.get(position));
                visualizar.putExtras(bundle);
                startActivity(visualizar);
            }
        });

        btnReceber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getDataJSON().execute(URLServer);
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CadastrarCarro.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private class getDataJSON extends AsyncTask<String,Void,String>{
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Pegando Dados!!!");
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String URL = params[0];//"http://padcit.pythonanywhere.com/carro/";
            String stream;

            HTTPDataHandler dataHandler = new HTTPDataHandler();
            stream = dataHandler.GetHTTPData(URL);

            return stream;
        }

        @Override
        protected void onPostExecute(String stream) {
            dialog.dismiss();

            if(stream!=null){
                Log.i("JSON",stream);

                try{
                    //JSONObject reader = new JSONObject(stream);
                    JSONArray r = new JSONArray(stream);

                    JSONObject item = r.getJSONObject(0);
                    int tam = r.length();

                    Toast.makeText(MainActivity.this, item.toString()+ " Quant: " + tam, Toast.LENGTH_LONG).show();

                    carros.clear();
                    for (int i = 0;i< r.length();i++){
                        JSONObject objs = r.getJSONObject(i);

                        Carro carro = new Carro();
                        carro.setId(objs.getLong("id"));
                        carro.setMarca(objs.getString("marca"));
                        carro.setModelo(objs.getString("modelo"));
                        //carro.setDataCriacao((Date) objs.get("created"));

                        carros.add(carro);

                    }

                    adapter = new ArrayAdapter<Carro>(MainActivity.this, android.R.layout.simple_list_item_1, carros);
                    listView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
