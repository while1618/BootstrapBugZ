<script lang="ts">
  import { enhance } from '$app/forms';
  import ApiErrors from '$lib/components/form/api-errors.svelte';
  import FormControl from '$lib/components/form/form-control.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import type { ActionData } from './$types';

  export let form: ActionData;
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.auth_signUp()}</h1>
        <form class="flex flex-col gap-4" method="POST" action="?/signUp" use:enhance>
          <FormControl {form} type="text" name="username" label={m.auth_username()} />
          <FormControl {form} type="email" name="email" label={m.auth_email()} />
          <FormControl {form} type="password" name="password" label={m.auth_password()} />
          <FormControl
            {form}
            type="password"
            name="confirmPassword"
            label={m.auth_confirmPassword()}
          />
          <ApiErrors {form} />
          <button class="btn btn-primary">{m.auth_signUp()}</button>
        </form>
      </div>
    </div>
  </div>
</section>
