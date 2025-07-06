export interface FormDataPlugin {
  /**
   * Echo a value for testing purposes
   */
  echo(options: { value: string }): Promise<{ value: string }>;
  
  /**
   * Upload multipart form data with image blobs to an API endpoint
   * @param options - Upload configuration
   * @param options.url - The API endpoint URL
   * @param options.headers - Optional HTTP headers (including Authorization)
   * @param options.formData - Form data fields (text, objects, base64 images)
   * @param options.timeout - Request timeout in milliseconds (default: 30000)
   * @returns Promise with response data
   */
  uploadFormData(options: {
    url: string;
    headers?: { [key: string]: string };
    formData: { [key: string]: any };
    timeout?: number;
  }): Promise<{
    status: number;
    statusText: string;
    headers: { [key: string]: string };
    data: any;
  }>;
}
