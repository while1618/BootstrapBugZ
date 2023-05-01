<script lang="ts">
  import { enhance } from '$app/forms';
  import { userStore } from '$lib/stores/user';
  import type { ActionData } from '../personal/$types';

  export let form: ActionData;
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      <form
        class="flex flex-col gap-4"
        method="POST"
        action="?/updateProfile"
        use:enhance={() => {
          return async ({ update }) => {
            update({ reset: false });
          };
        }}
      >
        <div class="form-control w-full max-w-xs">
          <label for="firstName" class="label">
            <span class="label-text">First name</span>
          </label>
          <input
            type="text"
            id="firstName"
            name="firstName"
            class="input-bordered input w-full max-w-xs"
            value={$userStore?.firstName}
          />
          {#if form?.errors?.firstName}
            <label for="firstName" class="label">
              <span class="label-text text-error">{form.errors.firstName[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full max-w-xs">
          <label for="lastName" class="label">
            <span class="label-text">Last name</span>
          </label>
          <input
            type="text"
            id="lastName"
            name="lastName"
            class="input-bordered input w-full max-w-xs"
            value={$userStore?.lastName}
          />
          {#if form?.errors?.lastName}
            <label for="lastName" class="label">
              <span class="label-text text-error">{form.errors.lastName[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full max-w-xs">
          <label for="username" class="label">
            <span class="label-text">Username</span>
          </label>
          <input
            type="text"
            id="username"
            name="username"
            class="input-bordered input w-full max-w-xs"
            value={$userStore?.username}
          />
          {#if form?.errors?.username}
            <label for="username" class="label">
              <span class="label-text text-error">{form.errors.username[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full max-w-xs">
          <label for="email" class="label">
            <span class="label-text">Email</span>
          </label>
          <input
            type="email"
            id="email"
            name="email"
            class="input-bordered input w-full max-w-xs"
            value={$userStore?.email}
          />
          {#if form?.errors?.email}
            <label for="email" class="label">
              <span class="label-text text-error">{form.errors.email[0]}</span>
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

        <button class="btn-primary btn">Update</button>
      </form>
    </div>
  </div>
</div>
