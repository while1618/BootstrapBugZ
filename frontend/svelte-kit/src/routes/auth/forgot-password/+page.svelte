<script lang="ts">
  import FormControl from '$lib/components/form/form-control.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from './$types';
  import { forgotPasswordSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();
  const superform = superForm(data.form, {
    validators: zodClient(forgotPasswordSchema),
  });
  const { message, errors, enhance } = superform;
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card bg-base-200 mx-auto w-full max-w-xl p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.auth_forgotPassword()}</h1>
        <form
          class="flex flex-col gap-4"
          method="POST"
          action="?/forgotPassword"
          use:enhance
          novalidate
        >
          <FormControl {superform} field="email" type="email" label={m.auth_email()} />
          <p class="label-text text-error">{$errors?._errors}</p>
          <button class="btn btn-primary">{m.general_send()}</button>
        </form>
      </div>
    </div>
    {#if $message}
      <div class="toast">
        <div class="alert alert-info">
          <span>{$message}</span>
        </div>
      </div>
    {/if}
  </div>
</section>
