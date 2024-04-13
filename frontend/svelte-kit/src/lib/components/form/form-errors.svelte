<script lang="ts">
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  export let form: any;
</script>

{#if form?.errorMessage}
  {#each form.errorMessage.details as error}
    <div class="flex gap-4">
      <p class="label-text text-error">{error.message}</p>
    </div>
  {/each}
{/if}
