import { WebPlugin } from '@capacitor/core';

import type { FormDataPlugin } from './definitions';

export class FormDataWeb extends WebPlugin implements FormDataPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async uploadFormData(options: {
    url: string;
    headers?: { [key: string]: string };
    formData: { [key: string]: any };
    timeout?: number;
  }): Promise<{
    status: number;
    statusText: string;
    headers: { [key: string]: string };
    data: any;
  }> {
    try {
      // On web, we can use the standard fetch API since it works properly
      const formData = new FormData();
      
      // Add all form data fields
      for (const [key, value] of Object.entries(options.formData)) {
        if (value instanceof Blob) {
          formData.append(key, value);
        } else if (value instanceof File) {
          formData.append(key, value);
        } else if (typeof value === 'string' && value.startsWith('data:')) {
          // Handle base64 data URLs by converting to Blob
          const blob = this.dataUrlToBlob(value);
          formData.append(key, blob);
        } else if (typeof value === 'object' && value !== null) {
          formData.append(key, JSON.stringify(value));
        } else {
          formData.append(key, String(value));
        }
      }

      const fetchOptions: RequestInit = {
        method: 'POST',
        body: formData,
        headers: options.headers || {},
      };

      if (options.timeout) {
        const controller = new AbortController();
        setTimeout(() => controller.abort(), options.timeout);
        fetchOptions.signal = controller.signal;
      }

      const response = await fetch(options.url, fetchOptions);
      
      // Parse response headers
      const responseHeaders: { [key: string]: string } = {};
      response.headers.forEach((value, key) => {
        responseHeaders[key] = value;
      });

      // Try to parse response as JSON, fallback to text
      let responseData: any;
      const contentType = response.headers.get('content-type');
      if (contentType && contentType.includes('application/json')) {
        responseData = await response.json();
      } else {
        responseData = await response.text();
      }

      return {
        status: response.status,
        statusText: response.statusText,
        headers: responseHeaders,
        data: responseData,
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error occurred';
      throw new Error(`Upload failed: ${errorMessage}`);
    }
  }

  private dataUrlToBlob(dataUrl: string): Blob {
    const arr = dataUrl.split(',');
    const mimeMatch = arr[0].match(/:(.*?);/);
    const mime = mimeMatch ? mimeMatch[1] : 'application/octet-stream';
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }
    return new Blob([u8arr], { type: mime });
  }
}
