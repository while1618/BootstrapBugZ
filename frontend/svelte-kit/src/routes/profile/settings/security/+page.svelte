<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form/form-control.svelte';
  import FormErrors from '$lib/components/form/form-errors.svelte';
  import Modal from '$lib/components/shared/modal.svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
  let dialog: HTMLDialogElement;
</script>

<div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
  <div class="flex flex-col gap-2">
    <h1 class="mb-6 text-center text-3xl font-bold">Security</h1>
    <form class="flex flex-col gap-4" method="POST" action="?/changePassword" use:enhance>
      <FormControl {form} type="password" id="oldPassword" label="Old password" />
      <FormControl {form} type="password" id="newPassword" label="New password" />
      <FormControl {form} type="password" id="confirmNewPassword" label="Confirm new password" />
      <FormErrors {form} />
      <button class="btn btn-primary">Change password</button>
    </form>
    <div class="divider" />
    <a href="/auth/sign-out-from-all-devices" class="btn btn-primary">
      Sign out form all devices
    </a>
    <div class="divider" />
    <button class="btn btn-error" on:click|stopPropagation={() => dialog.showModal()}>
      Delete Account
    </button>
  </div>
</div>

<Modal bind:dialog title="Delete account">
  <svelte:fragment slot="body">
    <p class="py-4">Are you sure you want to delete account?</p>
  </svelte:fragment>
  <svelte:fragment slot="actions">
    <form method="POST" action="?/delete" use:enhance>
      <button type="submit" class="btn text-error" on:click={() => dialog.close()}>Delete</button>
      <button type="button" class="btn" on:click={() => dialog.close()}>Cancel</button>
    </form>
  </svelte:fragment>
</Modal>
