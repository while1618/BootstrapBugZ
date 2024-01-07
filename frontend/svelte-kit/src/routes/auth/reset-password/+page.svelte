<script lang="ts">
  import { enhance } from '$app/forms';
  import { page } from '$app/stores';
  import FormControl from '$lib/components/form-control.svelte';
  import FormErrors from '$lib/components/form-errors.svelte';
  import type { ActionData } from './$types';

  export let form: ActionData;
  export let token = $page.url.searchParams.get('token');
</script>

<div class="flex h-screen items-center justify-center">
  <div class="card mx-auto w-full max-w-xl bg-base-100 p-8 shadow-xl">
    <div class="flex flex-col gap-8">
      <h1 class="text-center text-3xl font-bold">Reset password</h1>
      <form
        class="flex flex-col gap-4"
        method="POST"
        action="?/resetPassword&token={token}"
        use:enhance
      >
        <FormControl {form} type="password" id="password" label="Password" />
        <FormControl {form} type="password" id="confirmPassword" label="Confirm password" />
        <FormErrors {form} />

        {#if form?.errors?.token}
          <div class="flex">
            <p class="label-text text-error">{form.errors.token[0]}</p>
          </div>
        {/if}

        <button class="btn btn-primary">Reset password</button>
      </form>
    </div>
  </div>
</div>
