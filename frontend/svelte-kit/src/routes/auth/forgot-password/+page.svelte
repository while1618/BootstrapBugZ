<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form/form-control.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import PrintApiErrors from '../../../lib/components/form/print-api-errors.svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.auth_forgotPassword()}</h1>
        <form class="flex flex-col gap-4" method="POST" action="?/forgotPassword" use:enhance>
          <FormControl {form} type="email" name="email" label={m.auth_email()} />
          <PrintApiErrors {form} />
          <button class="btn btn-primary">{m.general_send()}</button>
        </form>
      </div>
    </div>
  </div>
</section>
