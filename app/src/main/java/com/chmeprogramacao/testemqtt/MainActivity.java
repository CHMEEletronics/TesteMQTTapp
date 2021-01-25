package com.chmeprogramacao.testemqtt;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    static String MQTTHOST = "tcp://test.mosquitto.org:1883";
    static String USERNAME = "cowboycnb";
    static String PASSWORD = "12345";
    String topicStr ="Teste";
    String topicStr2="Teste2";

    MqttAndroidClient client;

    TextView subText;

    MqttConnectOptions options;

    Vibrator vibrator;

    Ringtone myRingtone;

    private EditText textMessage;
    private Button publishMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subText=(TextView)findViewById(R.id.subText);
        textMessage = (EditText) findViewById(R.id.textMessage);
        publishMessage = (Button) findViewById(R.id.button);

        vibrator=(Vibrator) getSystemService(VIBRATOR_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone= RingtoneManager.getRingtone(getApplicationContext(),uri);

        String clientId = MqttClient.generateClientId();
        client =new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);

        options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());



        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"conectado",Toast.LENGTH_LONG).show();
                    setsubription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"conexão falhou",Toast.LENGTH_LONG).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                subText.setText(new String(message.getPayload()));

                vibrator.vibrate(500);

                myRingtone.play();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    public void pub(View v){
        String topic = topicStr2;
        String message = textMessage.getText().toString().trim();
        try {
            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void setsubription(){
        try{
          client.subscribe(topicStr,2);
        }catch(MqttException e){
            e.printStackTrace();
        }
    }



}