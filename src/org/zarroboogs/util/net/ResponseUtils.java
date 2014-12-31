
package org.zarroboogs.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public class ResponseUtils {

    /**
     * @param response
     * @return
     */
    public static String getResponseLines(boolean isDebug, HttpResponse response) {
        String allResponse = null;
        if (response == null) {
            return null;
        }
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream in;
            try {
                in = entity.getContent();
                String str = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                while ((str = br.readLine()) != null) {
                    allResponse += str + "\n";
                }

            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Error IllegalStateException----");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Error IOException");
            }
        }
        if (isDebug) {
            System.out.println("[getResponseLines]" + allResponse);
        }

        return allResponse;
    }
}
