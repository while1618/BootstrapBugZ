<script lang="ts">
  import FormControl from '$lib/components/form/form-control.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from './$types';
  import { resetPasswordSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();
  const superform = superForm(data.form, {
    validators: zodClient(resetPasswordSchema),
  });
  const { errors, enhance } = superform;
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card bg-base-200 mx-auto w-full max-w-xl p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.auth_resetPassword()}</h1>
        <form
          class="flex flex-col gap-4"
          method="POST"
          action="?/resetPassword"
          use:enhance
          novalidate
        >
          <FormControl {superform} field="password" type="password" label={m.auth_password()} />
          <FormControl
            {superform}
            field="confirmPassword"
            type="password"
            label={m.auth_confirmPassword()}
          />
          <p class="label-text text-error">{$errors?._errors}</p>
          <button class="btn btn-primary">{m.auth_resetPassword()}</button>
        </form>
      </div>
    </div>
  </div>
</section>
