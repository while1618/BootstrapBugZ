<script lang="ts">
  import { enhance } from '$app/forms';
  import en from '$lib/i18n/en.json';
  import type { ActionData } from './$types';

  export let form: ActionData;
  let usernameOrEmail = '';
</script>

<div class="hero min-h-screen">
  <div class="hero-content text-center">
    <div class="max-w-md">
      <form class="flex flex-col gap-4" method="POST" action="?/signIn" use:enhance>
        <div class="form-control w-full max-w-xs">
          <label for="usernameOrEmail" class="label">
            <span class="label-text">Username or email</span>
          </label>
          <input
            type="text"
            id="usernameOrEmail"
            name="usernameOrEmail"
            class="input-bordered input w-full max-w-xs"
            bind:value={usernameOrEmail}
          />
          {#if form?.errors?.usernameOrEmail}
            <label for="usernameOrEmail" class="label">
              <span class="label-text text-error">{form.errors.usernameOrEmail[0]}</span>
            </label>
          {/if}
        </div>

        <div class="form-control w-full max-w-xs">
          <label for="password" class="label">
            <span class="label-text">Password</span>
          </label>
          <input
            type="password"
            id="password"
            name="password"
            class="input-bordered input w-full max-w-xs"
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
                  href="/resend-confirmation-email?usernameOrEmail={usernameOrEmail}"
                  class="label-text text-info hover:underline"
                >
                  Resend confirmation email
                </a>
              {/if}
            </div>
          {/each}
        {/if}

        <button class="btn-primary btn">Sign in</button>

        <div class="flex gap-4">
          <span class="label-text">
            Not registered? <a href="/sign-up" class="text-info hover:underline">Sign up</a>
          </span>
          <a href="/forgot-password" class="label-text ml-auto text-info hover:underline">
            Forgot password?
          </a>
        </div>
      </form>
    </div>
  </div>
</div>
