<script lang="ts">
  import FormControl from '$lib/components/form/form-control.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import { superForm } from 'sveltekit-superforms';
  import { zodClient } from 'sveltekit-superforms/adapters';
  import type { PageServerData } from './$types';
  import { signInSchema } from './schema';

  interface Props {
    data: PageServerData;
  }

  const { data }: Props = $props();
  const superform = superForm(data.form, {
    validators: zodClient(signInSchema),
  });
  const { errors, enhance } = superform;
</script>

<section class="py-10 md:py-16">
  <div class="container">
    <div class="card bg-base-200 mx-auto w-full max-w-xl p-8 shadow-xl">
      <div class="flex flex-col gap-8">
        <h1 class="text-center text-3xl font-bold">{m.auth_signIn()}</h1>
        <form class="flex flex-col gap-4" method="POST" action="?/signIn" use:enhance novalidate>
          <FormControl
            {superform}
            field="usernameOrEmail"
            type="text"
            label={m.auth_usernameOrEmail()}
          />
          <FormControl {superform} field="password" type="password" label={m.auth_password()} />
          <p class="label-text text-error">{$errors?._errors}</p>
          <button class="btn btn-primary">{m.auth_signIn()}</button>
          <div class="flex gap-4">
            <span class="label-text">
              {m.auth_doNotHaveAnAccount()}
              <a href="/auth/sign-up" class="text-info hover:underline">
                {m.auth_signUp()}
              </a>
            </span>
            <a href="/auth/forgot-password" class="label-text text-info ml-auto hover:underline">
              {m.auth_forgotPassword()}
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>
</section>
