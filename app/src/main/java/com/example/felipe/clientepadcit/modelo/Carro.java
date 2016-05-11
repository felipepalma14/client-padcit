package com.example.felipe.clientepadcit.modelo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Roberlandio on 01/05/2016.
 */
public class Carro  implements Serializable{

    private long id;
    private String marca;
    private String modelo;
    private Date dataCriacao;

    public Carro(){
    }

    public Carro(long id, String marca, String modelo, Date dataCriacao) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.dataCriacao = dataCriacao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public String toString() {
        return this.marca;
    }
}
