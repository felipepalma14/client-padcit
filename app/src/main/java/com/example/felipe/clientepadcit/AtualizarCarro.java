package com.example.felipe.clientepadcit;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.felipe.clientepadcit.conexaoAPI.HTTPDataHandler;
import com.example.felipe.clientepadcit.modelo.Carro;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AtualizarCarro extends AppCompatActivity {

    Button btnAtualizar;
    EditText edtMarca,edtModelo;
    Bundle bundle;
    private String json;
    private Carro carro;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_carro);

        btnAtualizar = (Button)findViewById(R.id.btnAtualizar);
        edtMarca = (EditText)findViewById(R.id.edtMarca);
        edtModelo = (EditText)findViewById(R.id.edtModelo);

        bundle = getIntent().getExtras();
        if(bundle!=null)
            carro = (Carro)getIntent().getExtras().getSerializable("CARRO");
            //idCarro = bundle.getLong("ID");

        //Toast.makeText(AtualizarCarro.this,"ID do MEu Objeto: " + carro.getId(),Toast.LENGTH_LONG).show();

        //Recuperando Dados da URL via ID
        new getDataJSON().execute(String.valueOf(carro.getId()));


        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtualizarPost();
            }
        });

    }

    public void AtualizarPost(){

        if((edtModelo.getText().toString().length() >= 3 && edtModelo.getText().toString().length()  >= 3) ) {
            carro.setModelo(edtModelo.getText().toString());
            carro.setMarca(edtMarca.getText().toString());
            Gson gson = new Gson();
            json = gson.toJson(carro);

            //Toast.makeText(AtualizarCarro.this,json,Toast.LENGTH_LONG).show();

            new EnviaDadosAsync().execute(String.valueOf(carro.getId()));
        }else{
            Toast.makeText(AtualizarCarro.this,"Informe dados validos",Toast.LENGTH_LONG).show();
        }

        apagarDados();
    }

    public void apagarDados(){
        edtMarca.setText("");
        edtModelo.setText("");
    }



    private class getDataJSON extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... params) {
            String URL = "http://padcit.pythonanywhere.com/carro/"+ params[0];
            String stream;

            HTTPDataHandler dataHandler = new HTTPDataHandler();
            stream = dataHandler.GetHTTPData(URL);

            return stream;
        }

        @Override
        protected void onPostExecute(String stream) {

            if(stream!=null){
                Log.i("JSON",stream);

                try{
                    JSONObject reader = new JSONObject(stream);
                    //JSONArray r = new JSONArray(stream);

                    Carro carro = new Carro();
                    carro.setId(reader.getLong("id"));
                    carro.setMarca(reader.getString("marca"));
                    carro.setModelo(reader.getString("modelo"));
                        //carro.setDataCriacao((Date) objs.get("created"));

                    edtMarca.setText(carro.getMarca());

                    edtModelo.setText(carro.getModelo());


                    } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    }



    class EnviaDadosAsync extends AsyncTask<String,String,String>{

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AtualizarCarro.this);
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.setTitle("Conectando");
            dialog.setMessage("Atualizando novo carro...");
            dialog.show();


        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://padcit.pythonanywhere.com/carro/"+params[0]+"/");
                urlConnection = (HttpURLConnection) url.openConnection();

                // is output buffer writter
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Accept-encoding","gzip, deflate");

                OutputStream out = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(json);
                writer.close();
                out.close();

                int status = urlConnection.getResponseCode();
                if(status >= 400){
                    Log.i("STATUS ERROR",urlConnection.getResponseMessage());
                }else{
                    Log.i("STATUS",urlConnection.getResponseMessage());
                    urlConnection.connect();
                }
    // json data

                Log.i("CODE", "" + urlConnection.getResponseCode());
                Log.i("RESPOSTA", "" + urlConnection.getResponseMessage());
                Log.i("JSONPUT", "" + json);


                InputStream inputStream = urlConnection.getInputStream();
//input stream


                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ERROR", "Error closing stream", e);
                    }
                }
            }


            return null;
        }

            @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }
    }



}
