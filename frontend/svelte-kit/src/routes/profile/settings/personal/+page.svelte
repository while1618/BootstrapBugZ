<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form/form-control.svelte';
  import FormErrors from '$lib/components/form/form-errors.svelte';
  import * as m from '$lib/paraglide/messages.js';
  import { userStore } from '$lib/stores/user';
  import type { ActionData } from '../personal/$types';

  export let form: ActionData;
</script>

<div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
  <div class="flex flex-col gap-2">
    <h1 class="mb-6 text-center text-3xl font-bold">{m.profile()}</h1>
    <form
      class="flex flex-col gap-4"
      method="POST"
      action="?/updateProfile"
      use:enhance={() => {
        return async ({ update }) => {
          update({ reset: false });
        };
      }}
    >
      <FormControl
        {form}
        type="text"
        name="firstName"
        label={m.profile_firstName()}
        value={$userStore?.firstName}
      />
      <FormControl
        {form}
        type="text"
        name="lastName"
        label={m.profile_lastName()}
        value={$userStore?.lastName}
      />
      <FormControl
        {form}
        type="text"
        name="username"
        label={m.profile_username()}
        value={$userStore?.username}
      />
      <FormControl
        {form}
        type="email"
        name="email"
        label={m.profile_email()}
        value={$userStore?.email}
      />
      <FormErrors {form} />
      <button class="btn btn-primary">{m.general_update()}</button>
    </form>
  </div>
</div>
