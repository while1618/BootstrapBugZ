<script lang="ts">
  import * as m from '$lib/paraglide/messages.js';

  export let form: any;
</script>

{#if form?.errorMessage}
  {#each form.errorMessage.codes as code}
    <div class="flex gap-4">
      <p class="label-text text-error">{m[code] ? m[code]() : m.api_unknown_error()}</p>
    </div>
  {/each}
{/if}
