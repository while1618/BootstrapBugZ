<script lang="ts">
  import { page } from '$app/stores';
  import { Button } from '$lib/components/ui/button';
  import * as m from '$lib/paraglide/messages.js';
</script>

<section>
  <div class="container">
    <div class="m-20 flex items-center justify-center">
      <div class="flex max-w-lg flex-col items-center gap-5 text-center">
        <h1 class="text-6xl font-extrabold">{$page.status}</h1>
        <p>{$page.error?.message}</p>
        <Button href="/" class="bg-blue-400 hover:bg-blue-300">{m.general_homepage()}</Button>
      </div>
    </div>
  </div>
</section>
