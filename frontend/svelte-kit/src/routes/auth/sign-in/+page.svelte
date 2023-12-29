<script lang="ts">
  import { enhance } from '$app/forms';
  import en from '$lib/i18n/en.json';
  import type { ActionData } from './$types';

  export let form: ActionData;
  let usernameOrEmail = '';
</script>

<div class="flex h-screen items-center justify-center">
  <div class="card mx-auto w-full max-w-xl p-8 shadow-xl">
    <div class="flex flex-col gap-10">
      <h1 class="text-center text-3xl font-bold">Sign in</h1>
      <form class="flex flex-col gap-4" method="POST" action="?/signIn" use:enhance>
        <div class="form-control w-full">
          <label for="usernameOrEmail" class="label">
            <span class="label-text">Username or email</span>
          </label>
          <input
            type="text"
            id="usernameOrEmail"
            name="usernameOrEmail"
            class="input input-bordered w-full"
            bind:value={usernameOrEmail}
          />
          {#if form?.errors?.usernameOrEmail}
            <label for="usernameOrEmail" class="label">
              <span class="label-text text-error">{form.errors.usernameOrEmail[0]}</span>
            </label>
          {/if}
        </div>

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

        {#if form?.errorMessage}
          {#each form.errorMessage.details as error}
            <div class="flex gap-4">
              <p class="label-text text-error">{error.message}</p>
              {#if error.message === en['user.notActivated']}
                <a
                  href="/auth/resend-confirmation-email?usernameOrEmail={usernameOrEmail}"
                  class="label-text text-info hover:underline"
                >
                  Resend confirmation email
                </a>
              {/if}
            </div>
          {/each}
        {/if}

        <button class="btn btn-primary">Sign in</button>

        <div class="flex gap-4">
          <span class="label-text">
            Don't have an account? <a href="/auth/sign-up" class="text-info hover:underline">
              Sign up
            </a>
          </span>
          <a href="/auth/forgot-password" class="label-text ml-auto text-info hover:underline">
            Forgot password?
          </a>
        </div>
      </form>
    </div>
  </div>
</div>
