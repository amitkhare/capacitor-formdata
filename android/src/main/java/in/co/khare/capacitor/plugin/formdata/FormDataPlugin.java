package in.co.khare.capacitor.plugin.formdata;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "FormData")
public class FormDataPlugin extends Plugin {

    private FormData implementation = new FormData();

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void uploadFormData(PluginCall call) {
        String url = call.getString("url");
        JSObject headers = call.getObject("headers");
        JSObject formData = call.getObject("formData");
        Integer timeout = call.getInt("timeout");

        if (url == null || formData == null) {
            call.reject("URL and formData are required");
            return;
        }

        // Execute the upload in a background thread
        new Thread(() -> {
            try {
                JSObject result = implementation.uploadFormData(url, headers, formData, timeout);
                call.resolve(result);
            } catch (Exception e) {
                call.reject("Upload failed: " + e.getMessage(), e);
            }
        }).start();
    }
}
