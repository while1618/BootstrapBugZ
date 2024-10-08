<script lang="ts">
  import { ErrorCode } from '$lib/models/shared/error-code';
  import * as m from '$lib/paraglide/messages.js';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  export let form: any;

  function printApiError(code: keyof typeof ErrorCode) {
    return ErrorCode[code] ? m[ErrorCode[code]]() : m.unknown_error();
  }
</script>

{#if form?.errorMessage}
  {#each form.errorMessage.codes as code}
    <div class="flex gap-4">
      <p class="label-text text-error">{printApiError(code)}</p>
    </div>
  {/each}
{/if}
