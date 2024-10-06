import type { ParaglideLocals } from '@inlang/paraglide-sveltekit';
import type { AvailableLanguageTag } from '../../lib/paraglide/runtime';
// See https://kit.svelte.dev/docs/types#app

declare global {
  namespace App {
    interface Locals {
      paraglide: ParaglideLocals<AvailableLanguageTag>;
      userId: string | null;
    }
    interface Error {
      message: string;
    }
  }
}

export {};
