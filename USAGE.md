# Usage Examples for capacitor-formdata

## Basic Usage

```typescript
import { FormData } from 'capacitor-formdata';

// Example: Upload an image with form data
async function uploadImageWithFormData() {
  try {
    // Option 1: Using base64 data URL (from camera, file picker, etc.)
    const imageBase64 = 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD...';
    
    // Option 2: Using actual Blob object (from file input, fetch response, etc.)
    const imageBlob = new Blob([imageData], { type: 'image/jpeg' });
    
    const result = await FormData.uploadFormData({
      url: 'https://your-api.com/upload',
      headers: {
        'Authorization': 'Bearer your-token-here',
        'X-Custom-Header': 'custom-value'
      },
      formData: {
        'image': imageBase64,          // Can be base64 data URL
        'photo': imageBlob,            // Or actual Blob object
        'title': 'My Upload',          // Text field
        'description': 'A test image', // Text field
        'metadata': {                  // Object (will be JSON stringified)
          'userId': 123,
          'category': 'test'
        }
      },
      timeout: 30000 // 30 seconds timeout (optional)
    });
    
    console.log('Upload successful:', result);
    console.log('Status:', result.status);
    console.log('Response:', result.data);
    
  } catch (error) {
    console.error('Upload failed:', error);
  }
}
```

## With File Input (Blob)

```typescript
import { FormData } from 'capacitor-formdata';

async function uploadFromFileInput() {
  try {
    // Get file from input element
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
    const file = fileInput.files?.[0];
    
    if (file) {
      const result = await FormData.uploadFormData({
        url: 'https://your-api.com/upload',
        headers: {
          'Authorization': 'Bearer your-token-here'
        },
        formData: {
          'file': file,                    // File object (extends Blob)
          'filename': file.name,
          'filesize': file.size.toString()
        }
      });
      
      console.log('File uploaded:', result);
    }
    
  } catch (error) {
    console.error('Upload failed:', error);
  }
}
```

## With Capacitor Camera

```typescript
import { Camera, CameraResultType, CameraSource } from '@capacitor/camera';
import { FormData } from 'capacitor-formdata';

async function captureAndUpload() {
  try {
    // Take photo
    const photo = await Camera.getPhoto({
      resultType: CameraResultType.DataUrl,
      source: CameraSource.Camera,
      quality: 90
    });

    // Upload with form data
    const result = await FormData.uploadFormData({
      url: 'https://your-api.com/upload',
      headers: {
        'Authorization': 'Bearer your-token-here'
      },
      formData: {
        'photo': photo.dataUrl,
        'timestamp': new Date().toISOString(),
        'location': 'user-location'
      }
    });

    console.log('Photo uploaded:', result);
    
  } catch (error) {
    console.error('Error:', error);
  }
}
```

## Multiple Images

```typescript
async function uploadMultipleImages() {
  try {
    // Assuming you have multiple images in different formats
    const images = [
      'data:image/jpeg;base64,image1-data...',  // Base64 data URL
      'data:image/png;base64,image2-data...',   // Base64 data URL
      new Blob([binaryData], { type: 'image/jpeg' })  // Blob object
    ];

    const result = await FormData.uploadFormData({
      url: 'https://your-api.com/upload-multiple',
      headers: {
        'Authorization': 'Bearer your-token-here'
      },
      formData: {
        'image1': images[0],           // Base64 string
        'image2': images[1],           // Base64 string  
        'image3': images[2],           // Blob object
        'album_name': 'My Album',
        'upload_count': images.length.toString()
      }
    });

    console.log('Multiple images uploaded:', result);
    
  } catch (error) {
    console.error('Upload failed:', error);
  }
}
```

## Converting Between Formats

```typescript
// Convert File/Blob to base64 data URL
function blobToDataUrl(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(reader.result as string);
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  });
}

// Convert base64 data URL to Blob
function dataUrlToBlob(dataUrl: string): Blob {
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

// Example usage
async function uploadWithConversion() {
  const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement;
  const file = fileInput.files?.[0];
  
  if (file) {
    // Option 1: Upload as Blob directly
    const result1 = await FormData.uploadFormData({
      url: 'https://your-api.com/upload',
      formData: { 'image': file }
    });
    
    // Option 2: Convert to base64 first, then upload
    const dataUrl = await blobToDataUrl(file);
    const result2 = await FormData.uploadFormData({
      url: 'https://your-api.com/upload',
      formData: { 'image': dataUrl }
    });
  }
}
```

## Error Handling

```typescript
import { FormData } from 'capacitor-formdata';

async function uploadWithErrorHandling() {
  try {
    const result = await FormData.uploadFormData({
      url: 'https://your-api.com/upload',
      headers: {
        'Authorization': 'Bearer your-token-here'
      },
      formData: {
        'image': 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD...',
        'title': 'Test Upload'
      }
    });

    // Check response status
    if (result.status >= 200 && result.status < 300) {
      console.log('Success!', result.data);
    } else {
      console.error('Server error:', result.status, result.statusText);
    }
    
  } catch (error) {
    console.error('Network or other error:', error);
  }
}
```

## Response Format

The plugin returns a response object with the following structure:

```typescript
{
  status: number;           // HTTP status code (200, 404, 500, etc.)
  statusText: string;       // HTTP status text ("OK", "Not Found", etc.)
  headers: {               // Response headers
    [key: string]: string;
  };
  data: any;               // Response body (parsed JSON if applicable, otherwise string)
}
```

## Platform Behavior

- **Web**: Uses standard `fetch()` API with `FormData` - works as expected
- **Android**: Uses native HTTP client with proper multipart form data handling - solves the WebView limitation

## Notes

1. **Image Formats Supported**: 
   - **Base64 Data URLs**: `data:image/jpeg;base64,/9j/4AAQ...` (from camera, canvas, etc.)
   - **Blob Objects**: Actual Blob or File objects (from file inputs, fetch responses, etc.)
   - **Automatic Detection**: Plugin automatically detects the format and handles appropriately
2. **File Names**: The plugin automatically generates filenames based on the field name and MIME type
3. **Timeouts**: Default timeout is 30 seconds, can be customized
4. **Headers**: All custom headers are passed through, including Authorization
5. **Large Files**: The plugin handles large files efficiently in native code
6. **Cross-Platform**: Both Android and Web handle Blob and base64 formats identically
