// See https://kit.svelte.dev/docs/types#app

import type { UserDTO } from '$lib/models/user';

declare global {
  namespace App {
    interface Locals {
      user: UserDTO | null;
    }
  }
}

export {};
