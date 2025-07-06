import { FormData } from '../src/index';

// Test for the plugin
async function testPlugin() {
  console.log('Testing FormData plugin...');
  
  try {
    // Test echo function
    const echoResult = await FormData.echo({ value: 'Hello World' });
    console.log('Echo test passed:', echoResult);
    
    // Test uploadFormData function (this will use the web implementation in Node.js)
    const uploadResult = await FormData.uploadFormData({
      url: 'https://httpbin.org/post',
      headers: {
        'Authorization': 'Bearer test-token',
        'X-Custom-Header': 'test-value'
      },
      formData: {
        'text_field': 'test text',
        'number_field': '123',
        'object_field': { key: 'value' }
      },
      timeout: 10000
    });
    
    console.log('Upload test passed:', uploadResult);
    
  } catch (error) {
    console.error('Test failed:', error);
  }
}

// Run the test
testPlugin();
