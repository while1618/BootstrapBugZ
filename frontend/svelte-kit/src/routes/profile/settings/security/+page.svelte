<script lang="ts">
  import { enhance } from '$app/forms';
  import Modal from '$lib/components/modal.svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
  let dialog: HTMLDialogElement;
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      <div class="flex w-full flex-col">
        <div class="card">
          <form class="flex flex-col gap-4" method="POST" action="?/changePassword" use:enhance>
            <div class="form-control w-full max-w-xs">
              <label for="oldPassword" class="label">
                <span class="label-text">Old password</span>
              </label>
              <input
                type="password"
                id="oldPassword"
                name="oldPassword"
                class="input-bordered input w-full max-w-xs"
              />
              {#if form?.errors?.oldPassword}
                <label for="oldPassword" class="label">
                  <span class="label-text text-error">{form.errors.oldPassword[0]}</span>
                </label>
              {/if}
            </div>

            <div class="form-control w-full max-w-xs">
              <label for="newPassword" class="label">
                <span class="label-text">New password</span>
              </label>
              <input
                type="password"
                id="newPassword"
                name="newPassword"
                class="input-bordered input w-full max-w-xs"
              />
              {#if form?.errors?.newPassword}
                <label for="newPassword" class="label">
                  <span class="label-text text-error">{form.errors.newPassword[0]}</span>
                </label>
              {/if}
            </div>

            <div class="form-control w-full max-w-xs">
              <label for="confirmNewPassword" class="label">
                <span class="label-text">Confirm new password</span>
              </label>
              <input
                type="password"
                id="confirmNewPassword"
                name="confirmNewPassword"
                class="input-bordered input w-full max-w-xs"
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

            <button class="btn-primary btn">Change password</button>
          </form>
        </div>
        <div class="divider" />
        <div class="card">
          <a href="/auth/sign-out-from-all-devices" class="btn-primary btn">
            Sign out form all devices
          </a>
        </div>
        <div class="divider" />
        <div class="card">
          <button class="btn-error btn" on:click|stopPropagation={() => dialog.showModal()}>
            Delete Account
          </button>
        </div>
      </div>
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
