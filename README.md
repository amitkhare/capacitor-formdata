# capacitor-formdata

Capacitor plugin for multipart form data uploads with image blobs. Solves Android WebView limitations where standard fetch/axios fails to send form data with images.

**Platforms:** Android, Web

## Problem

Android WebView cannot properly send multipart form data with image blobs using fetch/axios - the form data arrives empty at the server.

## Solution

This plugin uses native HTTP clients to bypass WebView limitations while maintaining web compatibility.

## Install

Local installation:
```bash
npm install file:path/to/capacitor-formdata
npx cap sync
```

## Usage

```typescript
import { FormData } from 'capacitor-formdata';

const result = await FormData.uploadFormData({
  url: 'https://your-api.com/upload',
  headers: {
    'Authorization': 'Bearer your-token'
  },
  formData: {
    'image': imageBlob,           // Blob object or base64 data URL
    'title': 'My Upload',
    'metadata': { userId: 123 }
  }
});

console.log('Status:', result.status);
console.log('Data:', result.data);
```

## Supported Data Types

- **Images**: Blob objects or base64 data URLs (`data:image/jpeg;base64,...`)
- **Text**: Strings and numbers
- **Objects**: Automatically JSON stringified

## API

### uploadFormData(options)

| Parameter | Type | Description |
|-----------|------|-------------|
| `url` | `string` | API endpoint URL |
| `headers` | `object` | HTTP headers (optional) |
| `formData` | `object` | Form fields data |
| `timeout` | `number` | Request timeout in ms (default: 30000) |

**Returns:** `Promise<{ status, statusText, headers, data }>`
