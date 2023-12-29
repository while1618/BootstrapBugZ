<script lang="ts">
  import { enhance } from '$app/forms';
  import { page } from '$app/stores';
  import type { ActionData } from './$types';

  export let form: ActionData;
  export let token = $page.url.searchParams.get('token');
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      <form
        class="flex flex-col gap-4"
        method="POST"
        action="?/resetPassword&token={token}"
        use:enhance
      >
        <div class="form-control w-full max-w-xs">
          <label for="password" class="label">
            <span class="label-text">Password</span>
          </label>
          <input
            type="password"
            id="password"
            name="password"
            class="input input-bordered w-full max-w-xs"
          />
          {#if form?.errors?.password}
            <label for="password" class="label">
              <span class="label-text text-error">{form.errors.password[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full max-w-xs">
          <label for="confirmPassword" class="label">
            <span class="label-text">Confirm password</span>
          </label>
          <input
            type="password"
            id="confirmPassword"
            name="confirmPassword"
            class="input input-bordered w-full max-w-xs"
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
