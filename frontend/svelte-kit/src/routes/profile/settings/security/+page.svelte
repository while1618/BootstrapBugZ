<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/modal.svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
  let dialog: HTMLDialogElement;
</script>

<div class="flex h-screen items-center justify-center">
  <div class="card mx-auto w-full max-w-xl bg-base-100 p-8 shadow-xl">
    <div class="flex flex-col gap-2">
      <h1 class="mb-6 text-center text-3xl font-bold">Security</h1>
      <form class="flex flex-col gap-4" method="POST" action="?/changePassword" use:enhance>
        <div class="form-control w-full">
          <label for="oldPassword" class="label">
            <span class="label-text">Old password</span>
          </label>
          <input
            type="password"
            id="oldPassword"
            name="oldPassword"
            class="input input-bordered w-full"
          />
          {#if form?.errors?.oldPassword}
            <label for="oldPassword" class="label">
              <span class="label-text text-error">{form.errors.oldPassword[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full">
          <label for="newPassword" class="label">
            <span class="label-text">New password</span>
          </label>
          <input
            type="password"
            id="newPassword"
            name="newPassword"
            class="input input-bordered w-full"
          />
          {#if form?.errors?.newPassword}
            <label for="newPassword" class="label">
              <span class="label-text text-error">{form.errors.newPassword[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full">
          <label for="confirmNewPassword" class="label">
            <span class="label-text">Confirm new password</span>
          </label>
          <input
            type="password"
            id="confirmNewPassword"
            name="confirmNewPassword"
            class="input input-bordered w-full"
          />
          {#if form?.errors?.confirmNewPassword}
            <label for="confirmNewPassword" class="label">
              <span class="label-text text-error">{form.errors.confirmNewPassword[0]}</span>
            </label>
          {/if}
        </div>

        {#if form?.errorMessage}
          {#each form.errorMessage.details as error}
            <div class="flex">
              <p class="label-text text-error">{error.message}</p>
            </div>
          {/each}
        {/if}

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
