/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service;

import java.io.*;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.OkHttpClient;
import org.json.JSONObject;
import javax.swing.JOptionPane;
import com.clases.divisa.Divisa;
import com.clases.divisa.PesoMexicano;
import java.net.UnknownHostException;

/**
 * 
 * @author Eduardo Reyes Hernández
 */
public class ConexionApi {

    private static final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Recibe como parámetros el nombre de la divisa, ej (MXN) como tasaBase
     * y el segundo parámetro la tasa extranjera (tasaCambio). 
     * @param tasaBase
     * @param tasaCambio
     * @return
     * @throws Exception 
     */
    public static double tasaBaseCambio(String tasaBase, String tasaCambio) throws Exception {
        PesoMexicano pesoMexicano = new PesoMexicano();
        Request r = new Request.Builder()
                .url("https://api.apilayer.com/exchangerates_data/latest?symbols=" + tasaCambio + "&base=" + tasaBase + "")//EUR,GBP,USD,KRW,JPY
                .addHeader("apikey", "dYpjEe0JbW770473cXv6VOh0LSTxQvYx")
                .build();
        try {
            Response response = httpClient.newCall(r).execute();
            String res;
            double valorMoneda;
            if (!response.isSuccessful()) {
                throw new IOException("Código inesperado: " + response);
            }
            res = response.body().string();
            valorMoneda = obtenerValorRates(res);
            System.out.println("El valor actual es de: " + valorMoneda);
            return valorMoneda;

        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Tips:"
                    + "\nObtendrá a continuación la conversión con tipos de cambio para "
                    + "revalorización de balance del Banco de México con precio de cierre de jornada al\n"
                    + "31 de mayo de 2022"
                    + "\nPosibles causas de error: "
                    + "\n1. Para obtener el valor del peso mexicano actualizado, debe conectarse a una red."
                    + "\n2. El límite de consumo del servicio por la API ha excedido las 250 peticiones.",
                    "Error al conectar con la API.",
                    JOptionPane.ERROR_MESSAGE
            );
            if (tasaBase.contains(Divisa.getNOMBRE_DIVISA_USA())) {
                return pesoMexicano.getTasaCambioDolarAmericanoFijo();
            } else if (tasaBase.contains(Divisa.getNOMBRE_DIVISA_EUROPA())) {
                return pesoMexicano.getTasaCambioEurosFijo();
            } else if (tasaBase.contains(Divisa.getNOMBRE_DIVISA_GRAN_BRETANA())) {
                return pesoMexicano.getTasaCambioLibrasEsterlinasFijo();
            } else if (tasaBase.contains(Divisa.getNOMBRE_DIVISA_YEN_JAPON())) {
                return pesoMexicano.getTasaCambioYenJaponesFijo();
            } else {
                return pesoMexicano.getTasaCambioWonSulCoreanoFijo();
            }
        }
    }

    public static double obtenerValorRates(String response) {
        JSONObject json = new JSONObject(response);
        double mxnRate = json.getJSONObject("rates").getDouble("MXN");
        return mxnRate;
    }
}
