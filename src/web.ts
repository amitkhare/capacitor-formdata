import { WebPlugin } from '@capacitor/core';

import type { FormDataPlugin } from './definitions';

export class FormDataWeb extends WebPlugin implements FormDataPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
