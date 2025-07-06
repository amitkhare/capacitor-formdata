# capacitor-formdata

A Capacitor plugin that handles multipart form data uploads with image blobs to APIs. Solves the Android WebView limitation where standard fetch/axios fails to properly send multipart form data with image blobs.

**Supported Platforms:** Android, Web

## Problem This Solves

On Android, the Capacitor WebView has issues with sending multipart form data containing image blobs using standard JavaScript fetch/axios. The form data arrives empty at the server. This plugin bypasses the WebView limitation by handling the HTTP request natively on Android while maintaining compatibility with web platforms.

## Features

- ✅ Send multipart form data with image blobs
- ✅ Support for authorization headers (Bearer tokens)
- ✅ Custom headers support
- ✅ Multiple form fields (text, images, objects)
- ✅ Proper file handling with MIME types
- ✅ Configurable timeouts
- ✅ Works on both Android and Web

## Install

```bash
npm install capacitor-formdata
npx cap sync
```

## Quick Example

```typescript
import { FormData } from 'capacitor-formdata';

const result = await FormData.uploadFormData({
  url: 'https://your-api.com/upload',
  headers: {
    'Authorization': 'Bearer your-token-here'
  },
  formData: {
    'image': 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD...',
    'title': 'My Upload',
    'userId': '123'
  }
});

console.log('Status:', result.status);
console.log('Response:', result.data);
```

For more examples, see [USAGE.md](./USAGE.md)

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`uploadFormData(...)`](#uploadformdata)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

Echo a value for testing purposes

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### uploadFormData(...)

```typescript
uploadFormData(options: { url: string; headers?: { [key: string]: string; } | undefined; formData: { [key: string]: any; }; timeout?: number | undefined; }) => Promise<{ status: number; statusText: string; headers: { [key: string]: string; }; data: any; }>
```

Upload multipart form data with image blobs to an API endpoint

| Param         | Type                                                                                                                     | Description            |
| ------------- | ------------------------------------------------------------------------------------------------------------------------ | ---------------------- |
| **`options`** | <code>{ url: string; headers?: { [key: string]: string; }; formData: { [key: string]: any; }; timeout?: number; }</code> | - Upload configuration |

**Returns:** <code>Promise&lt;{ status: number; statusText: string; headers: { [key: string]: string; }; data: any; }&gt;</code>

--------------------

</docgen-api>
