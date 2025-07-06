package in.co.khare.capacitor.plugin.formdata;

import android.util.Log;
import android.util.Base64;
import com.getcapacitor.JSObject;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.Iterator;

public class FormData {
    private static final String TAG = "FormData";
    private static final String LINE_FEED = "\r\n";

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }

    public JSObject uploadFormData(String url, JSObject headers, JSObject formData, Integer timeout) throws Exception {
        Log.d(TAG, "Starting upload to: " + url);
        
        // Generate unique boundary per request
        String boundary = "----formdata-capacitor-" + UUID.randomUUID().toString();
        
        HttpURLConnection connection = null;
        try {
            // Setup connection
            URL uploadUrl = new URL(url);
            connection = (HttpURLConnection) uploadUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            
            // Set timeout
            int timeoutMs = timeout != null ? timeout : 30000; // Default 30 seconds
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            
            // Set content type
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            // Add custom headers
            if (headers != null) {
                Iterator<String> keys = headers.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = headers.getString(key);
                    connection.setRequestProperty(key, value);
                }
            }
            
            // Build multipart form data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            writeFormData(outputStream, formData, boundary);
            
            // Set content length
            byte[] postData = outputStream.toByteArray();
            connection.setRequestProperty("Content-Length", String.valueOf(postData.length));
            
            // Write data
            try (OutputStream os = connection.getOutputStream()) {
                os.write(postData);
                os.flush();
            }
            
            // Get response
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            
            // Read response headers
            JSObject responseHeaders = new JSObject();
            Map<String, java.util.List<String>> headerFields = connection.getHeaderFields();
            for (Map.Entry<String, java.util.List<String>> entry : headerFields.entrySet()) {
                String key = entry.getKey();
                if (key != null) {
                    // Join multiple header values with comma (compatible with older Android versions)
                    StringBuilder sb = new StringBuilder();
                    java.util.List<String> values = entry.getValue();
                    for (int i = 0; i < values.size(); i++) {
                        if (i > 0) sb.append(", ");
                        sb.append(values.get(i));
                    }
                    responseHeaders.put(key, sb.toString());
                }
            }
            
            // Read response body
            String responseBody;
            try (InputStream is = responseCode >= 200 && responseCode < 300 ? 
                    connection.getInputStream() : connection.getErrorStream()) {
                responseBody = readInputStream(is);
            }
            
            // Try to parse as JSON, fallback to string
            Object responseData;
            try {
                responseData = new JSONObject(responseBody);
            } catch (JSONException e) {
                try {
                    responseData = new JSONArray(responseBody);
                } catch (JSONException e2) {
                    responseData = responseBody;
                }
            }
            
            // Build result
            JSObject result = new JSObject();
            result.put("status", responseCode);
            result.put("statusText", responseMessage);
            result.put("headers", responseHeaders);
            result.put("data", responseData);
            
            Log.d(TAG, "Upload completed with status: " + responseCode);
            return result;
            
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private void writeFormData(OutputStream outputStream, JSObject formData, String boundary) throws IOException, JSONException {
        Iterator<String> keys = formData.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = formData.get(key);
            
            // Skip null values
            if (value == null) {
                continue;
            }
            
            if (value instanceof String) {
                String stringValue = (String) value;
                
                // Check if it's a base64 encoded image
                if (stringValue.startsWith("data:image/")) {
                    writeImageField(outputStream, key, stringValue, boundary);
                } else {
                    writeTextField(outputStream, key, stringValue, boundary);
                }
            } else if (value instanceof JSONObject) {
                writeTextField(outputStream, key, value.toString(), boundary);
            } else {
                writeTextField(outputStream, key, String.valueOf(value), boundary);
            }
        }
        
        // Write closing boundary
        String closingBoundary = "--" + boundary + "--" + LINE_FEED;
        outputStream.write(closingBoundary.getBytes());
    }
    
    private void writeTextField(OutputStream outputStream, String fieldName, String value, String boundary) throws IOException {
        String fieldHeader = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"" + LINE_FEED +
                LINE_FEED +
                value + LINE_FEED;
        outputStream.write(fieldHeader.getBytes());
    }
    
    private void writeImageField(OutputStream outputStream, String fieldName, String base64Data, String boundary) throws IOException {
        // Parse data URL: data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD...
        String[] parts = base64Data.split(",");
        if (parts.length != 2) {
            throw new IOException("Invalid base64 data URL format");
        }
        
        String mimeType = parts[0].substring(5); // Remove "data:"
        if (mimeType.contains(";")) {
            mimeType = mimeType.split(";")[0];
        }
        
        String extension = getFileExtension(mimeType);
        String filename = fieldName + "." + extension;
        
        // Decode base64
        byte[] imageData = Base64.decode(parts[1], Base64.DEFAULT);
        
        // Write field header
        String fieldHeader = "--" + boundary + LINE_FEED +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + filename + "\"" + LINE_FEED +
                "Content-Type: " + mimeType + LINE_FEED +
                LINE_FEED;
        outputStream.write(fieldHeader.getBytes());
        
        // Write image data
        outputStream.write(imageData);
        outputStream.write(LINE_FEED.getBytes());
    }
    
    private String getFileExtension(String mimeType) {
        switch (mimeType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/gif":
                return "gif";
            case "image/webp":
                return "webp";
            default:
                return "bin";
        }
    }
    
    private String readInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }
        
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
}
