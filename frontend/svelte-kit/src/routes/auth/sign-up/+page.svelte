<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form/form-control.svelte';
  import FormErrors from '$lib/components/form/form-errors.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import type { ActionData } from './$types';

  export let form: ActionData;
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.signUp()}</h1>
        <form class="flex flex-col gap-4" method="POST" action="?/signUp" use:enhance>
          <FormControl {form} type="text" id="firstName" label={m.firstName()} />
          <FormControl {form} type="text" id="lastName" label={m.lastName()} />
          <FormControl {form} type="text" id="username" label={m.username()} />
          <FormControl {form} type="email" id="email" label={m.email()} />
          <FormControl {form} type="password" id="password" label={m.password()} />
          <FormControl {form} type="password" id="confirmPassword" label={m.confirmPassword()} />
          <FormErrors {form} />
          <button class="btn btn-primary">{m.signUp()}</button>
        </form>
      </div>
    </div>
  </div>
</section>
