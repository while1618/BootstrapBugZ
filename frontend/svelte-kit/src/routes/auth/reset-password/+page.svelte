<script lang="ts">
  import { enhance } from '$app/forms';
  import { page } from '$app/stores';
  import FormControl from '$lib/components/form/form-control.svelte';
  import FormErrors from '$lib/components/form/form-errors.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import type { ActionData } from './$types';

  export let form: ActionData;
  export let token = $page.url.searchParams.get('token');
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.auth_resetPassword()}</h1>
        <form
          class="flex flex-col gap-4"
          method="POST"
          action="?/resetPassword&token={token}"
          use:enhance
        >
          <FormControl {form} type="password" name="password" label={m.auth_password()} />
          <FormControl
            {form}
            type="password"
            name="confirmPassword"
            label={m.auth_confirmPassword()}
          />
          {#if form?.errors?.token}
            <div class="flex">
              <p class="label-text text-error">{form.errors.token[0]}</p>
            </div>
          {/if}
          <FormErrors {form} />

          <button class="btn btn-primary">{m.auth_resetPassword()}</button>
        </form>
      </div>
    </div>
  </div>
</section>
