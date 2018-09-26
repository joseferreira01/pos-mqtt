/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpb.api.mqtt;

import java.io.Serializable;
import java.util.Arrays;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

/**
 *
 * @author jose
 */
@Path("sensor")
@Stateful
public class Recurso implements Serializable {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response recuperar() {
        String data = "rest jose";
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
        String topic = "sensor/temperatura/#";
        int qos = 2;
//        String broker = "ws://test.mosquitto.org:8080";
//        String broker = "ws://localhost:9001";
        String broker = "ws://iot.eclipse.org:80/ws";
        String clientId = "job";

        try {
            MqttClient client = new MqttClient(broker, clientId, dataStore);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Conectando ao broker: " + broker);
            ClienteCall clienteCall = new ClienteCall();
            client.setCallback(clienteCall);
            data = clienteCall.valor;
            client.connect(connOpts);
            client.subscribe(topic, qos);
            System.out.println("Conectado");
        } catch (MqttException me) {
            me.printStackTrace();
        }
        return Response.ok()
                .entity(data)
                .build();
    }

    class ClienteCall implements MqttCallback {

        private String valor;

        @Override
        public void connectionLost(Throwable thrwbl) {
            System.out.println("ex = " + thrwbl);
        }

        @Override
        public void messageArrived(String topic, MqttMessage mm) throws Exception {
            byte[] bytes = mm.getPayload();
            System.out.println("topic: " + topic);
            System.out.println("array transmitido: " + Arrays.toString(bytes));
            valor = new String(bytes);
            System.out.println("valor: " +valor);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken imdt) {
            //TODO
            System.out.println("deliveryComplete");
        }
    }
}
