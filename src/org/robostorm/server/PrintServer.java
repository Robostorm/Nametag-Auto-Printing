package org.robostorm.server;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.robostorm.config.Config;
import org.robostorm.model.NameTag;
import org.robostorm.model.Printer;
import org.robostorm.queue.NameTagQueue;
import org.robostorm.queue.PrinterQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class PrintServer implements Runnable {

    private boolean isStopped = true;

    @Autowired
    private Config config;
    @Autowired
    private PrinterQueue printerQueue;
    @Autowired
    private NameTagQueue nameTagQueue;
    private Thread thread = null;

    public synchronized Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        isStopped = false;
        synchronized (this) {
            thread = Thread.currentThread();
        }
        System.out.println("Starting print server");
        long time = System.currentTimeMillis();
        long oldTime = time;
        while (!isStopped()) {
            time = System.currentTimeMillis();
            if(time - oldTime >= config.getLoopTime()) {
                Printer printer = printerQueue.getNextPrinter();
                NameTag nameTag = nameTagQueue.getNextNameTag();
                if (printer != null && nameTag != null) {
                    System.out.printf("Assigning name tag %s to Printer %s\n", nameTag.toString(), printer.getName());
                    printer.setPrinting(true);
                    printer.setNameTag(nameTag);
                    nameTag.setPrinter(printer);
                    System.out.printf("Rendering name tag %s for printer %s\n", nameTag.toString(), printer.getName());
                    if (!nameTag.isGenerated())
                        nameTag.export();
                    System.out.printf("Slicing name tag %s for printer %s\n", nameTag.toString(), printer.getName());
                    if (!nameTag.isSliced())
                        printer.slice(nameTag);
                    if (!nameTag.isPrinting()) {
                        File file = new File(String.format("%s/%s.gcode", config.getGcodeDirectoryPath(), nameTag.toString()));
                        if (!file.exists()) {
                            System.err.println("Attempting to upload file that does not exist from nametag " + nameTag.toString());
                        } else {
                            String remotePath = String.format("http://%s:%s/api/files/local", printer.getIp(), Integer.toString(printer.getPort()));
                            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                            FileBody fileBody = new FileBody(file);
                            builder.addPart("file", fileBody);

                            HttpPost post = new HttpPost(remotePath);

                            post.setEntity(builder.build());
                            post.addHeader("X-Api-Key", printer.getApiKey());
                            HttpClient client = HttpClientBuilder.create().build();
                            HttpResponse response = null;
                            try {
                                response = client.execute(post);
                            } catch (HttpHostConnectException e) {
                                throw new RuntimeException("Could not connect to printer", e);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.printf("Server Returned Code: %d\n", response != null ? response.getStatusLine().getStatusCode() : -1);
                            String message;
                            if (response != null) {
                                switch (response.getStatusLine().getStatusCode()) {
                                    case 201:
                                        message = "Upload Successful";
                                        nameTag.setPrinting(true);
                                        printer.setPrinting(true);
                                        break;
                                    case 400:
                                        message = "File was not uploaded properly";
                                        break;
                                    case 401:
                                        message = "Incorrect API Key";
                                        break;
                                    case 404:
                                        message = "Either invalid save location was provided or API key was incorrect";
                                        break;
                                    case 409:
                                        message = "Either you are attemping to overwirte a file being printed or printer is not operational";
                                        break;
                                    case 415:
                                        message = "You attempting to uplaod a file other than a gcode or stl file";
                                        break;
                                    case 500:
                                        message = "Internal server error, upload failed";
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
                    try {
                        config.saveQueue(nameTagQueue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                time = System.currentTimeMillis();
                oldTime = time;
            }
        }
    }

    public synchronized void stop() {
        isStopped = true;
        System.out.println("Stopping print server");
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }
}
