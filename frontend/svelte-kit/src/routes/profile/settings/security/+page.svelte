<script lang="ts">
  import { enhance } from '$app/forms';
  import ApiErrors from '$lib/components/form/api-errors.svelte';
  import FormControl from '$lib/components/form/form-control.svelte';
  import Modal from '$lib/components/shared/modal.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import type { ActionData } from './$types';

  interface Props {
    form: ActionData;
  }

  let { form }: Props = $props();
  let dialog: HTMLDialogElement = $state() as HTMLDialogElement;
</script>

<div class="card bg-base-200 mx-auto w-full max-w-xl p-8 shadow-xl">
  <div class="flex flex-col gap-2">
    <h1 class="mb-6 text-center text-3xl font-bold">{m.profile_security()}</h1>
    <form class="flex flex-col gap-4" method="POST" action="?/changePassword" use:enhance>
      <FormControl {form} type="password" name="oldPassword" label={m.profile_currentPassword()} />
      <FormControl {form} type="password" name="newPassword" label={m.profile_newPassword()} />
      <FormControl
        {form}
        type="password"
        name="confirmNewPassword"
        label={m.profile_confirmNewPassword()}
      />
      <ApiErrors {form} />
      <button class="btn btn-primary">{m.profile_changePassword()}</button>
    </form>
    <div class="divider"></div>
    <a href="/auth/sign-out-from-all-devices" class="btn btn-primary">
      {m.profile_signOutFromAllDevices()}
    </a>
    <div class="divider"></div>
    <button
      class="btn btn-error"
      onclick={(event: Event) => {
        event.stopPropagation();
        dialog.showModal();
      }}
    >
      {m.profile_delete()}
    </button>
  </div>
</div>

<Modal bind:dialog title="Delete account">
  {#snippet body()}
    <p class="py-4">{m.profile_deleteAccountConfirmation()}</p>
  {/snippet}
  {#snippet actions()}
    <form method="POST" action="?/delete" use:enhance>
      <button type="submit" class="btn text-error" onclick={() => dialog.close()}>
        {m.general_delete()}
      </button>
      <button type="button" class="btn" onclick={() => dialog.close()}>
        {m.general_cancel()}
      </button>
    </form>
  {/snippet}
</Modal>
