<script lang="ts">
  import { enhance } from '$app/forms';
  import FormControl from '$lib/components/form-control.svelte';
  import FormErrors from '$lib/components/form-errors.svelte';
  import { userStore } from '$lib/stores/user';
  import type { ActionData } from '../personal/$types';

  export let form: ActionData;
</script>

<div class="card mx-auto w-full max-w-xl bg-base-200 p-8 shadow-xl">
  <div class="flex flex-col gap-2">
    <h1 class="mb-6 text-center text-3xl font-bold">Profile</h1>
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
        id="firstName"
        label="First name"
        value={$userStore?.firstName}
      />
      <FormControl
        {form}
        type="text"
        id="lastName"
        label="Last name"
        value={$userStore?.lastName}
      />
      <FormControl {form} type="text" id="username" label="Username" value={$userStore?.username} />
      <FormControl {form} type="email" id="email" label="Email" value={$userStore?.email} />
      <FormErrors {form} />
      <button class="btn btn-primary">Update</button>
    </form>
  </div>
</div>
