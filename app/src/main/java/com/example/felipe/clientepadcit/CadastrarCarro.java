package com.example.felipe.clientepadcit;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.felipe.clientepadcit.modelo.Carro;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class CadastrarCarro extends AppCompatActivity {

    Button btnCadastrar;
    EditText edtMarca,edtModelo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_carro);

        btnCadastrar = (Button)findViewById(R.id.btnCadastrar);
        edtMarca = (EditText)findViewById(R.id.edtMarca);
        edtModelo = (EditText)findViewById(R.id.edtModelo);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarPost();
            }
        });

    }

    public void enviarPost(){
        Carro carro = new Carro();
        if((edtModelo.getText().toString().length() >= 5 && edtModelo.getText().toString().length()  >= 5) ) {
            carro.setMarca(edtMarca.getText().toString());
            carro.setModelo(edtModelo.getText().toString());

            Gson gson = new Gson();
            String json = gson.toJson(carro);

            new EnviaDadosAsync().execute(json);
        }else{
            Toast.makeText(CadastrarCarro.this,"Informe dados validos",Toast.LENGTH_LONG).show();
        }

        apagarDados();
    }

    public void apagarDados(){
        edtMarca.setText("");
        edtModelo.setText("");
    }

    class EnviaDadosAsync extends AsyncTask<String,String,String>{

        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CadastrarCarro.this);
            dialog.setIndeterminate(false);
            dialog.setCancelable(true);
            dialog.setTitle("Conectando");
            dialog.setMessage("Inserindo novo carro...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("http://padcit.pythonanywhere.com/carro/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                // is output buffer writter
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
//set headers and method
                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonData);
// json data
                writer.close();
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
