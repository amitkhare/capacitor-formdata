import { registerPlugin } from '@capacitor/core';
import { Capacitor } from '@capacitor/core';

import type { FormDataPlugin } from './definitions';

// Register the base plugin
const FormDataBase = registerPlugin<FormDataPlugin>('FormData', {
  web: () => import('./web').then((m) => new m.FormDataWeb()),
});

// Create a wrapper that handles Blob conversion for native platforms
const FormData: FormDataPlugin = {
  echo: FormDataBase.echo.bind(FormDataBase),
  
  async uploadFormData(options) {
    if (Capacitor.isNativePlatform()) {
      // On native platforms, we need to convert Blobs to base64 before sending through the bridge
      const processedFormData: { [key: string]: any } = {};
      
      for (const [key, value] of Object.entries(options.formData)) {
        if (value instanceof Blob || value instanceof File) {
          // Convert Blob/File to base64 data URL
          const base64 = await blobToDataUrl(value);
          processedFormData[key] = base64;
        } else {
          processedFormData[key] = value;
        }
      }
      
      // Send processed data to native
      return FormDataBase.uploadFormData({
        ...options,
        formData: processedFormData
      });
    } else {
      // On web, use the web implementation directly
      return FormDataBase.uploadFormData(options);
    }
  }
};

// Helper function to convert Blob to base64 data URL
function blobToDataUrl(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  });
}

export * from './definitions';
export { FormData };
