// See https://kit.svelte.dev/docs/types#app

declare global {
  namespace App {
    interface Locals {
      userId: number | null;
    }
  }
}

export {};
