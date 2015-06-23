package nametagautoprint;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static nametagautoprint.NametagAutoPrint.gcodeDirectory;
import static nametagautoprint.NametagAutoPrint.p;

public class PrintServer {

    private static PrinterManager printerManager = new PrinterManager();
    private static Distributor distributor = new Distributor();
    private static ExecutorService slicerPool = Executors.newFixedThreadPool(5);

    public static void start() {
        new Thread(printerManager).start();
        new Thread(distributor).start();
        System.out.println("Starting Print Server");
    }

    public static void stop() {
        printerManager.stop();
        distributor.stop();
        System.out.println("Stoppping Print Server");
    }

    static class PrinterManager implements Runnable {

        private boolean isStopped = false;

        @Override
        public void run() {
            System.out.println("Starting Printer Manager");
            while (!isStopped()) {
                Printer printer = PrintMaster.getNextPrinter();
                if(printer != null) {
                    Nametag nametag = PrintMaster.getNextNameToSlicer();
                    if(nametag != null) {
                        System.out.printf("Assigning nametag %s to Printer %s\n", nametag.toString(), printer.getName());
                        printer.setAvailable(false);
                        slicerPool.execute(new Slicer(printer, nametag));
                    }
                }
            }
        }

        public void stop() {
            isStopped = true;
            System.out.println("Stopping Printer Manager");
        }

        public boolean isStopped() {
            return isStopped;
        }
    }

    static class Slicer implements Runnable {

        Printer printer;
        Nametag nametag;

        public Slicer(Printer printer, Nametag nametag) {
            this.printer = printer;
            this.nametag = nametag;
        }

        @Override
        public void run() {
            System.out.printf("Slicing nametag %s for printer %s\n", nametag.toString(), printer.getName());
            printer.slice(nametag);
        }
    }

    static class Distributor implements Runnable {

        private boolean isStopped = false;

        @Override
        public void run() {
            System.out.println("Starting Distributor");
            while (!isStopped()) {
                Nametag nametag = PrintMaster.getNextNameToUpload();
                if(nametag != null && nametag.getPrinter() != null) {
                    System.out.printf("uploading nametag %s to printer %s\n", nametag.toString(), nametag.getPrinter().getName());
                    try {
                        upload(nametag.getPrinter(), nametag);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void upload(Printer printer, Nametag nametag) throws IOException {
            File file = new File(String.format("%s/%s.gcode", gcodeDirectory, nametag.toString()));
            if(!file.exists()) {
                System.err.println("Attempting to upload file that does not exist from nametag " + nametag.toString());
                return;
            }
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
                System.err.println("Could not connect to printer");
                return;
            }
            System.out.printf("Server Returned Code: %d\n", response.getStatusLine().getStatusCode());
            String message;
            switch (response.getStatusLine().getStatusCode()) {
                case 201:
                    message = "Upload Successful";
                    PrintMaster.removeFromQueue(nametag);
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
                default:
                    message = "Unexpected responses";
                    break;
            }
            System.out.println(message);
        }

        public void stop() {
            isStopped = true;
            System.out.println("Stopping Distributor");
        }

        public boolean isStopped() {
            return isStopped;
        }
    }
}
