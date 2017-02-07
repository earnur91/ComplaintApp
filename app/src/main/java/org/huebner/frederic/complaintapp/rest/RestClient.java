package org.huebner.frederic.complaintapp.rest;

import org.huebner.frederic.complaintapp.content.Complaint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.huebner.frederic.complaintapp.content.Complaint.ComplaintFromJson;

/**
 * Rest client for backend communication
 */
public class RestClient {
    private static final String BACKEND_HOST = "192.168.2.12";
    public static final int TIMEOUT = 6000; // ms

    /**
     * @return List with all complaint entities
     * @throws IOException
     * @throws JSONException
     */
    public static List<Complaint> getAllComplaintsFromRemote() throws IOException, JSONException {

        List<Complaint> complaints = new ArrayList<>();
        HttpURLConnection urlConnection = openConnection(getBackendUrl(), false);
        try {
            String json = readStringFromStreamString(urlConnection.getInputStream());
            final JSONArray complaintArray = new JSONArray(json);
            for (int i = 0; i < complaintArray.length(); i++) {
                complaints.add(ComplaintFromJson(complaintArray.getJSONObject(i)));
            }
        } finally {
            urlConnection.disconnect();
        }
        return complaints;
    }

    /**
     * Saves a specific complaint to the backend. Either uses POST (for creation)
     * or PUT (for updates) requests.
     *
     * @param complaint the complaint to save
     * @return the saved complaint entity
     * @throws IOException
     * @throws JSONException
     */
    public static Complaint saveComplaint(Complaint complaint) throws IOException, JSONException {

        HttpURLConnection urlConnection = openConnection(getBackendUrl(complaint.getServerId()), true);
        // if complaint is new
        if (complaint.getServerId() == null)
            urlConnection.setRequestMethod("POST");
            // else send update via put
        else
            urlConnection.setRequestMethod("PUT");

        try {
            String output = complaint.toJson();
            OutputStream out = urlConnection.getOutputStream();
            writeStringToStream(output, out);

            String json = readStringFromStreamString(urlConnection.getInputStream());
            return ComplaintFromJson(new JSONObject(json));

        } catch (IOException e) {
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_CONFLICT) {
                Complaint conflictedEntity = ComplaintFromJson(new JSONObject(readStringFromStreamString(urlConnection.getErrorStream())));
                throw new UpdateConflictException(conflictedEntity);
            }
            throw e;
        } finally {
            urlConnection.disconnect();
        }
    }


    /**
     * Deletes a complaint on the remote server
     * @param Complaint the complaint to be deleted
     * @throws IOException
     */
    public static void deleteComplaint(Complaint Complaint) throws IOException {

        HttpURLConnection urlConnection = openConnection(getBackendUrl(Complaint.getServerId()), false);
        urlConnection.setRequestMethod("DELETE");

        try {
            readStringFromStreamString(urlConnection.getInputStream());
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Opens HttpUrlConnection
     * @param url the connectoin URL
     * @param writable write data
     * @return the HttpUrlConnection used for queries
     * @throws IOException
     */
    private static HttpURLConnection openConnection(URL url, boolean writable) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(TIMEOUT);
        urlConnection.setConnectTimeout(TIMEOUT);

        if (writable) {
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
        }

        return urlConnection;
    }

    /**
     * @param serverId
     * @return the backend url used for queries
     * @throws MalformedURLException
     */
    private static URL getBackendUrl(Long serverId) throws MalformedURLException {
        URL backendUrl = getBackendUrl();
        if (serverId != null)
            backendUrl = new URL(backendUrl, String.valueOf(serverId));
        return backendUrl;
    }

    /**
     * @return
     * @throws MalformedURLException
     */
    private static URL getBackendUrl() throws MalformedURLException {
        return new URL("http://" + BACKEND_HOST + ":8080/complaint-backend/api/complaint");
    }

    /**
     * @param is
     * @return the string read from input stream
     * @throws IOException
     */
    private static String readStringFromStreamString(InputStream is) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        is.close();
        return sb.toString();
    }

    /**
     * @param output
     * @param out
     * @throws IOException
     */
    private static void writeStringToStream(String output, OutputStream out) throws IOException {
        out.write(output.getBytes("UTF-8"));
        out.flush();
    }
}
