<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form/form-control.svelte';
  import PrintApiErrors from '$lib/components/form/print-api-errors.svelte';
  import Modal from '$lib/components/shared/modal.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import type { ActionData } from './$types';

  export let form: ActionData;
  let dialog: HTMLDialogElement;
</script>

<div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
  <div class="flex flex-col gap-2">
    <h1 class="mb-6 text-center text-3xl font-bold">{m.profile_security()}</h1>
    <form class="flex flex-col gap-4" method="POST" action="?/changePassword" use:enhance>
      <FormControl {form} type="password" name="oldPassword" label={m.profile_oldPassword()} />
      <FormControl {form} type="password" name="newPassword" label={m.profile_newPassword()} />
      <FormControl
        {form}
        type="password"
        name="confirmNewPassword"
        label={m.profile_confirmNewPassword()}
      />
      <PrintApiErrors {form} />
      <button class="btn btn-primary">{m.profile_changePassword()}</button>
    </form>
    <div class="divider" />
    <a href="/auth/sign-out-from-all-devices" class="btn btn-primary">
      {m.profile_signOutFromAllDevices()}
    </a>
    <div class="divider" />
    <button class="btn btn-error" on:click|stopPropagation={() => dialog.showModal()}>
      {m.profile_delete()}
    </button>
  </div>
</div>

<Modal bind:dialog title="Delete account">
  <svelte:fragment slot="body">
    <p class="py-4">{m.profile_deleteAccountConfirmation()}</p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form method="POST" action="?/delete" use:enhance>
      <button type="submit" class="btn text-error" on:click={() => dialog.close()}>
        {m.general_delete()}
      </button>
      <button type="button" class="btn" on:click={() => dialog.close()}>
        {m.general_cancel()}
      </button>
    </form>
  </svelte:fragment>
</Modal>
