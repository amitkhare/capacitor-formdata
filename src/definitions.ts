export interface FormDataPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
