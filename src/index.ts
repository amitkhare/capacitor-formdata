import { registerPlugin } from '@capacitor/core';

import type { FormDataPlugin } from './definitions';

const FormData = registerPlugin<FormDataPlugin>('FormData', {
  web: () => import('./web').then((m) => new m.FormDataWeb()),
});

export * from './definitions';
export { FormData };
