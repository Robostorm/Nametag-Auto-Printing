package org.robostorm.response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.robostorm.config.Config;
import org.robostorm.model.NameTag;
import org.robostorm.model.Printer;

import java.io.IOException;

public class Response implements Runnable {

    private NameTag nameTag;
    private Printer printer;

    public Response(NameTag nameTag, Printer printer) {
        this.nameTag = nameTag;
        this.printer = printer;
    }

    @Override
    public void run() {
        System.out.printf("Deleting %s on printer %s\n", nameTag.toString(), printer.toString());
        String remotePath = String.format("http://%s:%s/api/files/local/%s.gcode", printer.getIp(),
                Integer.toString(printer.getPort()), nameTag.getName());
        HttpDelete delete = new HttpDelete(remotePath);
        delete.addHeader("X-Api-Key", printer.getApiKey());
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = null;
        try {
            response = client.execute(delete);
        } catch (HttpHostConnectException e) {
            throw new RuntimeException("Could not connect to printer\n", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Server Returned Code: %d\n", response != null ? response.getStatusLine().getStatusCode() : -1);
        String message;
        if (response != null) {
            switch (response.getStatusLine().getStatusCode()) {
                case 204:
                    message = "Delete Successful";
                    break;
                case 400:
                    message = "File was not deleted properly";
                    break;
                case 401:
                    message = "Incorrect API Key";
                    break;
                case 404:
                    message = "Either invalid file location was provided or API key was incorrect";
                    break;
                case 409:
                    message = "Either you are attemping to delete a file being printed or printer is not operational";
                    break;
                case 500:
                    message = "Internal server error, delete failed";
                    break;
                case -1:
                    message = "Received null response";
                    break;
                default:
                    message = "Unexpected responses";
                    break;
            }
        } else {
            message = "Response was null";
        }
        System.out.println(message);
    }
}
