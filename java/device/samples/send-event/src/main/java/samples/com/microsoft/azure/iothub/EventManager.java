package samples.com.microsoft.azure.iothub;

import java.io.IOException;
import java.net.URISyntaxException;

import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.Message;

import samples.com.microsoft.azure.iothub.SendEvent.EventCallback;

public class EventManager {

    private DeviceClient client;
    public EventManager() {
        super();
        String connString = "HostName=rasBarryPi.azure-devices.net;DeviceId=raspberryPiTest;SharedAccessKey=onMnOPM4lqUJ86gZ4gT/bI8IzxLo+/j1l5s639Tzr/s=";
        try {
            client = new DeviceClient(connString, IotHubClientProtocol.AMQPS);
            System.out.println("Successfully created an IoT Hub client.");
            
            client.open();
            System.out.println("Opened connection to IoT Hub.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error opening connection to IoT Hub. Shutting now the program");
            System.exit(0);
        }
    }
    
    public void sentEvent(String msgStr) {
        String wrappingMessage = "{\"message\":" + "\"" + msgStr + "\"" + "}";
        System.out.println("Sending Message: " + wrappingMessage);
        Message msg = new Message(wrappingMessage);
//        msg.setProperty("messageCount", Integer.toString(i));
        msg.setExpiryTime(5000);

        EventCallback callback = new EventCallback();
        client.sendEventAsync(msg, callback, null);
    }

    public void closeConnection() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
