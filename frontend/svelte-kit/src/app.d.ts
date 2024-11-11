import type { ParaglideLocals } from '@inlang/paraglide-sveltekit';
import type { AvailableLanguageTag } from '../../lib/paraglide/runtime';
// See https://kit.svelte.dev/docs/types#app

declare global {
  namespace App {
    interface Locals {
      userId: string | null;
      paraglide: ParaglideLocals<AvailableLanguageTag>;
    }
    interface Error {
      message: string;
    }
  }
}

export {};
