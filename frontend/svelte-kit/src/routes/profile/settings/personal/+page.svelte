<script lang="ts">
  import FormControl from '$lib/components/form/form-control.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from '../personal/$types';
  import { updateProfileSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();
  const superform = superForm(data.form, {
    validators: zodClient(updateProfileSchema),
  });
  const { errors, enhance } = superform;
</script>

<div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
  <div class="flex flex-col gap-2">
    <h1 class="mb-6 text-center text-3xl font-bold">{m.profile()}</h1>
    <form class="flex flex-col gap-4" method="POST" action="?/updateProfile" use:enhance novalidate>
      <FormControl {superform} field="username" type="text" label={m.profile_username()} />
      <FormControl {superform} field="email" type="email" label={m.profile_email()} />
      <p class="label-text text-error">{$errors?._errors}</p>
      <button class="btn btn-primary">{m.general_update()}</button>
    </form>
  </div>
</div>
