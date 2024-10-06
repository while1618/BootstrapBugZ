<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form/form-control.svelte';
  import FormErrors from '$lib/components/form/form-errors.svelte';
  import Modal from '$lib/components/shared/modal.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import type { ActionData } from './$types';

  export let form: ActionData;
  let dialog: HTMLDialogElement;
</script>

<div class="card bg-base-200 mx-auto w-full max-w-xl p-8 shadow-xl">
  <div class="flex flex-col gap-2">
    <h1 class="mb-6 text-center text-3xl font-bold">{m.security()}</h1>
    <form class="flex flex-col gap-4" method="POST" action="?/changePassword" use:enhance>
      <FormControl {form} type="password" id="oldPassword" label={m.oldPassword()} />
      <FormControl {form} type="password" id="newPassword" label={m.newPassword()} />
      <FormControl {form} type="password" id="confirmNewPassword" label={m.confirmNewPassword()} />
      <FormErrors {form} />
      <button class="btn btn-primary">{m.changePassword()}</button>
    </form>
    <div class="divider" />
    <a href="/auth/sign-out-from-all-devices" class="btn btn-primary">
      {m.signOutFromAllDevices()}
    </a>
    <div class="divider" />
    <button class="btn btn-error" on:click|stopPropagation={() => dialog.showModal()}>
      {m.deleteAccount()}
    </button>
  </div>
</div>

<Modal bind:dialog title="Delete account">
  <svelte:fragment slot="body">
    <p class="py-4">{m.deleteAccountConfirmation()}</p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form method="POST" action="?/delete" use:enhance>
      <button type="submit" class="btn text-error" on:click={() => dialog.close()}>
        {m.myDelete()}
      </button>
      <button type="button" class="btn" on:click={() => dialog.close()}>{m.cancel()}</button>
    </form>
  </svelte:fragment>
</Modal>
