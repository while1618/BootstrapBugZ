<script lang="ts">
  import en from '$lib/i18n/en.json';

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  export let form: any;
</script>

{#if form?.errorMessage}
  {#each form.errorMessage.details as error}
    <div class="flex gap-4">
      <p class="label-text text-error">{error.message}</p>
      {#if error.message === en['user.notActivated']}
        <a
          href="/auth/resend-confirmation-email?usernameOrEmail={form.usernameOrEmail}"
          class="label-text text-info hover:underline"
        >
          Resend confirmation email
        </a>
      {/if}
    </div>
  {/each}
{/if}
