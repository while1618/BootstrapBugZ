<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form/form-control.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import type { ActionData } from './$types';

  export let form: ActionData;
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.auth_signIn()}</h1>
        <form class="flex flex-col gap-4" method="POST" action="?/signIn" use:enhance>
          <FormControl {form} type="text" name="usernameOrEmail" label={m.auth_usernameOrEmail()} />
          <FormControl {form} type="password" name="password" label={m.auth_password()} />

          {#if form?.errorMessage}
            {#each form.errorMessage.details as error}
              <div class="flex gap-4">
                <p class="label-text text-error">{error.message}</p>
                {#if error.message === 'User not activated.'}
                  <a
                    href="/auth/resend-confirmation-email?usernameOrEmail={form.usernameOrEmail}"
                    class="label-text text-info hover:underline"
                  >
                    {m.auth_resendConfirmationEmail()}
                  </a>
                {/if}
              </div>
            {/each}
          {/if}

          <button class="btn btn-primary">{m.auth_signIn()}</button>
          <div class="flex gap-4">
            <span class="label-text">
              {m.auth_doNotHaveAnAccount()}
              <a href="/auth/sign-up" class="text-info hover:underline">
                {m.auth_signUp()}
              </a>
            </span>
            <a href="/auth/forgot-password" class="label-text ml-auto text-info hover:underline">
              {m.auth_forgotPassword()}
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>
</section>
