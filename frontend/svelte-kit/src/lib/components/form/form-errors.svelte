<script lang="ts">
  import * as m from '$lib/paraglide/messages.js';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  export let form: any;
</script>

{#if form?.errorMessage}
  {#each form.errorMessage.codes as code}
    <div class="flex gap-4">
      <p class="label-text text-error">{m[code] ? m[code]() : m.api_unknown_error()}</p>
    </div>
  {/each}
{/if}
