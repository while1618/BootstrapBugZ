<script lang="ts">
  import { page } from '$app/stores';
  import * as m from '$lib/paraglide/messages.js';
</script>

<div class="flex h-screen flex-col items-center justify-center">
  <h1 class="text-9xl font-bold text-primary">{$page.status}</h1>
  <p class="mb-8 text-lg">{$page.error?.message}</p>
  <a href="/" class="btn btn-primary">{m.goHome()}</a>
</div>
