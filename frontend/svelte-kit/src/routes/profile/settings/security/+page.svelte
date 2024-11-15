<script lang="ts">
  import FormControl from '$lib/components/form/form-control.svelte';
  import Modal from '$lib/components/shared/modal.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from './$types';
  import { changePasswordSchema, deleteSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();

  const changePasswordSuperform = superForm(data.changePasswordForm, {
    validators: zodClient(changePasswordSchema),
  });
  const {
    message,
    errors: changePasswordErrors,
    enhance: changePasswordEnhance,
  } = changePasswordSuperform;

  const deleteSuperform = superForm(data.deleteForm, {
    validators: zodClient(deleteSchema),
  });
  const { errors: deleteErrors, enhance: deleteEnhance } = deleteSuperform;

  let dialog: HTMLDialogElement = $state() as HTMLDialogElement;
</script>

<div class="card bg-base-200 mx-auto w-full max-w-xl p-8 shadow-xl">
  <div class="flex flex-col gap-2">
    <h1 class="mb-6 text-center text-3xl font-bold">{m.profile_security()}</h1>
    <form
      class="flex flex-col gap-4"
      method="POST"
      action="?/changePassword&username={data.user?.username}"
      use:changePasswordEnhance
      novalidate
    >
      <FormControl
        superform={changePasswordSuperform}
        field="currentPassword"
        type="password"
        label={m.profile_currentPassword()}
      />
      <FormControl
        superform={changePasswordSuperform}
        field="newPassword"
        type="password"
        label={m.profile_newPassword()}
      />
      <FormControl
        superform={changePasswordSuperform}
        field="confirmNewPassword"
        type="password"
        label={m.profile_confirmNewPassword()}
      />
      <p class="label-text text-error">{$changePasswordErrors?._errors}</p>
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
    <p class="label-text text-error text-center">{$deleteErrors?._errors}</p>
  </div>
</div>
{#if $message}
  <div class="toast">
    <div class="alert alert-info">
      <span>{$message}</span>
    </div>
  </div>
{/if}

<Modal bind:dialog title="Delete account">
  {#snippet body()}
    <p class="py-4">{m.profile_deleteAccountConfirmation()}</p>
  {/snippet}
  {#snippet actions()}
    <form method="POST" action="?/delete" use:deleteEnhance>
      <button type="submit" class="btn text-error" onclick={() => dialog.close()}>
        {m.general_delete()}
      </button>
      <button type="button" class="btn" onclick={() => dialog.close()}>
        {m.general_cancel()}
      </button>
    </form>
  {/snippet}
</Modal>
