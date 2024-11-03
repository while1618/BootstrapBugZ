<script lang="ts">
  import { ErrorCode } from '$lib/models/shared/error-message';
  import * as m from '$lib/paraglide/messages.js';

  interface Props {
    /* eslint-disable @typescript-eslint/no-explicit-any */
    form: any;
    /* eslint-enable */
  }

  let { form }: Props = $props();

  function printApiError(code: keyof typeof ErrorCode) {
    return ErrorCode[code] ? m[ErrorCode[code]]() : m.API_ERROR_UNKNOWN();
  }
</script>

{#if form?.errorMessage}
  {#each form.errorMessage.codes as code}
    <div class="flex gap-4">
      <p class="label-text text-error">{printApiError(code)}</p>
    </div>
  {/each}
{/if}
