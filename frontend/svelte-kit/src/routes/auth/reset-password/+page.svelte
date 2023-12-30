<script lang="ts">
  import { enhance } from '$app/forms';
  import { page } from '$app/stores';
  import type { ActionData } from './$types';

  export let form: ActionData;
  export let token = $page.url.searchParams.get('token');
</script>

<div class="flex h-screen items-center justify-center">
  <div class="card mx-auto w-full max-w-xl p-8 shadow-xl">
    <div class="flex flex-col gap-10">
      <h1 class="text-center text-3xl font-bold">Reset password</h1>
      <form
        class="flex flex-col gap-4"
        method="POST"
        action="?/resetPassword&token={token}"
        use:enhance
      >
        <div class="form-control w-full">
          <label for="password" class="label">
            <span class="label-text">Password</span>
          </label>
          <input
            type="password"
            id="password"
            name="password"
            class="input input-bordered w-full"
          />
          {#if form?.errors?.password}
            <label for="password" class="label">
              <span class="label-text text-error">{form.errors.password[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full">
          <label for="confirmPassword" class="label">
            <span class="label-text">Confirm password</span>
          </label>
          <input
            type="password"
            id="confirmPassword"
            name="confirmPassword"
            class="input input-bordered w-full"
          />
          {#if form?.errors?.confirmPassword}
            <label for="confirmPassword" class="label">
              <span class="label-text text-error">{form.errors.confirmPassword[0]}</span>
            </label>
          {/if}
        </div>

        {#if form?.errors?.token}
          <div class="flex">
            <p class="label-text text-error">{form.errors.token[0]}</p>
          </div>
        {/if}

        {#if form?.errorMessage}
          {#each form.errorMessage.details as error}
            <div class="flex">
              <p class="label-text text-error">{error.message}</p>
            </div>
          {/each}
        {/if}

        <button class="btn btn-primary">Reset password</button>
      </form>
    </div>
  </div>
</div>
